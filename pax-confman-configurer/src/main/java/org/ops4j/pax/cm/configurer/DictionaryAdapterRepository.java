package org.ops4j.pax.cm.configurer;

import java.util.Dictionary;

/**
 * Created by IntelliJ IDEA.
 * User: alindreghiciu
 * Date: Feb 11, 2008
 * Time: 7:19:32 PM
 * To change this template use File | Settings | File Templates.
 */
public interface DictionaryAdapterRepository
{

    void register( DictionaryAdapter adapter );

    void unregister( DictionaryAdapter adapter );

    DictionaryAdapter find( Dictionary metadata );

}
