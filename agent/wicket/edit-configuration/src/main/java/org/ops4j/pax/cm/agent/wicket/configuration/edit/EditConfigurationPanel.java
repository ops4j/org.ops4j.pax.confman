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
import org.ops4j.pax.cm.agent.ApplicationConstant;
import org.ops4j.pax.cm.agent.ConfigurationConstant;
import org.ops4j.pax.cm.agent.configuration.ConfigurationAdminException;
import org.ops4j.pax.cm.agent.configuration.PaxConfiguration;
import org.ops4j.pax.cm.agent.configuration.PaxConfigurationFacade;
import org.osgi.service.cm.Configuration;
import wicket.Application;
import wicket.Localizer;
import wicket.PageParameters;
import wicket.RequestCycle;
import wicket.markup.html.basic.Label;
import wicket.markup.html.form.Form;
import wicket.markup.html.link.Link;
import wicket.markup.html.panel.Panel;
import wicket.model.CompoundPropertyModel;
import wicket.model.IModel;
import wicket.model.PropertyModel;

/**
 * @author Edward Yakop
 * @since 0.1.0
 */
final class EditConfigurationPanel extends Panel
{
    static final String PAGE_ID = "editConfigurationPage";

    private static final String LOCALE_PID_LABEL = "PIDLabel";
    private static final String LOCALE_FACTORY_PID_LABEL = "factoryPIDLabel";
    private static final String LOCALE_BUNDLE_LOCATION = "bundleLocationLabel";

    private static final String WICKET_ID_PID_LABEL = "pidLabel";
    private static final String WICKET_ID_PID_LABEL_VALUE = "pid";
    private static final String WICKET_ID_FACTORY_PID_LABEL = "factoryPidLabel";
    private static final String WICKET_ID_FACTORY_PID_LABEL_VALUE = "factorypid";
    private static final String WICKET_ID_BUNDLE_LOCATION_LABEL = "bundleLocation";
    private static final String WICKET_ID_BUNDLE_LOCATION_VALUE = "bundleLocationValue";
    private static final String WICKET_ID_CONFIG_PROPERTIES = "configProperties";
    private static final String WICKET_ID_SAVE = "save";
    private static final String WICKET_ID_RESET = "reset";
    private static final String WICKET_ID_DELETE = "delete";

    /**
     * @param id Wicket id. This argument must not be {@code null}.
     *
     * @since 0.1.0
     */
    public EditConfigurationPanel( String id, Configuration configuration )
        throws IllegalArgumentException
    {
        super( id );

        Localizer localizer = getLocalizer();
        PaxConfiguration paxConfiguration = new PaxConfiguration( configuration );
        CompoundPropertyModel formModel = new CompoundPropertyModel( paxConfiguration );
        EditConfigurationForm editConfigurationForm =
            new EditConfigurationForm( "form", formModel, localizer, paxConfiguration );
        add( editConfigurationForm );
    }

    private final class EditConfigurationForm extends Form
    {
        private final PaxConfiguration m_configuration;
        private Class m_responsePageClass;
        private PageParameters m_pageParameters;

