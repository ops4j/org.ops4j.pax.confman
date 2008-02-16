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
package org.ops4j.pax.cm.scanner.directory.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;
import org.ops4j.pax.cm.api.Configurer;
import org.ops4j.pax.cm.common.internal.processor.CommandProcessor;
import org.ops4j.pax.cm.scanner.directory.ServiceConstants;
import org.ops4j.pax.swissbox.lifecycle.AbstractLifecycle;

/**
 * Managed Service Factory for DirectoryScanner. Each configuration will have it's own directory scanner.
 * If a configuration is updated if a correspond scanner for the pid exists then it will be stopped. Then a new scanner
 * is created for the configured values (see bellow) and is started (if this managed service is active).
 * <br/>
 * Expected properties in each configuration:<br/>
 * - directory - file path to the targeted directory. Value must be a String and cannot be null.<br/>
 * - interval -  number of milliseconds betwen pooling of the directory. Value can be anything that can be converted to
 * a long value and is optional. If not set default scaner value will be used.
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 16, 2008
 */
class DirectoryScannerManagedServiceFactory
    extends AbstractLifecycle
    implements ManagedServiceFactory
{

    /**
     * Logger.
     */
    private static final Log LOG = LogFactory.getLog( DirectoryScannerManagedServiceFactory.class );

    /**
     * Commands processor.
     */
    private final CommandProcessor<Configurer> m_processor;
    /**
     * Mapping between pids and active directory scaners.
     */
    private final Map<String, DirectoryScanner> m_pidsToScanners;
    /**
     * Mapping between absolet firectory path and directory scaners.
     */
    private final Map<String, DirectoryScanner> m_pathsToScanners;
    /**
     * True if default scanners are started.
     */
    private boolean m_defaultScannersStarted;
    /**
     * Default list of scanners to be used when no configuration is available.
     */
    private final List<DirectoryScanner> m_defaultScanners;
    /**
     * Default scanning interval.
     */
    private Long m_defaultInterval;

    /**
     * Constructor.
     *
     * @param processor commands processor (used to create scaners)
     */
    DirectoryScannerManagedServiceFactory( final CommandProcessor<Configurer> processor )
    {
        m_processor = processor;
        m_pidsToScanners = Collections.synchronizedMap( new HashMap<String, DirectoryScanner>() );
        m_pathsToScanners = Collections.synchronizedMap( new HashMap<String, DirectoryScanner>() );
        m_defaultScanners = Collections.synchronizedList( new ArrayList<DirectoryScanner>() );
        m_defaultScannersStarted = false;
    }

    /**
     * Start all scanners.
     */
    protected void onStart()
    {
        synchronized( m_pidsToScanners )
        {
            for( DirectoryScanner scanner : m_pidsToScanners.values() )
            {
                try
                {
                    scanner.start();
                }
                catch( RuntimeException ignore )
                {
                    // maybe we still can start the other scanners
                    LOG.error( "Exception while starting " + scanner, ignore );
                }
            }
        }
        startDefaultScanners();
    }

    /**
     * Stop all scanners.
     */
    protected void onStop()
    {
        synchronized( m_pidsToScanners )
        {
            for( DirectoryScanner scanner : m_pidsToScanners.values() )
            {
                try
                {
                    scanner.stop();
                }
                catch( RuntimeException ignore )
                {
                    // maybe we still can stop the other scanners
                    LOG.error( "Exception while stopping " + scanner, ignore );
                }
            }
        }
        stopDefaultScanners();
    }

    /**
     * Getter.
     *
     * @return directory scaner name
     */
    public String getName()
    {
        return "org.ops4j.pax.cm.scanner.directory";
    }

    /**
     * When configuration gets updated a new directory scanner will be started (if already existed then it
     * will be stopped first).
     * Expected properties are:
     * - directory
     * - interval
     *
     * @param pid        persistent identifier
     * @param properties configuration properties
     *
     * @throws org.osgi.service.cm.ConfigurationException
     *          if directory property is not set
     */
    public void updated( final String pid, final Dictionary properties )
        throws ConfigurationException
    {
        deleted( pid, false );
        final Object directoryName = properties.get( ServiceConstants.PROPERY_DIRECTORY );
        if( directoryName == null )
        {
            throw new ConfigurationException( ServiceConstants.PROPERY_DIRECTORY, "Not set" );
        }
        if( !( directoryName instanceof String ) )
        {
            throw new ConfigurationException( ServiceConstants.PROPERY_DIRECTORY, "Must be a String" );
        }
        Object interval = properties.get( ServiceConstants.PROPERY_INTERVAL );
        if( interval != null && !( interval instanceof Long ) )
        {
            try
            {
                interval = Long.parseLong( interval.toString() );
            }
            catch( NumberFormatException e )
            {
                throw new ConfigurationException(
                    ServiceConstants.PROPERY_INTERVAL, "Must be a number (of milliseconds)"
                );
            }
        }
        if( interval == null )
        {
            interval = m_defaultInterval;
        }
        final File directory = new File( (String) directoryName );
        DirectoryScanner scanner;
        synchronized( m_pidsToScanners )
        {
            scanner = m_pathsToScanners.get( directory.getAbsolutePath() );
            if( scanner == null )
            {
                scanner = new DirectoryScanner(
                    m_processor,
                    directory,
                    (Long) interval
                );
            }
            // do delete again as in the mean time a lot can happen
            deleted( pid, false );
            m_pidsToScanners.put( pid, scanner );
            m_pathsToScanners.put( directory.getAbsolutePath(), scanner );
            if( isStarted() )
            {
                scanner.start();
            }
        }
        stopDefaultScanners();
    }

    /**
     * When a configuration gets deleted the coresponding directory scanner will be stopped.
     *
     * @param pid persistent identifier
     */
    public void deleted( final String pid )
    {
        deleted( pid, true );
    }

    /**
     * When a configuration gets deleted the coresponding directory scanner will be stopped.
     *
     * @param pid                  persistent identifier
     * @param startDefaultScanners if default scanner should be started on empty list of configured scanners
     */
    public void deleted( final String pid,
                         final boolean startDefaultScanners )
    {
        synchronized( m_pidsToScanners )
        {
            final DirectoryScanner scanner = m_pidsToScanners.get( pid );
            if( scanner != null )
            {
                scanner.stop();
                m_pidsToScanners.remove( pid );
                m_pathsToScanners.remove( scanner.getDirectory().getAbsolutePath() );
            }
            if( startDefaultScanners && m_pidsToScanners.size() == 0 )
            {
                startDefaultScanners();
            }
        }
    }

    /**
     * Set defaults.
     *
     * @param directories array of directories
     * @param interval    default scan interval. cann be null.
     */
    public void setDefaults( final File[] directories,
                             final Long interval )
    {
        m_defaultInterval = interval;
        stopDefaultScanners();
        synchronized( m_defaultScanners )
        {
            m_defaultScanners.clear();
            if( directories != null )
            {
                for( File directory : directories )
                {
                    m_defaultScanners.add( new DirectoryScanner( m_processor, directory, m_defaultInterval ) );
                }
            }
        }
        if( isStarted() )
        {
            startDefaultScanners();
        }
    }

    /**
     * Starts default scanners.
     */
    private void startDefaultScanners()
    {
        synchronized( m_defaultScanners )
        {
            if( !m_defaultScannersStarted )
            {
                for( DirectoryScanner scanner : m_defaultScanners )
                {
                    try
                    {
                        scanner.start();
                    }
                    catch( RuntimeException ignore )
                    {
                        // maybe we still can stop the other scanners
                        LOG.error( "Exception while starting " + scanner, ignore );
                    }
                }
                m_defaultScannersStarted = true;
            }
        }
    }

    /**
     * Stops default scanners.
     */
    private void stopDefaultScanners()
    {
        synchronized( m_defaultScanners )
        {
            if( m_defaultScannersStarted )
            {
                for( DirectoryScanner scanner : m_defaultScanners )
                {
                    try
                    {
                        scanner.stop();
                    }
                    catch( RuntimeException ignore )
                    {
                        // maybe we still can stop the other scanners
                        LOG.error( "Exception while stopping " + scanner, ignore );
                    }
                }
                m_defaultScannersStarted = false;
            }
        }
    }

}