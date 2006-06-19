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
    private static final byte[] EXPORT_HEADER = "List configurations = new ArrayList();\n".getBytes();
    private static final byte[] EXPORT_FOOTER = "configurations;\n".getBytes();
    private static final byte[] EXPORT_NEW_CONFIGURATION =
        "PaxConfiguratio configuration = new PaxConfiguration();\n".getBytes();

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

    private void streamPropertiesIfValid( PaxConfiguration configuration, OutputStream stream )
        throws IOException
    {
        Dictionary properties = configuration.getProperties();
        if( properties != null )
        {
            Enumeration keys = properties.keys();
            while( keys.hasMoreElements() )
            {
                String key = (String) keys.nextElement();
                Object value = properties.get( key );

                String escapedKey = escaped( key );
                String valueString;
                if( value != null )
                {
                    if( value instanceof Vector )
                    {
                    }
                    else if( value instanceof Array )
                    {
                    }
                    valueString = "null";
                }
                else
                {
                    valueString = "null";
                }

                String statement = "dictionary.put( \"" + escapedKey + "\", " + valueString + ");\n";
                byte[] statementBytes = statement.getBytes();
                stream.write( statementBytes );
            }
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
}

