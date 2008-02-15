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

import java.util.Dictionary;
import org.ops4j.pax.cm.api.MetadataConstants;
import org.ops4j.pax.cm.domain.ConfigurationSource;
import org.ops4j.pax.cm.domain.ConfigurationTarget;
import org.ops4j.pax.cm.domain.ServiceIdentity;

/**
 * Configuration strategy for a ManagedService.
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 15, 2008
 */
public class ManagedServiceStrategy
    implements ConfigurationStrategy
{

    /**
     * @see ConfigurationStrategy#createServiceIdentity(String, String, String)
     */
    public ServiceIdentity createServiceIdentity( final String pid,
                                                  final String factoryPid,
                                                  final String location )
    {
        return new ServiceIdentity( pid, location );
    }

    /**
     * @see ConfigurationStrategy#prepareSource(ConfigurationSource)
     */
    public void prepareSource( final ConfigurationSource source )
    {
        final Dictionary metadata = source.getPropertiesSource().getMetadata();
        metadata.put( MetadataConstants.SERVICE_PID, source.getServiceIdentity().getPid() );
        // be defensive and remove possible unwanted metadata
        metadata.remove( MetadataConstants.SERVICE_FACTORYPID );
    }

    /**
     * @see ConfigurationStrategy#createConfigurationCommand(ConfigurationTarget)
     */
    public AdminCommand createConfigurationCommand( final ConfigurationTarget configurationTarget )
    {
        return new UpdateManagedServiceCommand( configurationTarget );
    }
}