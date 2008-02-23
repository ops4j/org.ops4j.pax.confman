package org.ops4j.pax.cm.service;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: alindreghiciu
 * Date: Feb 22, 2008
 * Time: 6:10:39 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ConfigurationSource
{

    String getPid();

    String getFactoryPid();

    String getBundleLocation();

    ConfigurationProperties getProperties();

}
