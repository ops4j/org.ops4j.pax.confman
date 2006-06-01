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
package org.ops4j.pax.cm.agent.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ops4j.lang.NullArgumentException;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import wicket.application.IClassResolver;

/**
 * {@code SimpleClassResolver} is a simple class resolver that exposed <strong>EVERY</strong> class that the specified
 * bundle context can access too.
 *
 * @author Edward Yakop
 * @since 0.1.0
 */
public final class SimpleClassResolver
    implements IClassResolver
{
    private static final Log m_logger = LogFactory.getLog( SimpleClassResolver.class );

    private final BundleContext m_bundleContext;

    public SimpleClassResolver( BundleContext bundleContext )
    {
        NullArgumentException.validateNotNull( bundleContext, "bundleContext" );
        m_bundleContext = bundleContext;
    }

    /**
     * Resolves a class by name (which may or may not involve loading it; thus
     * the name class *resolver* not *loader*).
     *
     * @param classname Fully qualified classname to find
     *
     * @return Class
     */
    public Class resolveClass( final String classname )
    {
        if( classname == null )
        {
            return null;
        }

        Bundle bundle = m_bundleContext.getBundle();
        try
        {
            return bundle.loadClass( classname );
        } catch( ClassNotFoundException e )
        {
            // Expected if the class is not accessible by this bundle.
            if( m_logger.isDebugEnabled() )
            {
                String message =
                    "Class [" + classname + "] is not accessible by bundle [" + bundle.getSymbolicName() + "]";
                m_logger.debug( message );
            }
        }

        return null;
    }
}
