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
package org.ops4j.pax.cm.domain;

import java.util.Dictionary;
import java.util.Hashtable;
import org.ops4j.lang.NullArgumentException;

/**
 * Configuration properties source model.
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 14, 2008
 */
public class PropertiesSource
{

    /**
     * Properties source object
     */
    private final Object m_sourceObject;
    /**
     * Properties source related metadata.
     */
    private final Dictionary m_metadata;

    /**
     * Create a new configuration properties sourceObject model.
     *
     * @param sourceObject properties sourceObject object
     * @param metadata     properties sourceObject related metadata; optional
     *
     * @throws org.ops4j.lang.NullArgumentException
     *          - If sourceObject is null
     */
    public PropertiesSource( final Object sourceObject,
                             final Dictionary metadata )
    {
        NullArgumentException.validateNotNull( sourceObject, "Properties source object" );

        this.m_sourceObject = sourceObject;
        if( metadata == null )
        {
            m_metadata = new Hashtable();
        }
        else
        {
            m_metadata = metadata;
        }
    }

    /**
     * Getter.
     *
     * @return properties source related metadata
     */
    public Dictionary getMetadata()
    {
        return m_metadata;
    }

    /**
     * Getter.
     *
     * @return configuration properties source
     */
    public Object getSourceObject()
    {
        return m_sourceObject;
    }

    @Override
    public String toString()
    {
        return new StringBuilder()
            .append( this.getClass().getSimpleName() )
            .append( "{" )
            .append( "meta=" ).append( m_metadata )
            .append( ",source" ).append( m_sourceObject )
            .append( "}" )
            .toString();
    }

}