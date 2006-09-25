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

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * {@code Activator} start tracking {@code Importer} service and only register import tracker if there is at least one
 * {@code Importer} service registered.
 *
 * @author Edward Yakop
 * @since 0.1.0
 */
public final class Activator
    implements BundleActivator
{

    private ImporterTracker m_importerTracker;

    /**
     * @see BundleActivator#start(org.osgi.framework.BundleContext)
     * @since 0.1.0
     */
    public void start( BundleContext context )
        throws Exception
    {
        m_importerTracker = new ImporterTracker( context );
        m_importerTracker.open();
    }

    /**
     * @see BundleActivator#stop(org.osgi.framework.BundleContext)
     * @since 0.1.0
     */
    public void stop( BundleContext context )
        throws Exception
    {
        m_importerTracker.close();
    }
}
