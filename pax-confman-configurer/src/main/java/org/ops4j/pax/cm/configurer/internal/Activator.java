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
package org.ops4j.pax.cm.configurer.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.util.tracker.ServiceTracker;
import org.ops4j.pax.cm.configurer.Configurer;
import org.ops4j.pax.cm.configurer.DictionaryAdapter;
import org.ops4j.pax.cm.configurer.DictionaryAdapterRepository;

/**
 * Bundle Activator.
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 10, 2008
 */
public class Activator
    implements BundleActivator
{

    /**
     * Logger.
     */
    private static final Log LOG = LogFactory.getLog( Activator.class );

    /**
     * Configuration Admin Service Tracker.
     */
    private ServiceTracker m_configAdminTracker;
    /**
     * Dictionary Admin Service Tracker.
     */
    private ServiceTracker m_dictionaryAdaptorTracker;

    /**
     * @see BundleActivator#start(BundleContext)
     */
    public void start( final BundleContext bundleContext )
    {
        final DictionaryAdapterRepository dictionaryAdapterRepository = new DictionaryAdapterRepositoryImpl();
        final ProcessingQueueImpl processingQueue = new ProcessingQueueImpl();
        final ConfigurerImpl configurer = new ConfigurerImpl( dictionaryAdapterRepository, processingQueue );

        m_configAdminTracker = createConfigAdminTracker( bundleContext, processingQueue );
        m_configAdminTracker.open();

        m_dictionaryAdaptorTracker = createDictionaryAdaptorTracker( bundleContext, dictionaryAdapterRepository );
        m_dictionaryAdaptorTracker.open();

        bundleContext.registerService( Configurer.class.getName(), configurer, null );
        bundleContext.registerService( DictionaryAdapterRepository.class.getName(), dictionaryAdapterRepository, null );
    }

    /**
     * @see BundleActivator#stop(BundleContext)
     */
    public void stop( final BundleContext bundleContext )
    {
        if( m_configAdminTracker != null )
        {
            m_configAdminTracker.close();
            m_configAdminTracker = null;
        }
        if( m_dictionaryAdaptorTracker != null )
        {
            m_dictionaryAdaptorTracker.close();
            m_dictionaryAdaptorTracker = null;
        }
    }

    private static ServiceTracker createConfigAdminTracker( final BundleContext bundleContext,
                                                            final ProcessingQueueImpl processingQueue )
    {
        return new ServiceTracker( bundleContext, ConfigurationAdmin.class.getName(), null )
        {
            @Override
            public Object addingService( ServiceReference serviceReference )
            {
                final Object service = super.addingService( serviceReference );
                processingQueue.setConfigurationAdmin( (ConfigurationAdmin) service );
                return service;
            }

            @Override
            public void removedService( ServiceReference serviceReference, Object service )
            {
                super.removedService( serviceReference, service );
                processingQueue.setConfigurationAdmin( null );
            }

        };
    }

    private static ServiceTracker createDictionaryAdaptorTracker( final BundleContext bundleContext,
                                                                  final DictionaryAdapterRepository repository )
    {
        return new ServiceTracker( bundleContext, DictionaryAdapter.class.getName(), null )
        {
            @Override
            public Object addingService( ServiceReference serviceReference )
            {
                final Object service = super.addingService( serviceReference );
                repository.register( (DictionaryAdapter) service );
                return service;
            }

            @Override
            public void removedService( ServiceReference serviceReference, Object service )
            {
                super.removedService( serviceReference, service );
                repository.unregister( (DictionaryAdapter) service );
            }

        };
    }

}
