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
package org.ops4j.pax.cm.agent.importer.beanshell.internal;

import org.ops4j.pax.cm.agent.importer.beanshell.BeanShellImporter;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * {@code Activator} maintains lifecycle and registration of {@code BeanShellImporter} service.
 * 
 * @author Edward Yakop
 * @since 1.0.0
 */
public final class Activator
    implements BundleActivator
{

    private static final String SERVICE_PID = "org.ops4j.pax.configadmin.agent.importer.beanshell";

    private BeanShellImporter m_importer;

    public void start( BundleContext context )
        throws Exception
    {
        synchronized ( this )
        {
            m_importer = new BeanShellImporter( context, SERVICE_PID );
            m_importer.register();
        }
    }

    public void stop( BundleContext context )
        throws Exception
    {
        synchronized ( this )
        {
            m_importer.unregister();
        }
    }

}
