package org.ops4j.pax.cm.configurer.internal;

import java.io.IOException;
import java.util.Dictionary;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.ops4j.lang.NullArgumentException;

/**
 * TODO add JavaDoc
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 12, 2008
 */
class ProcessableConfiguration
    implements Processable
{

    /**
     * Process identifier. Cannot be null.
     */
    private final String m_pid;
    /**
     * Bound location. Can be null meaning an unbound location.
     */
    private final String m_location;
    /**
     * Dictionary of configuration properties.
     */
    private final Dictionary m_properties;

    /**
     * Creates a new configuration to be processed.
     *
     * @param pid        persistent identifier; cannot be null
     * @param location   the bundle location string; can be null
     * @param properties the new set of properties; can be null
     *
     * @throws NullArgumentException - If pid is null
     */
    ProcessableConfiguration( final String pid,
                              final String location,
                              final Dictionary properties )
    {
        NullArgumentException.validateNotEmpty( pid, true, "Persistent identifier" );

        m_pid = pid;
        m_location = location;
        m_properties = properties;
    }

    /**
     * Process the configuration properties by updating the configuration via configuration admin.
     *
     * @param configurationAdmin configuration admin service to be used
     *
     * @throws IOException           - re-thrown if the configurations can not be persisted
     * @throws NullArgumentException - if configuration admin service is null
     */
    public void process( final ConfigurationAdmin configurationAdmin )
        throws IOException
    {
        NullArgumentException.validateNotNull( configurationAdmin, "Configuration Admin service" );

        final Configuration configuration = configurationAdmin.getConfiguration( m_pid, m_location );
        if( configuration != null )
        {
            configuration.setBundleLocation( m_location );
            configuration.update( m_properties );
        }
    }

    @Override
    public String toString()
    {
        return new StringBuilder()
            .append( this.getClass().getSimpleName() )
            .append( "{" )
            .append( "pid=" ).append( m_pid )
            .append( "}" )
            .toString();
    }
}
