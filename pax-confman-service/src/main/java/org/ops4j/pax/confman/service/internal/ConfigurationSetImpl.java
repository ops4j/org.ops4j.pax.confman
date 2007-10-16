/*
 * Copyright 2007 Alin Dreghiciu.
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
package org.ops4j.pax.confman.service.internal;

import java.util.Dictionary;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ops4j.pax.confman.service.ConfigurationSet;

public class ConfigurationSetImpl
    implements ConfigurationSet
{

    /**
     * Logger.
     */
    private static final Log LOGGER = LogFactory.getLog( ConfigurationSetImpl.class );

    public void update( final Dictionary config )
    {
        LOGGER.debug( "Updating [" + this + "] with config [" + config );
    }

    public void remove()
    {
        LOGGER.debug( "Removing [" + this + "]");
    }

}