        private EditConfigurationForm( String id, IModel model, Localizer localizer, PaxConfiguration configuration )
        {
            super( id, model );

            m_configuration = configuration;
            m_responsePageClass = EditConfigurationPage.class;
            m_pageParameters = new PageParameters();

            // PID label and text field.
            String PIDLabelString = localizer.getString( LOCALE_PID_LABEL, this, "PID:" );
            Label PIDLabel = new Label( WICKET_ID_PID_LABEL, PIDLabelString );
            add( PIDLabel );
            PropertyModel PIDConfigModel = new PropertyModel( configuration, "pid" );
            Label pidLabelValue = new Label( WICKET_ID_PID_LABEL_VALUE, PIDConfigModel );
            add( pidLabelValue );

            // Factory PID label and text field.
            String factoryPIDLabelString = localizer.getString( LOCALE_FACTORY_PID_LABEL, this, "Factory PID:" );
            Label factoryPIDLabel = new Label( WICKET_ID_FACTORY_PID_LABEL, factoryPIDLabelString );
            add( factoryPIDLabel );
            String factoryPid = configuration.getFactoryPid();
            Label factoryPIDLabelValue = new Label( WICKET_ID_FACTORY_PID_LABEL_VALUE, configuration.getFactoryPid() );
            add( factoryPIDLabelValue );
            if( factoryPid == null )
            {
                factoryPIDLabel.setVisible( false );
                factoryPIDLabelValue.setVisible( false );
            }

            // Bundle location label and label.
            String bundleLocationLabelString = localizer.getString( LOCALE_BUNDLE_LOCATION, this, "Bundle location:" );
            Label bundleLocationLabel = new Label( WICKET_ID_BUNDLE_LOCATION_LABEL, bundleLocationLabelString );
            add( bundleLocationLabel );

            String bundleLocation = configuration.getBundleLocation();
            if( bundleLocation == null )
            {
                bundleLocation = "";
            }
            Label bundleLocationValue = new Label( WICKET_ID_BUNDLE_LOCATION_VALUE, bundleLocation );
            add( bundleLocationValue );

//        Dictionary properties = configuration.getProperties();
//        ConfigurationItemDataProvider dataProvider = new ConfigurationItemDataProvider( properties );
//        ConfigurationItemDataTable configurationDataTable =
//            new ConfigurationItemDataTable( WICKET_ID_CONFIG_PROPERTIES, dataProvider, 8 );
//        add( configurationDataTable );

            Link saveLink = new SaveLink( WICKET_ID_SAVE, this );
            add( saveLink );

            ResetLink resetLink = new ResetLink( WICKET_ID_RESET, this );
            add( resetLink );

            DeleteLink deleteLink = new DeleteLink( WICKET_ID_DELETE, this );
            add( deleteLink );
        }

        protected void onSubmit()
        {
            RequestCycle requestCycle = getRequestCycle();

            requestCycle.setResponsePage( m_responsePageClass, m_pageParameters );
            requestCycle.setRedirect( true );
        }

        private void setResponsePageLocation( Class responseClass, PageParameters pageParameters )
        {
            m_responsePageClass = responseClass;
            m_pageParameters = pageParameters;
        }

        private PaxConfiguration getConfiguration()
        {
            return m_configuration;
        }
    }

    private static final class DeleteLink extends Link
    {
        private final EditConfigurationForm m_form;

        public DeleteLink( String id, EditConfigurationForm form )
        {
            super( id );
            m_form = form;
        }

        public void onClick()
        {
            try
            {
                PaxConfiguration configuration = m_form.getConfiguration();

                PaxConfigurationFacade.deleteConfiguration( configuration );

                Application application = getApplication();
                Class homePage = application.getHomePage();
                PageParameters pageParameters = new PageParameters();
                String pageParamTabName = ApplicationConstant.Overview.PAGE_PARAM_TAB_NAME;
                String tabNameBrowser = ApplicationConstant.Overview.TAB_NAME_BROWSER;
                pageParameters.add( pageParamTabName, tabNameBrowser );

                m_form.setResponsePageLocation( homePage, pageParameters );
            } catch( ConfigurationAdminException cae )
            {
                // TODO add the exception message to explaination panel
                cae.printStackTrace();
            }
        }
    }

    private static final class ResetLink extends Link
    {
        private final EditConfigurationForm m_form;

        private ResetLink( String id, EditConfigurationForm form )
        {
            super( id );
            m_form = form;
        }

        public void onClick()
        {
            PaxConfiguration paxConfiguration = m_form.getConfiguration();
            String configurationPid = paxConfiguration.getPid();
            PageParameters pageParameters = new PageParameters();
            pageParameters.add( ConfigurationConstant.PARAM_KEY_PID, configurationPid );
            m_form.setResponsePageLocation( EditConfigurationPage.class, pageParameters );
        }
    }

    private static final class SaveLink extends Link
    {
        private final EditConfigurationForm m_form;

        private SaveLink( String id, EditConfigurationForm form )
        {
            super( id );
            m_form = form;
        }

        public void onClick()
        {
            PaxConfiguration paxConfiguration = m_form.getConfiguration();
            try
            {
                PaxConfigurationFacade.updateConfiguration( paxConfiguration );
            } catch( IOException e )
            {
                // TODO: need to add this error to explaination panel
                e.printStackTrace();
            }

            Application application = getApplication();
            Class homePage = application.getHomePage();
            PageParameters pageParameters = new PageParameters();
            String pageParamTabName = ApplicationConstant.Overview.PAGE_PARAM_TAB_NAME;
            String tabNameBrowser = ApplicationConstant.Overview.TAB_NAME_BROWSER;
            pageParameters.add( pageParamTabName, tabNameBrowser );

            m_form.setResponsePageLocation( homePage, pageParameters );
        }
    }
}