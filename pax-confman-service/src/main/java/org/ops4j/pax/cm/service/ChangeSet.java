package org.ops4j.pax.cm.service;

import java.util.Dictionary;

/**
 * Created by IntelliJ IDEA.
 * User: alindreghiciu
 * Date: Feb 21, 2008
 * Time: 5:16:37 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ChangeSet
{

    Dictionary[] getAdded();

    Dictionary[] getUpdated();

    String[] getDeleted();

}
