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
import org.ops4j.pax.cm.api.Adapter;
import org.ops4j.pax.cm.api.AdapterRepository;
import org.ops4j.pax.cm.api.Configurer;
import org.ops4j.pax.cm.api.MetadataConstants;
import org.ops4j.pax.cm.common.internal.processor.Command;
import org.ops4j.pax.cm.common.internal.processor.CommandProcessor;
import org.ops4j.pax.cm.domain.ConfigurationSource;
import org.ops4j.pax.cm.domain.ConfigurationTarget;
import org.ops4j.pax.cm.domain.Identity;
import org.ops4j.pax.cm.domain.PropertiesSource;
import org.ops4j.pax.cm.domain.PropertiesTarget;

/**
 * Configurer implementation.
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 12, 2008
 */
public class ConfigurationManager
    implements Configurer
{

    /**
     * Logger.
     */
    private static final Log LOG = LogFactory.getLog( ConfigurationManager.class );
    /**
     * Managed service strategy. Strategies should be stateless so are safe to be reused.
     */
    private static final ConfigurationStrategy MANAGED_SERVICE_STRATEGY = new ManagedServiceStrategy();
    /**
     * Managed service factory strategy. Strategies should be stateless so are safe to be reused.
     */
    private static final ConfigurationStrategy MANAGED_SERVICE_FACTORY_STRATEGY = new ManagedServiceFactoryStrategy();
    /**
     * Repository of dictionary adapters. Cannot be null.
     */
    private final AdapterRepository m_adapterRepository;
    /**
     * Configuration Admin commands processor.
     */
    private final CommandProcessor<ConfigurationAdmin> m_processor;

    /**
     * Constructor.
     *
     * @param adapterRepository adaptor repository
     * @param processor         processing queue
     *
     * @throws NullArgumentException - If dictionaryAdapterRepository is null
     *                               - If processingQueue is null
     */
    public ConfigurationManager( final AdapterRepository adapterRepository,
                                 final CommandProcessor<ConfigurationAdmin> processor )
    {
        NullArgumentException.validateNotNull( adapterRepository, "Dictionary adapters repository" );
        NullArgumentException.validateNotNull( processor, "Commands processor" );

        m_adapterRepository = adapterRepository;
        m_processor = processor;
    }

    /**
     * @see Configurer#update(String, String, Object, Dictionary)
     */
    public void update( final String pid,
                        final String location,
                        final Object propertiesSource,
                        final Dictionary metadata )
    {
        updateConfiguration(
            new ConfigurationSource(
                new Identity( pid, location ),
                new PropertiesSource(
                    propertiesSource,
                    DictionaryUtils.copy( metadata, new Hashtable() )
                )
            ),
            MANAGED_SERVICE_STRATEGY
        );
    }

    /**
     * @see Configurer#update(String, String, String, Object, Dictionary)
     */
    public void update( final String factoryPid,
                        final String factoryInstance,
                        final String location,
                        final Object propertiesSource,
                        final Dictionary metadata )
    {
        updateConfiguration(
            new ConfigurationSource(
                new Identity( factoryPid, factoryInstance, location ),
                new PropertiesSource(
                    propertiesSource,
                    DictionaryUtils.copy( metadata, new Hashtable() )
                )
            ),
            MANAGED_SERVICE_FACTORY_STRATEGY
        );
    }

    /**
     * @see Configurer#delete(String)
     */
    public void delete( final String pid )
    {
        deleteConfiguration(
            new Identity( pid, null ),
            MANAGED_SERVICE_STRATEGY
        );
    }

    /**
     * @see Configurer#delete(String, String)
     */
    public void delete( final String factoryPid,
                        final String factoryInstance )
    {
        deleteConfiguration(
            new Identity( factoryPid, factoryInstance, null ),
            MANAGED_SERVICE_FACTORY_STRATEGY
        );
    }

    /**
     * Updates configuration using the supplied strategy.
     *
     * @param source   configuration source
     * @param strategy configuration strategy
     */
    private void updateConfiguration( final ConfigurationSource source,
                                      final ConfigurationStrategy strategy )
    {
        strategy.prepareSource( source );
        // try to adapt the source object to a dictionary
        Dictionary adapted = null;
        Object sourceObject = source.getPropertiesSource().getSourceObject();
        // loop adaptors till we have an adapted dictionary or sourceObject becomes null
        while( adapted == null )
        {
            final Adapter adapter = m_adapterRepository.find(
                source.getPropertiesSource().getMetadata(),
                sourceObject
            );
            if( adapter == null )
            {
                // no adaptor found, so let's just get out
                break;
            }
            LOG.trace( "Using adapter " + adapter );
            final Class previousSourceClass = sourceObject.getClass();
            sourceObject = adapter.adapt( sourceObject );
            if( sourceObject == null )
            {
                // less probably but if a source object becomes null there is no reason to look further
                break;
            }
            if( sourceObject instanceof Dictionary )
            {
                adapted = (Dictionary) sourceObject;
            }
            LOG.trace( "Source object converted from " + previousSourceClass + " to " + sourceObject.getClass() );
            if( previousSourceClass.equals( sourceObject.getClass() ) )
            {
                // if still the same class get out to avoid an infinite loop as tere is no reason to believe
                // that on a next cycle anotehr adapter will be found.
                // TODO handle situation when an adaptor is added or removed during a cycle 
                break;
            }
        }
        if( adapted != null )
        {
            adapted = copyPropertiesFromMetadata( source.getPropertiesSource().getMetadata(), adapted );
            LOG.trace( "Adapted configuration properties: " + adapted );
            if( adapted != null )
            {
                final Command<ConfigurationAdmin> command = strategy.createUpdateCommand(
                    new ConfigurationTarget( source.getIdentity(), new PropertiesTarget( adapted ) )
                );
                if( command != null )
                {
                    m_processor.add( command );
                }
            }
        }
        else
        {
            LOG.info( "Configuration source object cannot be adapted to a dictionary" );
        }
    }

    /**
     * Deletes configuration using the supplied strategy.
     *
     * @param identity configuration identity
     * @param strategy configuration strategy
     */
    private void deleteConfiguration( final Identity identity,
                                      final ConfigurationStrategy strategy )
    {
        m_processor.add( strategy.createDeleteCommand( identity ) );
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
