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
import java.util.List;
import org.ops4j.pax.cm.agent.configuration.PaxConfiguration;

/**
 * {@code Exporter} handles export of {@code PaxConfiguration} to the specified {@code OuputStream}.
 *
 * @author Edward Yakop
 * @since 0.1.0
 */
public interface Exporter
{
    /**
     * {@code EXPORTER_ID} is configuration property name that uniquely identified this exporter.
     *
     * @since 0.1.0
     */
    String EXPORT_ID = "exporterId";

    /**
     * Returns the exporter id. This method must not return {@code null} or empty String.
     *
     * @return The exporter id.
     *
     * @since 0.1.0
     */
    String getExporterId();

    /**
     * Perform export of the specified {@code configurations} to the specified {@code stream}.
     *
     * @param configurations The configurations to be exported. This argument must not be {@code null}.
     * @param stream         The output stream. This argument must not be {@code null}.
     *
     * @throws ExportException Thrown if there is error ocurred during export.
     * @since 0.1.0
     */
    void performExport( List<PaxConfiguration> configurations, OutputStream stream )
        throws ExportException;
}
