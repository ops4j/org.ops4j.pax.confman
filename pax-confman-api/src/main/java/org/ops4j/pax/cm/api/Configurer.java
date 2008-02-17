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
 * Easy update / delete of configurations (both managed service and managed service factory). The updates are performed
 * by first adapting the properties source object to an dictionary.
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 12, 2008
 */
public interface Configurer
{

    /**
     * Updates a configuration. If the configuration does not exist the configuration will be created.
     * The update is perfomed asyncronous as soon as the properties source object can be adapted and a configuration
     * admin service is available. The targeted configuration can be managed service copnfiguration or a managed service
     * factory configuration.
     *
     * @param pid              persistent identifier
     * @param location         bundle location. Null for an unbounded configuration.
     * @param propertiesSource configuration properties source object
     * @param metadata         metadata related to properties source object
     */
    void update( String pid,
                 String location,
                 Object propertiesSource,
                 Dictionary metadata
    );

    /**
     * Updates a managed service factory configuration. If the configuration does not exist the configuration will be
     * created. The update is perfomed asyncronous as soon as the properties source object can be adapted and a
     * configuration admin service is available.
     *
     * @param factoryPid       factory persistent identifier
     * @param factoryInstance  configuration instance identifier
     * @param location         bundle location. Null for an unbounded configuration.
     * @param propertiesSource configuration properties source object
     * @param metadata         metadata related to properties source object
     */
    void update( String factoryPid,
                 String factoryInstance,
                 String location,
                 Object propertiesSource,
                 Dictionary metadata
    );

    /**
     * Deletes a configuration if such a configuration exists. The update is perfomed asyncronous as soon as a
     * configuration admin service is available. The targeted configuration can be managed service copnfiguration or
     * a managed service factory configuration.
     *
     * @param pid persistent identifier.
     */
    void delete( String pid );

    /**
     * Deletes a factory configuration if such a configuration exists. The update is perfomed asyncronous as soon as a
     * configuration admin service is available.
     *
     * @param factoryPid      factory persistent identifier
     * @param factoryInstance configuration instance identifier
     */
    void delete( String factoryPid,
                 String factoryInstance );

}
