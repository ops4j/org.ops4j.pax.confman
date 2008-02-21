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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Properties;
import org.osgi.framework.Constants;
import org.osgi.service.cm.ConfigurationAdmin;
import org.ops4j.lang.NullArgumentException;

/**
 * A file that can be used as a configuration.
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 19, 2008
 */
class ConfigurationFile
{

    /**
     * Persistent identifier.<br/>
     * The value is equal with:<br/>
     * - [file name without extension] for a managed service configuration<br/>
     * - [parent directory]-[file name without extension] for a managed service factory configuration<br/>
     * Cannot be null.
     */
    private final String m_pid;
    /**
     * Factory persistent identifier. The value is equal with the name of file's parent directory.
     * Can be null if this configuration is not a factory configuration.
     */
    private final String m_factoryPid;
    /**
     * File containing the configuration properties.
     */
    private final File m_configurationFile;
    /**
     * Configuration file time stamp equal with properties file modification time at creation time.
     */
    private final long m_timeStamp;
    /**
     * Lazy loaded properties.
     */
    private Dictionary m_properties;
    /**
     * True if properties are not yet loaded.
     */
    private boolean m_propertiesNotLoaded;

    /**
     * Constructor.
     *
     * @param pid               persistent identifier
     * @param factoryPid        factory persistent identifier
     * @param configurationFile file containing the configuration properties.
     *
     * @throws NullArgumentException - If persistent identifier is null or empty
     *                               - If configuration file is null
     */
    ConfigurationFile( final String pid, final String factoryPid, final File configurationFile )
    {
        NullArgumentException.validateNotEmpty( pid, true, "Persistent identifier" );
        NullArgumentException.validateNotNull( configurationFile, "Configuration file" );

        m_pid = pid;
        m_factoryPid = factoryPid;
        m_configurationFile = configurationFile;
        m_timeStamp = m_configurationFile.lastModified();
        m_propertiesNotLoaded = true;
    }

    /**
     * Getter. Cannot return null.
     *
     * @return configuration file.
     */
    public File getConfigurationFile()
    {
        return m_configurationFile;
    }

    /**
     * Getter. Returns null for a managed service configuration.
     *
     * @return factory persistent identifier.
     */
    public String getFactoryPid()
    {
        return m_factoryPid;
    }

    /**
     * Getter. Cannot return null.
     *
     * @return persistent identifier.
     */
    public String getPid()
    {
        return m_pid;
    }

    /**
     * Getter.
     *
     * @return timestamp
     */
    public long getTimeStamp()
    {
        return m_timeStamp;
    }

    @Override
    public boolean equals( Object o )
    {
        if( this == o )
        {
            return true;
        }
        if( !( o instanceof ConfigurationFile ) )
        {
            return false;
        }

        ConfigurationFile that = (ConfigurationFile) o;

        if( m_pid != null ? !m_pid.equals( that.m_pid ) : that.m_pid != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return m_pid.hashCode();
    }

    @Override
    public String toString()
    {
        return new StringBuilder()
            .append( "{" )
            .append( "pid=" ).append( m_pid )
            .append( ",factoryPid=" ).append( m_factoryPid )
            .append( ",file=" ).append( m_configurationFile.getAbsolutePath() )
            .append( "}" )
            .toString();
    }

    /**
     * Creates a configuration file for a managed service.
     *
     * @param configurationFile file containing configuration properties.
     *
     * @return configuration file for a managed service
     *
     * @throws NullArgumentException - If configuration file is null
     */
    static ConfigurationFile forManagedService( final File configurationFile )
    {
        NullArgumentException.validateNotNull( configurationFile, "Configuration file" );

        return new ConfigurationFile( generatePid( configurationFile.getName() ), null, configurationFile );
    }

    /**
     * Creates a configuration file for a managed service factory.
     *
     * @param configurationFile file containing configuration properties.
     *
     * @return configuration file for a managed service factory
     *
     * @throws NullArgumentException - If configuration file is null
     *                               - If parent directory is null
     *                               - If file only an extension (e.g. .svn)
     */
    static ConfigurationFile forManagedServiceFactory( final File configurationFile )
    {
        NullArgumentException.validateNotNull( configurationFile, "Configuration file" );

        final File parentDirectory = configurationFile.getParentFile();
        if( parentDirectory == null )
        {
            throw new NullArgumentException(
                "Parent directory for a managed service factory configuration cannot be null"
            );
        }
        final String pid = generatePid( configurationFile.getName() );
        if( pid == null || pid.trim().length() == 0 )
        {
            throw new NullArgumentException( "File names must have a name (not only an extension)" );
        }
        return new ConfigurationFile(
            parentDirectory.getName() + "-" + pid,
            parentDirectory.getName(),
            configurationFile
        );
    }

    /**
     * Generates a pid out of a filename by removing the extension (what is after last .).
     *
     * @param fileName file name to be used to generate pid
     *
     * @return persistent pid
     */
    private static String generatePid( final String fileName )
    {
        if( fileName == null )
        {
            return null;
        }
        if( fileName.contains( "." ) )
        {
            return fileName.substring( 0, fileName.lastIndexOf( "." ) );
        }
        return fileName;
    }

    /**
     * Loads properties if not already loaded.
     *
     * @return properties
     */
    public Dictionary getProperties()
    {
        if( m_propertiesNotLoaded )
        {
            m_propertiesNotLoaded = false;
            try
            {
                final Properties properties = new Properties();
                properties.load( new BufferedInputStream( new FileInputStream( m_configurationFile ) ) );
                m_properties = new Hashtable( properties );
                // add default properties
                m_properties.put( Constants.SERVICE_PID, m_pid );
                if( m_factoryPid != null )
                {
                    m_properties.put( ConfigurationAdmin.SERVICE_FACTORYPID, m_factoryPid );
                }
            }
            catch( IOException ignore )
            {
                //TODO log exception
            }
        }
        return m_properties;
    }


}