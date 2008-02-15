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
package org.ops4j.pax.cm.samples.registry.internal;

import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Activator for Pax ConfMan registry sample.
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
        LOG.debug( "Starting OPS4J Pax ConfMan registry sample" );

        // registration properties
        final Dictionary<String, String> msProps = new Hashtable<String, String>();
        // we target pax confman logger sample
        msProps.put( "target.service.pid", "org.ops4j.pax.cm.samples.logger" );

        Dictionary<String, Object> cfgAsDict;

        // register a dictionary where the keys / values are always the same so it should be sent to config admin only
        // once
        cfgAsDict = new Hashtable<String, Object>();
        cfgAsDict.put( "foo.from.a.dictionary", "bar" );
        cfgAsDict.put( "bar.from.a.dictionary", "foo" );
        // we configure also a property that should be removed while processing as is not supported by config admin
        cfgAsDict.put( "a.property.that.should.be.ignored", new Date() );
        bundleContext.registerService( Dictionary.class.getName(), cfgAsDict, msProps );

        // register a dictionary service properties that do not contain the pid property so this config should not be
        // picked by registry scanner
        cfgAsDict = new Hashtable<String, Object>();
        cfgAsDict.put( "this.should.never.be.picked.by.registry.scanner", "foo" );
        bundleContext.registerService( Dictionary.class.getName(), cfgAsDict, null );

        // registere a dictionary that has a property with a value that changes at each registration so it should be
        // updated every time
        cfgAsDict = new Hashtable<String, Object>();
        cfgAsDict.put( "a.changing.property", System.currentTimeMillis() );
        bundleContext.registerService( Dictionary.class.getName(), cfgAsDict, msProps );

    }

    /**
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop( final BundleContext bundleContext )
    {
        LOG.debug( "Stopping OPS4J Pax ConfMan registry sample" );
    }

}