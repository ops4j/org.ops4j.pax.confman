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

import org.ops4j.lang.NullArgumentException;

/**
 * Configuration source value object.
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 23, 2008
 */
public class ConfigurationSourceVO
    implements ConfigurationSource
{

    /**
     * Persistent identifier.
     */
    private final String m_pid;
    /**
     * Bundle location. Null if not yet bound.
     */
    private final String m_bundleLocation;
    /**
     * Configuration properties.
     */
    private final ConfigurationProperties m_properties;

    /**
     * Constructor.
     *
     * @param pid            persistent identifier
     * @param bundleLocation bundle location (null allowed for unbound configuration)
     * @param properties     configuration properties
     *
     * @throws NullArgumentException - If pid is null or empty
     *                               - If bundle location is empty
     *                               - If configuration properties is null
     */
    public ConfigurationSourceVO( final String pid,
                                  final String bundleLocation,
                                  final ConfigurationProperties properties )
    {
        NullArgumentException.validateNotEmpty( pid, true, "Persistent identifier" );
        if( bundleLocation != null )
        {
            NullArgumentException.validateNotEmpty( bundleLocation, true, "Bundle location" );
        }
        NullArgumentException.validateNotNull( properties, "Configuration properties" );

        m_pid = pid;
        m_bundleLocation = bundleLocation;
        m_properties = properties;
    }

    /**
     * Getter.
     *
     * @return persistent identifier; not null
     */
    public String getPid()
    {
        return m_pid;
    }

    /**
     * Getter.
     *
     * @return allways null
     */
    public String getFactoryPid()
    {
        return null;
    }

    /**
     * Getter.
     *
     * @return bundle location; null for an unbound configuration
     */
    public String getBundleLocation()
    {
        return m_bundleLocation;
    }

    /**
     * Getter.
     *
     * @return configuration properties; not null
     */
    public ConfigurationProperties getProperties()
    {
        return m_properties;
    }

}