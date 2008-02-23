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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.cm.service.ConfigurationProducer;
import org.ops4j.pax.cm.service.ConfigurationSource;
import org.ops4j.pax.cm.service.internal.event.EventDispatcher;

/**
 * ConfigurationProducerRepository implementation that acts as a composite ConfigurationProducer.
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 23, 2008
 */
public class ConfigurationProducerRepositoryImpl
    implements ConfigurationProducerRepository, ConfigurationProducer
{

    /**
     * Event dispatcher.
     */
    private final EventDispatcher m_dispatcher;
    /**
     * Set of configuration producers.
     */
    private final Set<ConfigurationProducer> m_producers;

    /**
     * Constructor.
     *
     * @param dispatcher event dispatcher
     *
     * @throws NullArgumentException - If event dispatcher is null
     */
    public ConfigurationProducerRepositoryImpl( final EventDispatcher dispatcher )
    {
        NullArgumentException.validateNotNull( dispatcher, "Event dispatcher" );

        m_dispatcher = dispatcher;
        m_producers = Collections.synchronizedSet( new HashSet<ConfigurationProducer>() );
    }

    /**
     * @throws NullArgumentException - If producer is null
     * @see ConfigurationProducerRepository#registerConfigurationProducer(ConfigurationProducer)
     */
    public void registerConfigurationProducer( final ConfigurationProducer producer )
    {
        NullArgumentException.validateNotNull( producer, "Configuration producer" );

        synchronized( m_producers )
        {
            m_producers.add( producer );
            m_dispatcher.fireEvent( new ConfigurationProducerRegisteredEvent( producer ) );
        }
    }

    /**
     * @throws NullArgumentException - If producer is null
     * @see ConfigurationProducerRepository#unregisterConfigurationProducer(ConfigurationProducer)
     */
    public void unregisterConfigurationProducer( final ConfigurationProducer producer )
    {
        NullArgumentException.validateNotNull( producer, "Configuration producer" );

        m_producers.remove( producer );
    }

    /**
     * Cycle through configurations producers to find the configuration with the highest timestemp.
     *
     * @see ConfigurationProducer#getConfiguration(String)
     */
    public ConfigurationSource getConfiguration( final String pid )
    {
        final List<ConfigurationSource> configurations = new ArrayList<ConfigurationSource>();
        final ConfigurationProducer[] producers;
        // take a snaphsot of producers
        synchronized( m_producers )
        {
            producers = m_producers.toArray( new ConfigurationProducer[m_producers.size()] );
        }
        for( ConfigurationProducer producer : producers )
        {
            // there is a small chance that the configuration producer has been removed from when the snapshot was made
            // so we skipp it if not still available
            // new configuration producers will not be taken in account
            if( m_producers.contains( producer ) )
            {
                configurations.add( producer.getConfiguration( pid ) );
            }
        }
        if( configurations.size() > 0 )
        {
            // TODO implement time stamp selection not first in list
            return configurations.get( 0 );
        }
        return null;
    }

    /**
     * Cycle all producres and get all configurations from each producer. If there are confingurations for the same pid
     * from more producres the one with the highes time stamp will be used.
     *
     * @return all configurations from all producers
     *
     * @see ConfigurationProducer#
     */
    public Collection<ConfigurationSource> getAllConfigurations()
    {
        final Map<String, ConfigurationSource> configurations = new HashMap<String, ConfigurationSource>();
        final ConfigurationProducer[] producers;
        // take a snaphsot of producers
        synchronized( m_producers )
        {
            producers = m_producers.toArray( new ConfigurationProducer[m_producers.size()] );
        }
        for( ConfigurationProducer producer : producers )
        {
            // there is a small chance that the configuration producer has been removed from when the snapshot was made
            // so we skipp it if not still available
            // new configuration producers will not be taken in account
            if( m_producers.contains( producer ) )
            {
                final Collection<? extends ConfigurationSource> configsPerProducer = producer.getAllConfigurations();
                if( configsPerProducer != null )
                {
                    for( ConfigurationSource config : configsPerProducer )
                    {
                        configurations.put( config.getPid(), config );
                        //TODO implement time stamp selection
                    }
                }
            }
        }
        return configurations.values();
    }

}