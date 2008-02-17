package org.ops4j.pax.cm.domain;

import org.ops4j.lang.NullArgumentException;

/**
 * Configuration source model.
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 14, 2008
 */
public class ConfigurationSource
{

    /**
     * Configuration identity model.
     */
    private final Identity m_identity;
    /**
     * Properties source model.
     */
    private final PropertiesSource m_source;

    /**
     * Creates a new configuration source model.
     *
     * @param identity configuration identity
     * @param source   properties source
     *
     * @throws NullArgumentException - If configuration identity is null
     *                               - If source is null
     */
    public ConfigurationSource( final Identity identity,
                                final PropertiesSource source )
    {
        NullArgumentException.validateNotNull( identity, "configuration identity" );
        NullArgumentException.validateNotNull( source, "Pproperties source" );

        m_identity = identity;
        m_source = source;
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
     * @return Properties source model
     */
    public PropertiesSource getPropertiesSource()
    {
        return m_source;
    }

    @Override
    public String toString()
    {
        return new StringBuilder()
            .append( this.getClass().getSimpleName() )
            .append( "{" )
            .append( m_identity )
            .append( "," ).append( m_source )
            .append( "}" )
            .toString();
    }

}