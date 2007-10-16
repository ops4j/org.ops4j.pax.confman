package org.ops4j.pax.configmanager.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.cm.ConfigurationAdmin;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.swissbox.tracker.ReplaceableServiceListener;

final class ConfigAdminServiceListener
    implements ReplaceableServiceListener<ConfigurationAdmin>
{

    /**
     * Logger.
     */
    private static final Log LOGGER = LogFactory.getLog( Activator.class );

    private ConfigurationAdminFacade m_facade;

    ConfigAdminServiceListener( final ConfigurationAdminFacade facade )
    {
        NullArgumentException.validateNotNull( facade, "Facade" );
        m_facade = facade;
    }

    public void serviceChanged( final ConfigurationAdmin oldService, final ConfigurationAdmin newService )
    {
        m_facade.setConfigurationAdminService( newService );
        try
        {
            m_facade.registerConfigurations( null, false );
        }
        catch( Exception e )
        {
            LOGGER.error( "Can't load configuration", e );
        }
    }

}
