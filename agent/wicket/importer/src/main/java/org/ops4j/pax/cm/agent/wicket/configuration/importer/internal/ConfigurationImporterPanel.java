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
package org.ops4j.pax.cm.agent.wicket.configuration.importer.internal;

import java.util.Set;
import wicket.Component;
import wicket.Localizer;
import wicket.markup.html.basic.Label;
import wicket.markup.html.panel.Panel;

final class ConfigurationImporterPanel extends Panel
{

    private static final String LOCALE_NO_IMPORTER = "NoImporterAvailable";
    private static final String DEFAULT_MESSAGE_NO_IMPORTER_AVAILABLE = "No importers available.";

    private static final String WICKET_ID_IMPORT_PANEL = "importPanel";

    ConfigurationImporterPanel( String wicketId )
    {
        super( wicketId );

        Component child = newChild();
        add( child );
    }

    private Component newChild()
    {
        Component child;

        Set<String> importerIds = ImporterTracker.getImporterIds();
        if( importerIds.isEmpty() )
        {
            Localizer localizer = getLocalizer();
            String message = localizer.getString( LOCALE_NO_IMPORTER, this, DEFAULT_MESSAGE_NO_IMPORTER_AVAILABLE );
            child = new Label( WICKET_ID_IMPORT_PANEL, message );
        }
        else
        {
            child = new ImportPanel( WICKET_ID_IMPORT_PANEL );
        }

        return child;
    }
}
