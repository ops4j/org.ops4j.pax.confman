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
package org.ops4j.pax.cm.scanner.registry.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.ops4j.pax.cm.api.Configurer;
import org.ops4j.pax.cm.common.internal.processor.CommandProcessor;
import org.ops4j.pax.cm.scanner.core.internal.ConfigurerSetter;
import org.ops4j.pax.cm.scanner.core.internal.ConfigurerTracker;

/**
 * Activator for RegistryScanner.
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 12, 2008
 */
public class Activator
    implements BundleActivator
{

    /**
     * Logger.
     */
    private static final Log LOG = LogFactory.getLog( Activator.class );
    /**
     * Configurer tracker.
     */
    private ConfigurerTracker m_configurerTracker;
    /**
     * Configuration Queue.
     */
    private CommandProcessor<Configurer> m_processor;
    /**
     * Registry scanner.
     */
    private RegistryScanner m_registryScanner;

    /**
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start( final BundleContext bundleContext )
    {
        LOG.debug( "Starting OPS4J Pax ConfMan service registry tracker" );

        m_processor = new CommandProcessor<Configurer>();
        m_configurerTracker = new ConfigurerTracker(
            bundleContext,
            new ConfigurerSetter()
            {
                /**
                 * Pass configurer to commands processor.
                 * @param configurer new configurer
                 */
                public void setConfigurer( final Configurer configurer )
                {
                    m_processor.setTargetService( configurer );
                }

            }
        );
        m_registryScanner = new RegistryScanner( bundleContext, m_processor );

        m_configurerTracker.start();
        m_processor.start();
        m_registryScanner.start();
    }

    /**
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop( final BundleContext bundleContext )
    {
        LOG.debug( "Stopping OPS4J Pax ConfMan service registry tracker" );

        if( m_registryScanner != null )
        {
            m_registryScanner.stop();
            m_registryScanner = null;
        }
        if( m_configurerTracker != null )
        {
            m_configurerTracker.stop();
            m_configurerTracker = null;
        }
        if( m_processor != null )
        {
            m_processor.stop();
            m_processor = null;
        }
    }

}