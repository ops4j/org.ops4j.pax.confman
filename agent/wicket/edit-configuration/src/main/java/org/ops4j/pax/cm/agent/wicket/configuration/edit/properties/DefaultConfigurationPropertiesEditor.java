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

import org.ops4j.pax.cm.agent.configuration.PaxConfiguration;
import org.ops4j.pax.cm.agent.wicket.PaxCssAttributeModel;
import org.ops4j.pax.cm.agent.wicket.PaxOrderByBorder;
import wicket.AttributeModifier;
import wicket.Localizer;
import wicket.extensions.markup.html.repeater.data.DataView;
import wicket.extensions.markup.html.repeater.refreshing.Item;
import wicket.markup.html.basic.Label;
import wicket.markup.html.link.Link;
import wicket.markup.html.navigation.paging.PagingNavigator;
import wicket.markup.html.panel.Panel;
import wicket.model.Model;

/**
 * @author Edward yakop
 * @since 0.1.0
 */
public final class DefaultConfigurationPropertiesEditor extends Panel
{

    private final static String LOCALE_COLUMN_HEADER_KEY = "keyColumnHeader";
    private final static String LOCALE_COLUMN_HEADER_VALUE = "valueColumnHeader";
    private static final String LOCALE_TAB_PANEL_LABEL_VALUE = "browse";

    private static final String WICKET_ID_TABLE = "table";

    private static final String WICKET_ID_HEADER_ACTION = "action";
    private static final String WICKET_ID_SORT_HEADER_KEY = "orderByKey";
    private static final String WICKET_ID_HEADER_KEY = "columnHeaderKey";
    private static final String WICKET_ID_SORT_HEADER_VALUE = "orderByValue";
    private static final String WICKET_ID_HEADER_VALUE = "columnHeaderValue";
    private static final String WICKET_ID_NAVIGATOR = "navigator";

    private final PaxConfiguration m_configuration;

    public DefaultConfigurationPropertiesEditor( String id, PaxConfiguration configuration )
    {
        super( id );

        if( configuration != null )
        {
            m_configuration = configuration;
        }
        else
        {
            m_configuration = new PaxConfiguration();
        }

        ConfigurationItemDataProvider dataProvider = new ConfigurationItemDataProvider( m_configuration );
        ConfigurationDataView dataView = new ConfigurationDataView( WICKET_ID_TABLE, dataProvider );
        dataView.setItemsPerPage( 20 );
        add( dataView );

        Localizer localizer = getLocalizer();

        Label actionColumnHeader = new Label( WICKET_ID_HEADER_ACTION, "actions" );
        add( actionColumnHeader );

        PaxOrderByBorder keyColumnHeader =
            new PaxOrderByBorder( WICKET_ID_SORT_HEADER_KEY, "key", dataProvider, dataView );
        add( keyColumnHeader );
        String keyColumnHeaderLabel = localizer.getString( LOCALE_COLUMN_HEADER_KEY, this, "Key" );
        keyColumnHeader.add( new Label( WICKET_ID_HEADER_KEY, keyColumnHeaderLabel ) );

        PaxOrderByBorder valueColumnHeader =
            new PaxOrderByBorder( WICKET_ID_SORT_HEADER_VALUE, "value", dataProvider, dataView );
        add( valueColumnHeader );
        String valueColumnHeaderLabel =
            localizer.getString( LOCALE_COLUMN_HEADER_VALUE, this, "Value" );
        valueColumnHeader.add( new Label( WICKET_ID_HEADER_VALUE, valueColumnHeaderLabel ) );

        String tabPanelLabel = localizer.getString( LOCALE_TAB_PANEL_LABEL_VALUE, this, "browse" );
        Model tabPanelLabelModel = new Model( tabPanelLabel );
        setModel( tabPanelLabelModel );

        PagingNavigator pagingNavigator = new PagingNavigator( WICKET_ID_NAVIGATOR, dataView );
        add( pagingNavigator );

    }

    private class ConfigurationDataView extends DataView
    {

        private static final String LOCALE_SELECT_LINK_LABEL = "selectLabel";

        private static final String WICKET_ID_SELECT_LINK = "selectLink";
        private static final String WICKET_ID_LINK_LABEL = "selectLinkLabel";
        private static final String WICKET_ID_DATA_KEY_LABEL = "key";
        private static final String WICKET_ID_VALUE_LABEL = "value";

        private final ConfigurationItemDataProvider m_dataProvider;

        ConfigurationDataView( String id, ConfigurationItemDataProvider dictionaryDataProvider )
        {
            super( id, dictionaryDataProvider );
            m_dataProvider = dictionaryDataProvider;
        }

        protected void populateItem( final Item item )
        {
            final ConfigurationItem configuration = (ConfigurationItem) item.getModelObject();

            String key = configuration.getKey();

            String valueString;
            Object valueObj = configuration.getValue();
            if( valueObj != null )
            {
                valueString = String.valueOf( valueObj );
            }
            else
            {
                valueString = null;
            }

            Link selectLink = new Link( WICKET_ID_SELECT_LINK )
            {
                public void onClick()
                {
                    m_dataProvider.selectProperty( configuration );
                }
            };
            item.add( selectLink );

            Localizer localizer = getLocalizer();
            String linkLabelString =
                localizer.getString( LOCALE_SELECT_LINK_LABEL, DefaultConfigurationPropertiesEditor.this, "select" );
            Label linkLabel = new Label( WICKET_ID_LINK_LABEL, linkLabelString );
            selectLink.add( linkLabel );

            Label keyLabel = new Label( WICKET_ID_DATA_KEY_LABEL, key );
            item.add( keyLabel );

            Label factoryPid = new Label( WICKET_ID_VALUE_LABEL, valueString );
            item.add( factoryPid );

            PaxCssAttributeModel cssAttributeModel = new PaxCssAttributeModel( item );
            AttributeModifier highlightBehaviour = new AttributeModifier( "class", true, cssAttributeModel );

            item.add( highlightBehaviour );
        }
    }
}
