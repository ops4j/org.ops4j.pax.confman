/*
 * Copyright 2006 Niclas Hedhman.
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
package org.ops4j.pax.cm.agent.wicket.configuration.edit;

import java.text.MessageFormat;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.cm.agent.configuration.PaxConfiguration;
import org.osgi.service.cm.Configuration;
import wicket.Localizer;
import wicket.markup.html.WebPage;
import wicket.markup.html.basic.Label;

/**
 * @author Edward Yakop
 * @since 0.1.0
 */
public final class EditConfigurationPage extends WebPage
{

    static final String PAGE_ID = "editConfigurationPage";

    private static final String LOCALE_DELETED_CONFIGURATION_MESSAGE = "deletedConfigurationMessage";
    private static final String WICKET_ID_EDIT_PANEL = "editPanel";
    private static final String DEFAULT_DELETED_CONFIGURATION_MESSAGE = "Configuration \"{0}\" is deleted.";

    EditConfigurationPage( String configurationPID, Configuration configuration,
                           EditConfigurationPageContainer pageContainer )
        throws IllegalArgumentException
    {
        NullArgumentException.validateNotEmpty( configurationPID, "configurationPID" );
        NullArgumentException.validateNotNull( configurationPID, "configuration" );

        if( !testValidityOfConfiguration( configuration ) )
        {
            Localizer localizer = getLocalizer();
            String deletedMessageString = localizer.getString(
                LOCALE_DELETED_CONFIGURATION_MESSAGE, this, DEFAULT_DELETED_CONFIGURATION_MESSAGE
            );
            deletedMessageString = MessageFormat.format( deletedMessageString, configurationPID );
            Label message = new Label( WICKET_ID_EDIT_PANEL, deletedMessageString );
            add( message );
        }
        else
        {
            PaxConfiguration paxConfiguration = new PaxConfiguration( configuration );
            EditConfigurationPanel editConfigurationPanel =
                new EditConfigurationPanel( WICKET_ID_EDIT_PANEL, paxConfiguration, pageContainer );
            add( editConfigurationPanel );
        }
    }

    private boolean testValidityOfConfiguration( Configuration configuration )
        throws IllegalArgumentException
    {
        NullArgumentException.validateNotNull( configuration, "configuration" );
        try
        {
            // R4 specs says that configuration.getPID() must throw IllegalStateException when it is deleted.
            configuration.getPid();

            return true;
        } catch( IllegalStateException e )
        {
            // Means the configuration has been deleted.
            return false;
        }
    }
}