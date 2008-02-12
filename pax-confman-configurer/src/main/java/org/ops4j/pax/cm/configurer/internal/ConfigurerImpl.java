package org.ops4j.pax.cm.configurer.internal;

import java.util.Dictionary;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.cm.configurer.Configurer;
import org.ops4j.pax.cm.configurer.DictionaryAdapter;
import org.ops4j.pax.cm.configurer.DictionaryAdapterRepository;

/**
 * Created by IntelliJ IDEA.
 * User: alindreghiciu
 * Date: Feb 11, 2008
 * Time: 6:18:25 PM
 * To change this template use File | Settings | File Templates.
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
                           final Object configuration,
                           final Dictionary metadata )
    {
        LOG.trace( "Configuring pid: " + pid );
        LOG.trace( "Metadata: " + metadata);
        LOG.trace( "Properties: " + configuration );

        final DictionaryAdapter adapter = m_dictionaryAdapterRepository.find( metadata );
        if( adapter != null )
        {
            LOG.trace( "Configuration adapter: " + adapter );
            final Dictionary adapted = adapter.adapt( configuration );
            LOG.trace( "Adapted configuguration properties: " + adapted );
            if( adapted != null )
            {
                m_processingQueue.add( new ProcessableConfiguration( pid, location, adapted ) );
            }
        }
        else
        {
            LOG.trace( "Configuration adapter not found. Skipping." );
        }
    }


}
