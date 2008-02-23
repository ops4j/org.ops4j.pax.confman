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

import org.ops4j.pax.cm.service.ConfigurationProducer;
import org.ops4j.pax.cm.service.ConfigurationProperties;
import org.ops4j.pax.cm.service.ConfigurationSource;
import org.ops4j.pax.cm.service.internal.event.EventDispatcher;
import org.ops4j.pax.cm.service.internal.event.EventHandler;
import org.ops4j.pax.cm.service.internal.di.RequiresConfigurationProducer;
import org.ops4j.pax.cm.service.internal.di.RequiresEventDispatcher;
import org.ops4j.pax.cm.service.internal.ms.ManagedServiceAddedEvent;
import org.ops4j.pax.cm.service.internal.ConfigurationAvailableEvent;

/**
 * Handles registration of a new registered ManagedService.
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 22, 2008
 */
public class ManagedServiceAddedEventHandler
    implements EventHandler<ManagedServiceAddedEvent>, RequiresConfigurationProducer, RequiresEventDispatcher
{

    /**
     * ConfigurationProducer. Guaranteed to be set by EventDispatcher before handling the evnt.
     */
    private ConfigurationProducer m_configurationProducer;
    /**
     * EventDispatcher. Guaranteed to be set by EventDispatcher before handling the evnt.
     */
    private EventDispatcher m_eventDispatcher;

    /**
     * Handles events related to adding a managed service.
     *
     * @return ManagedServiceAddedEvent class
     */
    public Class<ManagedServiceAddedEvent> getHandledEventType()
    {
        return ManagedServiceAddedEvent.class;
    }

    /**
     * When a new ManagedService is registered  f a configuration with valid properties is available will generate
     * another event that will eventually callback the ManagedService update.
     *
     * @param event ManagedServiceAddedEvent (expected)
     */
    public void handle( final ManagedServiceAddedEvent event )
    {
        // try to find a configuration for the pid of added managed service
        final ConfigurationSource configuration = m_configurationProducer.getConfiguration( event.getPid() );
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

    /**
     * @see RequiresConfigurationProducer#setConfigurationProducer(ConfigurationProducer)
     */
    public void setConfigurationProducer( final ConfigurationProducer configurationProducer )
    {
        m_configurationProducer = configurationProducer;
    }

    /**
     * @see RequiresEventDispatcher#setEventDispatcher(org.ops4j.pax.cm.service.internal.event.EventDispatcher)
     */
    public void setEventDispatcher( final EventDispatcher eventDispatcher )
    {
        m_eventDispatcher = eventDispatcher;
    }
}