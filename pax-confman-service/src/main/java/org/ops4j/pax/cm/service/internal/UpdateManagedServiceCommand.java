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
import org.ops4j.pax.cm.domain.ConfigurationTarget;

/**
 * TODO add JavaDoc
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 12, 2008
 */
class UpdateManagedServiceCommand
    implements AdminCommand
{

    /**
     * Logger.
     */
    private static final Log LOG = LogFactory.getLog( UpdateManagedServiceCommand.class );
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
    private final ConfigurationTarget m_target;

    /**
     * Constructor.
     *
     * @param target configuration target
     *
     * @throws NullArgumentException - If target is null
     */
    UpdateManagedServiceCommand( ConfigurationTarget target )
    {
        NullArgumentException.validateNotNull( target, "Configuration target" );

        m_target = target;
    }

    /**
     * Process the configuration properties by updating the configuration via configuration admin.
     *
     * @param configurationAdmin configuration admin service to be used
     *
     * @throws IOException           - re-thrown if the configurations can not be persisted
     * @throws NullArgumentException - if configuration admin service is null
     */
    public void execute( final ConfigurationAdmin configurationAdmin )
        throws IOException
    {
        NullArgumentException.validateNotNull( configurationAdmin, "Configuration Admin service" );

        LOG.trace( "Looking for a configuration for " + m_target.getServiceIdentity() );
        final Configuration configuration = configurationAdmin.getConfiguration(
            m_target.getServiceIdentity().getPid(),
            m_target.getServiceIdentity().getLocation()
        );
        if( configuration != null )
        {
            LOG.trace( "Found configuration with properties: " + configuration.getProperties() );
            if( configurationsAreNotEqual( configuration.getProperties(),
                                           m_target.getPropertiesTarget().getProperties()
            ) )
            {
                configuration.setBundleLocation( m_target.getServiceIdentity().getLocation() );
                configuration.update( m_target.getPropertiesTarget().getProperties() );
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
            .append( "identity=" ).append( m_target.getServiceIdentity() )
            .append( "}" )
            .toString();
    }

}
