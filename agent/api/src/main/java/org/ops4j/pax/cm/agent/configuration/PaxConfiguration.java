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
package org.ops4j.pax.cm.agent.configuration;

import java.io.Serializable;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import org.ops4j.lang.NullArgumentException;
import org.osgi.framework.Constants;
import org.osgi.service.cm.Configuration;

/**
 * @author Edward Yakop
 * @since 0.1.0
 */
public final class PaxConfiguration
    implements Serializable
{

    public static final String PROPERTY_PID = "pid";
    public static final String PROPERTY_BUNDLE_LOCATION = "bundleLocation";
    public static final String PROPERTY_FACTORY_PID = "factoryPid";

    private static final long serialVersionUID = 1L;

    private String m_pid;
    private String m_bundleLocation;
    private String m_factoryPID;
    private Dictionary m_properties;
    private boolean m_isNew;

    public PaxConfiguration()
    {
        m_isNew = false;
        m_properties = new Hashtable();
    }

    /**
     * Construct an instance of {@code PaxConfiguration} with internal values to be copied from the specified
     * {@code configuration}.
     *
     * @param configuration The configuration to be copied. This argument must not be {@code null}.
     *
     * @throws IllegalArgumentException Thrown if the specified {@code configuration} is {@code null}.
     * @throws IllegalStateException    Thrown if the specified configuration is deleted.
     * @since 0.1.0
     */
    public PaxConfiguration( Configuration configuration )
        throws IllegalArgumentException
    {
        NullArgumentException.validateNotNull( configuration, "configuration" );

        m_pid = configuration.getPid();
        m_bundleLocation = configuration.getBundleLocation();
        m_factoryPID = configuration.getFactoryPid();
        m_isNew = false;

        // Copy the properties
        Dictionary properties = configuration.getProperties();
        m_properties = new Properties();
        if( properties != null )
        {
            Enumeration enumeration = properties.keys();
            while( enumeration.hasMoreElements() )
            {
                String key = (String) enumeration.nextElement();

                if( Constants.SERVICE_PID.equals( key ) || "service.factoryPid".equals( key ) )
                {
                    continue;
                }

                Object value = properties.get( key );
                m_properties.put( key, value );
            }
        }
    }

    public String getBundleLocation()
    {
        return m_bundleLocation;
    }

    public void setBundleLocation( String bundleLocation )
    {
        m_bundleLocation = bundleLocation;
    }

    public String getPid()
    {
        return m_pid;
    }

    public void setPid( String pid )
    {
        m_pid = pid;
    }

    public Dictionary getProperties()
    {
        return m_properties;
    }

    public void setProperties( Dictionary properties )
    {
        NullArgumentException.validateNotNull( properties, "properties" );
        m_properties = properties;
    }

    public void setIsNew( boolean value )
    {
        m_isNew = value;
    }

    public String getPropertyValue( String propertyName )
        throws IllegalArgumentException
    {
        NullArgumentException.validateNotEmpty( propertyName, "propertyName" );

        if( "pid".equals( propertyName ) )
        {
            return getPid();
        }
        else if( "bundleLocation".equals( propertyName ) )
        {
            return getBundleLocation();
        }
        else
        {
            return getFactoryPid();
        }
    }

    public String getFactoryPid()
    {
        return m_factoryPID;
    }

    public boolean isNew()
    {
        return m_isNew;
    }

    public void setFactoryPid( String factoryPid )
    {
        m_factoryPID = factoryPid;
    }
}