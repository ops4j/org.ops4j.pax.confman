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
package org.ops4j.pax.cm.agent.wicket.configuration.edit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import org.ops4j.pax.cm.agent.configuration.PaxConfiguration;
import wicket.Localizer;
import wicket.extensions.markup.html.repeater.data.sort.ISortState;
import wicket.extensions.markup.html.repeater.data.table.DefaultDataTable;
import wicket.extensions.markup.html.repeater.data.table.IColumn;
import wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import wicket.extensions.markup.html.repeater.util.SortParam;
import wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import wicket.markup.html.panel.Panel;
import wicket.model.IModel;
import wicket.model.Model;

/**
 * @author Edward yakop
 * @since 0.1.0
 */
final class DefaultConfigurationPropertiesEditor extends Panel
{

    private final static String LOCALE_COLUMN_HEADER_KEY = "keyColumnHeader";
    private final static String LOCALE_COLUMN_HEADER_VALUE = "valueColumnHeader";

    DefaultConfigurationPropertiesEditor( String id, PaxConfiguration configuration )
    {
        super( id );

        Localizer localizer = getLocalizer();
        String keyColumnHeader = localizer.getString( LOCALE_COLUMN_HEADER_KEY, this, "Key" );
        PropertyColumn keyColumn = new PropertyColumn( new Model( keyColumnHeader ), "key", "key" );

        Serializable valueColumnHeader = localizer.getString( LOCALE_COLUMN_HEADER_VALUE, this, "Value" );
        PropertyColumn valueColumn = new PropertyColumn( new Model( valueColumnHeader ), "value", "value" );

        IColumn[] columns = { keyColumn, valueColumn };
        DictionaryDataProvider dataProvider = new DictionaryDataProvider( configuration );

        add( new DefaultDataTable( "table", columns, dataProvider, 20 ) );
    }

    private static final class DictionaryDataProvider extends SortableDataProvider
    {

        private final PaxConfiguration m_configuration;
        private List<ConfigurationItem> m_configurationProperties;

        private DictionaryDataProvider( PaxConfiguration configuration )
        {
            m_configuration = configuration;

            Dictionary properties = configuration.getProperties();
            if( properties == null )
            {
                properties = new Properties();
            }

            int numberOfProperties = properties.size();
            m_configurationProperties = new ArrayList<ConfigurationItem>( numberOfProperties );
            Enumeration enumeration = properties.keys();
            while( enumeration.hasMoreElements() )
            {
                String key = (String) enumeration.nextElement();
                String value = (String) properties.get( key );

                ConfigurationItem configurationItem = new ConfigurationItem( key, value );
                m_configurationProperties.add( configurationItem );
            }

            // By default sort key in ascending manner
            ISortState sortState = getSortState();
            sortState.setPropertySortOrder( ConfigurationItem.PROPERTY_KEY, ISortState.ASCENDING );
        }

        public IModel model( Object object )
        {
            return new Model( (Serializable) object );
        }

        public int size()
        {
            return m_configurationProperties.size();
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

                    String value1 = o1.getPropertyValue( property );
                    String value2 = o2.getPropertyValue( property );

                    return value1.compareTo( value2 );
                }
            }
            );
        }
    }

}
