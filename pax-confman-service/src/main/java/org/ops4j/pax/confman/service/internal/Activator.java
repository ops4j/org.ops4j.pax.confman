/*
 * Copyright 2007 Alin Dreghiciu.
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
package org.ops4j.pax.confman.service.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.ops4j.pax.swissbox.tracker.ReplaceableService;
import org.ops4j.pax.confman.service.ConfigurationManager;

/**
 * ConfMan Service Activator.
 *
 * @author Alin Dreghiciu
 * @since October 14, 2007
 */
public final class Activator implements BundleActivator
{

    /**
     * Logger.
     */
    private static final Log LOGGER = LogFactory.getLog( Activator.class );
    private ReplaceableService<ConfigurationAdmin> m_configAdminRS;
    private ServiceRegistration m_configManagerSR;

    /**
     * @see BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start( final BundleContext context )
        throws Exception
    {
        LOGGER.debug( "Starting [" + context.getBundle().getSymbolicName() + "]..." );
        m_configAdminRS = new ReplaceableService<ConfigurationAdmin>( context, ConfigurationAdmin.class );
        m_configAdminRS.start();
        m_configManagerSR =
            context.registerService( ConfigurationManager.class.getName(), new ConfigurationManagerImpl(), null );
    }

    /**
     * @see BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop( final BundleContext context )
        throws Exception
    {
        LOGGER.debug( "Stopping [" + context.getBundle().getSymbolicName() + "]..." );
        if( m_configAdminRS != null )
        {
            m_configAdminRS.stop();
            m_configAdminRS = null;
        }
        if (m_configManagerSR != null)
        {
            m_configManagerSR.unregister();
            m_configManagerSR = null;
        }
    }
}
