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
import org.osgi.framework.Constants;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.cm.api.Configurer;
import org.ops4j.pax.cm.api.DictionaryAdapter;
import org.ops4j.pax.cm.api.DictionaryAdapterRepository;
import org.ops4j.pax.cm.api.MetadataConstants;

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
     * Repository of dictionary adapters. Cannot be null.
     */
    private final DictionaryAdapterRepository m_dictionaryAdapterRepository;
    /**
     * Queue of processable items. Canot be null.
     */
    private final ProcessingQueue m_processingQueue;

    public ConfigurerImpl( final DictionaryAdapterRepository dictionaryAdapterRepository,
                           final ProcessingQueue processingQueue )
    {
        NullArgumentException.validateNotNull( dictionaryAdapterRepository, "Dictionary adapters repository" );
        NullArgumentException.validateNotNull( processingQueue, "Processing queue" );

        m_dictionaryAdapterRepository = dictionaryAdapterRepository;
        m_processingQueue = processingQueue;
    }

    public void configure( final String pid,
                           final String location,
                           final Dictionary metadata,
                           final Object configuration
    )
    {
        LOG.trace( "Configuring pid: " + pid );
        LOG.trace( "Metadata: " + metadata );
        LOG.trace( "Properties: " + configuration );

        // make a copy of metadata to avoid readonly dictionaries (if there is such a case?)
        final Dictionary writableMetadata = DictionaryUtils.copy( metadata, new Hashtable() );
        // add standard properties
        writableMetadata.put( Constants.SERVICE_PID, pid );

        final DictionaryAdapter adapter = m_dictionaryAdapterRepository.find( writableMetadata );
        if( adapter != null )
        {
            LOG.trace( "Configuration adapter: " + adapter );
            final Dictionary adapted = copyPropertiesFromMetadata( writableMetadata, adapter.adapt( configuration ) );
            LOG.trace( "Adapted configuration properties: " + adapted );
            if( adapted != null )
            {
                m_processingQueue.add(
                    new ProcessableConfiguration( pid, location, adapted )
                );
            }
        }
        else
        {
            LOG.trace( "Configuration adapter not found. Skipping." );
        }
    }

    /**
     * Copy all necessary metadata properties .
     *
     * @param metadata metadata
     * @param adapted  destination
     *
     * @return dictionary containing the info prepertties copied from metadata.
     */
    private static Dictionary copyPropertiesFromMetadata( final Dictionary metadata,
                                                          final Dictionary adapted )
    {
        if( adapted == null )
        {
            return null;
        }
        final Dictionary result = new Hashtable();
        DictionaryUtils.copy( adapted, result );
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
