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
package org.ops4j.pax.cm.scanner.core;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.cm.api.Configurer;
import org.ops4j.pax.cm.composite.ConfigurationSourceComposite;
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
public class ConfigurationQueue
    extends AbstractLifecycle
    implements ConfigurerSetter
{

    /**
     * Logger.
     */
    private static final Log LOG = LogFactory.getLog( ConfigurationQueue.class );

    /**
     * Configuration queue.
     */
    private BlockingQueue<Runnable> m_queue;
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
    public ConfigurationQueue()
    {
        m_queue = new LinkedBlockingQueue<Runnable>();
        m_configurerLock = new ReentrantLock();
        m_configurerAvailable = m_configurerLock.newCondition();
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
     * @param configurationSource configuration source
     *
     * @see Configurer#configure(String, String, java.util.Dictionary, Object)
     */
    public void configure( final ConfigurationSourceComposite configurationSource )
    {
        m_queue.add( new ConfigureCommand( configurationSource ) );
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
            Runnable command = null;
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
                            command.run();
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
                }
            }
            LOG.trace( "Stopped processing of configuration queue" );
        }

    }

    /**
     * Process configuration source.
     */
    private class ConfigureCommand
        implements Runnable
    {

        /**
         * Configuration source.
         */
        private final ConfigurationSourceComposite m_configurationSource;

        /**
         * Create a new configuration command.
         *
         * @param configurationSource configuration source
         */
        ConfigureCommand( final ConfigurationSourceComposite configurationSource )
        {
            NullArgumentException.validateNotNull( configurationSource, "Configuration source" );
            m_configurationSource = configurationSource;
        }

        /**
         * Process configuration source.
         */
        public void run()
        {
            m_configurer.configure(
                m_configurationSource.pid().get(),
                m_configurationSource.location().get(),
                m_configurationSource.metadata().get(),
                m_configurationSource.configurationSource().get()
            );
        }

        @Override
        public String toString()
        {
            return new StringBuilder()
                .append( this.getClass().getSimpleName() )
                .append( "{" )
                .append( m_configurationSource )
                .append( "}" )
                .toString();
        }

    }

}
