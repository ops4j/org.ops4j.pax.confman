/*
 * Copyright 2008 Alin Dreghiciu.
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
package org.ops4j.pax.cm.api;

import org.osgi.framework.Constants;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * TODO add JavaDoc
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 12, 2008
 */
public interface MetadataConstants
{

    /**
     * Copy of osgi objectclass property key.
     */
    String OBJECTCLASS = Constants.OBJECTCLASS;

    /**
     * Copy of osgi service pid.
     */
    String SERVICE_PID = Constants.SERVICE_PID;
    /**
     * Copy of osgi service factory pid.
     */
    String SERVICE_FACTORYPID = ConfigurationAdmin.SERVICE_FACTORYPID;
    /**
     * Service instance.
     */
    String SERVICE_FACTORYINSTANCE = "service.instance";
    /**
     * Copy of service bundle location.
     */
    String SERVICE_BUNDLELOCATION = ConfigurationAdmin.SERVICE_BUNDLELOCATION;

    /**
     * Service PID (service.pid) as regular expression.
     */
    String SERVICE_PID_AS_REGEX = Constants.SERVICE_PID.replaceAll( "\\.", "\\." ) + ".*";
    /**
     * Service factory PID (service.facpid) as regular expression.
     */
    String SERVICE_FACTORYPID_AS_REGEX = ConfigurationAdmin.SERVICE_FACTORYPID.replaceAll( "\\.", "\\." ) + ".*";

    /**
     * Prefix for all target properties.
     */
    String TARGET_PREFIX = "target";
    /**
     * Targeted service pid.
     */
    String TARGET_SERVICE_PID = TARGET_PREFIX + "." + SERVICE_PID;
    /**
     * Targeted service factory pid.
     */
    String TARGET_SERVICE_FACTORYPID = TARGET_PREFIX + "." + SERVICE_FACTORYPID;
    /**
     * Targeted service factory identity.
     */
    String TARGET_SERVICE_FACTORYINSTANCE = TARGET_PREFIX + ".instance";
    /**
     * Targeted service bundle location.
     */
    String TARGET_SERVICE_BUNDLELOCATION = TARGET_PREFIX + "." + SERVICE_BUNDLELOCATION;

    /**
     * Prefix for information properties.
     */
    String INFO_PREFIX = "info";
    /**
     * Prefix for information properties as a regular expression.
     */
    String INFO_PREFIX_AS_REGEX = INFO_PREFIX.replaceAll( "\\.", "\\." ) + ".*";
    /**
     * OPS4j Pax Configuration Manager Agent name.
     * Optional, provided as information.
     */
    String INFO_AGENT = INFO_PREFIX + ".agent";
    /**
     * OPS4j Pax Configuration Manager Agent adaptor type.
     * Optional, provided as information.
     */
    String INFO_ADAPTOR = INFO_PREFIX + ".adaptor";
    /**
     * OPS4j Pax Configuration Manager Agent human readable timestamp.
     * Optional, provided as information.
     */
    String INFO_TIMESTAMP = INFO_PREFIX + ".timestamp";
    /**
     * OPS4j Pax Configuration Manager Agent timestamp in milliseconds (java style).
     * Optional, provided as information.
     */
    String INFO_TIMESTAMP_MILLIS = INFO_PREFIX + ".timestamp.millis";

    /**
     * Mime type.
     */
    String MIME_TYPE = "mimeType";

}
