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
package org.ops4j.pax.cm.scanner.core.internal;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ops4j.pax.cm.api.Configurer;
import org.ops4j.pax.swissbox.lifecycle.AbstractLifecycle;

/**
 * Buffer between scanners and configurator. The purpose is to isolate scanners from Configurator availability. The
 * buffer mimics the interface of configurator and is included into the scanner (so is always available).
 * Any operation on the configurer is stored in a blocking queue. As soon as configurer becomes available the queued
 * operations are performed agains the available configurer.
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 13, 2008
 */
public class ConfigurerCommandProcessor
    extends AbstractLifecycle
    implements ConfigurerSetter
{

    /**
     * Logger.
     */
    private static final Log LOG = LogFactory.getLog( ConfigurerCommandProcessor.class );

    /**
     * Configuration queue.
     */
    private BlockingQueue<ConfigurerCommand> m_queue;
    /**
     * Configurer in use.
     */
    private Configurer m_configurer;
    /**
     * Configurer lock.
     */
    private final Lock m_configurerLock;

    /**
     * Configurer availability condition.
     */
    private final Condition m_configurerAvailable;
    /**
     * Processing thread.
     */
    private Thread m_processor;
    /**
     * Signal sent in order to stop the processor.
     */
    private boolean m_stopProcessorSignal;

    /**
     * Creates a new configurations queue.
     */
    public ConfigurerCommandProcessor()
    {
        m_queue = new LinkedBlockingQueue<ConfigurerCommand>();
        m_configurerLock = new ReentrantLock();
        m_configurerAvailable = m_configurerLock.newCondition();
    }

    /**
     * Adds configurer commands to be processed.
     *
     * @param command configurer command to be processed as soon as configurer service becomes available
     */
    public void add( final ConfigurerCommand command )
    {
        m_queue.add( command );
    }

    /**
     * Setter.
     *
     * @param configurer to be set
     */
    public void setConfigurer( final Configurer configurer )
    {
        m_configurerLock.lock();
        try
        {
            m_configurer = configurer;
            m_configurerAvailable.signal();
        }
        finally
        {
            m_configurerLock.unlock();
        }
    }

    /**
     * Start queue processing.
     */
    protected void onStart()
    {
        m_stopProcessorSignal = false;
        m_processor = new Thread( new CommandProcessor() );
        m_processor.start();
    }

    /**
     * Stop queue processing.
     */
    protected void onStop()
    {
        m_stopProcessorSignal = true;
        m_processor.interrupt();
    }

    /**
     * Queue processor.
     */
    private class CommandProcessor
        implements Runnable
    {

        /**
         * Processing loop.
         */
        public void run()
        {
            LOG.trace( "Started processing of configuration queue" );
            ConfigurerCommand command = null;
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
                        m_configurerLock.lock();
                        try
                        {
                            while( m_configurer == null )
                            {
                                LOG.trace( "Configurer not available. Await..." );
                                m_configurerAvailable.await();
                            }
                            LOG.trace( "Configurer available. Executing " + command );
                            // ! we use run directly as for the moment we do not create a new thread for
                            // running the command
                            try
                            {
                                command.execute( m_configurer );
                            }
                            catch( Throwable ignore )
                            {
                                LOG.error( "Exception while executing command " + command, ignore );
                            }
                            command = null;
                        }
                        finally
                        {
                            m_configurerLock.unlock();
                        }
                    }
                }
                catch( Throwable ignore )
                {
                    // this could be due to an interruption or an exception during configuration
                    LOG.trace( "Unexpected stop of processing queue", ignore );
                }
            }
            LOG.trace( "Stopped processing of configuration queue" );
        }

    }

}
