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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of EventHandlerRepository.
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 22, 2008
 */
public class EventHandlerRepositoryImpl
    implements EventHandlerRepository
{

    /**
     * Set of registered event handlers.
     */
    private final Set<EventHandler> m_handlers;

    public EventHandlerRepositoryImpl()
    {
        m_handlers = Collections.synchronizedSet( new HashSet<EventHandler>() );
    }

    /**
     * @see EventHandlerRepository#addEventHandler(EventHandler)
     */
    public void addEventHandler( final EventHandler handler )
    {
        m_handlers.add( handler );
    }

    /**
     * @see EventHandlerRepository#removeEventHandler(EventHandler)
     */
    public void removeEventHandler( final EventHandler handler )
    {
        m_handlers.remove( handler );
    }

    /**
     * @throws IllegalStateException - If no handler is registered for the event
     * @see EventHandlerRepository#getEventHandler(Event)
     */
    public Set<EventHandler> getEventHandler( final Event event )
    {
        final Set<EventHandler> handlers = new HashSet<EventHandler>();
        for( EventHandler handler : m_handlers )
        {
            if( handler.canHandle( event ) )
            {
                handlers.add( handler );
            }
        }
        if( handlers.size() == 0 )
        {
            throw new IllegalStateException( "No handler registered for handling event " + event );
        }
        return handlers;
    }
    
}