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
package org.ops4j.pax.cm.agent.wicket.configuration.edit.properties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.cm.agent.configuration.PaxConfiguration;
import wicket.extensions.markup.html.repeater.data.sort.ISortState;
import wicket.extensions.markup.html.repeater.util.SortParam;
import wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import wicket.model.IModel;
import wicket.model.Model;

/**
 * @author Edward Yakop
 * @since 0.1.0
 */
final class ConfigurationItemDataProvider extends SortableDataProvider
    implements Serializable
{

    private static final long serialVersionUID = 1L;

    private List<ConfigurationItem> m_configurationProperties;
    private final PaxConfiguration m_configuration;
    private EditConfigurationItemPanel m_listener;
    private int m_selected;

    ConfigurationItemDataProvider( PaxConfiguration configuration )
    {
        m_configuration = configuration;
        m_selected = 0;

        Dictionary properties = m_configuration.getProperties();
        if( properties == null )
        {
            properties = new Properties();
            m_configuration.setProperties( properties );
        }

        int numberOfProperties = properties.size();
        m_configurationProperties = new ArrayList<ConfigurationItem>( numberOfProperties );
        Enumeration enumeration = properties.keys();
        while( enumeration.hasMoreElements() )
        {
            String key = (String) enumeration.nextElement();
            Object value = properties.get( key );

            ConfigurationItem configurationItem = new ConfigurationItem();
            configurationItem.setKey( key );
            configurationItem.setValue( value );
            m_configurationProperties.add( configurationItem );
        }

        // By default sort key in ascending manner
        ISortState sortState = getSortState();
        sortState.setPropertySortOrder( ConfigurationItem.PROPERTY_KEY, ISortState.ASCENDING );
    }

    public Iterator iterator( int i, int i1 )
    {
        SortParam sort = getSort();

        if( sort != null )
        {
            String sortProperty = sort.getProperty();
            boolean ascending = sort.isAscending();

            sort( sortProperty, ascending );
        }
        return m_configurationProperties.iterator();
    }

    private void sort( final String property, final boolean ascending )
    {
        Collections.sort( m_configurationProperties, new Comparator<ConfigurationItem>()
        {
            public int compare( ConfigurationItem o1, ConfigurationItem o2 )
            {
                if( !ascending )
                {
                    ConfigurationItem temp = o1;
                    o1 = o2;
                    o2 = temp;
                }

                String value1 = String.valueOf( o1.getPropertyValue( property ) );
                String value2 = String.valueOf( o2.getPropertyValue( property ) );

                if( value1 != null )
                {
                    return value1.compareTo( value2 );
                }
                else if( value2 != null )
                {
                    return 1;
                }
                else
                {
                    return 0;
                }
            }
        }
        );
    }

    public IModel model( Object object )
    {
        return new Model( (Serializable) object );
    }

    public void selectProperty( ConfigurationItem item )
    {
        int i = 0;
        for( ConfigurationItem it : m_configurationProperties )
        {
            if( it == item )
            {
                m_selected = i;
                break;
            }
            i++;
        }

        notifyListener( item );
    }

    private void notifyListener( ConfigurationItem item )
    {
        if( m_listener != null )
        {
            m_listener.setConfigurationItem( item );
        }
    }

    public void setSelectionListener( EditConfigurationItemPanel editConfigurationItemPanel )
    {
        m_listener = editConfigurationItemPanel;
    }

    public int size()
    {
        return m_configurationProperties.size();
    }

    public void newConfigurationItem()
    {
        ConfigurationItem newConfigurationItem = new ConfigurationItem();
        newConfigurationItem.setIsNew( true );
        m_configurationProperties.add( m_selected, newConfigurationItem );

        notifyListener( newConfigurationItem );
    }

    public void deleteSelectedConfigurationItem()
    {
        boolean isEmpty = m_configurationProperties.isEmpty();
        if( !isEmpty )
        {
            m_configurationProperties.remove( m_selected );
            m_selected--;

            if( m_selected < 0 )
            {
                m_selected = 0;
            }

            ConfigurationItem selectedProperty;
            if( m_configurationProperties.isEmpty() )
            {
                selectedProperty = null;
            }
            else
            {
                selectedProperty = m_configurationProperties.get( m_selected );
            }

            notifyListener( selectedProperty );
        }
    }

    public void saveConfigurationItem( ConfigurationItem configurationItem )
    {
        NullArgumentException.validateNotNull( configurationItem, "configurationItem" );

        configurationItem.setIsNew( false );
        Dictionary properties = m_configuration.getProperties();
        Object value = configurationItem.getValue();
        String key = configurationItem.getKey();
        properties.put( key, value );

        notifyListener( configurationItem );
    }
}
