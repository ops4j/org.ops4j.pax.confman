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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.cm.agent.ApplicationConstant;
import org.ops4j.pax.wicket.service.DefaultContentContainer;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import wicket.Component;

/**
 * @author Edward Yakop
 * @since 0.1.0
 */
public final class ConfigurationBrowserPanelContent extends DefaultContentContainer
{

    private static final Log m_logger = LogFactory.getLog( ConfigurationBrowserPanelContent.class );
    private static final String CONTAINMENT_ID = "panel:configurationPanel";

    private final BundleContext m_bundleContext;

    /**
     * @since 0.1.0
     */
    public ConfigurationBrowserPanelContent( BundleContext bundleContext, String destinationId )
    {
        super( bundleContext, ApplicationConstant.APPLICATION_NAME, CONTAINMENT_ID, destinationId );

        NullArgumentException.validateNotNull( bundleContext, "bundleContext" );
        m_bundleContext = bundleContext;
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

        return new ConfigurationBrowserPanel( id, configurations );
    }
}
