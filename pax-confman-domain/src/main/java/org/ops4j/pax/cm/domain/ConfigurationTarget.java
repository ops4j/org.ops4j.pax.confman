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
     * Service identity model.
     */
    private final ServiceIdentity m_serviceIdentity;
    /**
     * Properties target model.
     */
    private final PropertiesTarget m_target;

    /**
     * Creates a new configuration target model.
     *
     * @param serviceIdentity service identity model
     * @param target          properties target
     *
     * @throws NullArgumentException - If serviceIdentity is null
     *                               - If target is null
     */
    public ConfigurationTarget( final ServiceIdentity serviceIdentity,
                                final PropertiesTarget target )
    {
        NullArgumentException.validateNotNull( serviceIdentity, "Service identity" );
        NullArgumentException.validateNotNull( target, "Properties target" );

        m_serviceIdentity = serviceIdentity;
        m_target = target;
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
            .append( "identity=" ).append( m_serviceIdentity )
            .append( ",target=" ).append( m_target )
            .append( "}" )
            .toString();
    }

}