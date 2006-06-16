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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.cm.agent.configuration.PaxConfiguration;

/**
 * {@code ImporterManager} manage instance of importers and acts as a facade for importer.
 *
 * @author Edward Yakop
 * @since 0.1.0
 */
public final class ImporterManager
{

    private static final ImporterManager INSTANCE;
    private final Map<String, Importer> m_importers;

    static
    {
        INSTANCE = new ImporterManager();
    }

    /**
     * Return the singleton instance of {@code ImporterManager} instance.
     *
     * @return The singleton instance of {@code ImporterManager}.
     *
     * @since 0.1.0
     */
    public static ImporterManager getInstance()
    {
        return INSTANCE;
    }

    private ImporterManager()
    {
        m_importers = new HashMap<String, Importer>();
    }

    /**
     * Add {@code Importer} with the specified {@code importerId}. The specified {@code importer} can be added with many
     * {@code importerId}.
     *
     * @param importerId The importer Id. This argument must not be {@code null} or empty.
     * @param importer   The importer to be added. This argument must not be {@code null}.
     */
    public void addImporter( String importerId, Importer importer )
    {
        NullArgumentException.validateNotEmpty( importerId, "importerId" );
        NullArgumentException.validateNotNull( importer, "importer" );

        synchronized( m_importers )
        {
            if( m_importers.containsKey( importerId ) )
            {
                throw new IllegalArgumentException( "Importer id [" + importerId + "] is already registered." );
            }

            m_importers.put( importerId, importer );
        }
    }

    /**
     * Returns importers ids .
     */
    public Set<String> getImporterIds()
    {
        Set<String> ids = m_importers.keySet();
        return Collections.unmodifiableSet( ids );
    }

    /**
     * Import the specified {@code inputStream} with importer with id {@code importerId}. Returns an empty collection if
     * there is no configuration defined inside {@code inputStream}.
     *
     * @param importerId  The importer id to be used to import the specified {@code inputerStream}. This argument must
     *                    not be {@code null} or empty and the importer with the specified id must be registred.
     * @param inputStream The input stream to be imported. This argument must not be {@code null}.
     *
     * @return A collection of {@code PaxConfiguration}.
     *
     * @throws IllegalArgumentException Thrown if either one or both arguments are either {@code null} or empty or
     *                                  importer with the specified {@code importerId} is registered.
     * @since 0.1.0
     */
    public List<PaxConfiguration> performImport( String importerId, InputStream inputStream )
        throws IllegalArgumentException
    {
        NullArgumentException.validateNotEmpty( importerId, "importerId" );
        NullArgumentException.validateNotNull( inputStream, "inputStream" );

        Importer importer = m_importers.get( importerId );
        if( importer == null )
        {
            throw new IllegalArgumentException( "Importer with id [" + importerId + "] can not be found." );
        }

        return importer.performImport( inputStream );
    }

    /**
     * Remove importer with the specified {@code importerId}. Returns {@code true} if this Importer with specified
     * {@code importerId} is removed, {@code false} otherwise.
     *
     * @param importerId The importerId to be deleted. This argument must not be {@code null} or empty.
     *
     * @return A {@code boolean} indicator whether the importer with the specified {@code importerId} is removed.
     *
     * @throws IllegalArgumentException Thrown if the specified {@code importerId} is {@code null}.
     * @since 0.1.0
     */
    public boolean removeImporter( String importerId )
    {
        NullArgumentException.validateNotEmpty( importerId, "importerId" );

        Importer importer = m_importers.remove( importerId );
        return importer != null;
    }
}
