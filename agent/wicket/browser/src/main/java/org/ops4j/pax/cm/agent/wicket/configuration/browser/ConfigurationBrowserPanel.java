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
import org.ops4j.pax.cm.agent.ConfigurationConstant;
import org.ops4j.pax.cm.agent.configuration.PaxConfiguration;
import org.ops4j.pax.cm.agent.wicket.configuration.edit.EditConfigurationPage;
import org.osgi.service.cm.Configuration;
import wicket.AttributeModifier;
import wicket.Component;
import wicket.Localizer;
import wicket.PageParameters;
import wicket.extensions.markup.html.repeater.data.DataView;
import wicket.extensions.markup.html.repeater.data.sort.ISortStateLocator;
import wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import wicket.extensions.markup.html.repeater.refreshing.Item;
import wicket.markup.html.basic.Label;
import wicket.markup.html.link.BookmarkablePageLink;
import wicket.markup.html.navigation.paging.PagingNavigator;
import wicket.markup.html.panel.Panel;
import wicket.model.AbstractReadOnlyModel;
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

    private static final String LOCALE_KEY_PID_COLUMN_LABEL = "pidColumnLabel";
    private static final String LOCALE_KEY_BUNDLE_LOCATION_COLUMN_LABEL = "bundleLocationColumnLabel";
    private static final String LOCALE_KEY_TAB_PANEL_LABEL_VALUE = "tabPanelLabel";

    private static final String WICKET_ID_NAVIGATOR = "navigator";

    private static final String WICKET_ID_SORT_HEADER_PID = "orderByPID";
    private static final String WICKET_ID_HEADER_PID = "columnHeaderPID";

    private static final String WICKET_ID_SORT_HEADER_BUNDLE_LOCATION = "orderByBundleLocation";
    private static final String WICKET_ID_HEADER_BUNDLE_LOCATION = "columnHeaderBundleLocation";

    private static final String WICKET_ID_TABLE_DATA = "sorting";
    private static final String WICKET_ID_DATA_PID_LINK = "pidLink";
    private static final String WICKET_ID_DATA_PID_LABEL = "pidLabel";
    private static final String WICKET_ID_DATA_BUNDLE_LOCATION = "bundleLocation";

    private static final int NUMBER_OF_ROWS_DISPLAYED = 20;

    ConfigurationBrowserPanel( String panelId, Configuration[] configurations )
    {
        super( panelId );
        NullArgumentException.validateNotNull( configurations, "configurations" );

        ConfigurationDataProvider confDataProvider = new ConfigurationDataProvider( configurations );

        DataView dataView = new DataView( WICKET_ID_TABLE_DATA, confDataProvider )
        {
            protected void populateItem( final Item item )
            {
                PaxConfiguration configuration =
                    (PaxConfiguration) item.getModelObject();

                String pid = configuration.getPid();
                String bundleLocation = configuration.getBundleLocation();

                PageParameters pageParameters = new PageParameters();
                pageParameters.add( ConfigurationConstant.PARAM_KEY_PID, pid );
                pageParameters.add( ConfigurationConstant.PARAM_KEY_LOCATION, bundleLocation );
                BookmarkablePageLink pidLink =
                    new BookmarkablePageLink( WICKET_ID_DATA_PID_LINK, EditConfigurationPage.class, pageParameters );
                item.add( pidLink );

                Label pidLabel = new Label( WICKET_ID_DATA_PID_LABEL, pid );
                pidLink.add( pidLabel );

                if( bundleLocation == null )
                {
                    bundleLocation = "";
                }

                Label locationLabel = new Label( WICKET_ID_DATA_BUNDLE_LOCATION, bundleLocation );
                item.add( locationLabel );

                PaxReplaceAttributeModel replaceModel = new PaxReplaceAttributeModel( item );
                AttributeModifier highlightBehaviour = new AttributeModifier( "class", true, replaceModel );
                item.add( highlightBehaviour );
            }
        };
        dataView.setItemsPerPage( NUMBER_OF_ROWS_DISPLAYED );
        add( dataView );

        Localizer localizer = getLocalizer();

        PaxOrderByBorder pidColumnHeader =
            new PaxOrderByBorder( WICKET_ID_SORT_HEADER_PID, "pid", confDataProvider, dataView );
        add( pidColumnHeader );
        String pidColumnHeaderLabel = localizer.getString( LOCALE_KEY_PID_COLUMN_LABEL, this, "pid" );
        pidColumnHeader.add( new Label( WICKET_ID_HEADER_PID, pidColumnHeaderLabel ) );

        PaxOrderByBorder bundleLocationColumnHeader = new PaxOrderByBorder(
            WICKET_ID_SORT_HEADER_BUNDLE_LOCATION, WICKET_ID_DATA_BUNDLE_LOCATION, confDataProvider, dataView
        );
        add( bundleLocationColumnHeader );
        String bundleLocationClmnHeaderLabel =
            localizer.getString( LOCALE_KEY_BUNDLE_LOCATION_COLUMN_LABEL, this, "bundle location" );
        bundleLocationColumnHeader.add( new Label( WICKET_ID_HEADER_BUNDLE_LOCATION, bundleLocationClmnHeaderLabel ) );

        String tabPanelLabel = localizer.getString( LOCALE_KEY_TAB_PANEL_LABEL_VALUE, this, "browse" );
        Model tabPanelLabelModel = new Model( tabPanelLabel );
        setModel( tabPanelLabelModel );

        add( new PagingNavigator( WICKET_ID_NAVIGATOR, dataView ) );
    }

    private static final class PaxOrderByBorder extends OrderByBorder
    {

        private final DataView m_dataView;

        private PaxOrderByBorder( String id, String property, ISortStateLocator stateLocator, DataView dataView )
        {
            super( id, property, stateLocator );

            NullArgumentException.validateNotNull( dataView, "dataView" );
            m_dataView = dataView;
        }

        protected void onSortChanged()
        {
            m_dataView.setCurrentPage( 0 );
        }
    }

    private static final class PaxReplaceAttributeModel extends AbstractReadOnlyModel
    {

        private static final String CSS_CLASS_EVEN = "even";
        private static final String CSS_CLASS_ODD = "odd";

        private final Item m_item;

        private PaxReplaceAttributeModel( Item item )
        {
            NullArgumentException.validateNotNull( item, "item" );
            m_item = item;
        }

        public Object getObject( Component component )
        {
            return ( m_item.getIndex() % 2 == 1 ) ? CSS_CLASS_EVEN : CSS_CLASS_ODD;
        }
    }
}
