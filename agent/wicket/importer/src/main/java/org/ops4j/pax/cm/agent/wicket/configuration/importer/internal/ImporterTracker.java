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

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.cm.agent.configuration.PaxConfiguration;
import org.ops4j.pax.cm.agent.importer.ImportException;
import org.ops4j.pax.cm.agent.importer.Importer;
import org.ops4j.pax.cm.agent.wicket.WicketApplicationConstant;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

/**
 * {@code ImporterTracker} tracks importer services, and register {@code Importer} wicket content if there is at least
 * one available {@code Importer} service.
 *
 * @since 0.1.0
 */
final class ImporterTracker extends ServiceTracker
{

    private static final String SERVICE_NAME_TO_TRACK = Importer.class.getName();
    private static final Map<String, Importer> m_services = new HashMap<String, Importer>();

    private final BundleContext m_bundleContext;
    private ServiceRegistration m_serviceRegistration;

    /**
     * Returns registered importer ids service.
     *
     * @since 0.1.0
     */
    static Set<String> getImporterIds()
    {
        Set<String> keys = m_services.keySet();
        return Collections.unmodifiableSet( keys );
    }

    /**
     * Perform import with of the specified {@code stream} with importer with the specified {@code importerId}.
     *
     * @param importerId The importer id. This argument must not be {@code null} or empty.
     * @param stream     The input stream to be imported. This argument must not be {@code null}.
     *
     * @throws IllegalArgumentException Thrown if one or both arguments are {@code null}. See each argument description
     *                                  for detail.
     * @throws ImportException          Thrown if the importer with the specified {@code importerId} is not found or
     *                                  there is an error occured during import.
     * @since 0.1.0
     */
    static List<PaxConfiguration> performImport( String importerId, InputStream stream )
        throws ImportException, IllegalArgumentException
    {
        NullArgumentException.validateNotEmpty( importerId, "importerId" );

        Importer importer = m_services.get( importerId );
        if( importer != null )
        {
            return importer.performImport( stream );
        }
        else
        {
            throw new ImportException( "Importer [" + importerId + "] does not exists." );
        }
    }

    ImporterTracker( BundleContext bundleContext )
    {
        super( bundleContext, SERVICE_NAME_TO_TRACK, null );
        m_bundleContext = bundleContext;
    }

    public void modifiedService( ServiceReference serviceReference, Object object )
    {
        removedService( serviceReference, object );
        addingService( serviceReference );
    }

    public void removedService( ServiceReference serviceReference, Object object )
    {
        Importer service = (Importer) object;

        String importerId = service.getImporterId();
        m_services.remove( importerId );

        unregisterContentIfThereIsNoAvailableImporter();
    }

    private synchronized void unregisterContentIfThereIsNoAvailableImporter()
    {
        if( m_services.isEmpty() && m_serviceRegistration != null )
        {
            m_serviceRegistration.unregister();
        }
    }

    public Object addingService( ServiceReference serviceReference )
    {
        Importer service = (Importer) m_bundleContext.getService( serviceReference );

        String importerId = service.getImporterId();
        m_services.put( importerId, service );

        registerContentIfThereIsAvailableImporter();

        return service;
    }

    /**
     * Register the importer content if there is an available importer and the content service has not been registered
     * before.
     *
     * @since 0.1.0
     */
    private synchronized void registerContentIfThereIsAvailableImporter()
    {
        if( !m_services.isEmpty() && m_serviceRegistration == null )
        {
            String destinationId = ConfigurationImporterContent.OVERVIEW_TAB_DESTINATION_ID;
            String applicationName = WicketApplicationConstant.APPLICATION_NAME;

            ConfigurationImporterContent cnt = new ConfigurationImporterContent( context, applicationName );
            cnt.setDestinationId( destinationId );

            m_serviceRegistration = cnt.register();
        }
    }
}
