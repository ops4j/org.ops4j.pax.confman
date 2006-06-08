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
package org.ops4j.pax.cm.agent.test.internal;

import java.io.IOException;
import java.util.HashSet;
import java.util.Hashtable;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Edward Yakop
 * @since 0.1.0
 */
final class ConfigAdminTracker extends ServiceTracker
{
    private static final Hashtable<String, String> m_configProperties;

    private final HashSet<String> m_configurationNames;
    private final BundleContext m_bundleContext;

    static
    {
        m_configProperties = new Hashtable<String, String>();
        for( int i = 0; i < 100; i++ )
        {
            String number = prependZero( i, 3 );
            String propertyKey = "property" + number;
            String propertyValue = "value" + number;
            m_configProperties.put( propertyKey, propertyValue );
        }
    }

    ConfigAdminTracker( BundleContext bundleContext )
    {
        super( bundleContext, ConfigurationAdmin.class.getName(), null );

        m_configurationNames = new HashSet<String>();
        m_bundleContext = bundleContext;
    }

    public Object addingService( ServiceReference serviceReference )
    {
        ConfigurationAdmin admin = (ConfigurationAdmin) m_bundleContext.getService( serviceReference );

        for( int i = 0; i < 100; i++ )
        {
            String number = prependZero( i, 3 );
            String bogusConfigurationName = "org.ops4j.pax.cm.agent.test" + number;

            try
            {
                Configuration configuration = admin.getConfiguration( bogusConfigurationName );
                configuration.update( m_configProperties );
                m_configurationNames.add( bogusConfigurationName );
            } catch( IOException e )
            {
                e.printStackTrace();
            }
        }

        m_bundleContext.ungetService( serviceReference );

        return serviceReference;
    }

    private static String prependZero( int toConvert, int numberOfDigit )
    {
        String numberString = Integer.toString( toConvert );
        while( numberString.length() < numberOfDigit )
        {
            numberString = "0" + numberString;
        }
        return numberString;
    }

    public synchronized void close()
    {
        super.close();

        if( !m_configurationNames.isEmpty() )
        {
            ServiceReference cmRef = m_bundleContext.getServiceReference( ConfigurationAdmin.class.getName() );
            removedService( cmRef, cmRef );
        }
    }

    public void removedService( ServiceReference serviceReference, Object object )
    {
        if( object == null )
        {
            return;
        }

        if( !m_configurationNames.isEmpty() )
        {
            ConfigurationAdmin admin = (ConfigurationAdmin) m_bundleContext.getService( serviceReference );

            for( String item : m_configurationNames )
            {
                try
                {
                    Configuration configuration = admin.getConfiguration( item );
                    configuration.delete();
                } catch( IOException e )
                {
                    e.printStackTrace();
                }
            }

            m_configurationNames.clear();
        }
    }
}
