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
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.lang.reflect.InvocationTargetException;
import org.ops4j.pax.cm.agent.configuration.PaxConfiguration;
import org.ops4j.pax.cm.agent.importer.AbstractImporter;
import org.ops4j.pax.cm.agent.importer.ImportException;
import org.ops4j.pax.cm.agent.importer.Importer;
import org.osgi.framework.BundleContext;

/**
 * {@code BeanShellImporter}
 *
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

    /**
     * The beanshell must have class with the following full name defined.
     *
     * @since 0.1.0
     */
    private static final String IMPORTER_CLASS_NAME = "Importer";

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

    /**
     * Perform import on data specified by {@code inputStream}.
     *
     * @param inputStream The input stream. This argument must not be {@code null}.
     *
     * @return List of {@code PaxConfiguration}, returns empty collection if there is no configuration.
     *
     * @throws IllegalArgumentException Thrown if the specified {@code inputStream} is {@code null}.
     * @throws ImportException          Thrown if one or more of the following conditions fullfilled:
     *                                  <ul>
     *                                  <li>The script is not a valid bean shell script.</li>
     *                                  <li>The script does not declare class with name as {@code IMPORTER_CLASS_NAME}.</li>
     *                                  <li>The class with {@code Importer} name does not have default constructor.</li>
     *                                  <li>The class with {@code Importer} name default constructor is not {@code public}.</li>
     *                                  <li>The class with {@code Importer} name does not implement {@code Import} interface.</li>
     *                                  <li>The method {@code performImport} is not public.</li>
     *                                  </ul>
     * @see BeanShellImporter#IMPORTER_CLASS_NAME
     * @see Import
     * @see java.util.Collections#emptyList()
     * @since 0.1.0
     */
    public List<PaxConfiguration> performImport( InputStream inputStream )
        throws IllegalArgumentException, ImportException
    {
        if( inputStream == null )
        {
            throw new IllegalArgumentException( "[inputStream] argument is [null]." );
        }

        Interpreter interpreter = new Interpreter();
        NameSpace nameSpace = interpreter.getNameSpace();
        ClassLoader classLoader = BeanShellImporter.class.getClassLoader();
        interpreter.setClassLoader( classLoader );
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

        if( !classManager.classExists( IMPORTER_CLASS_NAME ) )
        {
            throw new ImportException( "Input stream does not have [" + IMPORTER_CLASS_NAME + "] class defined." );
        }

        Class clazz = classManager.classForName( IMPORTER_CLASS_NAME );

        try
        {
            Object instance = clazz.newInstance();

            Class<? extends Object> importClassName = instance.getClass();
            if( !Import.class.isAssignableFrom( importClassName ) )
            {
                throw new ImportException(
                    "[" + IMPORTER_CLASS_NAME + "] does not implement [" + Import.class.getName() + "] interface."
                );
            }

            Import importz = (Import) instance;
            return importz.performImport();
        } catch( InstantiationException e )
        {
            throw new ImportException( "Fail to instantiate [" + IMPORTER_CLASS_NAME + "].", e );
        } catch( IllegalAccessException e )
        {
            throw new ImportException( "Fail to instantiate [" + IMPORTER_CLASS_NAME + "].", e );
        } catch( InvocationTargetException e )
        {
            throw new ImportException( "Fail to perform import.", e.getTargetException() );
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
