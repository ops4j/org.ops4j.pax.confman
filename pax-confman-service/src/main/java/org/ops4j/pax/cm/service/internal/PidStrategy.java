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

import java.io.IOException;
import java.util.Dictionary;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.ops4j.pax.cm.api.ServiceConstants;
import org.ops4j.pax.cm.commons.internal.processor.Command;
import org.ops4j.pax.cm.domain.ConfigurationSource;
import org.ops4j.pax.cm.domain.ConfigurationTarget;
import org.ops4j.pax.cm.domain.Identity;

/**
 * Configuration strategy when dealing with a pid (the pid can be pid of a managed service or pid of a managed service
 * factory).
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 15, 2008
 */
public class PidStrategy
    implements ConfigurationStrategy
{

    /**
     * Adds SERVICE_PID as property to metadata. Adaptors may use this properties into their specification.
     *
     * @see ConfigurationStrategy#prepareSource(ConfigurationSource)
     */
    @SuppressWarnings( "unchecked" )
    public void prepareSource( final ConfigurationSource source )
    {
        final Dictionary metadata = source.getPropertiesSource().getMetadata();
        metadata.put( ServiceConstants.SERVICE_PID, source.getIdentity().getPid() );
        // be defensive and remove possible unwanted metadata
        metadata.remove( ServiceConstants.SERVICE_FACTORYPID );
    }

    /**
     * Does nothing.
     *
     * @see ConfigurationStrategy#prepareTarget(ConfigurationTarget)
     */
    @SuppressWarnings( "unchecked" )
    public void prepareTarget( final ConfigurationTarget target )
    {
        // do nothing
    }

    /**
     * @see ConfigurationStrategy#createUpdateCommand(ConfigurationTarget)
     */
    public Command<ConfigurationAdmin> createUpdateCommand( final ConfigurationTarget configurationTarget )
    {
        return new UpdateCommand( configurationTarget )
        {
            /**
             * @see DeleteCommand#findConfiguration(ConfigurationAdmin)
             */
            @Override
            protected Configuration findConfiguration( final ConfigurationAdmin configurationAdmin )
                throws IOException
            {
                return PidStrategy.findConfiguration( configurationAdmin, m_target.getIdentity() );
            }
        };
    }

    /**
     * @see ConfigurationStrategy#createDeleteCommand(org.ops4j.pax.cm.domain.Identity)
     */
    public Command<ConfigurationAdmin> createDeleteCommand( final Identity identity )
    {
        return new DeleteCommand( identity )
        {
            /**
             * @see DeleteCommand#findConfiguration(ConfigurationAdmin)
             */
            @Override
            protected Configuration findConfiguration( final ConfigurationAdmin configurationAdmin )
                throws IOException
            {
                return PidStrategy.findConfiguration( configurationAdmin, m_identity );
            }
        };
    }

    /**
     * Search for a configuration by pid. If the configuration does not exist a new one is created.
     *
     * @param configurationAdmin configuration admin service to be used
     * @param identity           configuration identity
     *
     * @return found configuration or a new one if not found
     *
     * @throws IOException - re-thrown from configuration admin
     */
    private static Configuration findConfiguration( final ConfigurationAdmin configurationAdmin,
                                                    final Identity identity )
        throws IOException
    {
        return configurationAdmin.getConfiguration(
            identity.getPid(),
            identity.getLocation()
        );
    }

}