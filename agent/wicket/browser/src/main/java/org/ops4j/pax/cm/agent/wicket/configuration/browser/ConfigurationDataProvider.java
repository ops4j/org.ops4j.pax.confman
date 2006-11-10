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
package org.ops4j.pax.cm.agent.wicket.configuration.browser;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import org.apache.log4j.Logger;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.cm.agent.configuration.PaxConfiguration;
import org.ops4j.pax.cm.agent.configuration.PaxConfigurationFacade;
import org.osgi.service.cm.Configuration;
import wicket.extensions.markup.html.repeater.util.SortParam;
import wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import wicket.model.IModel;
import wicket.model.Model;

/**
 * @author Edward Yakop
 * @since 0.1.0
 */
final class ConfigurationDataProvider extends SortableDataProvider
{
    
    private static final long serialVersionUID = 1L;

    private static final Logger m_logger = Logger.getLogger( ConfigurationDataProvider.class );

    private ArrayList<PaxConfiguration> m_configurations;
    private int m_selected;
    private SelectionChangeListener m_listener;

    ConfigurationDataProvider( Configuration[] configurations )
    {
        NullArgumentException.validateNotNull( configurations, "configurations" );

        resetData( configurations );
    }

    void resetData( Configuration[] configurations )
    {
        m_configurations = new ArrayList<PaxConfiguration>( configurations.length );
        m_selected = 0;

        for( Configuration configuration : configurations )
        {
            PaxConfiguration entry = new PaxConfiguration( "", false );

            String pid = configuration.getPid();
            entry.setPid( pid );

            String factoryPid = entry.getFactoryPid();
            entry.setFactoryPid( factoryPid );

            String bundleLocation = configuration.getBundleLocation();
            entry.setBundleLocation( bundleLocation );

            m_configurations.add( entry );
        }

        setSort( PaxConfiguration.PROPERTY_PID, true );
        sort( PaxConfiguration.PROPERTY_PID, true );
    }

    private void sort( final String sortProperty, final boolean isAscending )
    {
        Collections.sort( m_configurations, new Comparator<PaxConfiguration>()
        {
            public int compare( PaxConfiguration o1, PaxConfiguration o2 )
            {
                if( !isAscending )
                {
                    PaxConfiguration temp = o1;
                    o1 = o2;
                    o2 = temp;
                }

                String o1Value = o1.getPropertyValue( sortProperty );
                String o2Value = o2.getPropertyValue( sortProperty );

                if( o1Value != null )
                {
                    if( o2Value != null )
                    {
                        return o1Value.compareTo( o2Value );
                    }
                    else
                    {
                        return 1;
                    }
                }
                else if( o2Value != null )
                {
                    return -1;
                }
                else
                {
                    return 0;
                }
            }
        }
        );
    }

    public Iterator iterator( int first, int count )
    {
        SortParam sort = getSort();

        if( sort != null )
        {
            String sortProperty = sort.getProperty();
            boolean isAscending = sort.isAscending();
            sort( sortProperty, isAscending );
            m_selected = 0;
        }

        if( m_configurations.isEmpty() )
        {
            m_selected = 0;
        }

        return m_configurations.listIterator( first );
    }

    public final int size()
    {
        return m_configurations.size();
    }

    public IModel model( Object configurationObject )
    {
        return new Model( (Serializable) configurationObject );
    }

    void createNewPaxConfiguration()
    {
        PaxConfiguration paxConfiguration = new PaxConfiguration( "", false );

        paxConfiguration.setIsNew( true );
        m_configurations.add( m_selected, paxConfiguration );
        notifyListenerOnSelectEvent( paxConfiguration );
    }

    private void notifyListenerOnSelectEvent( PaxConfiguration selected )
    {
        m_listener.setPaxConfiguration( selected );
    }

    public void deletePaxConfiguration( PaxConfiguration configuration )
    {
        try
        {
            if( !configuration.isNew() )
            {
                PaxConfigurationFacade.deleteConfiguration( configuration );
            }

            m_configurations.remove( m_selected );

            if( !m_configurations.isEmpty() )
            {
                int size = m_configurations.size();
                if( m_selected >= size )
                {
                    m_selected = size - 1;
                }
                else
                {
                    m_selected = m_selected - 1;

                    if( m_selected < 0 )
                    {
                        m_selected = 0;
                    }
                }
            }
            else
            {
                m_selected = 0;
            }
        } catch( IOException e )
        {
            m_logger.warn( "Unable to delete configuration [" + configuration.getPid() + "].", e );
        }

        PaxConfiguration selectedPaxConfiguration = getSelectedPaxconfiguration();
        notifyListenerOnSelectEvent( selectedPaxConfiguration );
    }

    PaxConfiguration getSelectedPaxconfiguration()
    {
        if( m_configurations.isEmpty() )
        {
            m_selected = 0;
            return null;
        }
        else
        {
            return m_configurations.get( m_selected );
        }
    }

    void savePaxConfiguration( PaxConfiguration configuration )
    {
        try
        {
            PaxConfigurationFacade.updateConfiguration( configuration );
        } catch( IOException e )
        {
            e.printStackTrace();
        }

        notifyListenerOnSelectEvent( configuration );
    }

    public void selectPaxConfiguration( PaxConfiguration configuration )
    {
        NullArgumentException.validateNotNull( configuration, "configuration" );

        int i = 0;
        for( PaxConfiguration config : m_configurations )
        {
            if( config == configuration )
            {
                m_selected = i;

                if( m_listener != null )
                {
                    PaxConfiguration configuration1;
                    if( m_configurations.isEmpty() )
                    {
                        configuration1 = null;
                    }
                    else
                    {
                        configuration1 = m_configurations.get( m_selected );
                    }
                    notifyListenerOnSelectEvent( configuration1 );
                }

                return;
            }
            i++;
        }
    }

    public final void setSelectionListener( SelectionChangeListener listener )
    {
        m_listener = listener;
    }
}
