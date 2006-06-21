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
package org.ops4j.pax.cm.agent.importer;

import java.io.InputStream;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.cm.agent.configuration.PaxConfiguration;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

/**
 * {@code AbstractImporter} provides some convenience to implement {@code Importer} service.
 *
 * @author Edward Yakop
 * @since 0.1.0
 */
public abstract class AbstractImporter
    implements Importer, ManagedService
{

    private static final Log m_logger = LogFactory.getLog( AbstractImporter.class );

    private BundleContext m_bundleContext;
    private String m_servicePID;
    private String m_importerId;
    private ServiceRegistration m_serviceRegistration;

    protected AbstractImporter( BundleContext bundleContext, String serviceId, String importerId )
        throws IllegalArgumentException
    {
        NullArgumentException.validateNotNull( bundleContext, "bundleContext" );
        NullArgumentException.validateNotEmpty( serviceId, "serviceId" );
        NullArgumentException.validateNotEmpty( importerId, "importerId" );

        m_servicePID = serviceId;
        m_importerId = importerId;
        m_bundleContext = bundleContext;
    }

    /**
     * Returns the importer id. This method must not return {@code null} or empty String.
     *
     * @return The importer id.
     *
     * @since 0.1.0
     */
    public final String getImporterId()
    {
        return m_importerId;
    }

    /**
     * Perform import on data specified by {@code inputStream}.
     *
     * @param inputStream The input stream. This argument must not be {@code null}.
     *
     * @return List of {@code PaxConfiguration}, returns empty collection if there is no configuration.
     *
     * @throws IllegalArgumentException Thrown if the specified {@code inputStream} is {@code null}.
     * @throws ImportException          Thrown if there is import exception.
     * @see java.util.Collections#emptyList()
     * @since 0.1.0
     */
    public abstract List<PaxConfiguration> performImport( InputStream inputStream )
        throws IllegalArgumentException, ImportException;

    /**
     * @see org.osgi.service.cm.ManagedService#updated(java.util.Dictionary)
     */
    public final void updated( Dictionary dictionary )
        throws ConfigurationException
    {
        dictionary = onUpdated( dictionary );

        String importerId = (String) dictionary.get( IMPORTER_ID );
        if( importerId != null )
        {
            m_importerId = importerId;
        }
        else
        {
            dictionary.put( IMPORTER_ID, m_importerId );
        }

        m_serviceRegistration.setProperties( dictionary );
    }

    /**
     * Override this method if child class wants to perform child specific operation.
     *
     * @param dictionary The dictionary. This argument might be {@code null}.
     *
     * @return A non-null dictionary instance.
     *
     * @throws org.osgi.service.cm.ConfigurationException
     *          Thrown if there is a configuration exception.
     * @see AbstractImporter#newImporterConfigurations()
     * @since 0.1.0
     */
    protected Dictionary onUpdated( Dictionary dictionary )
        throws ConfigurationException
    {
        if( dictionary == null )
        {
            return newImporterConfigurations();
        }

        return dictionary;
    }

    /**
     * @return new importer configurations.
     *
     * @since 0.1.0
     */
    protected final Hashtable newImporterConfigurations()
    {
        Hashtable configurations = new Hashtable();

        configurations.put( Constants.SERVICE_PID, m_servicePID );
        configurations.put( IMPORTER_ID, m_importerId );

        return configurations;
    }

    /**
     * Register this importer service.
     *
     * @see org.ops4j.pax.cm.agent.importer.AbstractImporter#unregister()
     * @since 0.1.0
     */
    public final synchronized void register()
    {
        if( m_serviceRegistration == null )
        {
            Hashtable configurations = newImporterConfigurations();

            String[] serviceNames = { Importer.class.getName(), ManagedService.class.getName() };
            m_serviceRegistration = m_bundleContext.registerService( serviceNames, this, configurations );

            if( AbstractImporter.m_logger.isDebugEnabled() )
            {
                AbstractImporter.m_logger.debug( "Importer [" + m_servicePID + "] is registered." );
            }
        }
        else
        {
            if( AbstractImporter.m_logger.isDebugEnabled() )
            {
                String message = "Unable to re-register importer [" + m_servicePID
                                 + "]. This importer is already in registered state.";
                AbstractImporter.m_logger.warn( message );
            }
        }
    }

    /**
     * Unregister this {@code Importer service}. This method must be invoked during {@code BundleActivator#stop()}
     * invocation.
     *
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     * @since 0.1.0
     */
    public synchronized final void unregister()
    {
        if( m_serviceRegistration == null )
        {
            if( AbstractImporter.m_logger.isWarnEnabled() )
            {
                String message =
                    "Unable to unregister importer [" + m_servicePID + "]. This importer is not in registered state.";
                AbstractImporter.m_logger.warn( message );
            }
        }
        else
        {
            m_serviceRegistration.unregister();
            m_serviceRegistration = null;

            if( AbstractImporter.m_logger.isDebugEnabled() )
            {
                AbstractImporter.m_logger.debug( "Importer [" + m_servicePID + "] is unregistered." );
            }
        }
    }
}
