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
package org.ops4j.pax.cm.agent.wicket.configuration.edit;

import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ops4j.pax.cm.agent.WicketApplicationConstant;
import org.ops4j.pax.cm.agent.configuration.ConfigurationAdminException;
import org.ops4j.pax.cm.agent.configuration.PaxConfiguration;
import org.ops4j.pax.cm.agent.configuration.PaxConfigurationFacade;
import org.ops4j.pax.cm.agent.wicket.configuration.edit.properties.ConfigurationPropertiesEditor;
import wicket.Application;
import wicket.Component;
import wicket.Localizer;
import wicket.Page;
import wicket.PageParameters;
import wicket.markup.html.basic.Label;
import wicket.markup.html.form.Button;
import wicket.markup.html.form.Form;
import wicket.markup.html.form.TextField;
import wicket.markup.html.panel.Panel;
import wicket.model.CompoundPropertyModel;

/**
 * @author Edward Yakop
 * @since 0.1.0
 */
final class EditConfigurationPanel extends Panel
{

    private static final Log m_logger = LogFactory.getLog( EditConfigurationPanel.class );

    static final String PAGE_ID = "editConfigurationPage";

    private static final String LOCALE_PID_LABEL = "PIDLabel";
    private static final String LOCALE_FACTORY_PID_LABEL = "factoryPIDLabel";
    private static final String LOCALE_BUNDLE_LOCATION = "bundleLocationLabel";

    private static final String WICKET_ID_PID_LABEL = "pidLabel";
    private static final String WICKET_ID_PID_LABEL_VALUE = "pid";
    private static final String WICKET_ID_FACTORY_PID_LABEL = "factoryPidLabel";
    private static final String WICKET_ID_FACTORY_PID_LABEL_VALUE = "factorypid";
    private static final String WICKET_ID_BUNDLE_LOCATION_LABEL = "bundleLocationLabel";
    private static final String WICKET_ID_BUNDLE_LOCATION_VALUE = "bundleLocation";
    private static final String WICKET_ID_SAVE = "save";
    private static final String WICKET_ID_RESET = "reset";
    private static final String WICKET_ID_DELETE = "delete";
    private static final String WICKET_ID_FORM = "form";
    private final ConfigurationPropertiesEditor m_propertiesEditor;

    /**
     * @param id Wicket id. This argument must not be {@code null}.
     *
     * @since 0.1.0
     */
    EditConfigurationPanel( String id, PaxConfiguration configuration,
                            EditConfigurationPageContainer pageContainer )
        throws IllegalArgumentException
    {
        super( id );

        EditConfigurationForm editConfigurationForm =
            new EditConfigurationForm( WICKET_ID_FORM, configuration );
        add( editConfigurationForm );

        Component configPropsComponent = pageContainer.createConfigurationPropertiesEditor( configuration );
        add( configPropsComponent );
        m_propertiesEditor = (ConfigurationPropertiesEditor) configPropsComponent;
    }

    private final class EditConfigurationForm extends Form
    {

        private PaxConfiguration m_configuration;

        private EditConfigurationForm(
            String id, PaxConfiguration configuration )
        {
            super( id );

            m_configuration = configuration;

            CompoundPropertyModel compoundPropertyModel = new CompoundPropertyModel( m_configuration );
            setModel( compoundPropertyModel );

            Localizer localizer = EditConfigurationPanel.this.getLocalizer();

            // PID label and text field.
            Label PIDLabel = newPidLabel( localizer );
            add( PIDLabel );

            TextField pidTextField = new TextField( WICKET_ID_PID_LABEL_VALUE );
            pidTextField.setEnabled( false );
            add( pidTextField );

            // Factory PID label and text field.
            Label factoryPIDLabel = newFactoryPidLabel( localizer );
            add( factoryPIDLabel );

            String factoryPid = configuration.getFactoryPid();
            TextField factoryPIDTextField = new TextField( WICKET_ID_FACTORY_PID_LABEL_VALUE );
            factoryPIDTextField.setEnabled( false );
            add( factoryPIDTextField );
            if( factoryPid == null )
            {
                factoryPIDLabel.setVisible( false );
                factoryPIDTextField.setVisible( false );
            }

            // Bundle location label and label.
            Label bundleLocationLabel = newBundleLocationLabel( localizer );
            add( bundleLocationLabel );

            TextField bundleLocationValue = new TextField( WICKET_ID_BUNDLE_LOCATION_VALUE );
            bundleLocationValue.setEnabled( false );
            add( bundleLocationValue );

            Button saveButton = newSaveButton();
            add( saveButton );

            Button resetButton = newResetButton();
            add( resetButton );

            Button deleteButton = newDeleteButton();
            add( deleteButton );
        }

