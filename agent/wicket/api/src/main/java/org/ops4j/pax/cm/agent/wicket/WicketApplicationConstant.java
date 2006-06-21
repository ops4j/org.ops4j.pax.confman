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
package org.ops4j.pax.cm.agent.wicket;

/**
 * @author Edward Yakop
 * @since 0.1.0
 */
public final class WicketApplicationConstant
{
    /**
     * Represents wicket application name.
     *
     * @since 0.1.0
     */
    public static final String APPLICATION_NAME = "ops4j_pax_configadmin";

    /**
     * Represents the mount point of this application.
     *
     * @since 0.1.0
     */
    public static final String MOUNT_POINT = "pax/configadmin";

    private WicketApplicationConstant()
    {
    }

    /**
     * All OverView Page constants are defined here.
     *
     * @since 0.1.0
     */
    public static final class Overview
    {
        /**
         * Represents the containment id of overview page.
         *
         * @since 0.1.0
         */
        public static final String CONTAINMENT_ID = "overview";

        /**
         * Represents the overview component menu tab. NOTE: This is not the destination id.
         *
         * @since 0.1.0
         */
        public static final String COMPONENT_MENU_TAB = "menu";

        /**
         * Represents the destionation id for menu tab.
         *
         * @since 0.1.0
         */
        public static final String DESTINATION_ID_MENU_TAB = CONTAINMENT_ID + "." + COMPONENT_MENU_TAB;

        /**
         * Represents the {@code PageParameters} key that used by {@code OverviewPage} to set the selected menu tab. If
         * this key is not present, {@code OverviewPage} must selects the first menu tab.
         *
         * @since 0.1.0
         */
        public static final String PAGE_PARAM_TAB_ID = "tabName";

        /**
         * Sets the {@code PAGE_PARAM_TAB_ID} with this value if {@code browser} tab should be selected.
         *
         * @since 0.1.0
         */
        public static final String MENU_TAB_ID_BROWSER = "browser";
    }

    /**
     * All Configuration Page constants are defined here.
     *
     * @since 0.1.0
     */
    public static final class Configuration
    {
        /**
         * All constants in regards to {@code EditConfigurationPage} are defined here.
         *
         * @since 0.1.0
         */
        public static final class Edit
        {
            /**
             * The containment id of {@code EditConfigurationPage}.
             *
             * @since 0.1.0
             */
            public static final String CONTAINMENT_ID = "editconfiguration";
            public static final String COMPONENT_CONFIGURATION_PROPERTIES = "configurationProperties";
            public static final String DESTINATION_ID_CONFIGURATION_PROPERTIES =
                CONTAINMENT_ID + COMPONENT_CONFIGURATION_PROPERTIES;
        }
    }
}