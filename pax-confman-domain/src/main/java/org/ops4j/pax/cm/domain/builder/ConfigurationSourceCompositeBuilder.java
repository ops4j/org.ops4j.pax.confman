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
package org.ops4j.pax.cm.domain.builder;

import java.util.Dictionary;
import org.qi4j.property.ReadOnlyPropertyMixin;
import org.ops4j.pax.cm.domain.composite.ConfigurationSourceComposite;
import org.ops4j.pax.cm.domain.mixin.ConfigurationSourceCompositeMixin;

/**
 * Bundle location.
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 12, 2008
 */
public class ConfigurationSourceCompositeBuilder
{

    private String m_pid;
    private String m_location;
    private Dictionary m_metadata;
    private Object m_source;

    public ConfigurationSourceCompositeBuilder indentifiedBy( String pid )
    {
        m_pid = pid;
        return this;
    }

    public ConfigurationSourceCompositeBuilder with( String location )
    {
        m_location = location;
        return this;
    }

    public ConfigurationSourceCompositeBuilder taggedWith( Dictionary metadata )
    {
        m_metadata = metadata;
        return this;
    }

    public ConfigurationSourceCompositeBuilder from( Object source )
    {
        m_source = source;
        return this;
    }

    public ConfigurationSourceComposite newInstance()
    {
        return new ConfigurationSourceCompositeMixin(
            new ReadOnlyPropertyMixin<String>( m_pid ),
            new ReadOnlyPropertyMixin<String>( m_location ),
            new ReadOnlyPropertyMixin<Dictionary>( m_metadata ),
            new ReadOnlyPropertyMixin<Object>( m_source )
        );
    }

}