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
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.cm.api.Adapter;
import org.ops4j.pax.cm.api.ServiceConstants;
import org.ops4j.pax.cm.domain.ConfigurationSource;
import org.ops4j.pax.cm.domain.ConfigurationTarget;
import org.ops4j.pax.cm.domain.PropertiesTarget;

/**
 * The process of transforming an configuration source to an update ready command.
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 17, 2008
 */
class Transformation
{

    /**
     * Logger.
     */
    private static final Log LOG = LogFactory.getLog( Transformation.class );

    /**
     * Configuration source to be transformed.
     */
    private final ConfigurationSource m_configurationSource;
    /**
     * Configuration strategy to be used while transforming.
     */
    private final ConfigurationStrategy m_strategy;
    /**
     * Adapter repository to be used for adapting the configuration source.
     */
    private final AdapterRepository m_adapterRepository;

    /**
     * Constructor.
     *
     * @param configurationSource configuration source to be transformed
     * @param strategy            configuration strategy to be used while transforming
     * @param adapterRepository   adapter repository to be used for adapting the configuration source
     *
     * @throws NullArgumentException - If configuration source is null
     *                               - If strategy is null
     *                               - If adapter repository is null
     */
    Transformation( final ConfigurationSource configurationSource,
                    final ConfigurationStrategy strategy,
                    final AdapterRepository adapterRepository )
    {
        NullArgumentException.validateNotNull( configurationSource, "Configuration configurationSource" );
        NullArgumentException.validateNotNull( strategy, "Configuration startegy" );
        NullArgumentException.validateNotNull( adapterRepository, "Adapter repository" );

        m_configurationSource = configurationSource;
        m_strategy = strategy;
        m_adapterRepository = adapterRepository;

        m_strategy.prepareSource( m_configurationSource );
    }

    /**
     * Performs the transformation process. If transformation is successful returns an update conmmand for the
     * transformed configuration source, otherwise returns null. The method can be called multiple times if something
     * changes as for example new adaptor is added.
     *  
     * @return an update command or null if transformation fails 
     */
    UpdateCommand execute()
    {
        // try to adapt the source object to a dictionary
        Dictionary adapted = null;
        Object sourceObject = m_configurationSource.getPropertiesSource().getSourceObject();
        // loop adaptors till we have an adapted dictionary or sourceObject becomes null
        while( adapted == null )
        {
            final Adapter adapter = m_adapterRepository.find(
                m_configurationSource.getPropertiesSource().getMetadata(),
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
                // that on a next cycle another adapter will be found.
                break;
            }
        }
        if( adapted != null )
        {
            adapted = copyPropertiesFromMetadata( m_configurationSource.getPropertiesSource().getMetadata(), adapted );
            LOG.trace( "Adapted configuration properties: " + adapted );

            final ConfigurationTarget target = new ConfigurationTarget(
                m_configurationSource.getIdentity(), new PropertiesTarget( adapted )
            );

            return m_strategy.createUpdateCommand( target );
        }
        else
        {
            LOG.trace( "Configuration source object cannot be adapted to a dictionary. Update postponed." );
        }
        return null;
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
                new DictionaryUtils.RegexSpecification( ServiceConstants.INFO_PREFIX_AS_REGEX ),
                new DictionaryUtils.RegexSpecification( ServiceConstants.SERVICE_PID_AS_REGEX ),
                new DictionaryUtils.RegexSpecification( ServiceConstants.SERVICE_FACTORYPID_AS_REGEX )
            ),
            metadata,
            result
        );
        return result;
    }


}