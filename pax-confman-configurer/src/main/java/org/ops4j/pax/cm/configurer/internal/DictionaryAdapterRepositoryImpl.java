package org.ops4j.pax.cm.configurer.internal;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ops4j.pax.cm.configurer.DictionaryAdapter;
import org.ops4j.pax.cm.configurer.DictionaryAdapterRepository;

/**
 * Created by IntelliJ IDEA.
 * User: alindreghiciu
 * Date: Feb 11, 2008
 * Time: 7:24:50 PM
 * To change this template use File | Settings | File Templates.
 */
class DictionaryAdapterRepositoryImpl
    implements DictionaryAdapterRepository
{

    /**
     * Logger.
     */
    private static final Log LOG = LogFactory.getLog( ConfigurerImpl.class );

    private final List<DictionaryAdapter> m_adapters;

    DictionaryAdapterRepositoryImpl()
    {
        m_adapters = new ArrayList<DictionaryAdapter>();
    }

    public void register( final DictionaryAdapter adapter )
    {
        LOG.trace( "Registered adapters: " + adapter );
        m_adapters.add( adapter );
    }

    public void unregister( final DictionaryAdapter adapter )
    {
        LOG.trace( "Unegistered adapters: " + adapter );
        m_adapters.remove( adapter );
    }

    public DictionaryAdapter find( final Dictionary metadata )
    {
        for( DictionaryAdapter adapter : m_adapters )
        {
            if( adapter.isSatisfiedBy( metadata ) )
            {
                return adapter;
            }
        }
        return null;
    }

}
