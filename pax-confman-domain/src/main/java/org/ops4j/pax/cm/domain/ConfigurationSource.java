package org.ops4j.pax.cm.domain;

import org.ops4j.lang.NullArgumentException;

/**
 * Configuration source model
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 14, 2008
 */
public class ConfigurationSource
{

    /**
     * Persistent identifier model.
     */
    private final Pid m_pid;
    /**
     * Properties source model.
     */
    private final PropertiesSource m_source;

    /**
     * Creates a new configuration source model.
     *
     * @param pid    persistent identifier model
     * @param source properties source
     *
     * @throws NullArgumentException - If pid is null
     *                               - If source is null
     */
    public ConfigurationSource( final Pid pid, final PropertiesSource source )
    {
        NullArgumentException.validateNotNull( pid, "Persistent identifier" );
        NullArgumentException.validateNotNull( source, "Pproperties source" );

        m_pid = pid;
        m_source = source;
    }

    /**
     * Getter.
     *
     * @return persistent identifier model
     */
    public Pid getPid()
    {
        return m_pid;
    }

    /**
     * Getter.
     *
     * @return Properties source model
     */
    public PropertiesSource getSource()
    {
        return m_source;
    }

    @Override
    public String toString()
    {
        return new StringBuilder( )
            .append( this.getClass().getSimpleName() )
            .append( "{" )
            .append( "pid=" ).append( m_pid )
            .append( "}" )
            .toString();
    }
    
}