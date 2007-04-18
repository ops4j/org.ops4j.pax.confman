package org.ops4j.pax.configmanager;

import java.io.File;
import java.util.Properties;

public interface IConfigurationFileHandler
{

    /**
     * Returns the configuration name of the specified {@code fileName}. The specified {@code fileName} must not have
     * file path prefix. Returns {@code null} if the file name can not be handled by this handler.
     *
     * @param fileName The file name. This argument must not be {@code null}.
     *
     * @return Returns the service pid given the {@code fileName} argument.
     */
    String getServicePID( String fileName );

    /**
     * Returns the properties after loading the specified {@code file}. Returns {@code empty} properties if the
     * specified {@code file} does not have any configuration properties.
     *
     * @param file The configuration file to be loaded. This argument must not be {@code null}.
     *
     * @return Returns the {@code Properties} of the specified {@code file}.
     */
    Properties handle( File file );

    /**
     * Returns {@code true} if the specified {@code file} can be handled by this {@code IConfigurationFileHandler}.
     *
     * @param file The file to be handled. This argument must not be {@code null}.
     *
     * @return A {@code boolean} indicator whether this {@code IConfigurationFileHandler} able to handle {@code file}.
     */
    boolean canHandle( File file );
}
