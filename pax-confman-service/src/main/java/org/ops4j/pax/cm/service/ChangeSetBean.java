package org.ops4j.pax.cm.service;

import java.util.Dictionary;

/**
 * Created by IntelliJ IDEA.
 * User: alindreghiciu
 * Date: Feb 21, 2008
 * Time: 5:16:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChangeSetBean
    implements ChangeSet
{

    private final Dictionary[] m_added;
    private final Dictionary[] m_updated;
    private final String[] m_deleted;

    public ChangeSetBean( Dictionary[] added, Dictionary[] updated, String[] deleted )
    {
        m_added = added;
        m_updated = updated;
        m_deleted = deleted;
    }

    public Dictionary[] getAdded()
    {
        return m_added;
    }

    public String[] getDeleted()
    {
        return m_deleted;
    }

    public Dictionary[] getUpdated()
    {
        return m_updated;
    }
}