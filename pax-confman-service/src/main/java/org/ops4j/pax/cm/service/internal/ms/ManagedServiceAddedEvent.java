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
package org.ops4j.pax.cm.service.internal.ms;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.cm.service.internal.event.Event;

/**
 * Signals that a ManagedService has been added.
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 22, 2008
 */
public class ManagedServiceAddedEvent
    implements Event
{

    /**
     * Logger.
     */
    private static final Log LOG = LogFactory.getLog( ManagedServiceAddedEvent.class );

    /**
     * Service PID of the added ManagedService.
     */
    private final String m_pid;

    /**
     * Consructor.
     *
     * @param pid service PID of the added ManagedService.
     *
     * @throws NullArgumentException - If pid is null or empty
     */
    public ManagedServiceAddedEvent( final String pid )
    {
        NullArgumentException.validateNotNull( pid, "Persistent identifier" );

        m_pid = pid;
        LOG.trace( "Managed Service with service PID " + m_pid + " has been added" );
    }

    /**
     * Getter.
     *
     * @return service PID; cannot be null
     */
    public String getPid()
    {
        return m_pid;
    }

}