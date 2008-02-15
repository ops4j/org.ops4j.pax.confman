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
import org.ops4j.pax.cm.api.Specification;

/**
 * Adapts a java bean to a dictionary using reflection to determine properties keys and values.
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, January 15, 2008
 */
public class JavaBeanToDictionaryAdapter
    extends SpecificationBasedAdapter
{

    /**
     * Constructor.
     *
     * @param specification delegate specification
     */
    public JavaBeanToDictionaryAdapter( final Specification specification )
    {
        super( specification );
    }

    /**
     * Adapts the received object to a dictionary.
     *
     * @param sourceObject to be adapted
     *
     * @return adapted dictionary
     */
    public Object adapt( final Object sourceObject )
    {
        // TODO implement 
        return sourceObject;
    }

}