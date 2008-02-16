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

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.util.tracker.ServiceTracker;
import org.ops4j.pax.cm.api.Adapter;
import org.ops4j.pax.cm.api.AdapterRepository;
import org.ops4j.pax.cm.api.Configurer;
import org.ops4j.pax.cm.common.internal.processor.CommandProcessor;

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
     * Configuration Admin Service Tracker.
     */
    private ServiceTracker m_configAdminTracker;
    /**
     * Dictionary Admin Service Tracker.
     */
    private ServiceTracker m_dictionaryAdaptorTracker;
    /**
     * Configuration Admin commands processor.
     */
    private CommandProcessor<ConfigurationAdmin> m_processor;

    /**
     * @see BundleActivator#start(BundleContext)
     */
    public void start( final BundleContext bundleContext )
    {
        m_processor = new CommandProcessor<ConfigurationAdmin>( "Pax ConfMan - Configurer - Commands Processor" );
        m_processor.start();

        final AdapterRepository adapterRepository = new AdapterRepositoryImpl();
        final ConfigurerImpl configurer = new ConfigurerImpl( adapterRepository, m_processor );

        m_configAdminTracker = createConfigAdminTracker( bundleContext, m_processor );
        m_configAdminTracker.open();

        m_dictionaryAdaptorTracker = createDictionaryAdaptorTracker( bundleContext, adapterRepository );
        m_dictionaryAdaptorTracker.open();

        bundleContext.registerService( Configurer.class.getName(), configurer, null );
        bundleContext.registerService( AdapterRepository.class.getName(), adapterRepository, null );
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
        if( m_processor != null )
        {
            m_processor.stop();
            m_processor = null;
        }
    }

    private static ServiceTracker createConfigAdminTracker( final BundleContext bundleContext,
                                                            final CommandProcessor<ConfigurationAdmin> processor )
    {
        return new ServiceTracker( bundleContext, ConfigurationAdmin.class.getName(), null )
        {
            @Override
            public Object addingService( ServiceReference serviceReference )
            {
                final Object service = super.addingService( serviceReference );
                processor.setTargetService( (ConfigurationAdmin) service );
                return service;
            }

            @Override
            public void removedService( ServiceReference serviceReference, Object service )
            {
                super.removedService( serviceReference, service );
                processor.setTargetService( null );
            }

        };
    }

    private static ServiceTracker createDictionaryAdaptorTracker( final BundleContext bundleContext,
                                                                  final AdapterRepository repository )
    {
        return new ServiceTracker( bundleContext, Adapter.class.getName(), null )
        {
            @Override
            public Object addingService( ServiceReference serviceReference )
            {
                final Object service = super.addingService( serviceReference );
                repository.register( (Adapter) service );
                return service;
            }

            @Override
            public void removedService( ServiceReference serviceReference, Object service )
            {
                super.removedService( serviceReference, service );
                repository.unregister( (Adapter) service );
            }

        };
    }

}
