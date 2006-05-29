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
import wicket.AttributeModifier;
import wicket.Component;
import wicket.extensions.markup.html.repeater.data.table.DataTable;
import wicket.extensions.markup.html.repeater.refreshing.Item;
import wicket.markup.html.form.TextField;
import wicket.model.AbstractReadOnlyModel;
import wicket.model.PropertyModel;

/**
 * @author Edward Yakop
 * @since 0.1.0
 */
final class ConfigurationItemDataTable extends DataTable
    implements Serializable
{
    private static final long serialVersionUID = 1L;

    private static final String PROPERTY_NAME_KEY = "key";
    private static final String WICKET_ID_KEY = PROPERTY_NAME_KEY;
    private static final String PROPERTY_NAME_VALUE = "value";
    private static final String WICKET_ID_VALUE = PROPERTY_NAME_VALUE;
    private static final String STYLESHEET_EVEN = "even";
    private static final String STYLE_SHEET_ODD = "odd";
    private static final String STYLESHEET_CLASS = "class";

    /**
     * @param id           component id
     * @param dataProvider data provider
     * @param itemsPerPage items per page
     */
    public ConfigurationItemDataTable( String id, ConfigurationItemDataProvider dataProvider, int itemsPerPage )
    {
        super( id, dataProvider.getColumns(), dataProvider, itemsPerPage );
    }

    /**
     * Populate the given Item container.
     * <p>
     * <b>be carefull</b> to add any components to the item and not the view
     * itself. So, don't do:
     *
     * <pre>
     * add(new Label(&quot;foo&quot;, &quot;bar&quot;));
     * </pre>
     *
     * but:
     *
     * <pre>
     * item.add(new Label(&quot;foo&quot;, &quot;bar&quot;));
     * </pre>
     *
     * </p>
     *
     * @param item The item to populate
     */
    protected void populateItem( final Item item )
    {
        ConfigurationItem configItem = (ConfigurationItem) item.getModelObject();

        PropertyModel keyPropertyModel = new PropertyModel( configItem, PROPERTY_NAME_KEY );
        TextField keyTextField = new TextField( WICKET_ID_KEY, keyPropertyModel );
        item.add( keyTextField );

        PropertyModel valuePropertyModel = new PropertyModel( configItem, PROPERTY_NAME_VALUE );
        TextField valueTextField = new TextField( WICKET_ID_VALUE, valuePropertyModel );
        item.add( valueTextField );

        AbstractReadOnlyModel highlightModel = new AbstractReadOnlyModel()
        {
            public Object getObject( Component component )
            {
                return ( item.getIndex() % 2 == 1 ) ? STYLESHEET_EVEN : STYLE_SHEET_ODD;
            }
        };
        item.add( new AttributeModifier( STYLESHEET_CLASS, true, highlightModel ) );
    }
}
