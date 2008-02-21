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

import java.util.Dictionary;
import java.util.Hashtable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;

/**
 * Bundle Activator.
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 19, 2008
 */
public class Activator
    implements BundleActivator
{

    /**
     * Logger.
     */
    private static final Log LOG = LogFactory.getLog( DirectoryScanner.class );

    /**
     * Configuration provider factory.
     */
    private DirectoryScannerFactory m_directoryScannerFactory;

    /**
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public synchronized void start( final BundleContext bundleContext )
    {
        if( m_directoryScannerFactory == null )
        {
            m_directoryScannerFactory = new DirectoryScannerFactory( bundleContext );
            m_directoryScannerFactory.start();
            // check if we have configuration via system properties
            final Dictionary<String, String> sysProps = getConfigFromSystemProperties( bundleContext );
            if( sysProps != null )
            {
                try
                {
                    m_directoryScannerFactory.updated( "systemProperties", sysProps );
                }
                catch( ConfigurationException ignore )
                {
                    LOG.error( "Cannot configure from system properties: " + ignore.getMessage() );
                }
            }
            Dictionary<String, String> props = new Hashtable<String, String>();
            props.put( ConfigurationAdmin.SERVICE_FACTORYPID, DirectoryScannerFactory.FACTORYPID );
            bundleContext.registerService(
                ManagedServiceFactory.class.getName(),
                m_directoryScannerFactory,
                props
            );
        }
    }

    /**
     * Creates a configuration dictionary out of system properties.
     *
     * @param bundleContext bundle context
     *
     * @return configuration dictionary if at least the directory property is set, otherwise null
     */
    private Dictionary<String, String> getConfigFromSystemProperties( final BundleContext bundleContext )
    {
        final String directory = bundleContext.getProperty( "org.ops4j.pax.cm.directory" );
        if( directory != null )
        {
            final Dictionary<String, String> props = new Hashtable<String, String>();
            props.put( DirectoryScannerFactory.PROPERTY_DIRECTORY, directory );
            final String interval = bundleContext.getProperty( "org.ops4j.pax.cm.directory.poll.interval" );
            if( interval != null )
            {
                props.put( DirectoryScannerFactory.PROPERTY_POLL_INTERVAL, interval );
            }
            return props;
        }
        return null;
    }

    /**
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public synchronized void stop( final BundleContext bundleContext )
    {
        if( m_directoryScannerFactory != null )
        {
            m_directoryScannerFactory.stop();
            m_directoryScannerFactory = null;
        }
    }

}