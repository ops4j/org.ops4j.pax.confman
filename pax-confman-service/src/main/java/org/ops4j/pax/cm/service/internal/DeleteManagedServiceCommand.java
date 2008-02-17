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
 * Deletes a configuration related to a managed service.
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 17, 2008
 */
class DeleteManagedServiceCommand
    implements Command<ConfigurationAdmin>
{

    /**
     * Logger.
     */
    private static final Log LOG = LogFactory.getLog( DeleteManagedServiceCommand.class );

    /**
     * Targeted service identity.
     */
    protected final ServiceIdentity m_serviceIdentity;

    /**
     * Constructor.
     *
     * @param serviceIdentity targeted service identity
     *
     * @throws NullArgumentException - If service identity is null
     */
    DeleteManagedServiceCommand( ServiceIdentity serviceIdentity )
    {
        NullArgumentException.validateNotNull( serviceIdentity, "Service identity" );

        m_serviceIdentity = serviceIdentity;
    }

    /**
     * Deletes the targeted configuration if configuation exists.
     *
     * @param configurationAdmin configuration admin service to be used
     *
     * @throws IOException           - re-thrown from configuration admin
     * @throws NullArgumentException - if configuration admin service is null
     */
    public void execute( final ConfigurationAdmin configurationAdmin )
        throws IOException
    {
        NullArgumentException.validateNotNull( configurationAdmin, "Configuration Admin service" );

        LOG.trace( "Looking for a configuration for " + m_serviceIdentity );
        final Configuration configuration = findConfiguration( configurationAdmin );
        if( configuration != null )
        {
            configuration.delete();
            LOG.info( "Deleted configuration " + m_serviceIdentity );
        }
        else
        {
            LOG.info( "Configuration " + m_serviceIdentity + " not found. Skipping delete." );
        }
    }

    /**
     * Search for configuration.
     *
     * @param configurationAdmin configuration admin service to be used
     *
     * @return found configuration or null if not found
     *
     * @throws IOException - re-thrown from configuration admin
     */
    protected Configuration findConfiguration( final ConfigurationAdmin configurationAdmin )
        throws IOException
    {
        return configurationAdmin.getConfiguration(
            m_serviceIdentity.getPid(),
            m_serviceIdentity.getLocation()
        );
    }

    @Override
    public String toString()
    {
        return new StringBuilder()
            .append( this.getClass().getSimpleName() )
            .append( "{" )
            .append( "identity=" ).append( m_serviceIdentity )
            .append( "}" )
            .toString();
    }

}