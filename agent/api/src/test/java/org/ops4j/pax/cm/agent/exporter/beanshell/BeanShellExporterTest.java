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
package org.ops4j.pax.cm.agent.exporter.beanshell;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.ops4j.pax.cm.agent.configuration.PaxConfiguration;
import org.ops4j.pax.cm.agent.importer.beanshell.BeanShellImporter;
import org.osgi.framework.BundleContext;

public class BeanShellExporterTest extends MockObjectTestCase
{

    private static final int BUFFER_SIZE = 102400;
    private static final String BOGUS_EXPORTER_PID = "BeanShellExporter";
    private static final String BOGUS_IMPORTER_PID = "BeanShellImporter";
    private static final String BOGUS_PAX_CONFIGURATION_PID = "pid";

    private BeanShellExporter m_beanShellExporter;
    private BeanShellImporter m_beanShellImporter;
    private ByteArrayOutputStream m_outputStream;

    public BeanShellExporterTest( String testName )
    {
        super( testName );
    }

    protected void setUp()
        throws Exception
    {
        m_outputStream = new ByteArrayOutputStream( BUFFER_SIZE );

        Mock mock = mock( BundleContext.class );
        BundleContext mockBundleContext = (BundleContext) mock.proxy();

        m_beanShellExporter = new BeanShellExporter( mockBundleContext, BOGUS_EXPORTER_PID );
        m_beanShellImporter = new BeanShellImporter( mockBundleContext, BOGUS_IMPORTER_PID );
    }

    public void testExportManyConfiguration()
    {
        List<PaxConfiguration> configurations = new ArrayList<PaxConfiguration>();

        Hashtable properties = new Hashtable();
        properties.put( "key", "value" );
        int numberOfConfigurations = 10;
        for( int i = 0; i < numberOfConfigurations; i++ )
        {
            String pid = "pid_" + i;
            PaxConfiguration configuration = new PaxConfiguration( pid, false );
            configuration.setProperties( properties );

            configurations.add( configuration );
        }

        configurations = performExportAndImport( configurations );

        if( configurations == null || configurations.size() != numberOfConfigurations )
        {
            fail( "Number of configurations has to be [" + numberOfConfigurations + "]." );
        }

        int i = 0;
        for( PaxConfiguration configuration : configurations )
        {
            String exportedPid = configuration.getPid();
            String expectedPid = "pid_" + i;
            if( !expectedPid.equals( exportedPid ) )
            {
                fail( "Configration [" + i + "] must have pid [" + expectedPid + "], found [" + exportedPid + "]" );
            }

            i++;
        }
    }

    private List<PaxConfiguration> performExportAndImport( List<PaxConfiguration> configurations )
    {
        m_beanShellExporter.performExport( configurations, m_outputStream );

        byte[] exportedBeanShellScript = m_outputStream.toByteArray();
        ByteArrayInputStream inputStream = new ByteArrayInputStream( exportedBeanShellScript );

        System.out.println( "Exported script\n" + m_outputStream.toString() );

        try
        {
            return m_beanShellImporter.performImport( inputStream );
        }
        catch( Exception ie )
        {
            String exportedScript = m_outputStream.toString();
            fail( "Fail to import with error [" + ie.getMessage() + "]. with script:\n" + exportedScript );
        }
        return Collections.emptyList();
    }

    public void testExportNoConfigurations()
    {
        ArrayList<PaxConfiguration> noConfigurations = new ArrayList<PaxConfiguration>();
        List<PaxConfiguration> paxConfigurations = performExportAndImport( noConfigurations );

        if( paxConfigurations == null || !paxConfigurations.isEmpty() )
        {
            fail( "There must be no pax configurations exported." );
        }
    }

    public void testExportOneConfigurationWithWeirdInputString()
    {
        String pid = "pid\'\"";
        PaxConfiguration paxConfiguration = new PaxConfiguration( pid, false );

        String bundleLocation = "bund\'\"-\"leL\'ocation";
        paxConfiguration.setBundleLocation( bundleLocation );

        List<PaxConfiguration> configurations = Collections.singletonList( paxConfiguration );
        if( configurations == null || configurations.size() != 1 )
        {
            fail( "There must be only one pax configurations exported." );
        }

        configurations = performExportAndImport( configurations );

        PaxConfiguration configuration = configurations.get( 0 );
        String exportedBundleLocation = configuration.getBundleLocation();
        if( !bundleLocation.equals( exportedBundleLocation ) )
        {
            fail( "Bundle location must be [" + bundleLocation + "], found [" + exportedBundleLocation + "]." );
        }

        String exportedPid = configuration.getPid();
        if( !pid.equals( exportedPid ) )
        {
            fail( "Pid must be [" + pid + "], found [" + exportedPid + "]." );
        }
    }

    public void testExportSimpleKey()
    {
        PaxConfiguration configuration = new PaxConfiguration( BOGUS_PAX_CONFIGURATION_PID, false );
        Hashtable<String, Object> properties = new Hashtable<String, Object>();
        configuration.setProperties( properties );

        String keyInteger = "Integer";
        Integer valueInteger = new Integer( 0 );
        properties.put( keyInteger, valueInteger );

        String keyString = "String";
        String valueString = "String";
        properties.put( keyString, valueString );

        String keyShort = "Short";
        Short valueShort = new Short( (short) 0 );
        properties.put( keyShort, valueShort );

        String keyByte = "Byte";
        Byte valueByte = new Byte( (byte) 0 );
        properties.put( keyByte, valueByte );

        String keyLong = "Long";
        Long valueLong = new Long( 0 );
        properties.put( keyLong, valueLong );

        String keyDouble = "Double";
        Double valueDouble = new Double( 0.1 );
        properties.put( keyDouble, valueDouble );

        String keyFloat = "Float";
        Float valueFloat = new Float( 0.1 );
        properties.put( keyFloat, valueFloat );

        List<PaxConfiguration> paxConfigurations = Collections.singletonList( configuration );
        List<PaxConfiguration> exportedConfigurations = performExportAndImport( paxConfigurations );

        if( exportedConfigurations == null || exportedConfigurations.size() != 1 )
        {
            fail( "There must be only [1] pax configuration." );
        }

        configuration = exportedConfigurations.get( 0 );
        Dictionary exportedProperties = configuration.getProperties();

        if( exportedProperties == null )
        {
            fail( "Properties must not be [null]." );
        }

        testProperty( exportedProperties, keyInteger, valueInteger );
        testProperty( exportedProperties, keyString, valueString );
        testProperty( exportedProperties, keyShort, valueShort );
        testProperty( exportedProperties, keyByte, valueByte );
        testProperty( exportedProperties, keyLong, valueLong );
        testProperty( exportedProperties, keyDouble, valueDouble );
        testProperty( exportedProperties, keyFloat, valueFloat );
    }

    private void testProperty( Dictionary properties, String key, Object expectedValue )
    {
        Object value = properties.get( key );
        if( !expectedValue.equals( value ) )
        {
            fail( "Property with key [" + key + "] must has value [" + expectedValue + "], found [" + value + "]." );
        }
    }
}
