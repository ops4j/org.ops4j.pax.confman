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
import org.ops4j.pax.cm.api.Specification;

/**
 * Specification that is statisfied if the class of the source object is instance of a certain type.
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 15, 2008
 */
public class InstanceOfSpecification
    implements Specification
{

    /**
     * Specification OSGi filter to match metadata against.
     */
    private final Class m_class;

    /**
     * Constructor
     *
     * @param clazz class of the source object
     *
     * @throws NullArgumentException - If clazz is null
     */
    public InstanceOfSpecification( final Class clazz )
    {
        NullArgumentException.validateNotNull( clazz, "Class" );

        m_class = clazz;
    }

    /**
     * Returns true if source object is instance of expected class.
     *
     * @see Specification#isSatisfiedBy(java.util.Dictionary, Object)
     */
    public boolean isSatisfiedBy( final Dictionary metadata, final Object sourceObject )
    {
        return m_class.isInstance( sourceObject );
    }

    @Override
    public String toString()
    {
        return "instanceOf " + m_class.getName();
    }

}