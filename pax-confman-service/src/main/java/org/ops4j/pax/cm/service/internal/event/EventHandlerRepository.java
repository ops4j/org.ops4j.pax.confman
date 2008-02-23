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

import java.util.Set;
import org.ops4j.pax.cm.service.internal.event.Event;

/**
 * Repository of event handlers.
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 22, 2008
 */
public interface EventHandlerRepository
{

    /**
     * Adds an event handler to repository.
     *
     * @param handler to be added
     */
    void addEventHandler( EventHandler handler );

    /**
     * Removes the specified event handler from repository.
     *
     * @param handler to be removed
     */
    void removeEventHandler( EventHandler handler );

    /**
     * Returns a set of event handlers for the specified event. If an event handler cannot be found it should throw an
     * exception, so it should never return null.
     *
     * @param event event
     *
     * @return matched event handlers
     */
    Set<EventHandler> getEventHandler( Event event );

}