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
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.cm.api.DictionaryAdapter;

/**
 * A DictionaryAdapter wrapper .
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 14, 2008
 */
class DictionaryAdapterWrapper
    implements DictionaryAdapter
{

    /**
     * Wrapped DictionaryAdapter. Cannot be null.
     */
    protected final DictionaryAdapter m_delegate;

    /**
     * Constructor.
     *
     * @param delegate wrapped DictionaryAdapter
     */
    DictionaryAdapterWrapper( final DictionaryAdapter delegate )
    {
        NullArgumentException.validateNotNull( delegate, "Wrapped dictionary adapter" );

        m_delegate = delegate;
    }

    /**
     * Delegates to wrapped DictionaryAdapter.
     *
     * @see org.ops4j.pax.cm.api.DictionaryAdapter#adapt(Object)
     */
    public Dictionary adapt( final Object object )
    {
        return m_delegate.adapt( object );
    }

    /**
     * Delegates to wrapped DictionaryAdapter.
     *
     * @see org.ops4j.pax.cm.api.DictionaryAdapter#isSatisfiedBy(java.util.Dictionary)
     */
    public boolean isSatisfiedBy
        (
            final Dictionary metadata )
    {
        return m_delegate.isSatisfiedBy( metadata );
    }

    @Override
    public String toString()
    {
        return m_delegate.toString();
    }

}