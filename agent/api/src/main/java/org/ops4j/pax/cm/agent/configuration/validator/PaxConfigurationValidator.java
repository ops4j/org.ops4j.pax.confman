/*
 * Copyright 2006 Edward Yakop.
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
package org.ops4j.pax.cm.agent.configuration.validator;

import java.lang.reflect.Array;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Vector;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.cm.agent.configuration.PaxConfiguration;

/**
 * {@code PaxConfigurationValidator} validates {@code PaxConfiguration}.
 *
 * @author Edward Yakop
 * @since 0.1.0
 */
public final class PaxConfigurationValidator
{

    public static void validatePaxConfiguration( PaxConfiguration configuration )
        throws IllegalArgumentException
    {
        NullArgumentException.validateNotNull( configuration, "configuration" );

        String pid = configuration.getPid();
        String factoryPid = configuration.getFactoryPid();

        if( pid != null && factoryPid != null )
        {
            throw new InvalidPaxConfigurationException(
                "It must only have either [pid] or [factoryPid] defined", configuration
            );
        }

        validateProperites( configuration );
    }

    public static void validateProperites( PaxConfiguration configuration )
        throws IllegalArgumentException, InvalidPaxConfigurationException
    {
        NullArgumentException.validateNotNull( configuration, "configuration" );

        Dictionary properties = configuration.getProperties();

        if( properties != null )
        {
            Enumeration keys = properties.keys();

            while( keys.hasMoreElements() )
            {
                Object keyObject = keys.nextElement();

                if( !( keyObject instanceof String ) )
                {
                    throw new InvalidPaxConfigurationException(
                        "Key must be instance of [java.lang.String]", configuration
                    );
                }

                String key = (String) keyObject;
                Object value = properties.get( key );

                if( value == null )
                {
                    continue;
                }

                if( isSimple( value ) )
                {
                    continue;
                }

                if( value instanceof Vector )
                {
                    Vector vector = (Vector) value;
                    validateVector( key, vector, configuration );
                }

                Class<? extends Object> aClass = value.getClass();
                if( !aClass.isArray() )
                {
                    String msg = "Value of key [" + key + "] must be either a primitive or simple or an array of "
                                 + "simple or an array of primitive or a vector of simple. "
                                 + "Found [" + aClass.getName() + "]";
                    throw new InvalidPaxConfigurationException( msg, configuration );
                }

                validateArray( key, value, configuration );
            }
        }
    }

    private static boolean isSimple( Object value )
    {
        return value instanceof String || value instanceof Byte || value instanceof Number ||
               value instanceof Boolean || value instanceof Character;
    }

    private static void validateVector( String key, Vector vector, PaxConfiguration configuration )
        throws InvalidPaxConfigurationException
    {
        int i = 0;
        for( Object value : vector )
        {
            if( isSimple( value ) )
            {
                String msg = "Value of key [" + key + "] has vector item no [" + i + "] is not either an "
                             + "instance of String, Byte, Integer, Long, Float, Double, Short, Character, or Boolean";
                throw new InvalidPaxConfigurationException( msg, configuration );
            }
            i++;
        }
    }

    private static void validateArray( String key, Object array, PaxConfiguration configuration )
    {
        if( array == null )
        {
            return;
        }

        int length = Array.getLength( array );
        for( int i = 0; i < length; i++ )
        {
            Object value = Array.get( array, i );

            if( value == null )
            {
                continue;
            }

            if( isSimple( value ) )
            {
                continue;
            }

            Class<? extends Object> clz = value.getClass();
            Class<?> componentType = clz.getComponentType();
            if( Integer.TYPE.equals( componentType ) || Long.TYPE.equals( componentType ) ||
                Float.TYPE.equals( componentType ) || Double.TYPE.equals( componentType ) ||
                Byte.TYPE.equals( componentType ) || Short.TYPE.equals( componentType ) ||
                Character.TYPE.equals( componentType ) || Boolean.TYPE.equals( componentType ) )
            {
                continue;
            }

            String msg = "Value of key [" + key + "] is not either simple or primitive. Found [" + clz.getName() + "]";
            throw new InvalidPaxConfigurationException( msg, configuration );
        }
    }

    private PaxConfigurationValidator()
    {
    }
}
