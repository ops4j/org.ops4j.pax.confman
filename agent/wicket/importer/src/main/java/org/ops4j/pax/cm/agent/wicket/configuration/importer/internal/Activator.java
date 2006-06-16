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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ops4j.pax.cm.agent.wicket.WicketApplicationConstant;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public final class Activator implements BundleActivator
{

    private static final Log m_logger = LogFactory.getLog( Activator.class );

    private ServiceRegistration m_serviceRegistration;

    public void start( BundleContext context )
        throws Exception
    {
        LogFactory.setBundleContext( context );

        if( m_logger.isDebugEnabled() )
        {
            m_logger.debug( "[" + Activator.class.getName() + "] is starting." );
        }

        String destinationId = WicketApplicationConstant.Overview.DESTINATION_ID_MENU_TAB;
        String applicationName = WicketApplicationConstant.APPLICATION_NAME;

        ConfigurationImporterContent cnt = new ConfigurationImporterContent( context, applicationName );
        cnt.setDestinationId( destinationId );
        m_serviceRegistration = cnt.register();
    }

    public void stop( BundleContext context )
        throws Exception
    {
        if( m_logger.isDebugEnabled() )
        {
            m_logger.debug( "[" + Activator.class.getName() + "] is stopping." );
        }
        m_serviceRegistration.unregister();
    }
}
