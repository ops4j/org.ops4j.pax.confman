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

import org.ops4j.lang.NullArgumentException;

/**
 * Persistent identifier model.
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 14, 2008
 */
public class ServiceIdentity
{

    /**
     * Persistent identifier.
     */
    private final String m_pid;
    /**
     * Factory persistent identifier. Cannot be null.
     */
    private final String m_factoryPid;
    /**
     * Bundle location.
     */
    private final String m_location;

    /**
     * Create a new persistent identifier model for a managed service.
     *
     * @param pid      persistent identifier
     * @param location bundle location; optional
     *
     * @throws NullArgumentException - If pid is null or empty
     */
    public ServiceIdentity( final String pid, final String location )
    {
        NullArgumentException.validateNotEmpty( pid, true, "Persistent identifier" );

        m_pid = pid;
        m_factoryPid = null;
        m_location = location;
    }

    /**
     * Create a new persistent identifier model for a managed service factory.
     *
     * @param pid        persistent identifier
     * @param factoryPid factory persistent identifier
     * @param location   bundle location; optional
     *
     * @throws NullArgumentException - If pid is null or empty
     *                               - If factory pid is null or empty
     */
    public ServiceIdentity( final String pid, final String factoryPid, final String location )
    {
        NullArgumentException.validateNotEmpty( pid, true, "Persistent identifier" );
        NullArgumentException.validateNotEmpty( factoryPid, true, "Factory persistent identifier" );

        m_pid = pid;
        m_factoryPid = factoryPid;
        m_location = location;
    }

    /**
     * Getter.
     *
     * @return persistent identifier
     */
    public String getPid()
    {
        return m_pid;
    }

    /**
     * Getter.
     *
     * @return factory persistent pid
     */
    public String getFactoryPid()
    {
        return m_factoryPid;
    }

    /**
     * Getter.
     *
     * @return bundle location
     */
    public String getLocation()
    {
        return m_location;
    }

    @Override
    public String toString()
    {
        final StringBuilder builder = new StringBuilder()
            .append( this.getClass().getSimpleName() )
            .append( "{" )
            .append( "pid=" ).append( m_pid );

        if( m_factoryPid != null )
        {
            builder.append( ",factoryPid=" ).append( m_factoryPid );
        }

        builder
            .append( ",location=" ).append( m_location )
            .append( "}" );

        return builder.toString();
    }

}
