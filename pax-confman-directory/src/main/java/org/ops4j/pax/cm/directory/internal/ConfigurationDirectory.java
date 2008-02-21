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
package org.ops4j.pax.cm.directory.internal;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import org.ops4j.lang.NullArgumentException;

/**
 * A directory of configuration files.
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 20, 2008
 */
class ConfigurationDirectory
{

    /**
     * Scanned file system directory. Cannot be null.
     */
    private final File m_directory;

    /**
     * Constructor.
     *
     * @param directory file system directory containing configuration files.
     *
     * @throws NullArgumentException - If directory is null
     */
    ConfigurationDirectory( final File directory )
    {
        NullArgumentException.validateNotNull( directory, "Scanned directory" );

        m_directory = directory;
    }

    @Override
    public String toString()
    {
        return new StringBuilder()
            .append( this.getClass().getSimpleName() )
            .append( "{" )
            .append( m_directory.getAbsolutePath() )
            .append( "}" )
            .toString();
    }

    /**
     * Scans the file system directory and returns the contained configuration files.
     *
     * @return set of scanned files.
     */
    Set<ConfigurationFile> scan()
    {
        final Set<ConfigurationFile> scanned = new HashSet<ConfigurationFile>();
        if( m_directory.isDirectory() && m_directory.canRead() )
        {
            final File[] contents = m_directory.listFiles();
            for( File msFile : contents )
            {
                if( msFile.isDirectory() )
                {
                    // files in subdirectories as managaed service factory configurations
                    final File[] factories = msFile.listFiles();
                    for( File msfFile : factories )
                    {
                        if( msfFile.isFile() && !msfFile.getName().startsWith( "." ) )
                        {
                            scanned.add( ConfigurationFile.forManagedServiceFactory( msfFile ) );
                        }
                    }
                }
                else if( !msFile.getName().startsWith( "." ) )
                {
                    // files in root directory are managed service configurations
                    scanned.add( ConfigurationFile.forManagedService( msFile ) );
                }
            }
        }
        return scanned;
    }

}