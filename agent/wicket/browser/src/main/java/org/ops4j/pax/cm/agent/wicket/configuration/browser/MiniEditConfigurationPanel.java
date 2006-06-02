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

import org.ops4j.pax.cm.agent.configuration.PaxConfiguration;
import wicket.markup.html.basic.Label;
import wicket.markup.html.form.Button;
import wicket.markup.html.form.CheckBox;
import wicket.markup.html.form.Form;
import wicket.markup.html.form.TextField;
import wicket.markup.html.panel.Panel;
import wicket.model.CompoundPropertyModel;
import wicket.model.Model;
import wicket.model.PropertyModel;

/**
 * @author Edward Yakop
 * @since 0.1.0
 */
final class MiniEditConfigurationPanel extends Panel
    implements SelectionChangeListener
{

    private static final String WICKET_ID_FORM = "form";

    private PaxConfiguration m_configuration;
    private final ConfigurationDataProvider m_confDataProvider;

    MiniEditConfigurationPanel( String id, ConfigurationDataProvider confDataProvider )
    {
        super( id );
        m_confDataProvider = confDataProvider;

        PaxConfiguration configuration = confDataProvider.getSelectedPaxconfiguration();
        confDataProvider.setSelectionListener( this );

        if( configuration == null )
        {
            m_configuration = new PaxConfiguration();
            m_configuration.setIsNew( true );
        }
        else
        {
            m_configuration = configuration;
        }

        MiniConfigurationForm form = new MiniConfigurationForm( WICKET_ID_FORM, configuration );

        if( configuration == null )
        {
            form.disableAllComponentsExceptsNew();
        }

        add( form );
    }

    public void showConfiguration( PaxConfiguration configuration )
    {
        modelChanging();

        m_configuration = configuration;
        setModel( new CompoundPropertyModel( configuration ) );

        modelChanged();
    }

    private final class MiniConfigurationForm extends Form
    {

        private static final String WICKET_ID_PID_LABEL = "PIDLabel";
        private static final String WICKET_ID_PID = "pid";
        private static final String WICKET_ID_FACTORY_PID_LABEL = "factoryPIDLabel";
        private static final String WICKET_ID_FACTORY_PID = "factoryPid";
        private static final String WICKET_ID_IS_FACTORY = "isFactory";
        private static final String WICKET_ID_BUNDLE_LOCATION_LABEL = "bundleLocationLabel";
        private static final String WICKET_ID_BUNDLE_LOCATION = "bundleLocation";
        private static final String WICKET_ID_DELETE = "delete";
        private static final String WICKET_ID_SAVE = "save";
        private static final String WICKET_ID_NEW = "new";

        private final Label m_pidLabel;
        private final TextField m_pidTextField;
        private final Label m_factoryPidLabel;
        private final TextField m_facPIDTextField;
        private final TextField m_bdlLocModelTextField;
        private final CheckBox m_isFactoryCheckBox;
        private boolean m_isFactory;

        private MiniConfigurationForm( String id, PaxConfiguration configuration )
        {
            super( id );

            CompoundPropertyModel model = new CompoundPropertyModel( configuration );
            setModel( model );

            boolean isConfigurationNew = m_configuration.isNew();

            m_pidLabel = newPidLabel( isConfigurationNew );
            add( m_pidLabel );

            m_pidTextField = newPidTextField();
            add( m_pidTextField );

            m_factoryPidLabel = newFactoryPidLabel( isConfigurationNew );
            add( m_factoryPidLabel );

            m_facPIDTextField = newFactoryPidTextField( isConfigurationNew );
            add( m_facPIDTextField );

            m_isFactoryCheckBox = newIsFactoryCheckBox( isConfigurationNew );
            add( m_isFactoryCheckBox );

            Label bundleLocation = new Label( WICKET_ID_BUNDLE_LOCATION_LABEL, "Bundle Location:" );
            add( bundleLocation );

            m_bdlLocModelTextField = new TextField( WICKET_ID_BUNDLE_LOCATION );
            add( m_bdlLocModelTextField );

            Button newButton = newNewButton();
            add( newButton );

            Button saveButton = newSaveButton();
            add( saveButton );

            Button deleteButton = newDeleteButton();
            add( deleteButton );
        }

        private CheckBox newIsFactoryCheckBox( boolean isConfigurationNew )
        {
            PropertyModel checkBoxModel = new PropertyModel( this, "isFactory", Boolean.class );

            CheckBox checkBox = new CheckBox( WICKET_ID_IS_FACTORY, checkBoxModel );
            setIsFactoryCheckBoxState( isConfigurationNew, checkBox );

            return checkBox;
        }

        public void setFactory( Boolean value )
        {
            if( value == null )
            {
                value = false;
            }
            m_isFactory = value;
        }

        public Boolean isFactory()
        {
            return m_isFactory;
        }

        private void setIsFactoryCheckBoxState( boolean isConfigurationNew, CheckBox checkBox )
        {
            if( isConfigurationNew )
            {
                checkBox.setVisible( true );
            }
            else
            {
                checkBox.setVisible( false );
            }
        }

        private Button newNewButton()
        {
            return new Button( WICKET_ID_NEW )
            {
                protected void onSubmit()
                {
                    PaxConfiguration configToDisplay = m_confDataProvider.createNewPaxConfiguration();
                    setConfiguration( configToDisplay );

                    boolean isConfigurationNew = configToDisplay.isNew();
                    String pidLabelString = getPidLabelString( isConfigurationNew );
                    Model pidLabelModel = new Model( pidLabelString );
                    m_pidLabel.setModel( pidLabelModel );

                    m_pidTextField.setEnabled( true );

                    setFactory( false );
                    setIsFactoryCheckBoxState( isConfigurationNew, m_isFactoryCheckBox );

                    setFactoryLabelState( isConfigurationNew, m_factoryPidLabel );

                    setFactoryPidTextFieldState( isConfigurationNew, m_facPIDTextField );
                }
            };
        }

        private Button newSaveButton()
        {
            Button saveButton = new Button( WICKET_ID_SAVE )
            {
                public void onSubmit()
                {
                    if( m_isFactory )
                    {
                        String factoryPid = m_configuration.getPid();
                        m_configuration.setFactoryPid( factoryPid );
                        m_configuration.setPid( null );
                    }
                    else
                    {
                        m_configuration.setFactoryPid( null );
                    }

                    PaxConfiguration configToDisplay = m_confDataProvider.savePaxConfiguration( m_configuration );
                    setConfiguration( configToDisplay );

                    boolean configurationIsNotNew = false;
                    String pidLabelString = getPidLabelString( configurationIsNotNew );
                    Model pidLabelModel = new Model( pidLabelString );
                    m_pidLabel.setModel( pidLabelModel );

                    m_pidTextField.setEnabled( false );

                    boolean isFactory = m_configuration.getFactoryPid() != null;
                    setFactory( isFactory );
                    setIsFactoryCheckBoxState( configurationIsNotNew, m_isFactoryCheckBox );

                    setFactoryLabelState( configurationIsNotNew, m_factoryPidLabel );

                    setFactoryPidTextFieldState( configurationIsNotNew, m_facPIDTextField );
                }
            };
            return saveButton;
        }

        private Button newDeleteButton()
        {
            Button deleteButton = new Button( WICKET_ID_DELETE )
            {
                public void onSubmit()
                {
                    PaxConfiguration paxConfiguration = m_confDataProvider.deletePaxConfiguration( m_configuration );
                    setConfiguration( paxConfiguration );
                }
            };
            deleteButton.setDefaultFormProcessing( false );

            return deleteButton;
        }

        private TextField newFactoryPidTextField( boolean configurationNew )
        {
            TextField factoryPidTextField = new TextField( WICKET_ID_FACTORY_PID );
            setFactoryPidTextFieldState( configurationNew, factoryPidTextField );

            return factoryPidTextField;
        }

        private void setFactoryPidTextFieldState( boolean configurationNew, TextField factoryPidTextField )
        {
            if( configurationNew )
            {
                factoryPidTextField.setVisible( false );
            }
            else
            {
                factoryPidTextField.setVisible( true );
                factoryPidTextField.setEnabled( false );
            }
        }

        private Label newFactoryPidLabel( boolean configurationnew )
        {
            Label factoryLabel = new Label( WICKET_ID_FACTORY_PID_LABEL, "Factory PID:" );
            setFactoryLabelState( configurationnew, factoryLabel );
            return factoryLabel;
        }

        private void setFactoryLabelState( boolean configurationnew, Label factoryLabel )
        {
            String factoryLabelString;
            if( configurationnew )
            {
                factoryLabelString = "is factory:";
            }
            else
            {
                factoryLabelString = "Factory Pid:";
            }
            Model factoryLabelModel = new Model( factoryLabelString );
            factoryLabel.setModel( factoryLabelModel );
        }

        private TextField newPidTextField()
        {
            TextField pidTextField = new TextField( WICKET_ID_PID );
            if( !m_configuration.isNew() )
            {
                pidTextField.setEnabled( false );
            }
            return pidTextField;
        }

        private Label newPidLabel( boolean configurationnew )
        {
            String pidLabelString = getPidLabelString( configurationnew );
            return new Label( WICKET_ID_PID_LABEL, pidLabelString );
        }

        private String getPidLabelString( boolean configurationnew )
        {
            String pidLabelString;
            if( configurationnew )
            {
                pidLabelString = "Pid/Factory Pid: ";
            }
            else
            {
                pidLabelString = "Pid:";
            }
            return pidLabelString;
        }

        private void disableAllComponentsExceptsNew()
        {
            m_bdlLocModelTextField.setEnabled( false );
            m_facPIDTextField.setEnabled( false );
            m_pidTextField.setEnabled( false );
        }

        private void setConfiguration( PaxConfiguration paxConfiguration )
        {
            modelChanging();

            m_configuration = paxConfiguration;
            CompoundPropertyModel newModel = new CompoundPropertyModel( m_configuration );

            setModel( newModel );
        }
    }
}
