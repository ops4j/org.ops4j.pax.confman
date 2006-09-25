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
package org.ops4j.pax.cm.agent.importer.beanshell;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.apache.log4j.Logger;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.ops4j.pax.cm.agent.configuration.PaxConfiguration;
import org.ops4j.pax.cm.agent.importer.ImportException;
import org.osgi.framework.BundleContext;

/**
 * @author Edward Yakop
 * @since 0.1.0
 */
public final class BeanShellImporterTest extends MockObjectTestCase
{

    private static final Logger m_logger = Logger.getLogger( BeanShellImporterTest.class );

    private static final String SUCCESSFULL_IMPORT_SCRIPT_FILE_NAME = "SuccessfullImport.bsh";
    private static final String FAIL_IMPORT_SCRIPT_FILE_NAME = "FailImport.bsh";

    private BeanShellImporter m_beanShellImporter;

    public BeanShellImporterTest( String testName )
    {
        super( testName );
    }

    public void setUp()
    {
        Mock mock = mock( BundleContext.class );
        BundleContext mockBundleContext = (BundleContext) mock.proxy();
        m_beanShellImporter = new BeanShellImporter( mockBundleContext, "beanShellImportPid" );
    }

    public void testFailImport()
    {
        InputStream resourceAsStream = getResourceAsStream( FAIL_IMPORT_SCRIPT_FILE_NAME );

        try
        {
            m_beanShellImporter.performImport( resourceAsStream );

            fail( "Import should fail." );
        } catch( ImportException ie )
        {
            // Expected.
        }
        finally
        {
            closeInputStream( resourceAsStream );
        }
    }

    private InputStream getResourceAsStream( String resourceFileName )
    {
        InputStream resourceAsStream = getClass().getResourceAsStream( resourceFileName );

        if( resourceAsStream == null )
        {
            fail( "Test file [" + resourceFileName + "] is not found." );
        }

        return resourceAsStream;
    }

    private static void closeInputStream( InputStream inputStream )
    {
        if( inputStream != null )
        {
            try
            {
                inputStream.close();
            } catch( IOException e )
            {
                m_logger.warn( "Inputstream failed to be close [" + inputStream + "].", e );
            }
        }
    }

    public void testInputArgument()
    {
        try
        {
            m_beanShellImporter.performImport( null );

            fail( "[IllegalArgumentException] is expected to be thrown." );
        } catch( IllegalArgumentException e )
        {
            // Expected.
        }
    }

    public void testSuccessfullImport()
    {
        InputStream resourceAsStream = getResourceAsStream( SUCCESSFULL_IMPORT_SCRIPT_FILE_NAME );

        try
        {
            List<PaxConfiguration> paxConfigurations = m_beanShellImporter.performImport( resourceAsStream );

            if( paxConfigurations == null || paxConfigurations.isEmpty() || paxConfigurations.size() != 100 )
            {
                fail( "There should be 100 pax configurations." );
            }
        }
        catch( ImportException importException )
        {
            fail( "Import failed. Error [" + importException.getMessage() + "]." );
            m_logger.error( importException );
        }
        finally
        {
            closeInputStream( resourceAsStream );
        }
    }
}
