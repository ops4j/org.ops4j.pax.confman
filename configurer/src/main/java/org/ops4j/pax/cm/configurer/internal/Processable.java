package org.ops4j.pax.cm.configurer.internal;

import java.io.IOException;
import org.osgi.service.cm.ConfigurationAdmin;
import org.ops4j.lang.NullArgumentException;

/**
 * Created by IntelliJ IDEA.
 * User: alindreghiciu
 * Date: Feb 12, 2008
 * Time: 1:25:35 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Processable
{

    /**
     * Processes the configuration.
     *
     * @param configurationAdmin configuration admin service to be used
     *
     * @throws IOException           - re-thrown if the configurations can not be persisted
     * @throws NullArgumentException - if configuration admin service is null
     */
    void process( ConfigurationAdmin configurationAdmin )
        throws IOException;

}
