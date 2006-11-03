package org.ops4j.pax.configmanager.internal;

import org.apache.commons.logging.Log;
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
    private Log mLogger;

    private final BundleContext mBundleContext;
    private ConfigurationAdminFacade mFacade;

    ConfigAdminServiceTracker( BundleContext bundleContext, ConfigurationAdminFacade facade )
    {
        super( bundleContext, CONFIG_ADMIN_SERVICE_NAME, null );

        NullArgumentException.validateNotNull( facade, "facade" );

        mBundleContext = bundleContext;
        mFacade = facade;
    }

    @Override
    public Object addingService( ServiceReference serviceReference )
    {
        ConfigurationAdmin configAdmin = (ConfigurationAdmin) mBundleContext.getService( serviceReference );
        mFacade.setConfigurationAdminService( configAdmin );

        try
        {
            mFacade.registerConfigurations( null, false );
        }
        catch( Exception e )
        {
            mLogger.error( "Can't load configuration", e );
        }

        return configAdmin;
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
        mFacade.setConfigurationAdminService( null );

        mBundleContext.ungetService( serviceReference );
    }
}
