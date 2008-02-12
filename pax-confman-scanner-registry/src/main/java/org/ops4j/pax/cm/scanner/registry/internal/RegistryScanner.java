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

import java.util.Dictionary;
import java.util.Hashtable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.cm.configurer.Configurer;
import org.ops4j.pax.cm.configurer.ConfigurerSetter;
import org.ops4j.pax.cm.configurer.Metadata;
import org.ops4j.pax.swissbox.lifecycle.AbstractLifecycle;

/**
 * Trackes objects (services) in Service Registry, to be used as configuration properties.
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 12, 2008
 */
public class RegistryScanner
    extends AbstractLifecycle
    implements ConfigurerSetter
{

    /**
     * Logger.
     */
    private static final Log LOG = LogFactory.getLog( RegistryScanner.class );
    /**
     * Configurer service tracker.
     */
    private final ServiceTracker m_registryTracker;
    /**
     * Configurer in use.
     */
    private Configurer m_configurer;

    /**
     * Creates a new registry scanner.
     *
     * @param bundleContext bundle context; cannot be null
     *
     * @throws NullArgumentException - If bundle context is null
     */
    public RegistryScanner( final BundleContext bundleContext )
    {
        NullArgumentException.validateNotNull( bundleContext, "Bundle context" );

        m_registryTracker = new ServiceTracker( bundleContext, createFilter( bundleContext ), null )
        {
            @Override
            public Object addingService( final ServiceReference serviceReference )
            {
                Object service = super.addingService( serviceReference );
                LOG.trace( "Found possible configuration: " + service );
                // TODO what shall be done if configurere is null. queue configurations?
                if( m_configurer != null )
                {
                    final Dictionary metadata = createMetdata( serviceReference );
                    LOG.trace( "Configuration metadata: " + metadata );
                    final String pid = getMetdataProperty( metadata, Metadata.CONFIG_PID );
                    if( pid != null )
                    {
                        final String factoryPid = getMetdataProperty( metadata, Metadata.CONFIG_FACTORY_PID );
                        final String location = getMetdataProperty( metadata, Metadata.CONFIG_BUNDLELOCATION );
                        m_configurer.configure( pid, location, service, metadata );
                        //TODO factory configuration
                    }
                }
                return service;
            }
        };
    }

    /**
     * Extracts a property by key out of metadata. Property values have to be strings otherwise  null is returned.
     *
     * @param metadata    metadata dictionary
     * @param propertyKey property key
     *
     * @return property value or null if not avail or not a String
     */
    private static String getMetdataProperty( final Dictionary metadata,
                                              final String propertyKey )
    {
        String value = null;
        Object propertyValue = metadata.get( propertyKey );
        if( propertyValue instanceof String )
        {
            value = (String) propertyValue;
        }
        return value;
    }

    /**
     * Creates metadata out of service properties.
     *
     * @param serviceReference service reference
     *
     * @return created metdata
     */
    @SuppressWarnings( "unchecked" )
    private static Dictionary createMetdata( final ServiceReference serviceReference )
    {
        // copy all service properties as metdata
        final Dictionary metadata = new Hashtable();
        final String[] keys = serviceReference.getPropertyKeys();
        for( String key : keys )
        {
            metadata.put( key, serviceReference.getProperty( key ) );
        }
        return metadata;
    }

    /**
     * Create an OSGi filtr that matches any service that has:
     * - a property names service.pid
     *
     * @param bundleContext bundle context
     *
     * @return created filter
     */
    private static Filter createFilter( final BundleContext bundleContext )
    {
        final StringBuilder filterBuilder = new StringBuilder()
            .append( "(" )
            .append( Metadata.CONFIG_PID ).append( "=*" )
            .append( ")" );
        try
        {
            return bundleContext.createFilter( filterBuilder.toString() );
        }
        catch( InvalidSyntaxException ignore )
        {
            // this should never happen
            LOG.trace( "Internal error: " + ignore.getMessage() );
            throw new IllegalStateException( "Internal error", ignore );
        }
    }

    /**
     * Setter.
     *
     * @param configurer to be set
     */
    public void setConfigurer( final Configurer configurer )
    {
        m_configurer = configurer;
    }

    /**
     * Starts registry scanning.
     */
    protected void onStart()
    {
        // track any object, even incompatible ones
        m_registryTracker.open( true );
    }

    /**
     * Stops registry scanning.
     */
    protected void onStop()
    {
        m_registryTracker.close();
    }
}
