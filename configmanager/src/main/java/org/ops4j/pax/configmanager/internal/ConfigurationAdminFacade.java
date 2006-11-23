package org.ops4j.pax.configmanager.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.configmanager.IConfigurationFileHandler;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * {@code ConfigurationAdminFacade} has most of the code from the old {@code Activator}.
 *
 * @author Edward Yakop
 * @author Makas Tzavellas
 */
final class ConfigurationAdminFacade
{

    /**
     * System property to set where the ConfigurationAdminFacade should load the configuration files
     * from. 
     */
    public static final String BUNDLES_CONFIGURATION_LOCATION = "bundles.configuration.location";
    
    private Log mLogger;

    private ConfigurationAdmin mConfigAdminService;

    private final List<IConfigurationFileHandler> mHandlers;

    public ConfigurationAdminFacade()
    {
        mLogger = LogFactory.getLog( getClass() );
        mHandlers = new ArrayList<IConfigurationFileHandler>();
    }

    /**
     * Add the specified {@code handler} to this {@code ConfigurationAdminFacade}. The handler will be used to handle
     * configuration file during {@code registerConfigurations}.
     *
     * @param handler The file handler. This argument must not be {@code null}.
     * @param bundleContext TODO
     *
     * @throws IllegalArgumentException Thrown if the specified {@code handler} is {@code null}.
     */
    final void addFileHandler( IConfigurationFileHandler handler, BundleContext bundleContext )
        throws IllegalArgumentException
    {
        NullArgumentException.validateNotNull( handler, "handler" );

        synchronized( mHandlers )
        {
            mHandlers.add( 0, handler );

            try
            {
                // Reload all configurations just in case if this is added later
                registerConfigurations( null, false, bundleContext );
            } catch( IOException e )
            {
                String tMsg = "IOException by either getting the configuration admin or loading the configuration file.";
                mLogger.error( tMsg, e );
            } catch( InvalidSyntaxException e )
            {
                mLogger.error( "Invalid syntax. This should not happened.", e );
            }
        }
    }

    /**
     * Registers configuration for OSGi Managed services.
     *
     * @param configuration if null then all configuration found will be registered.
     * @param overwrite   A {@code boolean} indicator to overwrite the configuration
     * @param bundleContext TODO
     * @throws java.io.IOException   Thrown if there is an IO problem during loading of {@code configuration}.
     * @throws org.osgi.framework.InvalidSyntaxException
     *                               Thrown if there is an invalid exception during retrieval of configurations.
     * @throws IllegalStateException Thrown if the configuration admin service is not available.
     */
    final void registerConfigurations( String configuration, boolean overwrite, BundleContext bundleContext )
        throws IOException, InvalidSyntaxException, IllegalStateException
    {
        if( mConfigAdminService == null )
        {
            throw new IllegalStateException( "Configuration admin service is not available. Please start configuration admin bundle." );
        }

        File configDir = getConfigDir();
        if( configDir == null )
        {
            return;
        }
        
        Configuration[] existingConfigurations;

        synchronized( this )
        {
            existingConfigurations = mConfigAdminService.listConfigurations( null );
        }
        HashSet<String> configCache = new HashSet<String>();
        if( existingConfigurations != null && !overwrite )
        {
            for( Configuration existingConfiguration : existingConfigurations )
            {
                configCache.add( existingConfiguration.getPid() );
            }
        }
        // Create configuration for ManagedServiceFactory
        createConfiguration( configuration, configDir, configCache, true );
        // Create configuration for ManagedService
        createConfiguration( configuration, configDir, configCache, false );
    }