        private Label newBundleLocationLabel( Localizer localizer )
        {
            String bundleLocationLabelString = localizer.getString( LOCALE_BUNDLE_LOCATION, this, "Bundle location:" );
            return new Label( WICKET_ID_BUNDLE_LOCATION_LABEL, bundleLocationLabelString );
        }

        private Label newFactoryPidLabel( Localizer localizer )
        {
            String factoryPIDLabelString = localizer.getString( LOCALE_FACTORY_PID_LABEL, this, "Factory PID:" );
            return new Label( WICKET_ID_FACTORY_PID_LABEL, factoryPIDLabelString );
        }

        private Label newPidLabel( Localizer localizer )
        {
            String PIDLabelString = localizer.getString( LOCALE_PID_LABEL, this, "PID:" );
            return new Label( WICKET_ID_PID_LABEL, PIDLabelString );
        }

        private Button newDeleteButton()
        {
            Button deleteButton = new Button( WICKET_ID_DELETE )
            {
                protected void onSubmit()
                {
                    try
                    {
                        PaxConfiguration configuration = m_configuration;

                        try
                        {
                            PaxConfigurationFacade.deleteConfiguration( configuration );
                        } catch( IOException e )
                        {
                            m_logger.warn( "Unable to delete [" + m_configuration.getPid() + "].", e );
                        }

                        Application application = getApplication();
                        Class homePage = application.getHomePage();
                        PageParameters pageParameters = new PageParameters();
                        String pageParamTabName = WicketApplicationConstant.Overview.PAGE_PARAM_TAB_ID;
                        String tabNameBrowser = WicketApplicationConstant.Overview.MENU_TAB_ID_BROWSER;
                        pageParameters.add( pageParamTabName, tabNameBrowser );

                        Page page = findPage();
                        page.setResponsePage( homePage, pageParameters );
                    } catch( ConfigurationAdminException cae )
                    {
                        m_logger.debug( "Unable to delete configuration [" + m_configuration.getPid() + "].", cae );
                    }
                }
            };
            deleteButton.setDefaultFormProcessing( false );
            return deleteButton;
        }

        private Button newSaveButton()
        {
            return new Button( WICKET_ID_SAVE )
            {
                protected void onSubmit()
                {
                    try
                    {
                        PaxConfigurationFacade.updateConfiguration( m_configuration );
                    } catch( IOException e )
                    {
                        m_logger.warn( "Unable to save configuration [" + m_configuration.getPid() + "]", e );
                    }
                }
            };
        }

        private Button newResetButton()
        {
            Button resetButton = new Button( WICKET_ID_RESET )
            {
                public void onSubmit()
                {
                    String pid = m_configuration.getPid();
                    try
                    {
                        String factoryPid = m_configuration.getFactoryPid();
                        if( factoryPid != null && factoryPid.length() > 0 )
                        {
                            m_configuration = PaxConfigurationFacade.getConfiguration( factoryPid, true );
                        }
                        else
                        {
                            m_configuration = PaxConfigurationFacade.getConfiguration( pid, false );
                        }

                        EditConfigurationForm.this.setModelObject( m_configuration );

                        m_propertiesEditor.setPaxConfiguration( m_configuration );
                    } catch( IOException e )
                    {
                        m_logger.warn( "Unable to load configuration [" + pid + "]", e );
                    }
                }
            };
            resetButton.setDefaultFormProcessing( false );
            return resetButton;
        }
    }
}