package org.ops4j.pax.cm.service.internal;

import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.cm.common.internal.processor.Command;
import org.ops4j.pax.cm.domain.Identity;

/**
 * Deletes a configuration using Configuration Admin.
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 17, 2008
 */
abstract class DeleteCommand
    implements Command<ConfigurationAdmin>
{

    /**
     * Logger.
     */
    private static final Log LOG = LogFactory.getLog( DeleteCommand.class );

    /**
     * Targeted configuration identity.
     */
    final Identity m_identity;

    /**
     * Constructor.
     *
     * @param identity targeted configuration identity
     *
     * @throws NullArgumentException - If configuration identity is null
     */
    DeleteCommand( final Identity identity )
    {
        NullArgumentException.validateNotNull( identity, "configuration identity" );

        m_identity = identity;
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

        LOG.trace( "Looking for configuration with " + m_identity );
        final Configuration configuration = findConfiguration( configurationAdmin );
        if( configuration != null )
        {
            configuration.delete();
            LOG.info( "Deleted configuration with " + m_identity );
        }
        else
        {
            LOG.info( "Configuration with " + m_identity + " not found. Skipping delete." );
        }
    }

    /**
     * Search for a configuration.
     *
     * @param configurationAdmin configuration admin service to be used
     *
     * @return found configuration or null if not found
     *
     * @throws IOException - re-thrown from configuration admin
     */
    protected abstract Configuration findConfiguration( final ConfigurationAdmin configurationAdmin )
        throws IOException;

    @Override
    public String toString()
    {
        return new StringBuilder()
            .append( DeleteCommand.class.getSimpleName() )
            .append( "{" )
            .append( m_identity )
            .append( "}" )
            .toString();
    }

}