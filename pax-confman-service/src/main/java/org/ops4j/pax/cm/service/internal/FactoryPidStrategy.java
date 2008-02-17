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
import org.ops4j.pax.cm.api.MetadataConstants;
import org.ops4j.pax.cm.common.internal.processor.Command;
import org.ops4j.pax.cm.domain.ConfigurationSource;
import org.ops4j.pax.cm.domain.ConfigurationTarget;
import org.ops4j.pax.cm.domain.Identity;

/**
 * Configuration strategy for a ManagedServiceFactory.
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 16, 2008
 */
public class FactoryPidStrategy
    implements ConfigurationStrategy
{

    /**
     * @see ConfigurationStrategy#prepareSource(ConfigurationSource)
     */
    public void prepareSource( final ConfigurationSource source )
    {
        final Dictionary metadata = source.getPropertiesSource().getMetadata();
        metadata.put( MetadataConstants.SERVICE_FACTORYPID, source.getIdentity().getFactoryPid() );
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
                return FactoryPidStrategy
                    .findConfiguration( configurationAdmin, m_target.getIdentity() );
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
                return FactoryPidStrategy
                    .findConfiguration( configurationAdmin, m_identity );
            }
        };
    }

    /**
     * Search for a configuration.
     *
     * @param configurationAdmin configuration admin service to be used
     * @param identity           configuration identity
     *
     * @return found configuration or null if not found
     *
     * @throws IOException - re-thrown from configuration admin
     */
    private static Configuration findConfiguration( final ConfigurationAdmin configurationAdmin,
                                                    final Identity identity )
        throws IOException
    {
        return null;
    }

}