package org.ops4j.pax.configmanager.internal;

import java.io.IOException;

import org.ops4j.pax.configmanager.IConfigurationUpdater;
import org.osgi.framework.InvalidSyntaxException;

public class ConfigurationUpdater
    implements IConfigurationUpdater
{

    private ConfigurationAdminFacade m_adminFacade;

    public ConfigurationUpdater( final ConfigurationAdminFacade adminFacade )
    {
        super();
        this.m_adminFacade = adminFacade;
    }

    public void updateConfiguration( final String servicePid )
        throws IllegalStateException,
        IOException,
        InvalidSyntaxException
    {
        this.m_adminFacade.registerConfigurations( servicePid, true );
    }
}
