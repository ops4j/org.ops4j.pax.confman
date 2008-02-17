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

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.cm.scanner.directory.ServiceConstants;
import org.ops4j.util.property.PropertyResolver;
import org.ops4j.util.property.PropertyStore;

/**
 * Configuration implementation.
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 16, 2008
 */
public class ConfigurationImpl
    extends PropertyStore
    implements Configuration
{

    /**
     * Property resolver. Cannot be null.
     */
    private final PropertyResolver m_propertyResolver;

    /**
     * Constructor
     *
     * @param propertyResolver propertyResolver used to resolve properties; mandatory
     */
    public ConfigurationImpl( final PropertyResolver propertyResolver )
    {
        NullArgumentException.validateNotNull( propertyResolver, "Property resolver" );

        m_propertyResolver = propertyResolver;
    }

    /**
     * @see Configuration#getDirectories()
     */
    public File[] getDirectories()
    {
        if( !contains( ServiceConstants.SYSPROP_DIRECTORIES ) )
        {
            File[] directories = null;
            final String directoriesProp = m_propertyResolver.get( ServiceConstants.SYSPROP_DIRECTORIES );
            if( directoriesProp != null && directoriesProp.trim().length() > 0 )
            {
                final String[] segments = directoriesProp.split( "," );
                final Set<File> dirList = new HashSet<File>();
                for( String segment : segments )
                {
                    dirList.add( new File( segment ) );
                }
                directories = dirList.toArray( new File[dirList.size()] );
            }
            return set( ServiceConstants.SYSPROP_DIRECTORIES, directories );
        }
        return get( ServiceConstants.SYSPROP_DIRECTORIES );
    }

    /**
     * @see Configuration#getInterval()
     */
    public Long getInterval()
    {
        if( !contains( ServiceConstants.SYSPROP_INTERVAL ) )
        {
            Long interval = null;
            final String intervalProp = m_propertyResolver.get( ServiceConstants.SYSPROP_INTERVAL );
            try
            {
                interval = Long.valueOf( intervalProp );
            }
            catch( NumberFormatException ignore )
            {
                // ignore
            }
            return set( ServiceConstants.SYSPROP_INTERVAL, interval );
        }
        return get( ServiceConstants.SYSPROP_INTERVAL );
    }

}