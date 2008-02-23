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
package org.ops4j.pax.cm.service.internal.producer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.cm.service.ConfigurationProducer;
import org.ops4j.pax.cm.service.internal.event.Event;

/**
 * Signals that a configuration producre has been registered.
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 23, 2008
 */
public class ConfigurationProducerRegisteredEvent
    implements Event
{

    /**
     * Logger.
     */
    private static final Log LOG = LogFactory.getLog( ConfigurationProducerRegisteredEvent.class );

    /**
     * Registered configuration producer.
     */
    private final ConfigurationProducer m_producer;

    /**
     * Constructor.
     *
     * @param producer registered configuration producer
     *
     * @throws NullArgumentException - If producre is null
     */
    public ConfigurationProducerRegisteredEvent( final ConfigurationProducer producer )
    {
        NullArgumentException.validateNotNull( producer, "Configuration producer" );

        m_producer = producer;
        LOG.trace( "Configuration producer " + m_producer + " has been registered" );
    }

    /**
     * Getter.
     *
     * @return registered configuration producer; not null
     */
    public ConfigurationProducer getProducer()
    {
        return m_producer;
    }
}