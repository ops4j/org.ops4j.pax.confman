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
package org.ops4j.pax.cm.service.internal;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.cm.ConfigurationAdmin;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.cm.commons.internal.processor.CommandProcessor;
import org.ops4j.pax.swissbox.lifecycle.AbstractLifecycle;

/**
 * Blocking queue for transformations.
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 17, 2008
 */
class TransformationsProcessor
    extends AbstractLifecycle
{

    /**
     * Logger.
     */
    private static final Log LOG = LogFactory.getLog( TransformationsProcessor.class );

    /**
     * Configuration Admin commands processor.
     */
    private final CommandProcessor<ConfigurationAdmin> m_commandsProcessor;
    /**
     * Transformations queue.
     */
    private final BlockingQueue<Transformation> m_queue;
    /**
     * List of Transformations that faild execution.
     */
    private final Queue<Transformation> m_failed;
    /**
     * Processing thread.
     */
    private Thread m_processingThread;
    /**
     * Signal sent in order to stop the processor.
     */
    private boolean m_stopSignal;

    /**
     * Constructor.
     *
     * @param commandsProcessor command processor for succesfully executed transformations
     *
     * @throws NullArgumentException - If comand processor is null
     */
    TransformationsProcessor( final CommandProcessor<ConfigurationAdmin> commandsProcessor )
    {
        NullArgumentException.validateNotNull( commandsProcessor, "Commands processor" );

        m_commandsProcessor = commandsProcessor;
        m_queue = new LinkedBlockingQueue<Transformation>();
        m_failed = new ConcurrentLinkedQueue<Transformation>();
    }

    /**
     * Add transformations to be processed.
     *
     * @param transformation transformation to be processed
     */
    public void add( final Transformation transformation )
    {
        LOG.trace( "Added " + transformation );
        NullArgumentException.validateNotNull( transformation, "Transformation" );

        m_queue.add( transformation );
    }

    /**
     * Add transformations that failed execution to the queue. Ususally due to a change in external factors, as for
     * example list of adaptors has changed.
     */
    public void scheduleFailedTransformations()
    {
        synchronized( m_failed )
        {
            m_queue.addAll( m_failed );
            m_failed.clear();
        }
    }

    /**
     * Start transformations processing.
     */
    protected void onStart()
    {
        m_stopSignal = false;
        m_processingThread = new Thread( new RunnableCommandProcessor(), "Pax ConfMan - Transformation Processor" );
        m_processingThread.start();
    }

    /**
     * Stop transformations processing.
     */
    protected void onStop()
    {
        m_stopSignal = true;
        m_processingThread.interrupt();
    }

    /**
     * Actual transformations processor.
     */
    private class RunnableCommandProcessor
        implements Runnable
    {

        /**
         * Processing loop.
         */
        public void run()
        {
            LOG.trace( "Started transformations processing" );
            while( !m_stopSignal )
            {
                try
                {
                    final Transformation transformation = m_queue.take();
                    final UpdateCommand command = transformation.execute();
                    if( command == null )
                    {
                        m_failed.add( transformation );
                    }
                    else
                    {
                        m_commandsProcessor.add( command );
                    }
                }
                catch( InterruptedException ignore )
                {
                    // ignore
                }
            }
            LOG.trace( "Stopped commands processing" );
            if( LOG.isInfoEnabled() )
            {
                LOG.info( "The following transformations were not executed:" );
                for( Transformation transformation : m_queue )
                {
                    LOG.info( "Not executed: " + transformation );
                }
                for( Transformation transformation : m_failed )
                {
                    LOG.info( "Failed transformation: " + transformation );
                }
            }
        }

    }

}