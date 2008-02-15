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
package org.ops4j.pax.cm.scanner.directory.internal;

import java.util.Dictionary;
import java.util.Hashtable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.cm.api.Configurer;
import org.ops4j.pax.cm.api.MetadataConstants;
import org.ops4j.pax.cm.common.internal.processor.CommandProcessor;
import org.ops4j.pax.swissbox.lifecycle.AbstractLifecycle;

/**
 * Scans properties file producers.
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 12, 2008
 */
public class DirectoryScanner
    extends AbstractLifecycle
{

    /**
     * Logger.
     */
    private static final Log LOG = LogFactory.getLog( DirectoryScanner.class );
    /**
     * Commands processor.
     */
    private final CommandProcessor<Configurer> m_processor;

    /**
     * Creates a new directory scanner.
     *
     * @param bundleContext bundle context; cannot be null
     * @param processor     configurations buffer; canot be null
     *
     * @throws NullArgumentException - If bundle context is null
     *                               - If configurations buffer is null
     */
    public DirectoryScanner( final BundleContext bundleContext,
                             final CommandProcessor<Configurer> processor )
    {
        NullArgumentException.validateNotNull( bundleContext, "Bundle context" );
        NullArgumentException.validateNotNull( processor, "Command processor" );

        m_processor = processor;
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
        final Dictionary metadata = new Hashtable();
        metadata.put( MetadataConstants.INFO_AGENT, "org.ops4j.pax.cm.scanner.directory" );
        return metadata;
    }

    /**
     * Starts directory scanning.
     */
    protected void onStart()
    {
    }

    /**
     * Stops directory scanning.
     */
    protected void onStop()
    {
    }
}