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
import org.ops4j.pax.cm.api.ConfigurationManager;
import org.ops4j.pax.cm.commons.internal.processor.CommandProcessor;

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
    private CommandProcessor<ConfigurationAdmin> m_commandsprocessor;
    /**
     * Transformations processor.
     */
    private TransformationsProcessor m_transformationsProcessor;

    /**
     * @see BundleActivator#start(BundleContext)
     */
    public void start( final BundleContext bundleContext )
    {
        m_commandsprocessor = new CommandProcessor<ConfigurationAdmin>( "Pax ConfMan - Configuration Manager" );
        m_commandsprocessor.start();

        m_transformationsProcessor = new TransformationsProcessor( m_commandsprocessor );
        m_transformationsProcessor.start();

        final AdapterRepository adapterRepository = new AdapterRepositoryImpl();
        final ConfigurationManagerImpl configurer = new ConfigurationManagerImpl(
            adapterRepository,
            m_commandsprocessor,
            m_transformationsProcessor
        );

        m_configAdminTracker = createConfigAdminTracker( bundleContext, m_commandsprocessor );
        m_configAdminTracker.open();

        m_dictionaryAdaptorTracker = createAdaptorTracker(
            bundleContext, adapterRepository, m_transformationsProcessor
        );
        m_dictionaryAdaptorTracker.open();

        bundleContext.registerService( ConfigurationManager.class.getName(), configurer, null );
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
        if( m_transformationsProcessor != null )
        {
            m_transformationsProcessor.stop();
            m_transformationsProcessor = null;
        }
        if( m_commandsprocessor != null )
        {
            m_commandsprocessor.stop();
            m_commandsprocessor = null;
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

    private static ServiceTracker createAdaptorTracker( final BundleContext bundleContext,
                                                        final AdapterRepository repository,
                                                        final TransformationsProcessor transformationsProcessor )
    {
        return new ServiceTracker( bundleContext, Adapter.class.getName(), null )
        {
            @Override
            public Object addingService( ServiceReference serviceReference )
            {
                final Object service = super.addingService( serviceReference );
                repository.register( (Adapter) service );
                transformationsProcessor.scheduleFailedTransformations();
                return service;
            }

            @Override
            public void removedService( ServiceReference serviceReference, Object service )
            {
                super.removedService( serviceReference, service );
                repository.unregister( (Adapter) service );
                transformationsProcessor.scheduleFailedTransformations();
            }

        };
    }

}
