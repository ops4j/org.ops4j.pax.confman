package org.ops4j.pax.cm.domain.mixin;

import java.util.Dictionary;
import org.qi4j.property.Property;
import org.ops4j.pax.cm.domain.composite.ConfigurationSourceComposite;

/**
 * Created by IntelliJ IDEA.
 * User: alindreghiciu
 * Date: Feb 13, 2008
 * Time: 8:01:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConfigurationSourceCompositeMixin
    implements ConfigurationSourceComposite
{

    private final Property<String> m_pid;
    private final Property<String> m_location;
    private final Property<Dictionary> m_metadata;
    private final Property<Object> m_source;

    public ConfigurationSourceCompositeMixin( final Property<String> pid,
                                              final Property<String> location,
                                              final Property<Dictionary> metadata,
                                              final Property<Object> source )
    {
        m_pid = pid;
        m_location = location;
        m_metadata = metadata;
        m_source = source;
    }

    public Property<String> pid()
    {
        return m_pid;
    }

    public Property<String> location()
    {
        return m_location;
    }

    public Property<Dictionary> metadata()
    {
        return m_metadata;
    }

    public Property<Object> configurationSource()
    {
        return m_source;
    }
}
