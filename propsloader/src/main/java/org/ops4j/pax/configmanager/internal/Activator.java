package org.ops4j.pax.configmanager.internal;

import java.util.Hashtable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ops4j.pax.configmanager.IConfigurationFileHandler;
import org.ops4j.pax.configmanager.internal.handlers.PropertiesFileConfigurationHandler;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

public final class Activator implements BundleActivator
{

    private static final Log LOGGER = LogFactory.getLog( Activator.class );
    private static final String SERVICE_NAME = IConfigurationFileHandler.class.getName();

    private ServiceTracker m_configTracker;
    private ConfigurationFileHandlerServiceTracker m_configFileTracker;
    private ServiceRegistration m_registration;
    private ConfigurationAdminFacade m_configAdminFacade;

    public void start( BundleContext context )
        throws Exception
    {
        if( LOGGER.isDebugEnabled() )
        {
            Bundle contextBundle = context.getBundle();
            String symbolicName = contextBundle.getSymbolicName();
            LOGGER.debug( "Starting [" + symbolicName + "]..." );
        }

        PropertiesFileConfigurationHandler handler = new PropertiesFileConfigurationHandler();
        m_registration = context.registerService( SERVICE_NAME, handler, new Hashtable() );
        m_configAdminFacade = new ConfigurationAdminFacade();

        m_configTracker = new ConfigAdminServiceTracker( context, m_configAdminFacade );
        m_configTracker.open();

        m_configFileTracker = new ConfigurationFileHandlerServiceTracker( context, m_configAdminFacade );
        m_configFileTracker.open();
    }

    public void stop( BundleContext context )
        throws Exception
    {
        if( LOGGER.isDebugEnabled() )
        {
            Bundle contextBundle = context.getBundle();
            String symbolicName = contextBundle.getSymbolicName();
            LOGGER.debug( "Stopping [" + symbolicName + "]" );
        }

        m_registration.unregister();
        m_registration = null;

        m_configFileTracker.close();
        m_configFileTracker = null;

        m_configTracker.close();
        m_configTracker = null;

        m_configAdminFacade.dispose();
        m_configAdminFacade = null;
    }
}
