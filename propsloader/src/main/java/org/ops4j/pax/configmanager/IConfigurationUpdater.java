package org.ops4j.pax.configmanager;

import java.io.IOException;

import org.osgi.framework.InvalidSyntaxException;

public interface IConfigurationUpdater
{

    /**
     * Initiate an update of a managed service with a given service.pid.
     * 
     * @param servicePid the service.pid of the service that should be updated.
     * @throws IllegalStateException if no ConfigurationAdmin service is available
     * @throws IOException in case of errors while loading the configuration
     * @throws InvalidSyntaxException if there are erroneous filters while trying to retrieve configurations from the
     *             ConfigurationAdmin
     */
    public void updateConfiguration( String servicePid )
        throws IllegalStateException,
        IOException,
        InvalidSyntaxException;
}
