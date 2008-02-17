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
package org.ops4j.pax.cm.scanner.core.internal;

import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.cm.api.Configurer;
import org.ops4j.pax.cm.common.internal.processor.Command;
import org.ops4j.pax.cm.domain.Identity;

/**
 * Delete command to be executed against a configurer.
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, January 12, 2008
 */
public class DeleteCommand
    implements Command<Configurer>
{

    /**
     * configuration identity.
     */
    private final Identity m_identity;

    /**
     * Create a new delete command.
     *
     * @param identity configuration identity of the configuration to be removed
     */
    public DeleteCommand( final Identity identity )
    {
        NullArgumentException.validateNotNull( identity, "configuration identity" );

        m_identity = identity;
    }

    /**
     * Execute remove against available configurer.
     */
    public void execute( final Configurer configurer )
    {
        if( m_identity.getFactoryPid() == null )
        {
            configurer.delete( m_identity.getPid() );
        }
        else
        {
            configurer.delete( m_identity.getFactoryPid(), m_identity.getFactoryInstance() );
        }
    }

    @Override
    public String toString()
    {
        return new StringBuilder()
            .append( this.getClass().getSimpleName() )
            .append( "{" )
            .append( m_identity )
            .append( "}" )
            .toString();
    }

}