package org.ops4j.pax.cm.service.internal.event;

import org.ops4j.pax.cm.service.internal.event.Event;

/**
 * Created by IntelliJ IDEA.
 * User: alindreghiciu
 * Date: Feb 22, 2008
 * Time: 6:44:40 PM
 * To change this template use File | Settings | File Templates.
 */
public interface EventDispatcher
{

    void fireEvent( Event event );
    
}
