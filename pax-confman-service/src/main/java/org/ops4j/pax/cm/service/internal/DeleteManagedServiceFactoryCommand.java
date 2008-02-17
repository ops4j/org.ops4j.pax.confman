package org.ops4j.pax.cm.service.internal;

import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.cm.common.internal.processor.Command;
import org.ops4j.pax.cm.domain.ServiceIdentity;

/**
 * Deletes a configuration related to a managed service factory.
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 17, 2008
 */
class DeleteManagedServiceFactoryCommand
    extends DeleteManagedServiceCommand
{

    /**
     * Constructor.
     *
     * @param serviceIdentity targeted service identity
     *
     * @throws org.ops4j.lang.NullArgumentException
     *          - If service identity is null
     */
    DeleteManagedServiceFactoryCommand( ServiceIdentity serviceIdentity )
    {
        super( serviceIdentity );
    }

    /**
     * Search for configuration.
     *
     * @param configurationAdmin configuration admin service to be used
     *
     * @return found configuration or null if not found
     *
     * @throws java.io.IOException - re-thrown from configuration admin
     */
    protected Configuration findConfiguration( final ConfigurationAdmin configurationAdmin )
        throws IOException
    {
        // TODO implement find for a managed service factory
        return null;
    }

}