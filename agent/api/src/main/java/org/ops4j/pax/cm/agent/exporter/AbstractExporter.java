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
package org.ops4j.pax.cm.agent.exporter;

import java.io.OutputStream;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.cm.agent.configuration.PaxConfiguration;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

/**
 * {@code AbstractExporter} provides some default implementation for export service registration.
 * 
 * @author Edward Yakop
 * @since 1.0.0
 */
public abstract class AbstractExporter
    implements Exporter, ManagedService
{
    private static final Logger m_logger = Logger.getLogger( AbstractExporter.class );

    private BundleContext m_bundleContext;
    private final String m_servicePID;
    private String m_exporterId;
    private ServiceRegistration m_serviceRegistration;

    protected AbstractExporter( BundleContext bundleContext, String serviceId, String exporterId )
        throws IllegalArgumentException
    {
        NullArgumentException.validateNotNull( bundleContext, "bundleContext" );
        NullArgumentException.validateNotEmpty( serviceId, "serviceId" );
        NullArgumentException.validateNotEmpty( exporterId, "exporterId" );

        m_servicePID = serviceId;
        m_exporterId = exporterId;
        m_bundleContext = bundleContext;
    }

    /**
     * Returns the exporter id. This method must not return {@code null} or empty String.
     * 
     * @return The exporter id.
     * 
     * @since 0.1.0
     */
    public final String getExporterId()
    {
        return m_exporterId;
    }

    /**
     * Perform export of the specified {@code configurations} to the specified {@code stream}.
     * 
     * @param configurations The configurations to be exported.
     * @param stream The output stream. This argument must not be {@code null}.
     * 
     * @throws org.ops4j.pax.cm.agent.exporter.ExportException Thrown if there is error ocurred during export.
     * @since 0.1.0
     */
    public abstract void performExport( List<PaxConfiguration> configurations, OutputStream stream )
        throws ExportException;

    /**
     * @see ManagedService#updated(java.util.Dictionary)
     */
    public final void updated( Dictionary dictionary )
        throws ConfigurationException
    {
        onUpdated( dictionary );

        String exporterId = (String) dictionary.get( EXPORT_ID );
        if ( exporterId != null )
        {
            m_exporterId = exporterId;
        }
        else
        {
            dictionary.put( EXPORT_ID, m_exporterId );
        }

        synchronized ( this )
        {
            m_serviceRegistration.setProperties( dictionary );
        }
    }

    /**
     * Override this method if child class wants to perform child specific operation.
     * 
     * @param dictionary The dictionary. This argument might be {@code null}.
     * 
     * @return A non-null dictionary instance.
     * 
     * @throws ConfigurationException Thrown if there is a configuration exception.
     * @see AbstractExporter#newExporterConfigurations()
     * @since 0.1.0
     */
    protected void onUpdated( Dictionary dictionary )
        throws ConfigurationException
    {
    }

    /**
     * @return new exporter configurations.
     * 
     * @since 0.1.0
     */
    protected final Properties newExporterConfigurations()
    {
        Properties configurations = new Properties();

        configurations.put( Constants.SERVICE_PID, m_servicePID );
        configurations.put( Exporter.EXPORT_ID, m_exporterId );

        return configurations;
    }

    /**
     * Register this exporter service.
     * 
     * @since 0.1.0
     */
    public final ServiceRegistration register()
    {
        Hashtable configurations = newExporterConfigurations();

        String[] serviceNames =
        {
            Exporter.class.getName(), ManagedService.class.getName()
        };

        ServiceRegistration registration = m_bundleContext.registerService( serviceNames, this, configurations );
        if ( m_logger.isDebugEnabled() )
        {
            m_logger.debug( "Exporter [" + m_servicePID + "] is registered." );
        }

        synchronized ( this )
        {
            m_serviceRegistration = registration;
        }

        return registration;
    }
}
