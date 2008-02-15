package org.ops4j.pax.configmanager.internal;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.configmanager.IConfigurationFileHandler;

/**
 * {@code ConfigurationFileHandlerServiceTracker} tracks {@code IConfigurationFileHandler} services.
 *
 * @author Edward Yakop
 * @author Makas Tzavellas
 */
final class ConfigurationFileHandlerServiceTracker extends ServiceTracker
{

    private static final String SERVICE_NAME = IConfigurationFileHandler.class.getName();

    private final ConfigurationAdminFacade m_configurationFacade;

    ConfigurationFileHandlerServiceTracker( BundleContext context, ConfigurationAdminFacade facade )
        throws IllegalArgumentException
    {
        super( context, SERVICE_NAME, null );

        NullArgumentException.validateNotNull( facade, "facade" );
        m_configurationFacade = facade;
    }

    @Override
    public final Object addingService( ServiceReference reference )
    {
        IConfigurationFileHandler fileHandler = (IConfigurationFileHandler) super.addingService( reference );

        m_configurationFacade.addFileHandler( fileHandler );

        return fileHandler;
    }

    @Override
    public final void removedService( ServiceReference serviceReference, Object objService )
    {
        IConfigurationFileHandler fileHandler = (IConfigurationFileHandler) objService;

        m_configurationFacade.removeFileHandler( fileHandler );

        super.removedService( serviceReference, objService );
    }
}
