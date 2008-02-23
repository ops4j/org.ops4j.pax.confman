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

import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.cm.service.ConfigurationProducer;
import org.ops4j.pax.cm.service.ConfigurationProperties;
import org.ops4j.pax.cm.service.ConfigurationSource;
import org.ops4j.pax.cm.service.internal.ConfigurationAvailableEvent;
import org.ops4j.pax.cm.service.internal.event.Event;
import org.ops4j.pax.cm.service.internal.event.EventDispatcher;
import org.ops4j.pax.cm.service.internal.event.EventHandler;

/**
 * Handles registration of a managed service.
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 22, 2008
 */
public class ManagedServiceRegisteredEventHandler
    implements EventHandler
{

    /**
     * Configuration producer
     */
    private final ConfigurationProducer m_configurationProducer;
    /**
     * Event dispatcher.
     */
    private final EventDispatcher m_eventDispatcher;

    /**
     * Constructor
     *
     * @param configurationProducer configuration producer
     * @param eventDispatcher       event dispatcher
     *
     * @throws NullArgumentException - If configuration producer is null
     *                               - If event dispatcher is null
     */
    public ManagedServiceRegisteredEventHandler( final ConfigurationProducer configurationProducer,
                                                 final EventDispatcher eventDispatcher )
    {
        NullArgumentException.validateNotNull( configurationProducer, "Configuration producer" );
        NullArgumentException.validateNotNull( eventDispatcher, "Event dispatcher" );

        m_configurationProducer = configurationProducer;
        m_eventDispatcher = eventDispatcher;
    }

    /**
     * Handles events of type ManagedServiceRegisteredEvent.
     *
     * @see EventHandler#canHandle(Event)
     */
    public boolean canHandle( final Event event )
    {
        return event instanceof ManagedServiceRegisteredEvent;
    }

    /**
     * When a new ManagedService is registered and a configuration with valid properties is available will generate
     * another event that will eventually callback the ManagedService update.
     *
     * @param event ManagedServiceRegisteredEvent (expected)
     *
     * @see EventHandler#handle(Event)
     */
    public void handle( final Event event )
    {
        // should be safe to cast as we handle only this kind of events
        final ManagedServiceRegisteredEvent registeredEvent = (ManagedServiceRegisteredEvent) event;
        // try to find a configuration for the pid of added managed service
        final ConfigurationSource configuration = m_configurationProducer.getConfiguration( registeredEvent.getPid() );
        // if we have found one
        if( configuration != null )
        {
            // and it has properties
            final ConfigurationProperties properties = configuration.getProperties();
            if( properties != null )
            {
                // create an event that will update the managed service
                m_eventDispatcher.fireEvent(
                    new ConfigurationAvailableEvent(
                        configuration.getPid(),
                        configuration.getBundleLocation(),
                        properties
                    )
                );
            }
        }
    }

}