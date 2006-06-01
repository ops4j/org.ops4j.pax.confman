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
package org.ops4j.pax.cm.agent.wicket.configuration.browser;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ops4j.pax.cm.agent.configuration.PaxConfiguration;
import wicket.markup.html.basic.Label;
import wicket.markup.html.form.Form;
import wicket.markup.html.form.TextField;
import wicket.markup.html.link.Link;
import wicket.markup.html.panel.Panel;
import wicket.model.Model;
import wicket.model.PropertyModel;

/**
 * @author Edward Yakop
 * @since 0.1.0
 */
class MiniEditConfigurationPanel extends Panel
{

    private static final Log m_logger = LogFactory.getLog( MiniEditConfigurationPanel.class );

    private static final String WICKET_ID_FORM = "form";

    private final PaxConfiguration m_configuration;

    public MiniEditConfigurationPanel( String id, ConfigurationDataProvider confDataProvider )
    {
        super( id );

        PaxConfiguration configuration = confDataProvider.getSelectedPaxconfiguration();

        m_configuration = new PaxConfiguration();

        if( configuration == null )
        {
            empty( m_configuration );
        }
        else
        {
            copy( m_configuration, configuration );
        }

        MiniConfigurationForm form = new MiniConfigurationForm( WICKET_ID_FORM, configuration, confDataProvider );

        if( configuration == null )
        {
            form.disableAllComponentsExceptsNew();
        }
        else
        {
            form.enableAllComponents();
        }

        add( form );
    }

    private static void empty( PaxConfiguration configuration )
    {
        configuration.setPid( "" );
        configuration.setBundleLocation( "" );
        configuration.setFactoryPid( "" );
        configuration.setIsNew( true );
    }

    private static void copy( PaxConfiguration configuration, PaxConfiguration configuration1 )
    {
        configuration.setPid( configuration1.getPid() );
        configuration.setBundleLocation( configuration1.getBundleLocation() );
        configuration.setFactoryPid( configuration1.getFactoryPid() );
        configuration.setIsNew( configuration1.isNew() );
    }

    private static final class MiniConfigurationForm extends Form
    {

        private final TextField m_PIDTextField;
        private final TextField m_facPIDTextField;
        private final TextField m_bdlLocModelTextField;
        private final PaxConfiguration m_configuration;
        private final ConfigurationDataProvider m_confDataProvider;

        public MiniConfigurationForm( String id, PaxConfiguration configuration,
                                      ConfigurationDataProvider confDataProvider )
        {
            super( id );
            Model model = new Model( configuration );
            setModel( model );

            m_configuration = configuration;
            m_confDataProvider = confDataProvider;

            Label pidLabel = new Label( "PIDLabel", "Pid:" );
            add( pidLabel );

            PropertyModel pidModel = new PropertyModel( model, PaxConfiguration.PROPERTY_PID );
            m_PIDTextField = new TextField( "pid", pidModel );
            add( m_PIDTextField );

            Label factoryLabel = new Label( "factoryPIDLabel", "Factory PID:" );
            add( factoryLabel );

            PropertyModel facPidModel = new PropertyModel( model, PaxConfiguration.PROPERTY_FACTORY_PID );
            m_facPIDTextField = new TextField( "factoryPid", facPidModel );
            add( m_facPIDTextField );

            Label bundleLocation = new Label( "bundleLocationLabel", "Bundle Location:" );
            add( bundleLocation );

            PropertyModel bdlLocModel = new PropertyModel( model, PaxConfiguration.PROPERTY_BUNDLE_LOCATION );
            m_bdlLocModelTextField = new TextField( "bundleLocation", bdlLocModel );
            add( m_bdlLocModelTextField );

            NewLink newLink = new NewLink( "new", this, confDataProvider );
            add( newLink );

            SaveLink saveLink = new SaveLink( "save", this, confDataProvider );
            add( saveLink );

            DeleteLink deleteLink = new DeleteLink( "delete", this, confDataProvider );
            add( deleteLink );
        }

        public void disableAllComponentsExceptsNew()
        {
            m_bdlLocModelTextField.setEnabled( false );
            m_facPIDTextField.setEnabled( false );
            m_PIDTextField.setEnabled( false );
        }

        public void enableAllComponents()
        {
            m_bdlLocModelTextField.setEnabled( true );
            m_facPIDTextField.setEnabled( true );
            m_PIDTextField.setEnabled( true );
        }

        public void setConfiguration( PaxConfiguration paxConfiguration )
        {
            modelChanging();
            copy( m_configuration, paxConfiguration );

            this.modelChanged();
        }

    }

    private static final class NewLink extends Link
    {

        private final MiniConfigurationForm m_form;
        private final ConfigurationDataProvider m_confDataProvider;

        public NewLink( String id, MiniConfigurationForm form, ConfigurationDataProvider confDataProvider )
        {
            super( id );

            m_form = form;
            m_confDataProvider = confDataProvider;
        }

        public void onClick()
        {
            PaxConfiguration paxConfiguration = m_confDataProvider.createNewPaxConfiguration();
            m_form.setConfiguration( paxConfiguration );
        }
    }

    private static final class SaveLink extends Link
    {

        private final MiniConfigurationForm m_form;
        private final ConfigurationDataProvider m_confDataProvider;

        public SaveLink( String id, MiniConfigurationForm form, ConfigurationDataProvider confDataProvider )
        {
            super( id );

            m_form = form;
            m_confDataProvider = confDataProvider;
        }

        public void onClick()
        {
            PaxConfiguration paxConfiguration = m_confDataProvider.savePaxConfiguration( m_form.m_configuration );
            m_form.setConfiguration( paxConfiguration );
        }
    }

    private static final class DeleteLink extends Link
    {

        private final MiniConfigurationForm m_form;
        private final ConfigurationDataProvider m_confDataProvider;

        public DeleteLink( String id, MiniConfigurationForm form, ConfigurationDataProvider confDataProvider )
        {
            super( id );

            m_form = form;
            m_confDataProvider = confDataProvider;
        }

        public void onClick()
        {
            PaxConfiguration paxConfiguration = m_confDataProvider.deletePaxConfiguration( m_form.m_configuration );
            m_form.setConfiguration( paxConfiguration );
        }
    }

}
