/*
 * Copyright 2008 Alin Dreghiciu.
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
package org.ops4j.pax.cm.scanner.core;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.swissbox.lifecycle.AbstractLifecycle;
import org.ops4j.pax.cm.api.Configurer;

/**
 * Tracks configurer service.
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, January 12, 2008
 */
public class ConfigurerTracker
    extends AbstractLifecycle
{

    /**
     * Configurer service tracker.
     */
    private final ServiceTracker m_configurerTracker;
    /**
     * Configurer setter. Canot be null.
     */
    private final ConfigurerSetter m_configurerSetter;

    /**
     * Creates a configurer tracker.
     *
     * @param bundleContext    bundle context
     * @param configurerSetter a configurer setter
     *
     * @throws NullArgumentException - If bundle context is null
     *                               - If configurer setter is null
     */
    public ConfigurerTracker( final BundleContext bundleContext,
                              final ConfigurerSetter configurerSetter )
    {
        NullArgumentException.validateNotNull( bundleContext, "Bundle context" );
        NullArgumentException.validateNotNull( configurerSetter, "Configurer setter" );

        m_configurerSetter = configurerSetter;
        m_configurerTracker = new ServiceTracker( bundleContext, Configurer.class.getName(), null )
        {
            @Override
            public Object addingService( ServiceReference serviceReference )
            {
                final Configurer configurer = (Configurer) super.addingService( serviceReference );
                m_configurerSetter.setConfigurer( configurer );
                return configurer;
            }

            @Override
            public void removedService( ServiceReference serviceReference, Object service )
            {
                super.removedService( serviceReference, service );
                m_configurerSetter.setConfigurer( null );
            }

        };
    }

    /**
     * Starts tracking of Configurer service.
     */
    protected void onStart()
    {
        m_configurerTracker.open();
    }

    /**
     * Stops tracking of Configurer service.
     */
    protected void onStop()
    {
        m_configurerTracker.close();
    }
    
}
