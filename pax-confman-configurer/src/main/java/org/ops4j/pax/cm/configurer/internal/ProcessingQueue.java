package org.ops4j.pax.cm.configurer.internal;

/**
 * Created by IntelliJ IDEA.
 * User: alindreghiciu
 * Date: Feb 12, 2008
 * Time: 1:21:57 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ProcessingQueue
{
    void add( Processable processable );
}
