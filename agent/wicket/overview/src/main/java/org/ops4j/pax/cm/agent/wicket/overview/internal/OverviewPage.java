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
package org.ops4j.pax.cm.agent.wicket.overview.internal;

import java.util.List;
import java.util.Locale;
import org.apache.log4j.Logger;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.cm.agent.wicket.WicketApplicationConstant;
import org.ops4j.pax.cm.agent.wicket.overview.OverviewTab;
import wicket.Component;
import wicket.PageParameters;
import wicket.extensions.ajax.markup.html.tabs.AjaxTabbedPanel;
import wicket.markup.html.WebPage;
import wicket.markup.html.basic.Label;

/**
 * TODO The current way to install TabItem requires for the tab panel to be instantiated, this could be prevented.
 *
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
    public OverviewPage( OverviewPageContainer container, PageParameters parameters )
    {
        NullArgumentException.validateNotNull( container, "container" );
        NullArgumentException.validateNotNull( parameters, "parameters" );

        Locale locale = getLocale();
        List<OverviewTab> tabs = container.createTab( locale );

        String tabIDToSelect = parameters.getString( WicketApplicationConstant.Overview.PAGE_PARAM_TAB_ID, "" );
        int selectedTab = getSelectedTabIndex( tabs, tabIDToSelect );

        Component child;
        if( tabs.isEmpty() )
        {
            if( m_logger.isDebugEnabled() )
            {
                m_logger.debug( "No menu is installed" );
            }
            child = new Label( WICKET_ID_MENU, "No Configuration Admin menu installed yet." );
        }
        else
        {
            if( m_logger.isDebugEnabled() )
            {
                m_logger.debug( tabs.size() + " menu items are installed." );
            }

            AjaxTabbedPanel tabPanel = new AjaxTabbedPanel( WICKET_ID_MENU, tabs );
            tabPanel.setSelectedTab( selectedTab );
            child = tabPanel;
        }

        add( child );
    }

    private static int getSelectedTabIndex( List<OverviewTab> tabs, String tabIDToSelect )
    {
        int selectedTab = 0;

        int i = 0;
        for( OverviewTab tab : tabs )
        {
            String tabID = tab.getOverviewTabItemIdentifier();

            if( tabIDToSelect.equals( tabID ) )
            {
                selectedTab = i;
            }
            else
            {
                i++;
            }
        }
        return selectedTab;
    }
}
