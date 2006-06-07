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

/**
 * @author Edward Yakop
 * @since 0.1.0
 */
public interface OverviewTabItem
{
    /**
     * The resource bundle key used to retrieve the label of this tab item. The value must be stored at
     * {@code <ImplementorClassName>_<locale>.properties} file. For example, if the implementor class name is
     * {@code ConfigurationBrowserPanel} the key and value must be at least stored inside
     * {@code ConfigurationBrowserPanel.properties}.
     *
     * @since 0.1.0
     */
    String LOCALE_TAB_ITEM_LABEL = "OverviewTabItemLabel";

    /**
     * Returns the overview tab item identifier. This is used by {@code OverviewPage} to match the user http request.
     *
     * @since 0.1.0
     */
    String getOverviewTabItemIdentifier();
}