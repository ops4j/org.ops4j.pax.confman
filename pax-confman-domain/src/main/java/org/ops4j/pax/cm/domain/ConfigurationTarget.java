package org.ops4j.pax.cm.domain;

import org.ops4j.lang.NullArgumentException;

/**
 * Configuration target model.
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 14, 2008
 */
public class ConfigurationTarget
{

    /**
     * configuration identity model.
     */
    private final Identity m_identity;
    /**
     * Properties target model.
     */
    private final PropertiesTarget m_target;

    /**
     * Creates a new configuration target model.
     *
     * @param identity configuration identity model
     * @param target          properties target
     *
     * @throws NullArgumentException - If identity is null
     *                               - If target is null
     */
    public ConfigurationTarget( final Identity identity,
                                final PropertiesTarget target )
    {
        NullArgumentException.validateNotNull( identity, "configuration identity" );
        NullArgumentException.validateNotNull( target, "Properties target" );

        m_identity = identity;
        m_target = target;
    }

    /**
     * Getter.
     *
     * @return configuration identity model
     */
    public Identity getIdentity()
    {
        return m_identity;
    }

    /**
     * Getter.
     *
     * @return Properties target model
     */
    public PropertiesTarget getPropertiesTarget()
    {
        return m_target;
    }

    @Override
    public String toString()
    {
        return new StringBuilder()
            .append( this.getClass().getSimpleName() )
            .append( "{" )
            .append( "identity=" ).append( m_identity )
            .append( ",target=" ).append( m_target )
            .append( "}" )
            .toString();
    }

}