/*
 * Copyright 2006 Edward Yakop.
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
package org.ops4j.pax.cm.agent.wicket.configuration.browser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.cm.agent.configuration.PaxConfiguration;
import org.osgi.service.cm.Configuration;
import wicket.extensions.markup.html.repeater.util.SortParam;
import wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import wicket.model.IModel;
import wicket.model.Model;

/**
 * @author Edward Yakop
 * @since 0.1.0
 */
final class ConfigurationDataProvider extends SortableDataProvider
{

    private ArrayList<PaxConfiguration> m_configurations;

    ConfigurationDataProvider( Configuration[] configurations )
    {
        NullArgumentException.validateNotNull( configurations, "configurations" );

        m_configurations = new ArrayList<PaxConfiguration>( configurations.length );
        for( Configuration configuration : configurations )
        {
            String pid = configuration.getPid();
            String bundleLocation = configuration.getBundleLocation();
            PaxConfiguration entry = new PaxConfiguration( pid, bundleLocation );
            m_configurations.add( entry );
        }

        setSort( "pid", true );
    }

    public Iterator iterator( int first, int count )
    {
        SortParam sort = getSort();

        if( sort != null )
        {
            String sortProperty = sort.getProperty();
            boolean isAscending = sort.isAscending();
            sort( sortProperty, isAscending );
        }

        return m_configurations.listIterator( first );
    }

    private void sort( final String sortProperty, final boolean isAscending )
    {
        Collections.sort( m_configurations, new Comparator<PaxConfiguration>()
        {
            public int compare( PaxConfiguration o1, PaxConfiguration o2 )
            {
                if( !isAscending )
                {
                    PaxConfiguration temp = o1;
                    o1 = o2;
                    o2 = temp;
                }

                String o1Value = o1.getPropertyValue( sortProperty );
                String o2Value = o2.getPropertyValue( sortProperty );

                if( o1Value != null )
                {
                    return o1Value.compareTo( o2Value );
                }
                else if( o2Value != null )
                {
                    return o2Value.compareTo( o1Value );
                }
                else
                {
                    return 0;
                }
            }
        }
        );
    }

    public IModel model( Object configurationObject )
    {
        return new Model( (Serializable) configurationObject );
    }

    public int size()
    {
        return m_configurations.size();
    }

}
