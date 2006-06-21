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
package org.ops4j.pax.cm.agent.wicket.overview;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.cm.agent.wicket.overview.internal.OverviewTabPanel;
import wicket.extensions.markup.html.tabs.AbstractTab;
import wicket.markup.html.panel.Panel;
import wicket.model.IModel;

/**
 * {@code DefaultOverviewTab} provides default implementation of {@code OverviewTab}. Do not use
 * {@code DefaultOverviewTab} if the panel use a lot of memory. It is best if the panel is instantiated in lazy manner.
 *
 * @author Edward Yakop
 * @since 0.1.0
 */
public final class DefaultOverviewTab extends AbstractTab
    implements OverviewTab
{

    public static final String WICKET_ID_PANEL = OverviewTabPanel.WICKET_ID_CONTENT;

    private Panel m_panel;
    private String m_tabItemIdentifier;
    private List<AbstractOverviewTabListener> m_listeners;

    /**
     * Construct an instance of {@code DefaultOverviewTab} with the specified arguments.
     *
     * @param title             The title of this tab. This argument must not be {@code null}.
     * @param tabItemIdentifier The unique tab item identifier. This will be used by {@code OverviewPage} to identify
     *                          selected tab. This argument must not be {@code null} or empty.
     * @param panel             The panel to be displayed. This argument must not be {@code null}.
     *
     * @throws IllegalArgumentException Thrown if one or some or all arguments are {@code null}.
     * @since 0.1.0
     */
    public DefaultOverviewTab( IModel title, String tabItemIdentifier, Panel panel )
        throws IllegalArgumentException
    {
        super( title );

        NullArgumentException.validateNotEmpty( tabItemIdentifier, "tabItemIdentifier" );

        NullArgumentException.validateNotNull( panel, "panel" );
        if( !WICKET_ID_PANEL.equals( panel.getId() ) )
        {
            throw new IllegalArgumentException( "[panel] argument must have wicket id [" + WICKET_ID_PANEL + "]." );
        }

        m_tabItemIdentifier = tabItemIdentifier;
        m_panel = panel;

        m_listeners = new ArrayList<AbstractOverviewTabListener>();
    }

    /**
     * Returns a non-null String of the overview tab item identifier. This is used by {@code OverviewPage} to identify
     * the requested selected tab.
     *
     * @since 0.1.0
     */
    public String getOverviewTabItemIdentifier()
    {
        return m_tabItemIdentifier;
    }

    /**
     * Add the specified {@code listener} of this {@code DefaultOverviewTab} instance.
     *
     * @param listener The listener to be added. This argument must not be {@code null}.
     *
     * @throws IllegalArgumentException Thrown if the specified {@code listener} is {@code null}.
     * @since 0.1.0
     */
    public void addListener( AbstractOverviewTabListener listener )
        throws IllegalArgumentException
    {
        NullArgumentException.validateNotNull( listener, "listener" );
        m_listeners.add( listener );
    }

    /**
     * Consturct panel with panel id as specified.
     *
     * @param panelId returned panel MUST have this id
     *
     * @return a Panel object that will be placed as the content panel
     *
     * @since 0.1.0
     */
    public Panel getPanel( final String panelId )
    {
        for( AbstractOverviewTabListener listener : m_listeners )
        {
            listener.preGetPanel( this );
        }

        return new OverviewTabPanel( panelId, m_panel );
    }

    /**
     * Remove the {@code listener} of this {@code DefaultOverviewTab} instance.
     *
     * @param listener The listener to be removed. This argument must not be {@code null}.
     *
     * @throws IllegalArgumentException Thrown if the specified {@code listener} is {@code null}.
     * @since 0.1.0
     */
    public void removeListener( AbstractOverviewTabListener listener )
    {
        NullArgumentException.validateNotNull( listener, "listener" );
        m_listeners.remove( listener );
    }

    /**
     * Instantiate {@code AbstractOverviewTabListener} and add to {@code DefaultOverviewTab} instance as listener to
     * receive {@code preGetPanel} event. This is useful if the panel internal state need to be updated.
     *
     * @author Edward Yakop
     * @since 0.1.0
     */
    public static abstract class AbstractOverviewTabListener
        implements Serializable
    {

        /**
         * This method is called prior {@code DefaultOverviewTab#getPanel()} is called. This method can be used to
         * prepares the {@code panel} that is about to be displayed.
         *
         * @param tab The overview tab that received this event. This argument must not be {@code null}.
         *
         * @since 0.1.0
         */
        public void preGetPanel( DefaultOverviewTab tab )
        {
        }
    }
}
