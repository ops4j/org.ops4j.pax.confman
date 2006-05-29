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
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Iterator;
import org.ops4j.lang.NullArgumentException;
import wicket.extensions.markup.html.repeater.data.table.IColumn;
import wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
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

    private static final IColumn[] m_columns;

    private ArrayList<ConfigurationItem> m_configurations;

    static
    {
        PropertyColumn keyPropertyColumn = new PropertyColumn( new Model( "Key" ), "key", "key" );
        PropertyColumn valuePropertyColumn = new PropertyColumn( new Model( "Value" ), "value", "value" );
        m_columns = new IColumn[]{ keyPropertyColumn, valuePropertyColumn };
    }

    ConfigurationItemDataProvider( Dictionary<String, String> properties )
    {
        NullArgumentException.validateNotNull( properties, "properties" );

        Enumeration<String> keys = properties.keys();
        m_configurations = new ArrayList<ConfigurationItem>( properties.size() );
        while( keys.hasMoreElements() )
        {
            String key = keys.nextElement();
            String value = properties.get( key );

            ConfigurationItem item = new ConfigurationItem( key, value );
            m_configurations.add( item );
        }
    }

    public IColumn[] getColumns()
    {
        return m_columns;
    }

    /**
     * Gets an iterator for the subset of total data
     *
     * @param first first row of data
     * @param count minumum number of elements to retrieve
     *
     * @return iterator capable of iterating over {first, first+count} items
     */
    public Iterator iterator( int first, int count )
    {
        return m_configurations.listIterator( first );
    }

    /**
     * Callback used by the consumer of this data provider to wrap objects
     * retrieved from {@link #iterator(int, int)} with a model (usually a
     * detachable one).
     *
     * @param object the object that needs to be wrapped
     *
     * @return the model representation of the object
     */
    public IModel model( Object object )
    {
        return new Model( (Serializable) object );
    }

    /**
     * Gets total number of items in the collection represented by the
     * DataProvider
     *
     * @return total item count
     */
    public int size()
    {
        return m_configurations.size();
    }
}
