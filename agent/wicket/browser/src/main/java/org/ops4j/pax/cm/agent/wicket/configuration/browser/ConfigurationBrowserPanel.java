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

import org.ops4j.lang.NullArgumentException;
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
 * <b>Resource Bundle:</b>
 * <ul>
 * <li>The column label of the table can be localized by creating a file with name
 * <b>ConfigurationBrowserPanel_&lt;localeName&gt;.properties</b>; and</li>
 * <li>The possible entries keys are every static properties value of <b>LOCALE_KEY_*</b>.</li>
 * </ul>
 *
 * @author Edward Yakop
 * @since 0.1.0
 */
final class ConfigurationBrowserPanel extends Panel
{
    private static final String LOCALE_SELECT_LINK_LABEL = "selectLinkLabel";
    private static final String LOCALE_PID_COLUMN_LABEL = "pidColumnLabel";
    private static final String LOCALE_FACTORY_PID_COLUMN_LABEL = "factoryPidColumnLabel";
    private static final String LOCALE_BUNDLE_LOCATION_COLUMN_LABEL = "bundleLocationColumnLabel";
    private static final String LOCALE_TAB_PANEL_LABEL_VALUE = "tabPanelLabel";

    private static final String WICKET_ID_EDITOR = "editor";

    private static final String WICKET_ID_NAVIGATOR = "navigator";

    private static final String WICKET_ID_HEADER_ACTION = "action";

    private static final String WICKET_ID_SORT_HEADER_PID = "orderByPID";
    private static final String WICKET_ID_HEADER_PID = "columnHeaderPID";

    private static final String WICKET_ID_SORT_HEADER_BUNDLE_LOCATION = "orderByBundleLocation";
    private static final String WICKET_ID_HEADER_BUNDLE_LOCATION = "columnHeaderBundleLocation";

    private static final String WICKET_ID_SORT_HEADER_FACTORY_PID = "orderByFactoryPID";
    private static final String WICKET_ID_HEADER_FACTORY_PID = "columnHeaderFactoryPID";

    private static final String WICKET_ID_TABLE_DATA = "sorting";
    private static final String WICKET_SELECT_LINK = "selectLink";
    private static final String WICKET_ID_LINK_LABEL = "selectLinkLabel";
    private static final String WICKET_ID_DATA_PID_LABEL = "pidLabel";
    private static final String WICKET_ID_FACTORY_PID = "factoryPid";
    private static final String WICKET_ID_DATA_BUNDLE_LOCATION = "bundleLocation";

    private static final int NUMBER_OF_ROWS_DISPLAYED = 20;
    private final MiniEditConfigurationPanel m_miniEditConfigurationPanel;
    private final ConfigurationDataProvider m_confDataProvider;

    ConfigurationBrowserPanel( String panelId, ConfigurationDataProvider confDataProvider )
    {
        super( panelId );
        NullArgumentException.validateNotNull( confDataProvider, "confDataProvider" );
        m_confDataProvider = confDataProvider;

        ConfigurationDataView dataView = new ConfigurationDataView( WICKET_ID_TABLE_DATA, confDataProvider );
        dataView.setItemsPerPage( NUMBER_OF_ROWS_DISPLAYED );
        add( dataView );

        Localizer localizer = getLocalizer();
        Label actionColumnHeader = new Label( WICKET_ID_HEADER_ACTION, "actions" );
        add( actionColumnHeader );

        PaxOrderByBorder pidColumnHeader =
            new PaxOrderByBorder( WICKET_ID_SORT_HEADER_PID, "pid", confDataProvider, dataView );
        add( pidColumnHeader );
        String pidColumnHeaderLabel = localizer.getString( LOCALE_PID_COLUMN_LABEL, this, "PID" );
        pidColumnHeader.add( new Label( WICKET_ID_HEADER_PID, pidColumnHeaderLabel ) );

        PaxOrderByBorder factoryPidColumnHeader =
            new PaxOrderByBorder( WICKET_ID_SORT_HEADER_FACTORY_PID, "factoryPid", confDataProvider, dataView );
        add( factoryPidColumnHeader );
        String factoryPidColumnHeaderLabel =
            localizer.getString( LOCALE_FACTORY_PID_COLUMN_LABEL, this, "Factory PID" );
        factoryPidColumnHeader.add( new Label( WICKET_ID_HEADER_FACTORY_PID, factoryPidColumnHeaderLabel ) );

        PaxOrderByBorder bundleLocationColumnHeader = new PaxOrderByBorder(
            WICKET_ID_SORT_HEADER_BUNDLE_LOCATION, WICKET_ID_DATA_BUNDLE_LOCATION, confDataProvider, dataView
        );
        add( bundleLocationColumnHeader );
        String bundleLocationClmnHeaderLabel =
            localizer.getString( LOCALE_BUNDLE_LOCATION_COLUMN_LABEL, this, "bundle location" );
        bundleLocationColumnHeader.add( new Label( WICKET_ID_HEADER_BUNDLE_LOCATION, bundleLocationClmnHeaderLabel ) );

        String tabPanelLabel = localizer.getString( LOCALE_TAB_PANEL_LABEL_VALUE, this, "browse" );
        Model tabPanelLabelModel = new Model( tabPanelLabel );
        setModel( tabPanelLabelModel );

        PagingNavigator pagingNavigator = new PagingNavigator( WICKET_ID_NAVIGATOR, dataView );
        add( pagingNavigator );

        m_miniEditConfigurationPanel = new MiniEditConfigurationPanel( WICKET_ID_EDITOR, confDataProvider );
        add( m_miniEditConfigurationPanel );
    }

    ConfigurationDataProvider getConfDataProvider()
    {
        return m_confDataProvider;
    }

    private final class ConfigurationDataView extends DataView
    {
        ConfigurationDataView( String id, ConfigurationDataProvider confDataProvider )
        {
            super( id, confDataProvider );
        }

        protected void populateItem( final Item item )
        {
            final PaxConfiguration configuration =
                (PaxConfiguration) item.getModelObject();

            String pid = configuration.getPid();
            String bundleLocation = configuration.getBundleLocation();

            Link selectLink = new Link( WICKET_SELECT_LINK )
            {
                public void onClick()
                {
                    m_confDataProvider.selectPaxConfiguration( configuration );
                }
            };
            item.add( selectLink );

            Localizer localizer = getLocalizer();
            String linkLabelString =
                localizer.getString( LOCALE_SELECT_LINK_LABEL, ConfigurationBrowserPanel.this, "select" );
            Label linkLabel = new Label( WICKET_ID_LINK_LABEL, linkLabelString );
            selectLink.add( linkLabel );

            Label pidLabel = new Label( WICKET_ID_DATA_PID_LABEL, pid );
            item.add( pidLabel );

            String factoryPidString = configuration.getFactoryPid();
            Label factoryPid = new Label( WICKET_ID_FACTORY_PID, factoryPidString );
            item.add( factoryPid );

            Label locationLabel = new Label( WICKET_ID_DATA_BUNDLE_LOCATION, bundleLocation );
            item.add( locationLabel );

            PaxCssAttributeModel cssAttributeModel = new PaxCssAttributeModel( item );
            AttributeModifier highlightBehaviour = new AttributeModifier( "class", true, cssAttributeModel );
            item.add( highlightBehaviour );
        }
    }
}