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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ops4j.pax.cm.api.DictionaryAdapter;
import org.ops4j.pax.cm.api.DictionaryAdapterRepository;

/**
 * TODO add JavaDoc
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 12, 2008
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
        m_adapters = Collections.synchronizedList( new ArrayList<DictionaryAdapter>() );
    }

    public void register( final DictionaryAdapter adapter )
    {
        LOG.trace( "Registered adapters: " + adapter );
        m_adapters.add(
            new CleanupDictionaryAdapterWrapper(
                new InfoDictionaryAdapterWrapper(
                    new AdaptorTypeInfoDictionaryAdapterWrapper(
                        adapter
                    )
                )
            )
        );
    }

    public void unregister( final DictionaryAdapter adapter )
    {
        LOG.trace( "Unegistered adapters: " + adapter );
        m_adapters.remove( adapter );
    }

    public DictionaryAdapter find( final Dictionary metadata )
    {
        synchronized( m_adapters )
        {
            for( DictionaryAdapter adapter : m_adapters )
            {
                if( adapter.isSatisfiedBy( metadata ) )
                {
                    return adapter;
                }
            }
        }
        return null;
    }

}
