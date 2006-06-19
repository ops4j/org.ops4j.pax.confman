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
import java.util.List;
import org.ops4j.pax.cm.agent.configuration.PaxConfiguration;

/**
 * @author Edward Yakop
 * @since 0.1.0
 */
public interface Importer
{

    /**
     * Perform import on data specified by {@code inputStream}.
     *
     * @param inputStream The input stream. This argument must not be {@code null}.
     *
     * @return List of {@code PaxConfiguration}, returns empty collection if there is no configuration.
     *
     * @throws ImportException Thrown if there is import exception.
     * @see java.util.Collections#emptyList()
     * @since 0.1.0
     */
    List<PaxConfiguration> performImport( InputStream inputStream )
        throws ImportException;
}