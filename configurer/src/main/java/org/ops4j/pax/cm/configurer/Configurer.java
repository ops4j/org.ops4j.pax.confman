package org.ops4j.pax.cm.configurer;

import java.util.Dictionary;

/**
 * Created by IntelliJ IDEA.
 * User: alindreghiciu
 * Date: Feb 11, 2008
 * Time: 6:17:35 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Configurer
{

    void configure( String pid, String location, Object configuration, Dictionary metadata );
    
}
