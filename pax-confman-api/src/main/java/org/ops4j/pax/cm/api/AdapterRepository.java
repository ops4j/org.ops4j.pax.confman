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
package org.ops4j.pax.cm.api;

import java.util.Dictionary;

/**
 * Adapters repository.
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 12, 2008
 */
public interface AdapterRepository
{

    /**
     * Adds an adapter to repository.
     *
     * @param adapter to add
     */
    void register( Adapter adapter );

    /**
     * Removes an adapter from repository.
     *
     * @param adapter to remove
     */
    void unregister( Adapter adapter );

    /**
     * Search for adapter that has a specification that match (based on metadata or object to be adapted.
     *
     * @param metadata         properties source object related metadata
     * @param propertiesSource properties source object
     *
     * @return matching adapter or null if no one matches
     */
    Adapter find( Dictionary metadata, Object propertiesSource );

}
