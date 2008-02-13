package org.qi4j.property;

/**
 * Created by IntelliJ IDEA.
 * User: alindreghiciu
 * Date: Feb 13, 2008
 * Time: 7:51:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class PropertyMixin<T>
    implements Property<T>
{

    private T m_value;

    public T get()
    {
        return m_value;
    }

    public void set( final T newValue )
    {
        m_value = newValue;
    }
}
