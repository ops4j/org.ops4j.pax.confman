/*
 * Copyright 2008 Alin Dreghiciu.
 *
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.cm.commons.internal.processor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.swissbox.lifecycle.AbstractLifecycle;

/**
 * Waiting queue for commands to be processed. As soon as a target service becomes available the commands are proceesed.
 * If targeted service becomes unavailable processing stops till it will become available again.
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 13, 2008
 */
public class CommandProcessor<T>
    extends AbstractLifecycle
{

    /**
     * Logger.
     */
    private static final Log LOG = LogFactory.getLog( CommandProcessor.class );

    /**
     * Commands queue.
     */
    private final BlockingQueue<Command<T>> m_queue;
    /**
     * Targeted service.
     */
    private T m_targetService;
    /**
     * Target service lock.
     */
    private final Lock m_targetLock;
    /**
     * Target service availability condition.
     */
    private final Condition m_targetAvailable;
    /**
     * Processing thread.
     */
    private Thread m_processor;
    /**
     * Signal sent in order to stop the processor.
     */
    private boolean m_stopProcessorSignal;
    /**
     * Thread name to be used.
     */
    private final String m_threadName;

    /**
     * Constructor.
     *
     * @param name thread name
     */
    public CommandProcessor( final String name )
    {
        m_queue = new LinkedBlockingQueue<Command<T>>();
        m_targetLock = new ReentrantLock();
        m_targetAvailable = m_targetLock.newCondition();
        m_threadName = name;
    }

    /**
     * Adds commands to be processed.
     *
     * @param command command to be executed agains the target service when service is available
     */
    public void add( final Command<T> command )
    {
        LOG.trace( "Added " + command );
        NullArgumentException.validateNotNull( command, "Command" );

        m_queue.add( command );
    }

    /**
     * Setter.
     *
     * @param targetService to be set
     */
    public void setTargetService( final T targetService )
    {
        m_targetLock.lock();
        try
        {
            m_targetService = targetService;
            m_targetAvailable.signal();
        }
        finally
        {
            m_targetLock.unlock();
        }
    }

    /**
     * Start command processing.
     */
    protected void onStart()
    {
        m_stopProcessorSignal = false;
        m_processor = new Thread( new RunnableCommandProcessor(), m_threadName );
        m_processor.start();
    }

    /**
     * Stop command processing.
     */
    protected void onStop()
    {
        m_stopProcessorSignal = true;
        m_processor.interrupt();
    }

    /**
     * Actual command processor.
     */
    private class RunnableCommandProcessor
        implements Runnable
    {

        /**
         * Processing loop.
         */
        public void run()
        {
            LOG.trace( "Started commands processing" );
            Command<T> command = null;
            while( !m_stopProcessorSignal )
            {
                try
                {
                    if( command == null )
                    {
                        command = m_queue.take();
                    }
                    if( command != null )
                    {
                        m_targetLock.lock();
                        try
                        {
                            while( m_targetService == null )
                            {
                                LOG.debug( "Service not available. Await..." );
                                m_targetAvailable.await();
                            }
                            LOG.debug( "Executing " + command );
                            try
                            {
                                command.execute( m_targetService );
                            }
                            catch( Throwable ignore )
                            {
                                LOG.error( "Exception while executing command " + command, ignore );
                            }
                            command = null;
                        }
                        finally
                        {
                            m_targetLock.unlock();
                        }
                    }
                }
                catch( Throwable ignore )
                {
                    // this could be due to an interruption
                    LOG.trace( "Unexpected stop of processing", ignore );
                }
            }
            LOG.trace( "Stopped commands processing" );
        }

    }

}
