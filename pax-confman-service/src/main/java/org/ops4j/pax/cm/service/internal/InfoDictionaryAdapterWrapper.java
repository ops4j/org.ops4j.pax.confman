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

import java.text.DateFormat;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import org.ops4j.pax.cm.api.DictionaryAdapter;
import org.ops4j.pax.cm.api.MetadataConstants;

/**
 * A DictionaryAdapter wrapper that adds extra properties containing pax confman related info.
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 14, 2008
 */
class InfoDictionaryAdapterWrapper
    extends DictionaryAdapterWrapper
{

    /**
     * Constructor.
     *
     * @param delegate wrapped DictionaryAdapter
     */
    InfoDictionaryAdapterWrapper( final DictionaryAdapter delegate )
    {
        super( delegate );
    }

    /**
     * Delegates to wrapped DictionaryAdapter ands information entries.
     *
     * @see org.ops4j.pax.cm.api.DictionaryAdapter#adapt(Object)
     */
    @SuppressWarnings( "unchecked" )
    public Dictionary adapt( final Object object )
    {
        final Dictionary wrapped = new Hashtable();
        // first we add the properties in order to allow adaptors to overide them
        final Date currentTime = new Date();
        wrapped.put( MetadataConstants.INFO_TIMESTAMP, DateFormat.getDateInstance().format( currentTime ) );
        wrapped.put( MetadataConstants.INFO_TIMESTAMP_MILLIS, currentTime.getTime() );
        // and do the adaptation
        DictionaryUtils.copyDictionary( m_delegate.adapt( object ), wrapped );
        return wrapped;
    }
}