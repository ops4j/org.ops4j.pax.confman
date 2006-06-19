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
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.cm.agent.wicket.overview.internal.OverviewTabPanel;
import wicket.extensions.markup.html.tabs.AbstractTab;
import wicket.markup.html.panel.Panel;
import wicket.model.IModel;

/**
 * @author Edward Yakop
 * @since 0.1.0
 */
public final class DefaultOverviewTab extends AbstractTab
    implements OverviewTab
{

    public static final String WICKET_ID_PANEL = OverviewTabPanel.WICKET_ID_CONTENT;

    private Panel m_panel;
    private String m_tabItemIdentifier;
    private OverviewTabListener m_listener;

    /**
     * @since 0.1.0
     */
    public DefaultOverviewTab( IModel title, String tabItemIdentifier, Panel panel )
        throws IllegalArgumentException
    {
        super( title );

        NullArgumentException.validateNotEmpty( tabItemIdentifier, "tabItemIdentifier" );
        m_tabItemIdentifier = tabItemIdentifier;

        NullArgumentException.validateNotNull( panel, "panel" );
        if( !WICKET_ID_PANEL.equals( panel.getId() ) )
        {
            throw new IllegalArgumentException( "[panel] argument must have wicket id [" + WICKET_ID_PANEL + "]." );
        }

        m_panel = panel;
    }

    /**
     * Set the listener of {@code DefaultOverviewTab}. Sets to {@code null} to remove listener.
     *
     * @param listener The listener.
     *
     * @since 0.1.0
     */
    public void setListener( OverviewTabListener listener )
    {
        m_listener = listener;
    }

    /**
     * Returns the overview tab item identifier. This is used by {@code OverviewPage} to match the user http request.
     *
     * @since 0.1.0
     */
    public String getOverviewTabItemIdentifier()
    {
        return m_tabItemIdentifier;
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
        if( m_listener != null )
        {
            m_listener.preGetPanel( this );
        }

        return new OverviewTabPanel( panelId, m_panel );
    }

    /**
     * @author Edward Yakop
     * @since 0.1.0
     */
    public static abstract class OverviewTabListener
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
