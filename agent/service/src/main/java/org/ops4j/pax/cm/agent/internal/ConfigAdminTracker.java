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
package org.ops4j.pax.cm.agent.internal;

import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.wicket.service.PaxWicketApplicationFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @since 0.1.0
 */
public class ConfigAdminTracker extends ServiceTracker
{
    private BundleContext m_bundleContext;
    private PaxWicketApplicationFactory m_application;
    private ServiceRegistration m_serviceRegistration;

    public ConfigAdminTracker( BundleContext bundleContext, PaxWicketApplicationFactory application )
    {
        super( bundleContext, ConfigurationAdmin.class.getName(), null );

        NullArgumentException.validateNotNull( bundleContext, "bundleContext" );
        m_bundleContext = bundleContext;
        m_application = application;
    }

    public Object addingService( ServiceReference serviceReference )
    {
        m_serviceRegistration = m_application.register();

        return serviceReference;
    }

    public synchronized void close()
    {
        super.close();

        if( m_serviceRegistration != null )
        {
            m_serviceRegistration.unregister();
        }

        m_application.dispose();
    }

    public void removedService( ServiceReference serviceReference, Object object )
    {
        if( object == null )
        {
            return;
        }

        m_serviceRegistration.unregister();
        m_serviceRegistration = null;
    }
}
