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
package org.ops4j.pax.cm.adapter.basic.internal;

import java.util.Dictionary;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.cm.api.Adapter;
import org.ops4j.pax.cm.api.Specification;

/**
 * Adapts a dictionary to a dictionary (quite easy).
 * TODO shall we strip out dictionary values that are not osgi compatible?
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, January 11, 2008
 */
public class DictionaryToDictionaryAdapter
    implements Adapter
{

    /**
     * Logger.
     */
    private static final Log LOG = LogFactory.getLog( DictionaryToDictionaryAdapter.class );

    /**
     * Specification in use. Cannot be null.
     */
    private final Specification m_specification;

    /**
     * Creates a new adapter.
     *
     * @param specification specification to be used; cannot be null.
     *
     * @throws NullArgumentException - If specification is null
     */
    public DictionaryToDictionaryAdapter( final Specification specification )
    {
        NullArgumentException.validateNotNull( specification, "Specification" );

        m_specification = specification;

        LOG.debug( "Started " + toString() );
    }

    /**
     * Adapts the received object (expected to be a dictionary) to a dictionary.
     *
     * @param object to be adapted
     *
     * @return adapted dictionary or null if source object is not a dictionary
     */
    public Object adapt( final Object object )
    {
        if( object instanceof Dictionary )
        {
            return object;
        }
        return null;
    }

    /**
     * Delegates to specification.
     *
     * @see org.ops4j.pax.cm.api.Adapter#isSatisfiedBy(Dictionary,Object)
     */
    public boolean isSatisfiedBy( final Dictionary metadata, final Object sourceObject )
    {
        return m_specification.isSatisfiedBy( metadata, sourceObject );
    }

    @Override
    public String toString()
    {
        return new StringBuilder( this.getClass().getSimpleName() )
            .append( "{" )
            .append( "spec=" ).append( m_specification.toString() )
            .append( "}" )
            .toString();
    }
}
