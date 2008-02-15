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
package org.ops4j.pax.cm.samples.logger.internal;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.cm.ManagedServiceFactory;

/**
 * Activator for Pax ConfMan logger sample.
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
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start( final BundleContext bundleContext )
    {
        LOG.debug( "Starting OPS4J Pax ConfMan logger sample" );
        // register managed service
        final Dictionary<String, String> msProps = new Hashtable<String, String>();
        msProps.put( Constants.SERVICE_PID, "org.ops4j.pax.cm.samples.logger" );
        bundleContext.registerService(
            ManagedService.class.getName(),
            new ManagedService()
            {

                public void updated( final Dictionary dictionary )
                {
                    logConfiguration( dictionary );
                }

            },
            msProps
        );
        // register managed service factory
        final Dictionary<String, String> msfProps = new Hashtable<String, String>();
        msProps.put( Constants.SERVICE_PID, "org.ops4j.pax.cm.samples.logger.factory" );
        bundleContext.registerService(
            ManagedServiceFactory.class.getName(),
            new ManagedServiceFactory()
            {

                public String getName()
                {
                    return bundleContext.getBundle().getSymbolicName() + ".factory";
                }

                public void updated( final String pid, final Dictionary dictionary )
                    throws ConfigurationException
                {
                    LOG.info( "Configuration updated for pid:" + pid );
                    logConfiguration( dictionary );
                }

                public void deleted( final String pid )
                {
                    LOG.info( "Configuration deleted for pid:" + pid );
                }

            },
            msfProps
        );
    }

    /**
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop( final BundleContext bundleContext )
    {
        LOG.debug( "Stopping OPS4J Pax ConfMan logger sample" );
    }

    /**
     * Logs configuration properties.
     *
     * @param dictionary of configuration properties
     */
    private static void logConfiguration( final Dictionary dictionary )
    {
        LOG.info( "Configured properties:" );
        if( dictionary == null )
        {
            LOG.info( "There are no configuration properties to be listed" );
        }
        else
        {
            final Enumeration keys = dictionary.keys();
            if( keys != null && keys.hasMoreElements() )
            {
                while( keys.hasMoreElements() )
                {
                    final Object key = keys.nextElement();
                    LOG.info( key + " -> " + dictionary.get( key ) );
                }
            }
            else
            {
                LOG.info( "There are no configuration properties to be listed" );
            }
        }
    }

}