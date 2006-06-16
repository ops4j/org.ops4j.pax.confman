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
import wicket.markup.html.panel.Panel;
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

    private final BundleContext m_bundleContext;

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
        Panel panel = (Panel) createComponent( DefaultOverviewTab.WICKET_ID_PANEL );
        return new DefaultOverviewTab( title, tabId, panel );
    }

    protected Component createComponent( String id )
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

            if( configurations == null )
            {
                configurations = new Configuration[0];
            }

            m_bundleContext.ungetService( configAdminSerRef );
        }

        if( configurations == null )
        {
            configurations = new Configuration[0];
        }

        ConfigurationDataProvider confDataProvider = new ConfigurationDataProvider( configurations );
        return new ConfigurationBrowserPanel( id, confDataProvider );
    }
}
