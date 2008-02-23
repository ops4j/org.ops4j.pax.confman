package org.ops4j.pax.cm.service.internal.msf;

import org.osgi.service.cm.ManagedServiceFactory;

/**
 * Created by IntelliJ IDEA.
 * User: alindreghiciu
 * Date: Feb 22, 2008
 * Time: 6:20:00 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ManagedServiceFactoryRepository
{

    void addManagedServiceFactory( String managedServiceFactoryPid, ManagedServiceFactory managedServiceFactory );

    void removeManagedServiceFactory( String managedServiceFactoryPid );

}