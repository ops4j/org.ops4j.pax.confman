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

import java.util.Collection;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.cm.service.ConfigurationSource;
import org.ops4j.pax.cm.service.internal.event.Event;
import org.ops4j.pax.cm.service.internal.event.EventDispatcher;
import org.ops4j.pax.cm.service.internal.event.EventHandler;
import org.ops4j.pax.cm.service.internal.ConfigurationAvailableEvent;

/**
 * Handles registration of a configuration producer.
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 22, 2008
 */
public class ConfigurationProducerRegisteredEventHandler
    implements EventHandler
{

    /**
     * Event dispatcher.
     */
    private final EventDispatcher m_eventDispatcher;

    /**
     * Constructor
     *
     * @param eventDispatcher event dispatcher
     *
     * @throws org.ops4j.lang.NullArgumentException
     *          - If configuration producer is null
     *          - If event dispatcher is null
     */
    public ConfigurationProducerRegisteredEventHandler( final EventDispatcher eventDispatcher )
    {
        NullArgumentException.validateNotNull( eventDispatcher, "Event dispatcher" );

        m_eventDispatcher = eventDispatcher;
    }

    /**
     * Handles events of type ConfigurationProducerRegisteredEvent.
     *
     * @see EventHandler#canHandle(Event)
     */
    public boolean canHandle( final Event event )
    {
        return event instanceof ConfigurationProducerRegisteredEvent;
    }

    /**
     * When a configuration producer is registered:
     * * gets all configurations from the producer and fires an configuration availabel event for each
     *
     * @param event ConfigurationProducerRegisteredEvent (expected)
     *
     * @see EventHandler#handle(Event)
     */
    public void handle( final Event event )
    {
        // should be safe to cast as we handle only this kind of events
        final ConfigurationProducerRegisteredEvent registeredEvent = (ConfigurationProducerRegisteredEvent) event;
        final Collection<? extends ConfigurationSource> configs = registeredEvent.getProducer().getAllConfigurations();
        if( configs != null )
        {
            for( ConfigurationSource config : configs )
            {
                m_eventDispatcher.fireEvent(
                    new ConfigurationAvailableEvent(
                        config.getPid(),
                        config.getBundleLocation(),
                        config.getProperties()
                    )
                );
            }
        }
    }

}