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
package org.ops4j.pax.cm.adapter.basic.internal.spec;

import java.util.Dictionary;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.cm.api.Specification;

/**
 * Specification that acts as a logical OR between specifications.
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 15, 2008
 */
public class OrSpecification
    implements Specification
{

    /**
     * OR-ed specifications.
     */
    private final Specification[] m_specifications;

    /**
     * Constructor.
     *
     * @param specifications OR-ed specifications
     *
     * @throws NullArgumentException - If specifications array is null or empty
     */
    public OrSpecification( final Specification... specifications )
    {
        NullArgumentException.validateNotEmpty( specifications, "Specifications" );

        m_specifications = specifications;
    }

    /**
     * Applys a logical OR between specifications.
     *
     * @see Specification#isSatisfiedBy(java.util.Dictionary, Object)
     */
    public boolean isSatisfiedBy( final Dictionary metadata, final Object sourceObject )
    {
        for( Specification spec : m_specifications )
        {
            if( spec.isSatisfiedBy( metadata, sourceObject ) )
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString()
    {
        final StringBuilder builder = new StringBuilder();
        for( Specification spec : m_specifications )
        {
            if( !( builder.length() == 0 ) )
            {
                builder.append( " OR " );
            }
            builder.append( spec );
        }
        return builder.toString();
    }

}