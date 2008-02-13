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
import org.osgi.framework.Filter;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.cm.api.Specification;

/**
 * Specification that is statisfied by matching metadata agains an OSGi filter.
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 11, 2008
 */
public class FilterBasedSpecification
    implements Specification
{

    /**
     * Specification OSGi filter to match metadata against.
     */
    private final Filter m_filter;

    /**
     * Creates a new specification.
     *
     * @param filter OSGi filter to match metadata against
     *
     * @throws NullArgumentException - If filter is null
     */
    public FilterBasedSpecification( final Filter filter )
    {
        NullArgumentException.validateNotNull( filter, "Filter" );

        m_filter = filter;
    }

    /**
     * Matches metadata aginst the provided filter. Returns true if filter is matching metadata.
     *
     * @param metadata metadata to be matched
     */
    public boolean isSatisfiedBy( final Dictionary metadata )
    {
        return m_filter.match( metadata );
    }

    @Override
    public String toString()
    {
        return m_filter.toString();
    }

}
