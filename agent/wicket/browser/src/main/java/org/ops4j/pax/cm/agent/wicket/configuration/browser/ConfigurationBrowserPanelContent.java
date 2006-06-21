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
import java.util.Locale;
import org.apache.log4j.Logger;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.cm.agent.wicket.WicketApplicationConstant;
import org.ops4j.pax.cm.agent.wicket.overview.DefaultOverviewTab;
import org.ops4j.pax.cm.agent.wicket.overview.OverviewTab;
import org.ops4j.pax.cm.agent.wicket.overview.OverviewTabContent;
import org.ops4j.pax.wicket.service.DefaultContent;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import wicket.Component;
import wicket.model.Model;

/**
 * @author Edward Yakop
 * @since 0.1.0
 */
public final class ConfigurationBrowserPanelContent extends DefaultContent
    implements OverviewTabContent
{
    private static final Logger m_logger = Logger.getLogger( ConfigurationBrowserPanelContent.class );

    private static final String CONTENT_ID = "panel:configurationPanel";

    private static final Configuration[] NO_CONFIGURATIONS = new Configuration[0];

    private static BundleContext m_bundleContext;

    /**
     * @since 0.1.0
     */
    public ConfigurationBrowserPanelContent( BundleContext bundleContext, String applicationName )
    {
        super( bundleContext, CONTENT_ID, applicationName );

        NullArgumentException.validateNotNull( bundleContext, "bundleContext" );
        m_bundleContext = bundleContext;
    }

    /**
     * Create tab with {@code locale} as specified.
     *
     * @param locale The locale of the tab.
     *
     * @return Returns tab with the specified locale.
     *
     * @since 0.1.0
     */
    public OverviewTab createTab( Locale locale )
    {
        Model title = new Model( "Browser" );
        String tabId = WicketApplicationConstant.Overview.MENU_TAB_ID_BROWSER;
        ConfigurationBrowserPanel panel =
            (ConfigurationBrowserPanel) createComponent( DefaultOverviewTab.WICKET_ID_PANEL );

        DefaultOverviewTab tab = new DefaultOverviewTab( title, tabId, panel );
        OverviewTabListener listener = new OverviewTabListener( panel );
        tab.addListener( listener );

        return tab;
    }

    protected Component createComponent( String id )
    {
        Configuration[] configurations = getAvailableConfigurations();
        ConfigurationDataProvider confDataProvider = new ConfigurationDataProvider( configurations );
        return new ConfigurationBrowserPanel( id, confDataProvider );
    }

    /**
     * Returns the available configurations. Returns an empty array if there is no available configurations.
     *
     * @return Returns the available configurations.
     *
     * @since 0.1.0
     */
    private static Configuration[] getAvailableConfigurations()
    {
        String configurationAdminClassName = ConfigurationAdmin.class.getName();
        ServiceReference configAdminSerRef = m_bundleContext.getServiceReference( configurationAdminClassName );

        Configuration[] configurations = null;
        if( configAdminSerRef != null )
        {
            ConfigurationAdmin configAdmin = (ConfigurationAdmin) m_bundleContext.getService( configAdminSerRef );
            try
            {
                configurations = configAdmin.listConfigurations( null );
            } catch( IOException e )
            {
                m_logger.error( "Configurations failed to be retrieved.", e );
            } catch( InvalidSyntaxException e )
            {
                m_logger.error( "Configurations failed to be retrieved.", e );
            }
            finally
            {
                m_bundleContext.ungetService( configAdminSerRef );
            }
        }

        if( configurations == null )
        {
            configurations = NO_CONFIGURATIONS;
        }

        return configurations;
    }

    /**
     * {@code OverviewTabListener} handles of reseting {@code Configuration}.
     *
     * @author Edward Yakop
     * @since 0.1.0
     */
    private static final class OverviewTabListener extends DefaultOverviewTab.AbstractOverviewTabListener
    {
        private ConfigurationBrowserPanel m_panel;

        private OverviewTabListener( ConfigurationBrowserPanel panel )
        {
            NullArgumentException.validateNotNull( panel, "panel" );

            m_panel = panel;
        }

        /**
         * This method is called prior {@code DefaultOverviewTab#getPanel()} is called. This method can be used to
         * prepares the {@code panel} that is about to be displayed.
         *
         * @param tab The overview tab that received this event. This argument must not be {@code null}.
         *
         * @since 0.1.0
         */
        public void preGetPanel( DefaultOverviewTab tab )
        {
            Configuration[] availableConfigurations = getAvailableConfigurations();

            ConfigurationDataProvider confDataProvider = m_panel.getConfDataProvider();
            confDataProvider.resetData( availableConfigurations );
        }
    }
}
