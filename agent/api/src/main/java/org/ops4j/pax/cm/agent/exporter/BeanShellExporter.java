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
package org.ops4j.pax.cm.agent.exporter;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.cm.agent.configuration.PaxConfiguration;

public final class BeanShellExporter
    implements Exporter
{

    private static final Log m_logger = LogFactory.getLog( BeanShellExporter.class );

    private static final byte[] EXPORT_HEADER =
        ( "package org.ops4j.pax.cm.agent.importer.beanshell;\n\n" +
          "import java.util.ArrayList;\n" +
          "import java.util.List;\n" +
          "import java.util.Properties;\n" +
          "import org.ops4j.pax.cm.agent.configuration.PaxConfiguration;\n" +
          "import org.ops4j.pax.cm.agent.importer.beanshell.Import;\n\n" +
          "public final class Importer\n" +
          "    implements Import\n" +
          "{\n" +
          "    public Importer()\n" +
          "    {\n" +
          "    }\n\n" +
          "    public List performImport()\n" +
          "    {\n" +
          "        List configurations = new ArrayList();\n" +
          "        PaxConfiguration configuration;\n" +
          "        HashTable dictionary;\n" ).getBytes();

    private static final byte[] EXPORT_FOOTER =
        ( "        return configurations;\n" +
          "    }\n" +
          "}\n" ).getBytes();

    private static final byte[] EXPORT_NEW_CONFIGURATION =
        ( "configuration = new PaxConfiguration();\n" +
          "configurations.add( configuration );\n" ).getBytes();

    private static final byte[] DICTIONARY_DECLARATIONS = "dictionary = new HashTable();\n".getBytes();

    public void exportConfiguration( List<PaxConfiguration> configurations, OutputStream stream )
        throws ExportException
    {
        NullArgumentException.validateNotNull( configurations, "configurations" );
        NullArgumentException.validateNotNull( stream, "stream" );

        try
        {
            stream.write( EXPORT_HEADER );

            for( PaxConfiguration configuration : configurations )
            {
                stream.write( EXPORT_NEW_CONFIGURATION );

                streamPIDIfValid( configuration, stream );
                streamFactoryPidIfValid( configuration, stream );
                streamBundleLocationIfValid( configuration, stream );
                streamPropertiesIfValid( configuration, stream );
            }

            stream.write( EXPORT_FOOTER );
        } catch( IOException ioe )
        {
            m_logger.fatal( ioe );
            throw new ExportException( "Stream fails.", ioe );
        }
    }

    private void streamPIDIfValid( PaxConfiguration configuration, OutputStream stream )
        throws IOException
    {
        String pid = configuration.getPid();
        if( pid != null )
        {
            String escaped = escaped( pid );
            String pidStatement = "configuration.setPid( \"" + escaped + "\" );\n";
            byte[] pidStatementBytes = pidStatement.getBytes();
            stream.write( pidStatementBytes );
        }
    }

    private static String escaped( String string )
    {
        return StringEscapeUtils.escapeJava( string );
    }

    private void streamFactoryPidIfValid( PaxConfiguration configuration, OutputStream stream )
        throws IOException
    {
        String factoryPid = configuration.getFactoryPid();
        if( factoryPid != null )
        {
            String escaped = escaped( factoryPid );
            String factoryPidStatement = "configuration.setFactoryPid( \"" + escaped + "\" );\n";
            byte[] factoryPidStatementBytes = factoryPidStatement.getBytes();
            stream.write( factoryPidStatementBytes );
        }
    }

    private void streamBundleLocationIfValid( PaxConfiguration configuration, OutputStream stream )
        throws IOException
    {
        String bundleLocation = configuration.getBundleLocation();
        if( bundleLocation != null )
        {
            String bundleLocationStatement = "configuration.setBundleLocation( \"" + bundleLocation + "\" );\n";
            byte[] bundleLocationStatementBytes = bundleLocationStatement.getBytes();
            stream.write( bundleLocationStatementBytes );
        }
    }

    private void streamPropertiesIfValid( PaxConfiguration configuration, OutputStream stream )
        throws IOException
    {
        Dictionary properties = configuration.getProperties();
        if( properties != null )
        {
            stream.write( DICTIONARY_DECLARATIONS );

            Enumeration keys = properties.keys();
            while( keys.hasMoreElements() )
            {
                String key = (String) keys.nextElement();
                Object value = properties.get( key );

                String escapedKey = escaped( key );
                String statement;
                if( value == null )
                {
                    statement = "dictionary.put( \"" + escapedKey + "\", null );\n";
                }
                else
                {
                    if( isSimple( value ) )
                    {
                        statement = "dictionary.put( \"" + escapedKey + "\", " + handleSimple( value ) + " );\n";
                    }
                    else if( value instanceof Vector )
                    {
                        statement = handleVector( escapedKey, value );
                    }
                    else if( value instanceof Array )
                    {
                        int length = Array.getLength( value );
                        if( isPrimitiveArray( value ) )
                        {
                            statement = handlePrimitiveArray( escapedKey, value, length );
                        }
                        else
                        {
                            statement = handleSimpleArray( escapedKey, value, length );
                        }
                    }
                    else
                    {
                        m_logger.debug( "Unhandled case [" + value.getClass().getName() + "]." );
                        continue;
                    }
                }

                byte[] statementBytes = statement.getBytes();
                stream.write( statementBytes );
            }
        }
    }

    private boolean isSimple( Object value )
    {
        if( value instanceof String || value instanceof Number || value instanceof Boolean
            || value instanceof Character )
        {
            return true;
        }

        return false;
    }

    private String handleSimple( Object value )
    {
        if( value == null )
        {
            return "null";
        }

        String clazz = value.getClass().getName().substring( 10 );
        if( value instanceof String )
        {
            String castedToString = value.toString();
            return "\"" + escaped( castedToString ) + "\";\n";
        }
        else if( value instanceof Character )
        {
            Character character = (Character) value;
            int charAsInt = (int) character;
            return "new Character( (char) " + charAsInt + " )";
        }

        return "new " + clazz + "( " + value.toString() + " );";
    }

    private String handleVector( String key, Object value )
    {
        if( value == null )
        {
            return "";
        }

        Vector vector = (Vector) value;

        StringBuffer statementBuffer = new StringBuffer();

        statementBuffer.append( "{\n" );
        statementBuffer.append( "Vector vector = new Vector();" );

        int i = 0;
        for( Object item : vector )
        {
            i++;

            String valueStatement;
            if( isSimple( item ) )
            {
                valueStatement = handleSimple( item );
            }
            else
            {
                valueStatement = "null";

                String msg = "Property [" + key + "] of vector item [" + i + "] is not of simple type. (" + item + ")";
                m_logger.fatal( msg );
            }

            statementBuffer.append( "vector.add( " ).append( valueStatement ).append( " );" );
        }

        statementBuffer.append( "dictionary.put( \"" ).append( key ).append( "\", vector );\n" );
        statementBuffer.append( "}\n" );

        String statement = statementBuffer.toString();
        statementBuffer.setLength( 0 );
        return statement;
    }

    private boolean isPrimitiveArray( Object value )
    {
        Class aClass = value.getClass();
        if( !aClass.isArray() )
        {
            return false;
        }

        Class componentType = aClass.getComponentType();
        return componentType.isPrimitive();
    }

    private String handlePrimitiveArray( String key, Object array, int length )
    {
        if( array == null )
        {
            return "";
        }

        StringBuffer statementBuffer = new StringBuffer();

        Class clazz = array.getClass();
        Class componentType = clazz.getComponentType();
        String primitiveName = componentType.getName();

        statementBuffer.append( "{\n" );
        statementBuffer.append( primitiveName ).append( "[] temp = new " );
        statementBuffer.append( primitiveName ).append( "[" ).append( length ).append( "];\n" );

        for( int i = 0; i < length; i++ )
        {
            Object value = Array.get( array, i );

            String valueStatement;
            if( value == null )
            {
                valueStatement = "null";
            }
            else if( value instanceof Byte )
            {
                Byte aByte = (Byte) value;
                valueStatement = "(byte) " + aByte.intValue();
            }
            else if( value instanceof Character )
            {
                Character aChar = (Character) value;
                int charCode = (int) aChar.charValue();
                valueStatement = "(char) " + charCode;
            }
            else
            {
                valueStatement = value.toString();
            }

            statementBuffer.append( "temp[" ).append( i ).append( "] = " ).append( valueStatement ).append( ";\n" );
        }
        statementBuffer.append( "dictionary.put( \"" ).append( key ).append( "\", temp );\n" );
        statementBuffer.append( "}\n" );

        String statement = statementBuffer.toString();
        statementBuffer.setLength( 0 );

        return statement;
    }

    private String handleSimpleArray( String key, Object array, int length )
    {
        if( array == null )
        {
            return "";
        }

        StringBuffer statementBuffer = new StringBuffer();

        Class clazz = array.getClass();
        Class componentType = clazz.getComponentType();
        String simpleClassName = componentType.getName();

        statementBuffer.append( "{\n" );
        statementBuffer.append( simpleClassName ).append( "[] temp = new " );
        statementBuffer.append( simpleClassName ).append( "[" ).append( length ).append( "];\n" );

        for( int i = 0; i < length; i++ )
        {
            Object item = Array.get( array, i );

            String valueStatement;
            if( item == null )
            {
                valueStatement = "null";
            }
            else if( isSimple( item ) )
            {
                valueStatement = handleSimple( item );
            }
            else
            {
                valueStatement = "null";

                String msg = "Property [" + key + "] of array item [" + i + "] is not of simple type. (" + item + ")";
                m_logger.fatal( msg );
            }

            statementBuffer.append( "temp[" ).append( i ).append( "] = " ).append( valueStatement ).append( ";\n" );
        }
        statementBuffer.append( "dictionary.put( \"" ).append( key ).append( "\", temp );\n" );
        statementBuffer.append( "}\n" );

        String statement = statementBuffer.toString();
        statementBuffer.setLength( 0 );

        return statement;
    }
}