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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * A diff(erence) between two sets of configuration files at the time the diff was created.
 *
 * TODO optimize?
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 20, 2008
 */
class ConfigurationFileSetsDiff
{

    /**
     * Array of added fonfiguration files.
     */
    private final ConfigurationFile[] m_added;
    /**
     * Array of changed configuration files.
     */
    private final ConfigurationFile[] m_updated;
    /**
     * Array of deleted configuration files.
     */
    private final ConfigurationFile[] m_deleted;

    /**
     * Constructor.
     *
     * @param set1 first set of configuration files
     * @param set2 second set of configuration files
     */
    ConfigurationFileSetsDiff( final Collection<ConfigurationFile> set1,
                               final Collection<ConfigurationFile> set2 )
    {
        final Collection<ConfigurationFile> workingSet1 = set1 == null ? new HashSet<ConfigurationFile>() : set1;
        final Collection<ConfigurationFile> workingSet2 = set2 == null ? new HashSet<ConfigurationFile>() : set2;

        final Set<ConfigurationFile> added = new HashSet<ConfigurationFile>( workingSet2 );
        added.removeAll( workingSet1 );
        m_added = added.toArray( new ConfigurationFile[added.size()] );

        final Set<ConfigurationFile> deleted = new HashSet<ConfigurationFile>( workingSet1 );
        deleted.removeAll( workingSet2 );
        m_deleted = deleted.toArray( new ConfigurationFile[deleted.size()] );

        final Set<ConfigurationFile> updated = new HashSet<ConfigurationFile>( workingSet1 );
        updated.removeAll( added );
        updated.removeAll( deleted );
        for( ConfigurationFile config1 : updated.toArray( new ConfigurationFile[updated.size()] ) )
        {
            for( ConfigurationFile config2 : workingSet2 )
            {
                if( config1.equals( config2 ) && config1.getTimeStamp() == config2.getTimeStamp() )
                {
                    updated.remove( config1 );
                }
            }
        }
        m_updated = updated.toArray( new ConfigurationFile[updated.size()] );
    }

    /**
     * Getter.
     *
     * @return added configuration files
     */
    public ConfigurationFile[] getAdded()
    {
        return m_added;
    }

    /**
     * Getter.
     *
     * @return deleted configuration files
     */
    public ConfigurationFile[] getDeleted()
    {
        return m_deleted;
    }

    /**
     * Getter.
     *
     * @return modified configuration files
     */
    public ConfigurationFile[] getUpdated()
    {
        return m_updated;
    }
}