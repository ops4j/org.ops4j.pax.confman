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
package org.ops4j.pax.cm.scanner.directory;

/**
 * Constants related to directory scanner.
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 16, 2008
 */
public interface ServiceConstants
{

    /**
     * Service factory PID used for configuration.
     */
    static final String FACTORY_PID = "org.ops4j.pax.cm.scanner.directory";

    /**
     * Directories system property.
     */
    static final String SYSPROP_DIRECTORIES = FACTORY_PID + ".directories";
    /**
     * Interval system property.
     */
    static final String SYSPROP_INTERVAL = FACTORY_PID + ".interval";

    /**
     * Directory name configuration property.
     */
    static final String PROPERY_DIRECTORY = "directory";
    /**
     * Interval configuration property.
     */
    static final String PROPERY_INTERVAL = "interval";

}
