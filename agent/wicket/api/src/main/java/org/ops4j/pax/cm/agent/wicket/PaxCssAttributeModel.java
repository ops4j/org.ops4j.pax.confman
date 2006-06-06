/*
 * Copyright 2006 Edward Yakop.
 *
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package org.ops4j.pax.cm.agent.wicket;

import org.ops4j.lang.NullArgumentException;
import wicket.Component;
import wicket.extensions.markup.html.repeater.refreshing.Item;
import wicket.model.AbstractReadOnlyModel;

public final class PaxCssAttributeModel extends AbstractReadOnlyModel
{
    private static final String CSS_CLASS_EVEN = "even";
    private static final String CSS_CLASS_ODD = "odd";

    private final Item m_item;

    public PaxCssAttributeModel( Item item )
    {
        NullArgumentException.validateNotNull( item, "item" );
        m_item = item;
    }

    public final Object getObject( Component component )
    {
        return ( m_item.getIndex() % 2 == 1 ) ? CSS_CLASS_EVEN : CSS_CLASS_ODD;
    }
}
