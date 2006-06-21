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
package org.ops4j.pax.cm.agent.wicket.overview.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.cm.agent.wicket.WicketApplicationConstant;
import org.ops4j.pax.cm.agent.wicket.overview.OverviewTab;
import org.ops4j.pax.cm.agent.wicket.overview.OverviewTabContent;
import org.ops4j.pax.wicket.service.Content;
import org.ops4j.pax.wicket.service.DefaultPageContainer;
import org.osgi.framework.BundleContext;

public final class OverviewPageContainer extends DefaultPageContainer
{
    private static final Log m_logger = LogFactory.getLog( OverviewPageContainer.class );

    public OverviewPageContainer( BundleContext bundleContext, String containmentId, String applicationName )
    {
        super( bundleContext, containmentId, applicationName );
    }

    public List<OverviewTab> createTab( Locale locale )
    {
        NullArgumentException.validateNotNull( locale, "locale" );

        Map<String, List<Content>> children = getChildren();

        List<Content> contents = children.get( WicketApplicationConstant.Overview.COMPONENT_MENU_TAB );
        List<OverviewTab> tabs = new ArrayList<OverviewTab>();
        for( Content content : contents )
        {
            if( !( content instanceof OverviewTabContent ) )
            {
                if( m_logger.isDebugEnabled() )
                {
                    m_logger.debug( "Content [" + content + "] is not instance of [OverviewTabContet]." );
                }
                continue;
            }

            OverviewTabContent cnt = (OverviewTabContent) content;
            OverviewTab tab = cnt.createTab( locale );

            tabs.add( tab );
        }

        return tabs;
    }
}
