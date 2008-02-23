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

import org.apache.felix.cm.PersistenceManager;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.ops4j.lang.NullArgumentException;

/**
 * Registers / unregisters itself as a service in service registry
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 20, 2008
 */
class DirectoryScannerService
    extends DirectoryScanner
{

    /**
     * Bundle context.
     */
    private final BundleContext m_bundleContext;
    /**
     * Self service reference.
     */
    private ServiceRegistration m_registration;

    /**
     * Constructor.
     *
     * @param bundleContext          bundle context
     * @param configurationDirectory see DirectoryScanner
     * @param pollInterval           see DirectoryScanner
     */
    DirectoryScannerService( final BundleContext bundleContext,
                             final ConfigurationDirectory configurationDirectory,
                             final long pollInterval )
    {
        super( configurationDirectory, pollInterval );
        NullArgumentException.validateNotNull( bundleContext, "Bundle context" );
        m_bundleContext = bundleContext;
    }

    /**
     * Registers itself as a service and then starts directory scanner.
     */
    synchronized void start()
    {
        if( m_registration == null )
        {
            super.start();
            m_registration = m_bundleContext.registerService(
                PersistenceManager.class.getName(),
                this,
                null
            );
        }
    }

    /**
     * Stops directory scanning.
     */
    synchronized void stop()
    {
        if( m_registration != null )
        {
            m_registration.unregister();
            m_registration = null;
            super.stop();
        }
    }
}