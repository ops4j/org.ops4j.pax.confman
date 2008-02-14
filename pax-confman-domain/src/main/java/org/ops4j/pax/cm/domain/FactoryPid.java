package org.ops4j.pax.cm.domain;

import org.ops4j.lang.NullArgumentException;

/**
 * Factory Persistent identifier model.
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 14, 2008
 */
public class FactoryPid
    extends Pid
{

    /**
     * Factory persistent identifier. Cannot be null.
     */
    private final String m_factoryPid;

    /**
     * Creates a new factory persistent identifier model.
     *
     * @param factoryPid factory persistent identifier
     * @param pid        persistent identifier
     * @param location   bundle location
     *
     * @throws NullArgumentException - If factory pid is null or empty
     *                               - If pid is null or empty
     */
    public FactoryPid( final String factoryPid, final String pid, final String location )
    {
        super( location, pid );

        NullArgumentException.validateNotEmpty( factoryPid, true, "Persistent identifier" );
        m_factoryPid = factoryPid;
    }

    /**
     * Getter.
     *
     * @return factory persistent pid
     */
    public String getFactoryPid()
    {
        return m_factoryPid;
    }

    @Override
    public String toString()
    {
        return new StringBuilder()
            .append( this.getClass().getSimpleName() )
            .append( "{" )
            .append( "factoryPid=" ).append( m_factoryPid )
            .append( ",pid=" ).append( getPid() )
            .append( ",location" ).append( getLocation() )
            .append( "}" )
            .toString();
    }

}
