/*
 * Copyright 2008 Alin Dreghiciu.
 *
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.cm.service.internal;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ops4j.pax.cm.api.DictionaryAdapter;

/**
 * A DictionaryAdapter wrapper that removes configuration unsupported types.
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 14, 2008
 */
class CleanupDictionaryAdapterWrapper
    extends DictionaryAdapterWrapper
{

    /**
     * Logger.
     */
    private static final Log LOG = LogFactory.getLog( CleanupDictionaryAdapterWrapper.class );

    /**
     * Supported value classes.
     */
    private static final Class[] SUPPORTED = {
        String.class,
        Integer.class,
        Long.class,
        Float.class,
        Double.class,
        Byte.class,
        Short.class,
        Character.class,
        Boolean.class,
        String[].class,
        Integer[].class,
        Long[].class,
        Float[].class,
        Double[].class,
        Byte[].class,
        Short[].class,
        Character[].class,
        Boolean[].class,
    };

    /**
     * Constructor.
     *
     * @param delegate wrapped DictionaryAdapter
     */
    CleanupDictionaryAdapterWrapper( final DictionaryAdapter delegate )
    {
        super( delegate );
    }

    /**
     * Delegates to wrapped DictionaryAdapter and removes unsuppoted keys/values.
     *
     * @see DictionaryAdapter#adapt(Object)
     */
    public Dictionary adapt( final Object object )
    {
        final Dictionary adapted = new Hashtable();
        DictionaryUtils.copy( m_delegate.adapt( object ), adapted );
        if( !adapted.isEmpty() )
        {
            final Enumeration keys = adapted.keys();
            while( keys.hasMoreElements() )
            {
                Object key = keys.nextElement();
                if( key instanceof String )
                {
                    Object value = adapted.get( key );
                    if( value == null )
                    {
                        continue;
                    }
                    // for vectors check entries
                    if( value instanceof Vector )
                    {
                        final Vector asVector = (Vector) value;
                        if( asVector.isEmpty() )
                        {
                            continue;
                        }
                        boolean unsupported = false;
                        for( Object vectorEntry : asVector )
                        {
                            unsupported = isSupportedType( vectorEntry );
                            if( unsupported )
                            {
                                break;
                            }
                        }
                        if( !unsupported )
                        {
                            continue;
                        }
                    }
                    else
                    {
                        if( isSupportedType( value ) )
                        {
                            continue;
                        }
                    }
                    LOG.trace(
                        "Removing property [" + key + "] due to unsupported value of type: " + value.getClass()
                    );
                }
                else
                {
                    LOG.trace( "Removing property [" + key + "] as only String keys are supported" );
                }
                adapted.remove( key );
            }
        }
        return adapted;
    }

    /**
     * Checks if the type of the object is supported.
     *
     * @param object to check
     *
     * @return true if object is null or it matches the supported classes
     */
    private boolean isSupportedType( final Object object )
    {
        if( object == null )
        {
            return true;
        }
        Class objectClass = object.getClass();
        for( Class clazz : SUPPORTED )
        {
            if( clazz.isAssignableFrom( objectClass ) )
            {
                return true;
            }
        }
        return false;
    }
}