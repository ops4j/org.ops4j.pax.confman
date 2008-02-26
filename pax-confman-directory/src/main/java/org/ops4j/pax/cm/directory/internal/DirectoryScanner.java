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
package org.ops4j.pax.cm.directory.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.lang.PreConditionException;
import org.ops4j.pax.cm.service.ChangeSetBean;
import org.ops4j.pax.cm.service.ChangeSetListener;
import org.ops4j.pax.cm.service.ChangeSetProducer;
import org.ops4j.pax.cm.service.ConfigurationProducer;
import org.ops4j.pax.cm.service.ConfigurationSource;

/**
 * Polls directories for file changes.
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 20, 2008
 */
class DirectoryScanner
    implements ConfigurationProducer, ChangeSetProducer
{

    /**
     * Logger.
     */
    private static final Log LOG = LogFactory.getLog( DirectoryScanner.class );

    /**
     * Scanned configuration directory.
     */
    private final ConfigurationDirectory m_configurationDirectory;
    /**
     * Interval of time in milliseconds between scanning the target directory.
     */
    private final long m_pollInterval;
    /**
     * List of change set listeners.
     */
    private final List<ChangeSetListener> m_listeners;
    /**
     * Map between configuration pid and configuration file.
     */
    private final Map<String, ConfigurationFile> m_configurationFiles;

    /**
     * Sheduled executor service.
     */
    private ScheduledExecutorService m_executor;

    /**
     * Constructor.
     *
     * @param configurationDirectory configurations directory to be scanned
     * @param pollInterval           poll interval between scans
     *
     * @throws NullArgumentException - If configuration directory is null
     * @throws PreConditionException - If poll interval <= 0
     */
    DirectoryScanner( final ConfigurationDirectory configurationDirectory,
                      final long pollInterval )
    {
        NullArgumentException.validateNotNull( configurationDirectory, "Directory" );
        PreConditionException.validateGreaterThan( pollInterval, 0, "Poll interval" );

        m_configurationDirectory = configurationDirectory;
        m_pollInterval = pollInterval;
        m_listeners = Collections.synchronizedList( new ArrayList<ChangeSetListener>() );
        m_configurationFiles = new HashMap<String, ConfigurationFile>();
    }

    /**
     * Starts directory scanning.
     */
    synchronized void start()
    {
        if( m_executor == null )
        {
            m_executor = Executors.newSingleThreadScheduledExecutor();
            m_executor.scheduleWithFixedDelay( new Scanner(), 0, m_pollInterval, TimeUnit.MILLISECONDS );
        }
    }

    /**
     * Stops directory scanning.
     */
    synchronized void stop()
    {
        if( m_executor != null )
        {
            m_executor.shutdown();
            m_executor = null;
        }
    }

    /**
     * @see org.ops4j.pax.cm.service.ChangeSetProducer#addListener(ChangeSetListener)
     */
    public void addListener( final ChangeSetListener listener )
    {
        m_listeners.add( listener );
    }

    /**
     * @see org.ops4j.pax.cm.service.ChangeSetProducer#removeListener(ChangeSetListener)
     */
    public void removeListener( final ChangeSetListener listener )
    {
        m_listeners.remove( listener );
    }

    /**
     * @see ConfigurationProducer#getConfiguration(String)
     */
    public ConfigurationSource getConfiguration( final String pid )
    {
        synchronized( m_configurationFiles )
        {
            return m_configurationFiles.get( pid );
        }
    }

    /**
     * @see ConfigurationProducer#getConfiguration(String)
     */
    public Collection<ConfigurationFile> getAllConfigurations()
    {
        synchronized( m_configurationFiles )
        {
            return m_configurationFiles.values();
        }
    }

    /**
     * Scanning thread.
     */
    private class Scanner
        implements Runnable
    {

        public void run()
        {
            try
            {
                synchronized( m_configurationFiles )
                {
                    scan();
                }
            }
            catch( Throwable ignore )
            {
                // catch everything as we should not die if something goes wrong during scanning
                LOG.error( "Exception while scanning " + m_configurationDirectory, ignore );
            }

        }

    }

    /**
     * Scans the configuration directory and notifies listeners on changes.
     */
    private void scan()
    {
        final Set<Dictionary> added = new HashSet<Dictionary>();
        final Set<Dictionary> updated = new HashSet<Dictionary>();
        final Set<String> deleted = new HashSet<String>();

        synchronized( m_configurationFiles )
        {
            final ConfigurationFileSetsDiff diff = new ConfigurationFileSetsDiff(
                m_configurationFiles.values(),
                m_configurationDirectory.scan()
            );
            // clear the current collection and fill it in with the new values
            m_configurationFiles.clear();

            // process added configuration files
            for( ConfigurationFile config : diff.getAdded() )
            {
                if( config.getProperties().get() != null )
                {
                    m_configurationFiles.put( config.getPid(), config );
                    //added.add( properties );
                }
            }

            // process updated configuration files
            for( ConfigurationFile config : diff.getUpdated() )
            {
                if( config.getProperties().get() != null )
                {
                    m_configurationFiles.put( config.getPid(), config );
                    //updated.add( properties );
                }
                else
                {
                    deleted.add( config.getPid() );
                }
            }

            // process deleted configuration files
            for( ConfigurationFile config : diff.getAdded() )
            {
                deleted.add( config.getPid() );
            }
        }
        // notify listners
        synchronized( m_listeners )
        {
            for( ChangeSetListener listener : m_listeners )
            {
                listener.notify(
                    new ChangeSetBean(
                        added.toArray( new Dictionary[added.size()] ),
                        updated.toArray( new Dictionary[updated.size()] ),
                        deleted.toArray( new String[deleted.size()] )
                    )
                );
            }
        }
    }

}