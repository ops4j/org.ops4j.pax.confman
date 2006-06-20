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

/**
 * {@code ExportException} represents error occured during exporting configuration. It is advised for 
 *
 * @author Edward Yakop
 * @since 0.1.0
 */
public class ExportException extends RuntimeException
{

    /**
     * Construct {@code ExportException} with the specified argument.
     *
     * @param message The exception message. This argument must not be {@code null}.
     *
     * @since 0.1.0
     */
    public ExportException( String message )
    {
        super( message );
    }

    /**
     * Construct {@code ExportException} with the specified arguments.
     *
     * @param message   The message that caused throwable to be thrown. This argument must not be {@code null}.
     * @param throwable The throwable that caused this exception to be thrown. This argument must not be {@code null}.
     *
     * @since 0.1.0
     */
    public ExportException( String message, Throwable throwable )
    {
        super( message, throwable );
    }
}
