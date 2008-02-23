package org.ops4j.pax.cm.service.internal.ms;

import org.osgi.service.cm.ManagedService;

/**
 * Created by IntelliJ IDEA.
 * User: alindreghiciu
 * Date: Feb 22, 2008
 * Time: 6:20:00 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ManagedServiceRepository
{

    void addManagedService( String pid, ManagedService service );

    void removeManagedService( String pid );

}
