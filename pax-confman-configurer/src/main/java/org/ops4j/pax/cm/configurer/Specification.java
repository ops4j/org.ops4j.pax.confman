package org.ops4j.pax.cm.configurer;

import java.util.Dictionary;

/**
 * Created by IntelliJ IDEA.
 * User: alindreghiciu
 * Date: Feb 11, 2008
 * Time: 7:47:46 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Specification
{
    boolean isSatisfiedBy( Dictionary metadata );
}
