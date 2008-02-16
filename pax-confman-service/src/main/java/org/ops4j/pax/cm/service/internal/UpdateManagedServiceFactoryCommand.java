package org.ops4j.pax.cm.service.internal;

import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.cm.domain.ConfigurationTarget;

/**
 * Command for updating a managed service factory.
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 16, 2008
 */
class UpdateManagedServiceFactoryCommand
    extends UpdateManagedServiceCommand
{

    /**
     * Logger.
     */
    private static final Log LOG = LogFactory.getLog( UpdateManagedServiceFactoryCommand.class );

    /**
     * Constructor.
     *
     * @param target configuration target
     *
     * @throws org.ops4j.lang.NullArgumentException
     *          - If target is null
     */
    UpdateManagedServiceFactoryCommand( ConfigurationTarget target )
    {
        super( target );
    }

    /**
     * Process the configuration properties by updating the configuration via configuration admin.
     *
     * @param configurationAdmin configuration admin service to be used
     *
     * @throws java.io.IOException - re-thrown if the configurations can not be persisted
     * @throws org.ops4j.lang.NullArgumentException
     *                             - if configuration admin service is null
     */
    public void execute( final ConfigurationAdmin configurationAdmin )
        throws IOException
    {
        NullArgumentException.validateNotNull( configurationAdmin, "Configuration Admin service" );

        LOG.trace( "Looking for a factory configuration for " + m_target.getServiceIdentity() );
        Configuration configuration = configurationAdmin.createFactoryConfiguration(
            m_target.getServiceIdentity().getFactoryPid(),
            null
        );
        LOG.trace( "Created pid: " + configuration.getPid() );
        LOG.trace( "Created factory pid: " + configuration.getFactoryPid() );
        configuration.setBundleLocation( m_target.getServiceIdentity().getLocation() );
        configuration.update( m_target.getPropertiesTarget().getProperties() );
    }

}