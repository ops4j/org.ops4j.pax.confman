package org.ops4j.pax.configmanager.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ops4j.lang.NullArgumentException;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.util.tracker.ServiceTracker;

/**
 * {@code ConfigAdminServiceTracker} trackes configuration admin service.
 *
 * @author Edward Yakop
 * @author Makas Tzavellas
 */
final class ConfigAdminServiceTracker extends ServiceTracker
{

    private static final String CONFIG_ADMIN_SERVICE_NAME = ConfigurationAdmin.class.getName();
    private static final Log mLogger = LogFactory.getLog( ConfigAdminServiceTracker.class );

    private ConfigurationAdminFacade mFacade;

    ConfigAdminServiceTracker( BundleContext bundleContext, ConfigurationAdminFacade facade )
    {
        super( bundleContext, CONFIG_ADMIN_SERVICE_NAME, null );
        NullArgumentException.validateNotNull( facade, "facade" );
        mFacade = facade;
    }

    @Override
    public final Object addingService( ServiceReference serviceReference )
    {
        ConfigurationAdmin service = (ConfigurationAdmin) super.addingService( serviceReference );

        mFacade.setConfigurationAdminService( service );

        try
        {
            mFacade.registerConfigurations( null, false );
        }
        catch( Throwable e )
        {
            mLogger.error( "Can't load configuration", e );
        }

        return service;
    }

    @Override
    public void removedService( ServiceReference serviceReference, Object service )
    {
        mFacade.setConfigurationAdminService( null );
        context.ungetService( serviceReference );
    }
}
