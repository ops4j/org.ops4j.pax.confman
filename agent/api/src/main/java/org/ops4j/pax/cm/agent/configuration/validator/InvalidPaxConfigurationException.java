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
package org.ops4j.pax.cm.agent.configuration.validator;

import org.ops4j.pax.cm.agent.configuration.PaxConfiguration;

/**
 * {@code InvalidPaxConfigurationException} is thrown by {@code PaxConfigurationValidator} if the one of the validation
 * rule is not fullfilled by {@code PaxConfiguration}.
 *
 * @author Edward Yakop
 * @since 0.1.0
 */
public final class InvalidPaxConfigurationException extends RuntimeException
{

    private static final long serialVersionUID = 1L;

    /**
     * Construct {@code InvalidPaxConfigurationException} with the specified message. Only use this constructor if the
     * pax configuration does not have pid or factory pid. Example of usage,
     * <pre>
     * <code>
     * throw new InvalidPaxConfigurationException(
     *  "PaxConfiguration [" + paxConfiguration + "] does not have both pid and factory pid defined." );
     * </code>
     * </pre>
     *
     * @param message The error message. This argument must not be {@code null} or empty.
     *
     * @since 0.1.0
     */
    InvalidPaxConfigurationException( String message )
    {
        super( message );
    }

    /**
     * Construct {@code InvalidPaxConfigurationException} with the specifed message. Example of usage,
     * <pre>
     * {@code
     * throw new InvalidPaxConfigurationException( "Both pid and factory pid are not null.", configuration );
     * }
     * </pre>
     *
     * @param message       The error message. This argument must not be {@code null} or empty.
     * @param configuration The configuration. This argument is used to further explain which {@code PaxConfiguration}
     *                      is invalid.
     *
     * @since 0.1.0
     */
    InvalidPaxConfigurationException( String message, PaxConfiguration configuration )
    {
        super( constructPreMessage( configuration ) + " is invalid. Cause [" + message + "]" );
    }

    /**
     * Construct identifier pax configuration string message.
     *
     * @param configuration The configuration. This argument must not be {@code null}.
     *
     * @return Human readable string of identification of the specified {@code configuration}.
     *
     * @since 0.1.0
     */
    private static String constructPreMessage( PaxConfiguration configuration )
    {
        String pid = configuration.getPid();
        if( pid != null && pid.length() > 0 )
        {
            return "PaxConfiguration with pid [" + pid + "]";
        }

        String factoryPid = configuration.getFactoryPid();
        if( factoryPid != null && factoryPid.length() > 0 )
        {
            return "PaxConfiguration with factory pid [" + factoryPid + "]";
        }

        return "PaxConfiguration [" + configuration + "]";
    }
}

