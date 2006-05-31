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
package org.ops4j.pax.cm.agent.wicket.overview;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.cm.agent.WicketApplicationConstant;
import org.ops4j.pax.wicket.service.DefaultPageContainer;
import wicket.Component;
import wicket.Localizer;
import wicket.PageParameters;
import wicket.extensions.ajax.markup.html.tabs.AjaxTabbedPanel;
import wicket.extensions.markup.html.tabs.AbstractTab;
import wicket.extensions.markup.html.tabs.ITab;
import wicket.markup.html.WebPage;
import wicket.markup.html.basic.Label;
import wicket.markup.html.panel.Panel;
import wicket.model.Model;

/**
 * @author Edward Yakop
 * @since 0.1.0
 */
public final class OverviewPage extends WebPage
{

    private static final Logger m_logger = Logger.getLogger( OverviewPage.class );
    private static final String WICKET_ID_MENU = "menu";

    /**
     * Construct an instance of {@code OverviewPage} with the specified container.
     *
     * @param container The page container to be used to retrieves overview page component. This argument must not be
     *                  {@code null}.
     *
     * @throws IllegalArgumentException Thrown if the specified {@code container} is {@code null}.
     * @since 0.1.0
     */
    public OverviewPage( DefaultPageContainer container, PageParameters parameters )
    {
        NullArgumentException.validateNotNull( container, "container" );
        NullArgumentException.validateNotNull( parameters, "parameters" );

        final List<Component> menus = container.createComponents( WicketApplicationConstant.Overview.COMPONENT_MENU_TAB );
        List<ITab> tabs = new ArrayList<ITab>();

        String tabIDToSelect = parameters.getString( WicketApplicationConstant.Overview.PAGE_PARAM_TAB_ID, "" );
        int selectedTab = 0;

        int i = 0;
        for( final Component menu : menus )
        {
            OverviewTabItem tabPanel = (OverviewTabItem) menu;
            String tabID = tabPanel.getOverviewTabItemIdentifier();
            if( tabIDToSelect.equals( tabID ) )
            {
                selectedTab = i;
            }
            else
            {
                i++;
            }

            Localizer menuLocalizer = menu.getLocalizer();
            String tabLabel = menuLocalizer.getString( OverviewTabItem.LOCALE_TAB_ITEM_LABEL, menu, tabID );
            tabs.add( new AbstractTab( new Model( tabLabel ) )
            {
                public Panel getPanel( String panelId )
                {
                    Panel panel = new OverviewTabPanel( panelId );
                    panel.add( menu );
                    return panel;
                }
            }
            );

        }

        if( tabs.isEmpty() )
        {
            if( m_logger.isDebugEnabled() )
            {
                m_logger.debug( "No menu is installed" );
            }
            add( new Label( WICKET_ID_MENU, "No Configuration Admin menu installed yet." ) );
        }
        else
        {
            if( m_logger.isDebugEnabled() )
            {
                m_logger.debug( tabs.size() + " menu items are installed." );
            }

            AjaxTabbedPanel tabPanel = new AjaxTabbedPanel( WICKET_ID_MENU, tabs );
            tabPanel.setSelectedTab( selectedTab );
            add( tabPanel );
        }
    }
}
