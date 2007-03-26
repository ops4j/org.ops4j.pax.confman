package org.ops4j.pax.configmanager.internal;

import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.configmanager.IConfigurationFileHandler;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * {@code ConfigurationFileHandlerServiceTracker} tracks {@code IConfigurationFileHandler} services.
 *
 * @author Edward Yakop
 * @author Makas Tzavellas
 */
final class ConfigurationFileHandlerServiceTracker extends ServiceTracker
{

    private static final String SERVICE_NAME = IConfigurationFileHandler.class.getName();

    private final ConfigurationAdminFacade mConfigurationFacade;

    public ConfigurationFileHandlerServiceTracker( BundleContext bundleContext,
                                                   ConfigurationAdminFacade iConfigurationFacade )
    {
        super( bundleContext, SERVICE_NAME, null );
        NullArgumentException.validateNotNull( iConfigurationFacade, "iConfigurationFacade" );

        mConfigurationFacade = iConfigurationFacade;
    }

    @Override
    public Object addingService( ServiceReference serviceReference )
    {
        IConfigurationFileHandler tConfigFileHandler = (IConfigurationFileHandler) context.getService( serviceReference );
        mConfigurationFacade.addFileHandler( tConfigFileHandler, context );
        return tConfigFileHandler;
    }

    @Override
    public void modifiedService( ServiceReference serviceReference, Object service )
    {
        removedService( serviceReference, service );
        addingService( serviceReference );
    }

    @Override
    public void removedService( ServiceReference serviceReference, Object service )
    {
        IConfigurationFileHandler tConfigFileHandler = (IConfigurationFileHandler) service;

        mConfigurationFacade.removeFileHandler( tConfigFileHandler );
        context.ungetService( serviceReference );
    }
}
