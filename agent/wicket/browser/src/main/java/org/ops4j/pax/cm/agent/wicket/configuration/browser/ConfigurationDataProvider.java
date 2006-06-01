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

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.cm.agent.configuration.PaxConfiguration;
import org.ops4j.pax.cm.agent.configuration.PaxConfigurationFacade;
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
    private int m_selected;

    ConfigurationDataProvider( Configuration[] configurations )
    {
        NullArgumentException.validateNotNull( configurations, "configurations" );

        m_configurations = new ArrayList<PaxConfiguration>( configurations.length );
        m_selected = 0;

        for( Configuration configuration : configurations )
        {
            PaxConfiguration entry = new PaxConfiguration();

            String pid = configuration.getPid();
            entry.setPid( pid );

            String factoryPid = entry.getFactoryPid();
            entry.setFactoryPid( factoryPid );

            String bundleLocation = configuration.getBundleLocation();
            entry.setBundleLocation( bundleLocation );

            m_configurations.add( entry );
        }

        setSort( PaxConfiguration.PROPERTY_PID, true );
        sort( PaxConfiguration.PROPERTY_PID, true );
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
                    if( o2Value != null )
                    {
                        return o1Value.compareTo( o2Value );
                    }
                    else
                    {
                        return 1;
                    }
                }
                else if( o2Value != null )
                {
                    return -1;
                }
                else
                {
                    return 0;
                }
            }
        }
        );
    }

    PaxConfiguration getSelectedPaxconfiguration()
    {
        if( m_configurations.isEmpty() )
        {
            return null;
        }
        else
        {
            return m_configurations.get( m_selected );
        }
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

        if( m_configurations.isEmpty() )
        {
            m_selected = 0;
        }
        else
        {
            m_selected = first;
        }

        return m_configurations.listIterator( first );
    }

    public IModel model( Object configurationObject )
    {
        return new Model( (Serializable) configurationObject );
    }

    PaxConfiguration createNewPaxConfiguration()
    {
        PaxConfiguration paxConfiguration = new PaxConfiguration();
        paxConfiguration.setIsNew( true );
        paxConfiguration.setPid( "NEW RECORD" );

        m_configurations.add( m_selected, paxConfiguration );

        return paxConfiguration;
    }

    PaxConfiguration savePaxConfiguration( PaxConfiguration configuration )
    {
        try
        {
            boolean isNew = configuration.isNew();
            PaxConfigurationFacade.updateConfiguration( configuration );
            if( isNew )
            {
                m_configurations.add( m_selected, configuration );
            }
        } catch( IOException e )
        {
            e.printStackTrace();
        }

        return configuration;
    }

    void setSelectedPaxConfiguration( int i )
    {
        m_selected = i;
    }

    public int size()
    {
        return m_configurations.size();
    }
}
