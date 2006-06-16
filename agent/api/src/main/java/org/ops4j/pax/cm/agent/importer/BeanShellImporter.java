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
package org.ops4j.pax.cm.agent.importer;

import bsh.EvalError;
import bsh.Interpreter;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collections;
import java.util.List;
import org.ops4j.pax.cm.agent.configuration.PaxConfiguration;

public class BeanShellImporter
    implements Importer
{
    public static final String IMPORTER_ID = "BeanShell";

    public List<PaxConfiguration> performImport( InputStream inputStream )
    {
        if( inputStream == null )
        {
            return Collections.emptyList();
        }

        Interpreter interpreter = new Interpreter();
        InputStreamReader in = new InputStreamReader( inputStream );
        Reader reader = new BufferedReader( in );

        try
        {
            return (List<PaxConfiguration>) interpreter.eval( reader );
        } catch( EvalError evalError )
        {
            evalError.printStackTrace();  //TODO: Auto-generated, need attention.
        }

        return Collections.emptyList();
    }
}
