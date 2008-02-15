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
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.cm.api.Adapter;
import org.ops4j.pax.cm.api.Specification;

/**
 * Adapts an input stream containing Properties.
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, January 15, 2008
 */
public abstract class SpecificationBasedAdapter
    implements Adapter
{

    /**
     * Specification used to match to this adapter.
     */
    private final Specification m_specification;

    /**
     * Constructor.
     *
     * @param specification delegate specification
     *
     * @throws NullArgumentException - If specification is null
     */
    public SpecificationBasedAdapter( final Specification specification )
    {
        NullArgumentException.validateNotNull( specification, "Specification" );

        m_specification = specification;
    }

    /**
     * Delegates to specification.
     *
     * @see org.ops4j.pax.cm.api.Adapter#isSatisfiedBy(java.util.Dictionary,Object)
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
            .append( m_specification )
            .append( "}" )
            .toString();
    }

}