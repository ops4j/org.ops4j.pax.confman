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
import java.util.Hashtable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.cm.ConfigurationAdmin;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.cm.api.Configurer;
import org.ops4j.pax.cm.api.DictionaryAdapter;
import org.ops4j.pax.cm.api.DictionaryAdapterRepository;
import org.ops4j.pax.cm.api.MetadataConstants;
import org.ops4j.pax.cm.common.internal.processor.Command;
import org.ops4j.pax.cm.common.internal.processor.CommandProcessor;
import org.ops4j.pax.cm.domain.ConfigurationSource;
import org.ops4j.pax.cm.domain.ConfigurationTarget;
import org.ops4j.pax.cm.domain.PropertiesSource;
import org.ops4j.pax.cm.domain.PropertiesTarget;

/**
 * TODO add JavaDoc
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 12, 2008
 */
public class ConfigurerImpl
    implements Configurer
{

    /**
     * Logger.
     */
    private static final Log LOG = LogFactory.getLog( ConfigurerImpl.class );
    /**
     * Managed service strategy. Strategies should be stateless so are safe to be reused.
     */
    private static final ConfigurationStrategy MANAGED_SERVICE_STRATEGY = new ManagedServiceStrategy();
    /**
     * Repository of dictionary adapters. Cannot be null.
     */
    private final DictionaryAdapterRepository m_adapterRepository;
    /**
     * Configuration Admin commands processor.
     */
    private final CommandProcessor<ConfigurationAdmin> m_processor;

    /**
     * Constructor.
     *
     * @param adapterRepository adaptor repository to use
     * @param processor         processing queue to use
     *
     * @throws NullArgumentException - If dictionaryAdapterRepository is null
     *                               - If processingQueue is null
     */
    public ConfigurerImpl( final DictionaryAdapterRepository adapterRepository,
                           final CommandProcessor<ConfigurationAdmin> processor )
    {
        NullArgumentException.validateNotNull( adapterRepository, "Dictionary adapters repository" );
        NullArgumentException.validateNotNull( processor, "Commands processor" );

        m_adapterRepository = adapterRepository;
        m_processor = processor;
    }

    /**
     * @see Configurer#configure(String, String, Dictionary, Object)
     */
    public void configure( final String pid,
                           final String location,
                           final Dictionary metadata,
                           final Object propertiesSource
    )
    {
        LOG.trace( "Configuring pid: " + pid );
        LOG.trace( "Metadata: " + metadata );
        LOG.trace( "Properties source: " + propertiesSource );

        processConfiguration(
            new ConfigurationSource(
                MANAGED_SERVICE_STRATEGY.createServiceIdentity( pid, null, location ),
                new PropertiesSource(
                    propertiesSource,
                    DictionaryUtils.copy( metadata, new Hashtable() )
                )
            ),
            MANAGED_SERVICE_STRATEGY
        );
    }

    /**
     * Process configuration using the supplied strategy.
     *
     * @param source   configuration source
     * @param strategy configuration strategy
     */
    private void processConfiguration( final ConfigurationSource source,
                                       final ConfigurationStrategy strategy )
    {
        strategy.prepareSource( source );
        final DictionaryAdapter adapter = m_adapterRepository.find( source.getPropertiesSource().getMetadata() );
        if( adapter != null )
        {
            LOG.trace( "Configuration adapter: " + adapter );
            final Dictionary adapted = copyPropertiesFromMetadata(
                source.getPropertiesSource().getMetadata(),
                adapter.adapt(
                    source.getPropertiesSource().getSourceObject()
                )
            );
            LOG.trace( "Adapted configuration properties: " + adapted );
            if( adapted != null )
            {
                final Command<ConfigurationAdmin> adminCommand = strategy.createConfigurationCommand(
                    new ConfigurationTarget( source.getServiceIdentity(), new PropertiesTarget( adapted ) )
                );
                if( adminCommand != null )
                {
                    m_processor.add( adminCommand );
                }
            }
        }
        else
        {
            LOG.trace( "Configuration adapter not found. Skipping." );
        }
    }

    /**
     * Copy all necessary metadata properties to configuration properties dictionary.
     *
     * @param metadata   metadata
     * @param properties destination
     *
     * @return dictionary containing the info prepertties copied from metadata.
     */
    private static Dictionary copyPropertiesFromMetadata( final Dictionary metadata,
                                                          final Dictionary properties )
    {
        if( properties == null )
        {
            return null;
        }
        final Dictionary result = new Hashtable();
        DictionaryUtils.copy( properties, result );
        DictionaryUtils.copy(
            new DictionaryUtils.OrSpecification(
                new DictionaryUtils.RegexSpecification( MetadataConstants.INFO_PREFIX_AS_REGEX ),
                new DictionaryUtils.RegexSpecification( MetadataConstants.SERVICE_PID_AS_REGEX ),
                new DictionaryUtils.RegexSpecification( MetadataConstants.SERVICE_FACTORYPID_AS_REGEX )
            ),
            metadata,
            result
        );
        return result;
    }

}
