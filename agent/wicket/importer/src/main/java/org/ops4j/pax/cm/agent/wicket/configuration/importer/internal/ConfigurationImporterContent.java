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
package org.ops4j.pax.cm.agent.wicket.configuration.importer.internal;

import java.util.Locale;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ops4j.pax.cm.agent.wicket.overview.DefaultOverviewTab;
import org.ops4j.pax.cm.agent.wicket.overview.OverviewTab;
import org.ops4j.pax.cm.agent.wicket.overview.OverviewTabContent;
import org.ops4j.pax.wicket.service.DefaultContent;
import org.osgi.framework.BundleContext;
import wicket.Component;
import wicket.markup.html.panel.Panel;
import wicket.model.Model;

/**
 * {@code ConfigurationImporterContent} provides content for {@code OverviewPage}.
 *
 * @author Edward Yakop
 * @since 0.1.0
 */
final class ConfigurationImporterContent extends DefaultContent
    implements OverviewTabContent
{

    private static final String CONTENT_ID = "panel:configurationImporter";
    private static final Log m_logger = LogFactory.getLog( ConfigurationImporterContent.class );

    /**
     * Construct an instance of {@code ConfigurationImporterContent} with the specified arguments.
     *
     * @param bundleContext   The bundle context. This argument must not be {@code null}.
     * @param applicationName The application name that this content need to be registered. This argument must not be
     *                        {@code null}.
     *
     * @since 0.1.0
     */
    ConfigurationImporterContent( BundleContext bundleContext, String applicationName )
        throws IllegalArgumentException
    {
        super( bundleContext, CONTENT_ID, applicationName );
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
        Model title = new Model( "Importer" );
        String tabItemIdentifier = "Importer";
        Panel panel = (Panel) createComponent( DefaultOverviewTab.WICKET_ID_PANEL );

        return new DefaultOverviewTab( title, tabItemIdentifier, panel );
    }

    protected Component createComponent( String id )
    {
        if( m_logger.isDebugEnabled() )
        {
            m_logger.debug( "Creating ConfigurationImporterPanel with id [" + id + "]" );
        }

        return new ConfigurationImporterPanel( id );
    }

}
