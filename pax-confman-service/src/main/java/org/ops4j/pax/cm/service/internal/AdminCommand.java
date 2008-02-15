/*
 * Copyright 2008 Alin Dreghiciu.
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
package org.ops4j.pax.cm.service.internal;

import java.io.IOException;
import org.osgi.service.cm.ConfigurationAdmin;
import org.ops4j.lang.NullArgumentException;

/**
 * Command to be executed agains configuration admin/
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 12, 2008
 */
public interface AdminCommand
{

    /**
     * Executes the command.
     *
     * @param configurationAdmin configuration admin service to be used
     *
     * @throws IOException           - re-thrown if the configurations can not be persisted
     * @throws NullArgumentException - if configuration admin service is null
     */
    void execute( ConfigurationAdmin configurationAdmin )
        throws IOException;

}
