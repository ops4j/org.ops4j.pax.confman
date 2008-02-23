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
package org.ops4j.pax.cm.service;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Configuration properties value object.
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 23, 2008
 */
public class ConfigurationPropertiesVO
    implements ConfigurationProperties
{

    /**
     * Map of configuration properties.
     */
    private final Map<String, Object> m_properties;

    /**
     * Constructor.
     *
     * @param properties map of properties
     */
    public ConfigurationPropertiesVO( final Map<String, Object> properties )
    {
        m_properties = properties;
    }

    /**
     * Convenience constructor that can be called also with a Properties object.
     *
     * @param properties map of properties
     *
     * @throws IllegalArgumentException - If any of the keys is not a String.
     */
    public ConfigurationPropertiesVO( final Hashtable<Object, Object> properties )
    {
        if( properties != null )
        {
            m_properties = new HashMap<String, Object>();
            for( Map.Entry<Object, Object> entry : properties.entrySet() )
            {
                if( !( entry.getKey() instanceof String ) )
                {
                    throw new IllegalArgumentException(
                        "Every key must be an object. Found " + entry.getClass() + " for key " + entry.getKey()
                    );
                }
                m_properties.put( (String) entry.getKey(), entry.getValue() );
            }
        }
        else
        {
            m_properties = null;
        }
    }

    /**
     * @see ConfigurationProperties#get()
     */
    public Map<String, Object> get()
    {
        return m_properties;
    }

}