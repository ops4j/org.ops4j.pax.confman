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

import java.io.File;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;

/**
 * Managed Service Factory for this configuration provider.
 * When a configuration is created (or updated) it registers a new configuration provider. When a configuration is
 * deleted it configuration provider gets unregistered.
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 19, 2008
 */
class DirectoryScannerFactory
    implements ManagedServiceFactory
{

    /**
     * Persistent factroy identifier for this managed service factory.
     */
    static final String FACTORYPID = "org.ops4j.pax.cm.directory";
    /**
     * Name of the configuration property containing the scanned directory file name.
     */
    static final String PROPERTY_DIRECTORY = "directory";
    /**
     * Name of the configuration property containing the poll interval.
     */
    static final String PROPERTY_POLL_INTERVAL = "poll.interval";
    /**
     * Default poll interval.
     */
    private static long DEFAULT_POLL_INTERVAL = 2000L;

    /**
     * Bundle context used to register active scanners.
     */
    private final BundleContext m_bundleContext;
    /**
     * Mapping between pids and corresponding scanner.
     */
    private final Map<String, DirectoryScanner> m_pidsToScanners;
    /**
     * Mapping between pids and absolute directory name. Used to block multiple configurations for the same directory.
     */
    private final Map<String, String> m_pidsToDirectoryNames;
    /**
     * True if this factory ias already started.
     */
    private boolean m_started;

    /**
     * Constructor.
     *
     * @param bundleContext bundle context
     */
    DirectoryScannerFactory( final BundleContext bundleContext )
    {
        m_bundleContext = bundleContext;
        m_pidsToScanners = new HashMap<String, DirectoryScanner>();
        m_pidsToDirectoryNames = new HashMap<String, String>();
        m_started = false;
    }

    /**
     * Getter.
     *
     * @return name of the factory
     */
    public String getName()
    {
        return this.getClass().getName();
    }

    /**
     * Creates a configuration provider.
     *
     * @param pid        persistent identifier
     * @param properties configuration properties
     *
     * @throws ConfigurationException - If properties does not contain a property named "directory"
     *                                - If "directory" property is empty
     *                                - If "directory" property is not a String
     *                                - If "poll.interval" cannot be converted to a long
     */
    public void updated( final String pid, final Dictionary properties )
        throws ConfigurationException
    {
        final File directory = asDirectory( properties.get( PROPERTY_DIRECTORY ) );
        final long interval = asPollInterval( properties.get( PROPERTY_POLL_INTERVAL ) );
        synchronized( m_pidsToScanners )
        {
            if( m_pidsToDirectoryNames.containsValue( directory.getAbsolutePath() ) )
            {
                throw new ConfigurationException(
                    PROPERTY_DIRECTORY, directory.getAbsolutePath() + " already configured"
                );
            }
            final DirectoryScanner scanner = new DirectoryScannerService(
                m_bundleContext,
                new ConfigurationDirectory( directory ),
                interval
            );
            m_pidsToScanners.put( pid, scanner );
            m_pidsToDirectoryNames.put( pid, directory.getAbsolutePath() );
            if( m_started )
            {
                scanner.start();
            }
        }
    }

    /**
     * Unregisters the content provider for the pid.
     *
     * @param pid persistent identifier
     */
    public void deleted( final String pid )
    {
        synchronized( m_pidsToScanners )
        {
            final DirectoryScanner scanner = m_pidsToScanners.get( pid );
            if( scanner != null )
            {
                scanner.stop();
                m_pidsToDirectoryNames.remove( pid );
            }
            m_pidsToDirectoryNames.remove( pid );
        }
    }

    /**
     * Starts the configured configuration providers.
     */
    public void start()
    {
        synchronized( m_pidsToScanners )
        {
            if( !m_started )
            {
                for( DirectoryScanner scanner : m_pidsToScanners.values() )
                {
                    scanner.start();
                }
                m_started = true;
            }
        }
    }

    /**
     * Stops the configured configuration providers.
     */
    public void stop()
    {
        synchronized( m_pidsToScanners )
        {
            if( m_started )
            {
                for( DirectoryScanner scanner : m_pidsToScanners.values() )
                {
                    scanner.stop();
                }
                m_started = false;
            }
        }
    }

    /**
     * Validates a directory name and returns the file corresponding to directory name.
     *
     * @param directoryName directory name
     *
     * @return directory name as a File
     *
     * @throws ConfigurationException - If directory name is null
     *                                - If directory name is empty
     *                                - If directory name is not a String
     */
    private static File asDirectory( final Object directoryName )
        throws ConfigurationException
    {
        if( directoryName == null )
        {
            throw new ConfigurationException( PROPERTY_DIRECTORY, "Not set" );
        }
        if( !( directoryName instanceof String ) )
        {
            throw new ConfigurationException( PROPERTY_DIRECTORY, "Must be a String" );
        }
        if( ( (String) directoryName ).trim().length() == 0 )
        {
            throw new ConfigurationException( PROPERTY_DIRECTORY, "Cannot be empty" );
        }
        return new File( (String) directoryName );
    }

    /**
     * Validates a poll interval and returns the pool interval as long. If poll interval is null it returns the default
     * poll interval.
     *
     * @param pollInterval poll interval
     *
     * @return poll interval as a long
     *
     * @throws ConfigurationException - If poll interval cannot be converted to a long
     */
    private static long asPollInterval( final Object pollInterval )
        throws ConfigurationException
    {
        long convertedPollInterval = DEFAULT_POLL_INTERVAL;
        if( pollInterval != null && !( pollInterval instanceof Long ) )
        {
            try
            {
                convertedPollInterval = Long.parseLong( pollInterval.toString() );
            }
            catch( NumberFormatException e )
            {
                throw new ConfigurationException( PROPERTY_POLL_INTERVAL, "Must be a number (of milliseconds)" );
            }
        }
        return convertedPollInterval;
    }

}