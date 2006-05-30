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

import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.cm.agent.ApplicationConstant;
import org.ops4j.pax.wicket.service.AbstractPageContent;
import org.ops4j.pax.wicket.service.DefaultPageContainer;
import org.osgi.framework.BundleContext;
import wicket.Page;
import wicket.PageParameters;

/**
 * @author Edward Yakop
 * @since 0.1.0
 */
public final class OverviewPageContent extends AbstractPageContent
{
    private final DefaultPageContainer m_container;

    public OverviewPageContent( BundleContext bundleContext, DefaultPageContainer container )
    {
        super(
            bundleContext,
            ApplicationConstant.Overview.CONTAINMENT_ID,
            ApplicationConstant.APPLICATION_NAME,
            ApplicationConstant.Overview.CONTAINMENT_ID
        );

        NullArgumentException.validateNotNull( container, "container" );
        m_container = container;
    }

    /**
     * Returns the OverviewPage.class object.
     *
     * @since 0.1.0
     */
    public Class getPageClass()
    {
        return OverviewPage.class;
    }

    public Page createPage( PageParameters params )
    {
        if( params == null )
        {
            params = new PageParameters();
        }
        
        return new OverviewPage( m_container, params );
    }
}
