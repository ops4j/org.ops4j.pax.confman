/*
 * Copyright 2006 Edward Yakop.
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
package org.ops4j.pax.cm.agent.wicket.configuration.edit.properties;

import java.io.Serializable;

/**
 * @author Edward Yakop
 * @since 0.1.0
 */
final class ConfigurationItem
    implements Serializable
{
    public static final String PROPERTY_KEY = "key";

    private static final long serialVersionUID = 1L;

    private String m_key;

    private Object m_value;

    ConfigurationItem()
    {
        m_key = "";
        m_value = "";
    }

    ConfigurationItem( String key, Object value )
    {
        m_key = key;
        m_value = value;
    }

    public String getKey()
    {
        return m_key;
    }

    public void setKey( String key )
    {
        m_key = key;
    }

    public Object getValue()
    {
        return m_value;
    }

    public void setValue( Object value )
    {
        m_value = value;
    }

    public Object getPropertyValue( String property )
    {
        if( PROPERTY_KEY.equals( property ) )
        {
            return getKey();
        }
        return getValue();
    }
}
