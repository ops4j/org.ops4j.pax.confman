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
package org.ops4j.pax.cm.scanner.bundle.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.ops4j.pax.cm.api.ConfigurationManager;
import org.ops4j.pax.cm.commons.internal.processor.CommandProcessor;
import org.ops4j.pax.cm.scanner.commons.internal.ConfigurerSetter;
import org.ops4j.pax.cm.scanner.commons.internal.ConfigurerTracker;

/**
 * Activator for bundle scanner.
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 18, 2008
 */
public class Activator
    implements BundleActivator
{

    /**
     * Logger.
     */
    private static final Log LOG = LogFactory.getLog( Activator.class );
    /**
     * ConfigurationManager tracker.
     */
    private ConfigurerTracker m_configurationManagerTracker;
    /**
     * Command processor.
     */
    private CommandProcessor<ConfigurationManager> m_commandsProcessor;
    /**
     * Bundle scanner.
     */
    private BundleScanner m_bundleScanner;

    /**
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start( final BundleContext bundleContext )
    {
        LOG.debug( "Starting OPS4J Pax ConfMan bundle scanner" );

        m_commandsProcessor = new CommandProcessor<ConfigurationManager>( "Pax ConfMan - Bundle Scanner" );
        m_configurationManagerTracker = new ConfigurerTracker(
            bundleContext,
            new ConfigurerSetter()
            {
                /**
                 * Pass configurationManager to commands processor.
                 *
                 * @param configurationManager new configurationManager
                 */
                public void setConfigurer( final ConfigurationManager configurationManager )
                {
                    m_commandsProcessor.setTargetService( configurationManager );
                }

            }
        );
        m_bundleScanner = new BundleScanner( bundleContext, m_commandsProcessor );

        // ready to start
        m_configurationManagerTracker.start();
        m_commandsProcessor.start();
        m_bundleScanner.start();
    }

    /**
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop( final BundleContext bundleContext )
    {
        LOG.debug( "Stopping OPS4J Pax ConfMan bundle scanner" );

        if( m_bundleScanner != null )
        {
            m_bundleScanner.stop();
            m_bundleScanner = null;
        }
        if( m_configurationManagerTracker != null )
        {
            m_configurationManagerTracker.stop();
            m_configurationManagerTracker = null;
        }
        if( m_commandsProcessor != null )
        {
            m_commandsProcessor.stop();
            m_commandsProcessor = null;
        }
    }

}