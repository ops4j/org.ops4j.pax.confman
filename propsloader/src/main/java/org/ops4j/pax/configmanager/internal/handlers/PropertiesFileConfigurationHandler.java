package org.ops4j.pax.configmanager.internal.handlers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.configmanager.IConfigurationFileHandler;

/**
 * {@code PropertiesConfigurationFileHandler} handles configuration files with extension of {@code .properties}.
 *
 * @author Edward Yakop
 * @author Makas Tzavellas
 */
public final class PropertiesFileConfigurationHandler
    implements IConfigurationFileHandler
{

    private static final String PROPERTIES_EXTENSION_FILE_NAME = ".properties";

    private static final Log LOGGER = LogFactory.getLog( PropertiesFileConfigurationHandler.class );

    public PropertiesFileConfigurationHandler()
    {
    }

    /**
     * Returns the configuration name of the specified {@code fileName}. The specified {@code fileName} must not have
     * file path prefix. Returns {@code null} if the file name can not be handled by this handler.
     *
     * @param fileName The file name. This argument must not be {@code null}.
     *
     * @return Returns the configuration name given the {@code fileName} argument.
     */
    public final String getServicePID( String fileName )
    {
        NullArgumentException.validateNotEmpty( fileName, "fileName" );

        if( fileName.endsWith( PROPERTIES_EXTENSION_FILE_NAME ) )
        {
            int index = fileName.lastIndexOf( PROPERTIES_EXTENSION_FILE_NAME );
            return fileName.substring( 0, index );
        }

        return null;
    }

    /**
     * Returns the properties after loading the specified {@code file}. Returns {@code empty} properties if the
     * specified {@code file} does not have any configuration properties.
     *
     * @param file The configuration file to be loaded. This argument must not be {@code null}.
     *
     * @return Returns the {@code Properties} of the specified {@code file}.
     *
     * @throws IllegalArgumentException thrown if the specified {@code file} argument is {@code null}.
     * @since 1.0.0
     */
    public final Properties handle( File file )
        throws IllegalArgumentException
    {
        NullArgumentException.validateNotNull( file, "file" );

        try
        {
            Properties prop = new Properties();
            FileInputStream fis = new FileInputStream( file );
            prop.load( fis );
            return prop;
        } catch( IOException e )
        {
            // TODO: handle error handling
            LOGGER.error( "Fail to handle file [" + file.getAbsolutePath() + "] configuration property.", e );
        }

        return null;
    }

    /**
     * Returns {@code true} if the specified {@code file} can be handled by this {@code IConfigurationFileHandler}.
     *
     * @param file The file to be handled. This argument must not be {@code null}.
     *
     * @return A {@code boolean} indicator whether this {@code IConfigurationFileHandler} able to handle {@code file}.
     *
     * @throws IllegalArgumentException Thrown if the specified {@code file} argument is {@code null}.
     * @since 1.0.6
     */
    public final boolean canHandle( File file )
        throws IllegalArgumentException
    {
        NullArgumentException.validateNotNull( file, "file" );

        String fileName = file.getName();
        return fileName.endsWith( PROPERTIES_EXTENSION_FILE_NAME );
    }
}
