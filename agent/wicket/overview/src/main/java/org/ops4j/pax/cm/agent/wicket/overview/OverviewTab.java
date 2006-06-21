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

import wicket.extensions.markup.html.tabs.ITab;

/**
 * {@code OverviewTab} represent tab in {@code OverviewPage}.
 * In most cases, use {@code DefaultOverviewTab} as default implementation of {@code OverviewTab} interface.
 *
 * @author Edward Yakop
 * @see DefaultOverviewTab
 * @see OverviewTabContent
 * @since 0.1.0
 */
public interface OverviewTab extends ITab
{
    /**
     * Returns a non-null String of the overview tab item identifier. This is used by {@code OverviewPage} to identify
     * the requested selected tab.
     *
     * @since 0.1.0
     */
    String getOverviewTabItemIdentifier();
}
