package org.ops4j.pax.cm.service.internal;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.cm.api.MetadataConstants;

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
     * Logger.
     */
    private static final Log LOG = LogFactory.getLog( ProcessableConfiguration.class );
    /**
     * Key specification that eliminates INFO keys.
     */
    private final static DictionaryUtils.KeySpecification NOT_INFO_KEY_SPEC =
        new DictionaryUtils.NotSpecification(
            new DictionaryUtils.RegexSpecification( MetadataConstants.INFO_PREFIX_AS_REGEX )
        );

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

        LOG.trace( "Looking for a configuration for " + m_pid );
        final Configuration configuration = configurationAdmin.getConfiguration( m_pid, m_location );
        if( configuration != null )
        {
            LOG.trace( "Found configuration with properties: " + configuration.getProperties() );
            if( configurationsAreNotEqual( configuration.getProperties(), m_properties ) )
            {
                configuration.setBundleLocation( m_location );
                configuration.update( m_properties );
            }
            else
            {
                LOG.trace( "Configuration is the same as the processing one. Not updating" );
            }
        }
    }

    /**
     * Compare two configurations excluding the info entries.
     *
     * @param target target dictionary to compare
     * @param source source dictionary to compare
     *
     * @return true if configurations are not equal
     */
    private boolean configurationsAreNotEqual( final Dictionary source, final Dictionary target )
    {
        return !DictionaryUtils.equal(
            DictionaryUtils.copy( NOT_INFO_KEY_SPEC, source, new Hashtable() ),
            DictionaryUtils.copy( NOT_INFO_KEY_SPEC, target, new Hashtable() )
        );
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
