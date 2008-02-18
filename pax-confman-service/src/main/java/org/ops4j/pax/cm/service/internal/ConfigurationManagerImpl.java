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

import java.util.Dictionary;
import java.util.Hashtable;
import org.osgi.service.cm.ConfigurationAdmin;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.cm.api.ConfigurationManager;
import org.ops4j.pax.cm.commons.internal.processor.CommandProcessor;
import org.ops4j.pax.cm.domain.ConfigurationSource;
import org.ops4j.pax.cm.domain.Identity;
import org.ops4j.pax.cm.domain.PropertiesSource;

/**
 * ConfigurationManager implementation.
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 12, 2008
 */
public class ConfigurationManagerImpl
    implements ConfigurationManager
{

    /**
     * Strategy based on pid. Strategies should be stateless so are safe to be reused.
     */
    private static final ConfigurationStrategy PID_STRATEGY = new PidStrategy();
    /**
     * Strategy based on factory pid. Strategies should be stateless so are safe to be reused.
     */
    private static final ConfigurationStrategy FACTORY_PID_STRATEGY = new FactoryPidStrategy();
    /**
     * Repository of dictionary adapters. Cannot be null.
     */
    private final AdapterRepository m_adapterRepository;
    /**
     * Configuration Admin commands processor.
     */
    private final CommandProcessor<ConfigurationAdmin> m_commandsProcessor;
    /**
     * Transformation processor.
     */
    private final TransformationsProcessor m_transformationsProcessor;

    /**
     * Constructor.
     *
     * @param adapterRepository        adaptor repository
     * @param commandsProcessor        commands processor
     * @param transformationsProcessor transformations processor
     *
     * @throws NullArgumentException - If dictionary adapter repository is null
     *                               - If commands processor is null
     *                               - If transformations processor is null
     */
    public ConfigurationManagerImpl( final AdapterRepository adapterRepository,
                                     final CommandProcessor<ConfigurationAdmin> commandsProcessor,
                                     final TransformationsProcessor transformationsProcessor )
    {
        NullArgumentException.validateNotNull( adapterRepository, "Dictionary adapters repository" );
        NullArgumentException.validateNotNull( commandsProcessor, "Commands processor" );
        NullArgumentException.validateNotNull( transformationsProcessor, "Transformations processor" );

        m_adapterRepository = adapterRepository;
        m_commandsProcessor = commandsProcessor;
        m_transformationsProcessor = transformationsProcessor;
    }

    /**
     * @see org.ops4j.pax.cm.api.ConfigurationManager#update(String, String, Object, Dictionary)
     */
    public void update( final String pid,
                        final String location,
                        final Object propertiesSource,
                        final Dictionary metadata )
    {
        m_transformationsProcessor.add(
            new Transformation(
                new ConfigurationSource(
                    new Identity( pid, location ),
                    new PropertiesSource(
                        propertiesSource,
                        DictionaryUtils.copy( metadata, new Hashtable() )
                    )
                ),
                PID_STRATEGY,
                m_adapterRepository
            )
        );
    }

    /**
     * @see org.ops4j.pax.cm.api.ConfigurationManager#update(String, String, String, Object, Dictionary)
     */
    public void update( final String factoryPid,
                        final String factoryInstance,
                        final String location,
                        final Object propertiesSource,
                        final Dictionary metadata )
    {
        m_transformationsProcessor.add(
            new Transformation(
                new ConfigurationSource(
                    new Identity( factoryPid, factoryInstance, location ),
                    new PropertiesSource(
                        propertiesSource,
                        DictionaryUtils.copy( metadata, new Hashtable() )
                    )
                ),
                FACTORY_PID_STRATEGY,
                m_adapterRepository
            )
        );
    }

    /**
     * @see org.ops4j.pax.cm.api.ConfigurationManager#delete(String)
     */
    public void delete( final String pid )
    {
        m_commandsProcessor.add(
            PID_STRATEGY.createDeleteCommand(
                new Identity( pid, null )
            )
        );
    }

    /**
     * @see org.ops4j.pax.cm.api.ConfigurationManager#delete(String, String)
     */
    public void delete( final String factoryPid,
                        final String factoryInstance )
    {
        m_commandsProcessor.add(
            FACTORY_PID_STRATEGY.createDeleteCommand(
                new Identity( factoryPid, factoryInstance, null )
            )
        );
    }

}
