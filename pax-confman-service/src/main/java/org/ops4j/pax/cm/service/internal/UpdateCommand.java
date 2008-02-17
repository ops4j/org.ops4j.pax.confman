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
import org.ops4j.pax.cm.common.internal.processor.Command;
import org.ops4j.pax.cm.domain.ConfigurationTarget;

/**
 * Updates a configuration using Configuration Admin.
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 12, 2008
 */
abstract class UpdateCommand
    implements Command<ConfigurationAdmin>
{

    /**
     * Logger.
     */
    private static final Log LOG = LogFactory.getLog( UpdateCommand.class );
    /**
     * Key specification that eliminates INFO keys.
     */
    private final static DictionaryUtils.KeySpecification NOT_INFO_KEY_SPEC =
        new DictionaryUtils.NotSpecification(
            new DictionaryUtils.RegexSpecification( MetadataConstants.INFO_PREFIX_AS_REGEX )
        );

    /**
     * Update configuration target.
     */
    final ConfigurationTarget m_target;

    /**
     * Constructor.
     *
     * @param target configuration target
     *
     * @throws NullArgumentException - If target is null
     */
    UpdateCommand( ConfigurationTarget target )
    {
        NullArgumentException.validateNotNull( target, "Configuration target" );

        m_target = target;
    }

    /**
     * Process the configuration properties by updating the configuration via configuration admin.
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

        LOG.trace( "Looking for a configuration for " + m_target.getIdentity() );
        final Configuration configuration = findConfiguration( configurationAdmin );
        if( configuration != null )
        {
            LOG.trace( "Found configuration with properties: " + configuration.getProperties() );
            if( !equal( configuration.getProperties(), m_target.getPropertiesTarget().getProperties() ) )
            {
                configuration.setBundleLocation( m_target.getIdentity().getLocation() );
                configuration.update( m_target.getPropertiesTarget().getProperties() );
                LOG.info( "Updated configuration " + m_target.getIdentity() );
            }
            else
            {
                LOG.info(
                    "Configuration " + m_target.getIdentity()
                    + " is the same as the processing one. Not updating"
                );
            }
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

    /**
     * Compare two configurations excluding the info entries.
     *
     * @param target target dictionary to compare
     * @param source source dictionary to compare
     *
     * @return true if configurations are equal
     */
    private boolean equal( final Dictionary source, final Dictionary target )
    {
        return DictionaryUtils.equal(
            DictionaryUtils.copy( NOT_INFO_KEY_SPEC, source, new Hashtable() ),
            DictionaryUtils.copy( NOT_INFO_KEY_SPEC, target, new Hashtable() )
        );
    }

    @Override
    public String toString()
    {
        return new StringBuilder()
            .append( DeleteCommand.class.getSimpleName() )
            .append( "{" )
            .append( m_target.getIdentity() )
            .append( "}" )
            .toString();
    }

}
