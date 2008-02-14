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
public class Pid
{

    /**
     * Persistent identifier.
     */
    private final String m_pid;
    /**
     * Bundle location.
     */
    private final String m_location;

    /**
     * Create a new persistent identifier model.
     *
     * @param pid      persistent identifier
     * @param location bundle location; optional
     *
     * @throws NullArgumentException - If pid is null or empty
     */
    public Pid( final String pid, final String location )
    {
        NullArgumentException.validateNotEmpty( pid, true, "Persistent identifier" );

        this.m_pid = pid;
        this.m_location = location;
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
     * @return bundle location
     */
    public String getLocation()
    {
        return m_location;
    }

}
