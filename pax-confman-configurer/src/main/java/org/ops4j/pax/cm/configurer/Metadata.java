package org.ops4j.pax.cm.configurer;

import org.osgi.framework.Constants;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * Created by IntelliJ IDEA.
 * User: alindreghiciu
 * Date: Feb 11, 2008
 * Time: 8:18:14 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Metadata
{

    String OBJECTCLASS = Constants.OBJECTCLASS;
    String CONFIG_PID = "config." + Constants.SERVICE_PID;
    String CONFIG_FACTORY_PID = "config." + ConfigurationAdmin.SERVICE_FACTORYPID;
    String CONFIG_BUNDLELOCATION = "config." + ConfigurationAdmin.SERVICE_BUNDLELOCATION;
    
}
