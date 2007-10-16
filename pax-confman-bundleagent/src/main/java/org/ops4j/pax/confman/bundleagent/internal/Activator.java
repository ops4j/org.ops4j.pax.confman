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
package org.ops4j.pax.confman.bundleagent.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.ops4j.pax.confman.service.ConfigurationManager;
import org.ops4j.pax.swissbox.bundle.BundleResource;
import org.ops4j.pax.swissbox.bundle.BundleResourceScanner;
import org.ops4j.pax.swissbox.bundle.BundleWatcher;
import org.ops4j.pax.swissbox.tracker.ReplaceableService;

/**
 * ConfMan Bundle Agent Service Activator.
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

    private BundleWatcher<BundleResource> m_bundleWatcher;
    private ReplaceableService<ConfigurationManager> m_replaceableService;

    /**
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start( final BundleContext context )
        throws Exception
    {
        LOGGER.debug( "Starting [" + context.getBundle().getSymbolicName() + "]..." );
        m_replaceableService = new ReplaceableService<ConfigurationManager>( context, ConfigurationManager.class );
        m_replaceableService.start();
        m_bundleWatcher =
            new BundleWatcher<BundleResource>( context, new BundleResourceScanner( "META-INF/log", false ) );
        m_bundleWatcher.start();
    }

    /**
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop( final BundleContext context )
        throws Exception
    {
        LOGGER.debug( "Stopping [" + context.getBundle().getSymbolicName() + "]..." );
        m_replaceableService.stop();
        m_bundleWatcher.stop();
    }


}
