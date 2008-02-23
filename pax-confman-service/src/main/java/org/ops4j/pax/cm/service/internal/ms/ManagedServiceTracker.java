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
package org.ops4j.pax.cm.service.internal.ms;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ManagedService;
import org.osgi.util.tracker.ServiceTracker;
import org.ops4j.lang.NullArgumentException;

/**
 * Tracks ManagedServices in OSGi Service Registry.
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 22, 2008
 */
public class ManagedServiceTracker
{

    /**
     * Logger.
     */
    private static final Log LOG = LogFactory.getLog( ManagedServiceTracker.class );

    /**
     * Service tracker.
     */
    private final ServiceTracker m_serviceTracker;
    /**
     * Managed service repository.
     */
    private final ManagedServiceRepository m_repository;

    /**
     * Constructor.
     *
     * @param bundleContext bundle context
     * @param repository    managed service repository
     *
     * @throws NullArgumentException - If bundle context is null
     *                               - If repository is null
     */
    public ManagedServiceTracker( final BundleContext bundleContext,
                                  final ManagedServiceRepository repository )
    {
        NullArgumentException.validateNotNull( bundleContext, "Bundle context" );
        NullArgumentException.validateNotNull( repository, "Managed service repository" );

        m_repository = repository;
        m_serviceTracker = new ServiceTracker( bundleContext, ManagedService.class.getName(), null )
        {
            /**
             * Add managed service to repository if has a valid service pid.
             * @see ServiceTracker#addingService(ServiceReference)
             */
            @Override
            public Object addingService( final ServiceReference serviceReference )
            {
                final Object servicePid = serviceReference.getProperty( Constants.SERVICE_PID );
                if( servicePid == null )
                {
                    LOG.warn(
                        "Managed Service registred as " + serviceReference
                        + " must have a property " + Constants.SERVICE_PID
                    );
                    return null;
                }
                if( !( servicePid instanceof String ) )
                {
                    LOG.warn(
                        "Managed Service registred as " + serviceReference
                        + " has a property " + Constants.SERVICE_PID
                        + " that is not a String"
                    );
                    return null;
                }
                final ManagedService service = (ManagedService) super.addingService( serviceReference );
                m_repository.registerManagedService(
                    (String) servicePid,
                    service
                );
                return service;
            }

            /**
             * Remove and add managed service to repository if has a valid service pid.
             * @see ServiceTracker#modifiedService(ServiceReference, Object)
             */
            @Override
            public void modifiedService( final ServiceReference serviceReference,
                                         final Object service )
            {
                // TODO removed service from repository is service pid is changed
            }

            /**
             * Remove managed service from repository.
             * @see ServiceTracker#removedService(ServiceReference, Object)
             */
            @Override
            public void removedService( final ServiceReference serviceReference,
                                        final Object service )
            {
                // we do not need to verify anymore if the service pid is set
                super.removedService( serviceReference, service );
                m_repository.unregisterManagedService(
                    (String) serviceReference.getProperty( Constants.SERVICE_PID )
                );
            }
        };
    }

    /**
     * Starts tracking.
     */
    public synchronized void start()
    {
        LOG.trace( "Starting tracking ManagedService(s)" );
        m_serviceTracker.open();
    }

    /**
     * Stops tracking.
     */
    public synchronized void stop()
    {
        m_serviceTracker.close();
        LOG.trace( "Stopped tracking ManagedService(s)" );
    }

}