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
     * Service identity model.
     */
    private final ServiceIdentity m_serviceIdentity;
    /**
     * Properties source model.
     */
    private final PropertiesSource m_source;

    /**
     * Creates a new configuration source model.
     *
     * @param serviceIdentity service identity
     * @param source          properties source
     *
     * @throws NullArgumentException - If service identity is null
     *                               - If source is null
     */
    public ConfigurationSource( final ServiceIdentity serviceIdentity,
                                final PropertiesSource source )
    {
        NullArgumentException.validateNotNull( serviceIdentity, "Service identity" );
        NullArgumentException.validateNotNull( source, "Pproperties source" );

        m_serviceIdentity = serviceIdentity;
        m_source = source;
    }

    /**
     * Getter.
     *
     * @return service identity model
     */
    public ServiceIdentity getServiceIdentity()
    {
        return m_serviceIdentity;
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
            .append( "identity=" ).append( m_serviceIdentity )
            .append( ",source=" ).append( m_source )
            .append( "}" )
            .toString();
    }

}