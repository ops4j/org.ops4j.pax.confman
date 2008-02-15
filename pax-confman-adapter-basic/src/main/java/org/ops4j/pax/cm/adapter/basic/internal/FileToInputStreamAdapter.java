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
package org.ops4j.pax.cm.adapter.basic.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ops4j.pax.cm.api.Specification;

/**
 * Adapts an input stream containing Properties.
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, January 15, 2008
 */
public class FileToInputStreamAdapter
    extends SpecificationBasedAdapter
{

    /**
     * Logger.
     */
    private static final Log LOG = LogFactory.getLog( Activator.class );

    /**
     * Constructor.
     *
     * @param specification delegate specification
     */
    public FileToInputStreamAdapter( final Specification specification )
    {
        super( specification );
    }

    /**
     * Adapts the received object (expected to be File) to an input stream.
     *
     * @param sourceObject to be adapted
     *
     * @return input stream  or original object if source object is not an file.
     */
    public Object adapt( final Object sourceObject )
    {
        if( sourceObject instanceof File )
        {
            try
            {
                return new FileInputStream( (File) sourceObject );
            }
            catch( FileNotFoundException ignore )
            {
                LOG.error( "Could not adapt file", ignore );
                return null;
            }
        }
        return sourceObject;
    }

}