    private void createConfiguration( String configuration, File configDir, HashSet<String> configCache, boolean isFactory ) throws IOException
    {
        File dir = null;
        if( isFactory )
        {
            dir = new File( configDir, "factories" );
        }
        else
        {
            dir = new File( configDir, "services" );
        }
        if( !dir.exists() )
        {
            return;
        }
        String[] files = dir.list();
        for( String configFile : files )
        {
            if( configuration != null && !configFile.equals( configuration ) )
            {
                continue;
            }

            // If configuration already exist for the service, dont update.
            // Will be empty if iIsOverwrite is true.
            if( configCache.contains( configFile ) )
            {
                continue;
            }

            File f = new File( dir, configFile );
            if( !f.isDirectory() )
            {
                List<IConfigurationFileHandler> handlers;

                synchronized( mHandlers )
                {
                    handlers = new ArrayList<IConfigurationFileHandler>( mHandlers );
                }

                for( IConfigurationFileHandler handler : handlers )
                {
                    if( handler.canHandle( f ) )
                    {
                        String servicePid = handler.getServicePID( configFile );
                        Properties prop = handler.handle( f );
                        System.out.println( prop );
                        Configuration conf = null;
                        // Find out if a service pid is included, use it if it does
                        String str = (String) prop.get( Constants.SERVICE_PID );
                        if( str != null )
                        {
                            servicePid = str;
                        }
                        synchronized( this )
                        {
                            if( isFactory )
                            {
                                conf = mConfigAdminService.createFactoryConfiguration( servicePid, null );
                            }
                            else
                            {
                                conf = mConfigAdminService.getConfiguration( servicePid, null );
                            }
                        }

                        conf.update( prop );
                        mLogger.info( "Register configuration [" + servicePid + "]" );
                    }
                }
            }
        }
    }

    private File getConfigDir()
    {
        String configArea = System.getProperty( BUNDLES_CONFIGURATION_LOCATION );
        // Only run the configuration changes if the configArea is set.
        if( configArea == null )
            return null;
        mLogger.info( "Using configuration from '" + configArea + "'" );
		File dir = new File( configArea );
		if( !dir.exists() )
		{
			mLogger.error( "Configuration area '" + configArea + "' does not exist. Unable to load properties." );
			return null;
		}
		return dir;
    }

    /**
     * Dispose this {@code ConfigurationAdminFacade} instance. Once this object instance is disposed, it is not meant to
     * be used again.
     *
     */
    void dispose()
    {
        mConfigAdminService = null;
        mHandlers.clear();
    }

    final void printConfigFileList( PrintWriter writer, String fileName )
    {
        File configDir = getConfigDir();

        if( configDir == null )
        {
            writer.println( "Configuration dir is not setup." );
            return;
        }

        if( fileName != null )
        {
            printConfiguration( writer, fileName, configDir );
            return;
        }

        String configAbsolutePath = configDir.getAbsolutePath();
        writer.println( "config dir: '" + configAbsolutePath + "' contains the following config files:" );
        String[] files = configDir.list();
        for( String file : files )
        {
            writer.println( file );
        }
    }

    private void printConfiguration( PrintWriter writer, String fileName, File configDir )
    {
        File configFile = new File( configDir, fileName );
        if( !configFile.canRead() || !configFile.exists() )
        {
            writer.println( "Can't read configfile '" + configFile.getAbsolutePath() + "'" );
            return;
        }

        Properties props = new Properties();
        try
        {
            InputStream in = new FileInputStream( configFile );
            props.load( in );
        }
        catch( Exception e )
        {
            String message = "Can't read configfile '" + configFile.getAbsolutePath() + "' - not a correct config file";
            writer.println( message );
            return;
        }

        writer.println( "Config file: " + configFile.getAbsolutePath() );
        for( Object keyObject : props.keySet() )
        {
            String key = (String) keyObject;
            String value = props.getProperty( key );
            writer.println( key + " = " + value );
        }
    }

    /**
     * Remove the specified {@code handler} from this {@code ConfigurationAdminFacade}.
     *
     * @param handler The handler to be removed. This argument must not be {@code null}.
     *
     * @throws IllegalArgumentException Thrown if the specified {@code handler} is {@code null}.
     */
    final void removeFileHandler( IConfigurationFileHandler handler )
        throws IllegalArgumentException
    {
        NullArgumentException.validateNotNull( handler, "handler" );

        synchronized( mHandlers )
        {
            mHandlers.remove( handler );
        }
    }

    /**
     * Set the configuration admin service. Sets to {@code null} if the configuration admin service is not available.
     *
     * @param configurationAdminService The configuration admin.
     */
    final void setConfigurationAdminService( ConfigurationAdmin configurationAdminService )
    {
        synchronized( this )
        {
            mConfigAdminService = configurationAdminService;
        }
    }
}