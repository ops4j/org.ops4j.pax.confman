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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.ops4j.pax.cm.api.ServiceConstants;
import org.ops4j.pax.cm.domain.ConfigurationSource;
import org.ops4j.pax.cm.domain.ConfigurationTarget;
import org.ops4j.pax.cm.domain.Identity;

/**
 * Configuration strategy when dealing with pactory pid/instance.
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 16, 2008
 */
public class FactoryPidStrategy
    implements ConfigurationStrategy
{

    /**
     * Logger.
     */
    private static final Log LOG = LogFactory.getLog( FactoryPidStrategy.class );

    /**
     * Adds SERVICE_FACTORYPID & SERVICE_FACTORYINSTANCE as properties to metadata. Adaptors may use this properties
     * into their specification.
     *
     * @see ConfigurationStrategy#prepareSource(ConfigurationSource)
     */
    @SuppressWarnings( "unchecked" )
    public void prepareSource( final ConfigurationSource source )
    {
        final Dictionary metadata = source.getPropertiesSource().getMetadata();
        metadata.put( ServiceConstants.SERVICE_FACTORYPID, source.getIdentity().getFactoryPid() );
        metadata.put( ServiceConstants.SERVICE_FACTORYINSTANCE, source.getIdentity().getFactoryInstance() );
    }

    /**
     * @see ConfigurationStrategy#createUpdateCommand(ConfigurationTarget)
     */
    public UpdateCommand createUpdateCommand( final ConfigurationTarget configurationTarget )
    {
        // add SERVICE_FACTORYINSTANCE as into configuration properties. This property is use for sequential updates or
        // deletes for a specific instance (see find configuration).
        configurationTarget.getPropertiesTarget().getProperties().put(
            ServiceConstants.SERVICE_FACTORYINSTANCE,
            configurationTarget.getIdentity().getFactoryInstance()
        );
        return new UpdateCommand( configurationTarget )
        {
            /**
             * @see DeleteCommand#findConfiguration(ConfigurationAdmin)
             */
            @Override
            protected Configuration findConfiguration( final ConfigurationAdmin configurationAdmin )
                throws IOException
            {
                return FactoryPidStrategy.findConfiguration( configurationAdmin, m_target.getIdentity() );
            }
        };
    }

    /**
     * @see ConfigurationStrategy#createDeleteCommand(org.ops4j.pax.cm.domain.Identity)
     */
    public DeleteCommand createDeleteCommand( final Identity identity )
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
                return FactoryPidStrategy.findConfiguration( configurationAdmin, m_identity );
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
        final StringBuilder filter = new StringBuilder()
            .append( "(&(" )
            .append( ServiceConstants.SERVICE_FACTORYPID ).append( "=" ).append( identity.getFactoryPid() )
            .append( ")(" )
            .append( ServiceConstants.SERVICE_FACTORYINSTANCE ).append( "=" ).append( identity.getFactoryInstance() )
            .append( "))" );
        try
        {
            final Configuration[] configurations = configurationAdmin.listConfigurations( filter.toString() );
            if( configurations.length > 0 )
            {
                return configurations[ 0 ];
            }
            else
            {
                return configurationAdmin.createFactoryConfiguration( identity.getFactoryPid(), null );
            }
        }
        catch( InvalidSyntaxException ignore )
        {
            LOG.error( "Unexpected exception", ignore );
        }
        return null;
    }

}