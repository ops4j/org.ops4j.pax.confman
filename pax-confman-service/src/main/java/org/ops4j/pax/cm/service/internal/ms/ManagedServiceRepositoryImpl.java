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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.osgi.service.cm.ManagedService;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.cm.service.internal.event.ManagedServiceRemovedEvent;
import org.ops4j.pax.cm.service.internal.ms.ManagedServiceAddedEvent;
import org.ops4j.pax.cm.service.internal.event.EventDispatcher;

/**
 * TODO Add JavaDoc
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 22, 2008
 */
public class ManagedServiceRepositoryImpl
    implements ManagedServiceRepository
{

    private final EventDispatcher m_dispatcher;
    private final Map<String, ManagedService> m_services;

    public ManagedServiceRepositoryImpl( final EventDispatcher dispatcher )
    {
        NullArgumentException.validateNotNull( dispatcher, "Event dispatcher" );

        m_dispatcher = dispatcher;
        m_services = Collections.synchronizedMap( new HashMap<String, ManagedService>() );
    }

    public void addManagedService( final String pid,
                                   final ManagedService service )
    {
        NullArgumentException.validateNotEmpty( pid, true, "Managed service PID" );
        NullArgumentException.validateNotNull( service, "Managed service" );

        m_services.put( pid, service );
        m_dispatcher.fireEvent( new ManagedServiceAddedEvent( pid ) );
    }

    public void removeManagedService( final String pid )
    {
        NullArgumentException.validateNotEmpty( pid, true, "Managed service PID" );

        m_services.remove( pid );
    }
}