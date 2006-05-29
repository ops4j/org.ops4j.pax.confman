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

import java.util.ArrayList;
import java.util.Dictionary;
import org.ops4j.lang.NullArgumentException;
import org.osgi.service.cm.Configuration;
import wicket.extensions.markup.html.repeater.data.table.DefaultDataTable;
import wicket.extensions.markup.html.repeater.data.table.IColumn;
import wicket.markup.html.WebPage;
import wicket.markup.html.basic.Label;
import wicket.markup.html.form.TextField;
import wicket.model.Model;
import wicket.model.PropertyModel;
import wicket.model.StringResourceModel;

/**
 * @author Edward Yakop
 * @since 0.1.0
 */
public final class EditConfigurationPage extends WebPage
{
    static final String PAGE_ID = "editConfigurationPage";

    private static final String WICKET_ID_PID = "pid";
    private static final String WICKET_ID_CONFIG_PROPERTIES = "configProperties";
    private static final String WICKET_ID_FACTORY_PID = "factorypid";
    private static final String WICKET_ID_DELETED_MESSAGE = "configurationDeletedMessage";
    private static final String WICKET_ID_BUNDLE_LOCATION = "bundleLocation";
    private static final String WICKET_ID_PID_LABEL = "pidLabel";
    private static final String WICKET_ID_FACTORY_PID_LABEL = "factoryPidLabel";

    EditConfigurationPage( String configurationPid, Configuration configuration )
        throws IllegalArgumentException
    {
        if( !testValidityOfConfiguration( configuration ) )
        {
            displayConfigurationHasBeenDeleted( configurationPid );
        }

        Label deletedMessage = new Label( WICKET_ID_DELETED_MESSAGE, "" );
        deletedMessage.setVisible( false );
        add( deletedMessage );

        Model emptyModel = new Model();
        StringResourceModel pidLabelResMdl = new StringResourceModel( WICKET_ID_PID_LABEL, this, emptyModel );
        Label pidLabel = new Label( WICKET_ID_PID_LABEL, pidLabelResMdl );
        add( pidLabel );

        PropertyModel pidConfigModel = new PropertyModel( configuration, "pid" );
        TextField pidTextInput = new TextField( WICKET_ID_PID, pidConfigModel );
        add( pidTextInput );

        StringResourceModel facPidResMdl = new StringResourceModel( WICKET_ID_FACTORY_PID_LABEL, this, emptyModel );
        Label factoryPidLabel = new Label( WICKET_ID_FACTORY_PID_LABEL, facPidResMdl );
        add( factoryPidLabel );

        PropertyModel factoryPropertyModel = new PropertyModel( configuration, "factoryPid" );
        TextField factoryLabel = new TextField( WICKET_ID_FACTORY_PID, factoryPropertyModel );
        String factoryPid = configuration.getFactoryPid();
        if( factoryPid == null )
        {
            factoryLabel.setVisible( false );
        }
        add( factoryLabel );

        String bundleLocation = configuration.getBundleLocation();
        if( bundleLocation == null )
        {
            bundleLocation = "no bound to any location";
        }

        Label bundleLocLabel = new Label( WICKET_ID_BUNDLE_LOCATION, bundleLocation );
        add( bundleLocLabel );

        Dictionary properties = configuration.getProperties();
        ConfigurationItemDataProvider dataProvider = new ConfigurationItemDataProvider( properties );
        ConfigurationItemDataTable configurationDataTable =
            new ConfigurationItemDataTable( WICKET_ID_CONFIG_PROPERTIES, dataProvider, 8 );
        add( configurationDataTable );
    }

    private boolean testValidityOfConfiguration( Configuration configuration )
        throws IllegalArgumentException
    {
        NullArgumentException.validateNotNull( configuration, "configuration" );
        try
        {
            configuration.getPid();
            return true;
        } catch( IllegalStateException e )
        {
            // Means the configuration has been deleted.
            return false;
        }
    }

    private void displayConfigurationHasBeenDeleted( String configurationPid )
        throws IllegalArgumentException
    {
        NullArgumentException.validateNotEmpty( configurationPid, "configurationPid" );

        Label message = new Label( WICKET_ID_DELETED_MESSAGE, "Configuration [" + configurationPid + "] is deleted." );
        add( message );

        Label pidLabel = new Label( WICKET_ID_PID, "" );
        pidLabel.setVisible( false );
        add( pidLabel );

        Label factoryLabel = new Label( WICKET_ID_FACTORY_PID, "" );
        factoryLabel.setVisible( false );
        add( factoryLabel );

        DefaultDataTable propertiesDataTable =
            new DefaultDataTable( WICKET_ID_CONFIG_PROPERTIES, new ArrayList<IColumn>(), null, 10 );
        propertiesDataTable.setVisible( false );
        add( propertiesDataTable );
    }
}