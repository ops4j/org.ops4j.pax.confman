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
package org.ops4j.pax.cm.domain;

import java.util.Dictionary;

/**
 * Configuration properties target model.
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 14, 2008
 */
public class PropertiesTarget
{

    /**
     * Properties source related metadata.
     */
    private final Dictionary m_properties;

    /**
     * Create a new configuration properties target model.
     *
     * @param properties configuration properties
     */
    public PropertiesTarget( final Dictionary properties )
    {
        m_properties = properties;
    }

    /**
     * Getter.
     *
     * @return configuration properties
     */
    public Dictionary getProperties()
    {
        return m_properties;
    }

}