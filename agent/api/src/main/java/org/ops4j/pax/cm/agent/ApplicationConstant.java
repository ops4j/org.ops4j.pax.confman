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
package org.ops4j.pax.cm.agent;

public final class ApplicationConstant
{

    public static final String APPLICATION_NAME = "ops4j:pax:configadmin";
    public static final String MOUNT_POINT = "pax/configadmin";

    private ApplicationConstant()
    {
    }

    public static final class Overview
    {

        public static final String CONTAINMENT_ID = "overview";
        public static final String COMPONENT_MENU_TAB = "menu";
        public static final String DESTINATION_ID_MENU_TAB = CONTAINMENT_ID + "." + COMPONENT_MENU_TAB;

        public static final String PAGE_PARAM_TAB_ID = "tabName";
        
        public static final String TAB_NAME_BROWSER = "browser";
    }
}
