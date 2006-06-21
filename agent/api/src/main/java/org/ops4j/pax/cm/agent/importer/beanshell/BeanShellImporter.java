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

import bsh.BshClassManager;
import bsh.EvalError;
import bsh.Interpreter;
import bsh.NameSpace;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import org.ops4j.pax.cm.agent.configuration.PaxConfiguration;
import org.ops4j.pax.cm.agent.importer.AbstractImporter;
import org.ops4j.pax.cm.agent.importer.ImportException;
import org.ops4j.pax.cm.agent.importer.Importer;
import org.osgi.framework.BundleContext;

/**
 * @author Edward Yakop
 * @since 0.1.0
 */
public final class BeanShellImporter extends AbstractImporter
    implements Importer
{
    /**
     * The importer id of {@code BeanShellImporter}.
     *
     * @since 0.1.0
     */
    public static final String ID = "BeanShell";
    private static final String IMPORTER_CLASS_NAME = "org.ops4j.pax.cm.agent.importer.beanshell.Importer";

    /**
     * Construct instance of {@code BeanShellImporter} with the specified arguments.
     *
     * @param bundleContext The bundle context. This argument must not be {@code null}.
     * @param servicePID    The service pid. This argument must not be {@code null} or empty.
     *
     * @throws IllegalArgumentException Thrown if one or both arguments are {@code null}.
     * @since 0.1.0
     */
    public BeanShellImporter( BundleContext bundleContext, String servicePID )
        throws IllegalArgumentException
    {
        super( bundleContext, servicePID, ID );
    }

    public List<PaxConfiguration> performImport( InputStream inputStream )
        throws ImportException
    {
        if( inputStream == null )
        {
            return Collections.emptyList();
        }

        Interpreter interpreter = new Interpreter();
        NameSpace nameSpace = interpreter.getNameSpace();
        interpreter.setClassLoader( getClass().getClassLoader() );
        initializeNamespace( nameSpace );

        InputStreamReader in = new InputStreamReader( inputStream );
        Reader reader = new BufferedReader( in );

        try
        {
            interpreter.eval( reader );
        } catch( EvalError evalError )
        {
            throw new ImportException( "Fail to evaluate.", evalError );
        }

        BshClassManager classManager = interpreter.getClassManager();

        if( classManager.classExists( IMPORTER_CLASS_NAME ) )
        {
            Class clazz = classManager.classForName( IMPORTER_CLASS_NAME );

            try
            {
                Import importz = (Import) clazz.newInstance();
                return importz.performImport();
            } catch( InstantiationException e )
            {
                throw new ImportException( "Fail to instantiate [" + IMPORTER_CLASS_NAME + "].", e );
            } catch( IllegalAccessException e )
            {
                throw new ImportException( "Fail to instantiate [" + IMPORTER_CLASS_NAME + "].", e );
            }
        }
        else
        {
            throw new ImportException( "Input stream does not have [" + IMPORTER_CLASS_NAME + "] defined." );
        }
    }

    private static void initializeNamespace( NameSpace nameSpace )
    {
        nameSpace.importClass( List.class.getName() );
        nameSpace.importClass( ArrayList.class.getName() );
        nameSpace.importClass( Dictionary.class.getName() );
        nameSpace.importClass( Hashtable.class.getName() );
        nameSpace.importClass( Properties.class.getName() );
        nameSpace.importClass( PaxConfiguration.class.getName() );
        nameSpace.importClass( Import.class.getName() );
    }
}
