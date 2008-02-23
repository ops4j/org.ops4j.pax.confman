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
package org.ops4j.pax.cm.service.internal.event;

import java.util.HashSet;
import java.util.Set;
import org.ops4j.pax.cm.service.internal.event.Event;
import org.ops4j.pax.cm.service.internal.event.EventHandler;
import org.ops4j.pax.cm.service.internal.event.EventHandlerRepository;

/**
 * Implementation of EventHandlerRepository.
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 22, 2008
 */
public class EventHandlerRepositoryImpl
    implements EventHandlerRepository
{

    private final Set<EventHandler<? extends Event>> m_handlers;

    public EventHandlerRepositoryImpl()
    {
        m_handlers = new HashSet<EventHandler<? extends Event>>();
    }

    /**
     * @see EventHandlerRepository#addEventHandler(EventHandler)
     */
    public void addEventHandler( final EventHandler<? extends Event> handler )
    {
        m_handlers.add( handler );
    }

    /**
     * @see EventHandlerRepository#removeEventHandler(EventHandler)
     */
    public void removeEventHandler( final EventHandler<? extends Event> handler )
    {
        m_handlers.remove( handler );
    }

    /**
     * @throws IllegalStateException - If no handler is registered for the event
     * @see EventHandlerRepository#getEventHandlerForEvent(Event)
     */
    public EventHandler<? extends Event> getEventHandlerForEvent( final Event event )
    {
        throw new IllegalStateException( "No handler registered for handling event " + event );
    }
}