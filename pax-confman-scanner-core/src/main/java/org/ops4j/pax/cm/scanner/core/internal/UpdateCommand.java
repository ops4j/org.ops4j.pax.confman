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
import org.ops4j.pax.cm.api.ConfigurationManager;
import org.ops4j.pax.cm.common.internal.processor.Command;
import org.ops4j.pax.cm.domain.ConfigurationSource;

/**
 * Update command to be executed against a configurer.
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, January 12, 2008
 */
public class UpdateCommand
    implements Command<ConfigurationManager>
{

    /**
     * Configuration source.
     */
    private final ConfigurationSource m_configurationSource;

    /**
     * Create a new update command.
     *
     * @param configurationSource configuration source
     */
    public UpdateCommand( final ConfigurationSource configurationSource )
    {
        NullArgumentException.validateNotNull( configurationSource, "Configuration source" );

        m_configurationSource = configurationSource;
    }

    /**
     * Execute update against available configurationManager.
     */
    public void execute( final ConfigurationManager configurationManager )
    {
        if( m_configurationSource.getIdentity().getFactoryPid() == null )
        {
            configurationManager.update(
                m_configurationSource.getIdentity().getPid(),
                m_configurationSource.getIdentity().getLocation(),
                m_configurationSource.getPropertiesSource().getSourceObject(),
                m_configurationSource.getPropertiesSource().getMetadata()
            );
        }
        else
        {
            configurationManager.update(
                m_configurationSource.getIdentity().getFactoryPid(),
                m_configurationSource.getIdentity().getFactoryInstance(),
                m_configurationSource.getIdentity().getLocation(),
                m_configurationSource.getPropertiesSource().getSourceObject(),
                m_configurationSource.getPropertiesSource().getMetadata()
            );
        }
    }

    @Override
    public String toString()
    {
        return new StringBuilder()
            .append( this.getClass().getSimpleName() )
            .append( "{" )
            .append( m_configurationSource.getIdentity() )
            .append( "}" )
            .toString();
    }

}