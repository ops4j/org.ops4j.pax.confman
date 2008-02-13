package org.qi4j.property;

/**
 * Created by IntelliJ IDEA.
 * User: alindreghiciu
 * Date: Feb 13, 2008
 * Time: 7:56:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReadOnlyPropertyMixin<T>
    implements Property<T>
{

    private final PropertyMixin<T> m_delegate;

    public ReadOnlyPropertyMixin( final T value )
    {
        m_delegate = new PropertyMixin<T>();
        m_delegate.set( value );
    }

    public T get()
    {
        return m_delegate.get();
    }

    public void set( T newValue )
        throws PropertyVetoException
    {
        throw new PropertyVetoException( "Read only property" );
    }
}
