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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
    private static final Log m_logger = LogFactory.getLog( BeanShellImporterTest.class );

    private static final String SUCCESSFULL_IMPORT_SCRIPT_FILE_NAME = "SuccessfullImport.bsh";

    private BeanShellImporter mBeanShellImporter;

    public BeanShellImporterTest( String testName )
    {
        super( testName );
    }

    public void setUp()
    {
        BundleContext mockBundleContext = newBundleContextMock();
        mBeanShellImporter = new BeanShellImporter( mockBundleContext, "beanShellImportPid" );
    }

    private BundleContext newBundleContextMock()
    {
        Mock mock = mock( BundleContext.class );
        return (BundleContext) mock.proxy();
    }

    public void testSuccessfullImport()
    {
        InputStream resourceAsStream = getClass().getResourceAsStream( SUCCESSFULL_IMPORT_SCRIPT_FILE_NAME );
        if( resourceAsStream == null )
        {
            fail( "Test file [" + SUCCESSFULL_IMPORT_SCRIPT_FILE_NAME + "] is not found." );
        }

        try
        {
            List<PaxConfiguration> paxConfigurations = mBeanShellImporter.performImport( resourceAsStream );

            if( paxConfigurations == null || paxConfigurations.isEmpty() || paxConfigurations.size() != 100 )
            {
                fail( "Import fail. There should be 100 pax configurations." );
            }
        }
        catch( ImportException importException )
        {
            fail( "Import failed. Error [" + importException.getMessage() + "]." );
        }
        finally
        {
            closeInputStream( resourceAsStream );
        }
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
}
