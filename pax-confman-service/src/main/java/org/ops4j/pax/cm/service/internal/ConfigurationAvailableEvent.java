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
package org.ops4j.pax.cm.service.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.cm.service.ConfigurationProperties;
import org.ops4j.pax.cm.service.internal.event.Event;

/**
 * TODO Add JavaDoc
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 22, 2008
 */
public class ConfigurationAvailableEvent
    implements Event
{

    /**
     * Logger.
     */
    private static final Log LOG = LogFactory.getLog( ConfigurationAvailableEvent.class );

    /**
     * Configuration persistent identifier.
     */
    private final String m_pid;
    /**
     * Bundle location. Null if the configration is not yet bounded.
     */
    private final String m_location;
    /**
     * Configuration properties.
     */
    private final ConfigurationProperties m_properties;

    /**
     * Constrctor.
     *
     * @param pid        configuration persistent identifier
     * @param location   bundle location. Can be null for a not yet bounded configuration.
     * @param properties configuration properties
     *
     * @throws NullArgumentException - If pid is null or empty
     *                               - If properties are null
     */
    public ConfigurationAvailableEvent( final String pid,
                                        final String location,
                                        final ConfigurationProperties properties )
    {
        NullArgumentException.validateNotEmpty( pid, true, "Configuration persistent dentifier" );
        NullArgumentException.validateNotNull( properties, "Configuration properties" );

        m_pid = pid;
        m_location = location;
        m_properties = properties;

        LOG.trace( "Configuration with pid " + m_pid + " is available" );
    }

    /**
     * Getter.
     *
     * @return bundle location; null for a not yet bounded configuration.
     */
    public String getLocation()
    {
        return m_location;
    }

    /**
     * Getter.
     *
     * @return configuration persistent identifier; cannot be null
     */
    public String getPid()
    {
        return m_pid;
    }

    /**
     * Getter.
     *
     * @return configuration properties; cannot be null
     */
    public ConfigurationProperties getProperties()
    {
        return m_properties;
    }
}