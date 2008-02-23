package org.ops4j.pax.cm.service.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.ops4j.pax.cm.service.internal.event.EventDispatcherImpl;
import org.ops4j.pax.cm.service.internal.event.EventHandlerRepository;
import org.ops4j.pax.cm.service.internal.event.EventHandlerRepositoryImpl;
import org.ops4j.pax.cm.service.internal.ms.ManagedServiceRegisteredEventHandler;
import org.ops4j.pax.cm.service.internal.ms.ManagedServiceRepository;
import org.ops4j.pax.cm.service.internal.ms.ManagedServiceRepositoryImpl;
import org.ops4j.pax.cm.service.internal.ms.ManagedServiceTracker;
import org.ops4j.pax.cm.service.internal.producer.ConfigurationProducerRepositoryImpl;
import org.ops4j.pax.cm.service.internal.producer.ConfigurationProducerTracker;
import org.ops4j.pax.cm.service.internal.producer.ConfigurationProducerRegisteredEventHandler;

/**
 * Created by IntelliJ IDEA.
 * User: alindreghiciu
 * Date: Feb 22, 2008
 * Time: 5:44:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class Activator
    implements BundleActivator
{

    /**
     * Event dispatcher.
     */
    private EventDispatcherImpl m_eventDispatcher;
    /**
     * Managed Services tracker.
     */
    private ManagedServiceTracker m_managedServiceTracker;
    /**
     * Configuration producers tracker.
     */
    private ConfigurationProducerTracker m_configurationProducerTracker;

    /**
     * @see BundleActivator#start(BundleContext)
     */
    public void start( final BundleContext bundleContext )
    {
        final EventHandlerRepository eventHandlerRepository = new EventHandlerRepositoryImpl();
        m_eventDispatcher = new EventDispatcherImpl( eventHandlerRepository );

        final ConfigurationProducerRepositoryImpl producerRepository =
            new ConfigurationProducerRepositoryImpl( m_eventDispatcher );

        m_configurationProducerTracker = new ConfigurationProducerTracker( bundleContext, producerRepository );

        eventHandlerRepository.addEventHandler(
            new ManagedServiceRegisteredEventHandler(
                producerRepository,
                m_eventDispatcher
            )
        );
        eventHandlerRepository.addEventHandler(
            new ConfigurationProducerRegisteredEventHandler(
                m_eventDispatcher
            )
        );

        final ManagedServiceRepository managedServiceRepository = new ManagedServiceRepositoryImpl( m_eventDispatcher );
        m_managedServiceTracker = new ManagedServiceTracker( bundleContext, managedServiceRepository );

        m_eventDispatcher.start();
        m_configurationProducerTracker.start();
        m_managedServiceTracker.start();
    }

    /**
     * @see BundleActivator#stop(BundleContext)
     */
    public void stop( final BundleContext bundleContext )
    {
        if( m_managedServiceTracker != null )
        {
            m_managedServiceTracker.stop();
            m_managedServiceTracker = null;
        }
        if( m_configurationProducerTracker != null )
        {
            m_configurationProducerTracker.stop();
            m_configurationProducerTracker = null;
        }
        if( m_eventDispatcher != null )
        {
            m_eventDispatcher.stop();
            m_eventDispatcher = null;
        }
    }
}
