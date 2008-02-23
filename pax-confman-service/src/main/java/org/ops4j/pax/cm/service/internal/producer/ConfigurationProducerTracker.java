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
package org.ops4j.pax.cm.service.internal.producer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.cm.service.ConfigurationProducer;

/**
 * Tracks ConfigurationProducers in OSGi Service Registry.
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 22, 2008
 */
public class ConfigurationProducerTracker
{

    /**
     * Logger.
     */
    private static final Log LOG = LogFactory.getLog( ConfigurationProducerTracker.class );

    /**
     * Service tracker.
     */
    private final ServiceTracker m_serviceTracker;
    /**
     * Configuration producer repository.
     */
    private final ConfigurationProducerRepository m_repository;

    /**
     * Constructor.
     *
     * @param bundleContext bundle context
     * @param repository    managed service repository
     *
     * @throws NullArgumentException - If bundle context is null
     *                               - If repository is null
     */
    public ConfigurationProducerTracker( final BundleContext bundleContext,
                                         final ConfigurationProducerRepository repository )
    {
        NullArgumentException.validateNotNull( bundleContext, "Bundle context" );
        NullArgumentException.validateNotNull( repository, "Configuration producer repository" );

        m_repository = repository;
        m_serviceTracker = new ServiceTracker( bundleContext, ConfigurationProducer.class.getName(), null )
        {
            /**
             * Add configuration producer to repository.
             * @see org.osgi.util.tracker.ServiceTracker#addingService(org.osgi.framework.ServiceReference)
             */
            @Override
            public Object addingService( final ServiceReference serviceReference )
            {
                final ConfigurationProducer service = (ConfigurationProducer) super.addingService( serviceReference );
                m_repository.registerConfigurationProducer( service );
                return service;
            }

            /**
             * Remove configuration producer from repository.
             * @see org.osgi.util.tracker.ServiceTracker#removedService(org.osgi.framework.ServiceReference, Object)
             */
            @Override
            public void removedService( final ServiceReference serviceReference,
                                        final Object service )
            {
                // we do not need to verify anymore if the service pid is set
                super.removedService( serviceReference, service );
                m_repository.unregisterConfigurationProducer( (ConfigurationProducer) service );
            }
        };
    }

    /**
     * Starts tracking.
     */
    public synchronized void start()
    {
        LOG.trace( "Starting tracking ConfigurationProducer(s)" );
        m_serviceTracker.open();
    }

    /**
     * Stops tracking.
     */
    public synchronized void stop()
    {
        m_serviceTracker.close();
        LOG.trace( "Stopped tracking ConfigurationProducer(s)" );
    }

}