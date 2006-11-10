package org.ops4j.pax.configmanager.internal;

import java.util.Hashtable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ops4j.pax.configmanager.IConfigurationFileHandler;
import org.ops4j.pax.configmanager.internal.handlers.PropertiesFileConfigurationHandler;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.util.tracker.ServiceTracker;

public final class Activator implements BundleActivator
{

    private Log mLogger;

    private ServiceTracker mConfigTracker;
    private ConfigurationFileHandlerServiceTracker mConfigFileTracker;
    private ServiceRegistration mPropConfigHandlerServiceReg;
    private ConfigurationAdminFacade mConfigAdminFacade;

    public void start( BundleContext context )
        throws Exception
    {
    	mLogger = LogFactory.getLog( getClass() );
        if( mLogger.isDebugEnabled() )
        {
            Bundle contextBundle = context.getBundle();
            String symbolicName = contextBundle.getSymbolicName();
            mLogger.debug( "starting " + symbolicName + "..." );
        }

        registerPropertiesHandler(context);
        mConfigAdminFacade = new ConfigurationAdminFacade();

        mConfigTracker = new ConfigAdminServiceTracker( context, mConfigAdminFacade );
        mConfigTracker.open();

        mConfigFileTracker = new ConfigurationFileHandlerServiceTracker( context, mConfigAdminFacade );
        mConfigFileTracker.open();
    }

	private void registerPropertiesHandler( BundleContext context ) 
	{
		PropertiesFileConfigurationHandler handler = new PropertiesFileConfigurationHandler();
        mPropConfigHandlerServiceReg = context.registerService( IConfigurationFileHandler.class.getName(), handler, new Hashtable() );
	}

    public void stop( BundleContext context )
        throws Exception
    {
        if( mLogger.isDebugEnabled() )
        {
            Bundle contextBundle = context.getBundle();
            String symbolicName = contextBundle.getSymbolicName();
            mLogger.debug( "Stopping " + symbolicName );
        }

        mPropConfigHandlerServiceReg.unregister();
        mConfigFileTracker.close();
        mConfigTracker.close();

        mConfigAdminFacade.dispose();
    }
}
