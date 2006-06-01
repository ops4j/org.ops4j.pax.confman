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
package org.ops4j.pax.cm.agent.internal;

import java.util.Properties;
import org.apache.commons.logging.LogFactory;
import org.ops4j.pax.cm.agent.WicketApplicationConstant;
import org.ops4j.pax.cm.agent.configuration.PaxConfigurationFacade;
import org.ops4j.pax.cm.agent.utils.SimpleClassResolver;
import org.ops4j.pax.cm.agent.wicket.configuration.browser.ConfigurationBrowserPanelContent;
import org.ops4j.pax.cm.agent.wicket.configuration.edit.EditConfigurationPageContainer;
import org.ops4j.pax.cm.agent.wicket.configuration.edit.EditConfigurationPageContent;
import org.ops4j.pax.cm.agent.wicket.overview.OverviewPage;
import org.ops4j.pax.cm.agent.wicket.overview.OverviewPageContent;
import org.ops4j.pax.wicket.service.Content;
import org.ops4j.pax.wicket.service.DefaultPageContainer;
import org.ops4j.pax.wicket.service.PaxWicketApplicationFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import wicket.application.IClassResolver;

/**
 * {@code Activator} responsibles to activate the pax config admin.
 *
 * @author Edward Yakop
 * @since 0.1.0
 */
public final class Activator
    implements BundleActivator
{
    private OverviewPageContent m_overviewPageContent;

    private ServiceRegistration m_overviewPageContainerSerReg;
    private ConfigurationBrowserPanelContent m_configurationBrowserPanelContent;

    private EditConfigurationPageContent m_editConfigurationPageContent;
    private ServiceRegistration m_configurationEditorPageContainerSerReg;

    private ConfigAdminTracker m_configAdminTracker;
    private ServiceRegistration m_classResolverRegistration;

    public void start( BundleContext bundleContext )
        throws Exception
    {
        LogFactory.setBundleContext( bundleContext );
        PaxConfigurationFacade.setContext( bundleContext );

        m_configurationBrowserPanelContent =
            new ConfigurationBrowserPanelContent(
                bundleContext, WicketApplicationConstant.Overview.DESTINATION_ID_MENU_TAB
            );
        m_configurationBrowserPanelContent.register();

        String applicationName = WicketApplicationConstant.APPLICATION_NAME;

        SimpleClassResolver service = new SimpleClassResolver( bundleContext );
        Properties properties = new Properties();
        properties.setProperty( Content.APPLICATION_NAME, applicationName );
        String classResolverServiceName = IClassResolver.class.getName();
        m_classResolverRegistration = bundleContext.registerService( classResolverServiceName, service, properties );

        DefaultPageContainer overviewPageContainer = new DefaultPageContainer(
            bundleContext, WicketApplicationConstant.Overview.CONTAINMENT_ID, applicationName
        );
        m_overviewPageContainerSerReg = overviewPageContainer.register();
        m_overviewPageContent = new OverviewPageContent( bundleContext, overviewPageContainer );
        m_overviewPageContent.register();

        PaxWicketApplicationFactory application = new PaxWicketApplicationFactory(
            bundleContext, OverviewPage.class, WicketApplicationConstant.MOUNT_POINT,
            applicationName
        );
        application.setDeploymentMode( true );

        EditConfigurationPageContainer configurationEditorPageContainer = new EditConfigurationPageContainer(
            bundleContext, WicketApplicationConstant.Configuration.Edit.CONTAINMENT_ID, applicationName
        );
        m_configurationEditorPageContainerSerReg = configurationEditorPageContainer.register();
        m_editConfigurationPageContent = new EditConfigurationPageContent(
            bundleContext, configurationEditorPageContainer, applicationName
        );
        m_editConfigurationPageContent.register();

        m_configAdminTracker = new ConfigAdminTracker( bundleContext, application );
        m_configAdminTracker.open();
    }

    /**
     * Unregister all registered services.
     *
     * @since 0.1.0
     */
    public void stop( BundleContext bundleContext )
        throws Exception
    {
        m_configurationBrowserPanelContent.dispose();

        m_classResolverRegistration.unregister();

        m_overviewPageContainerSerReg.unregister();
        m_overviewPageContent.dispose();

        m_configurationEditorPageContainerSerReg.unregister();
        m_editConfigurationPageContent.dispose();

        m_configAdminTracker.close();
        PaxConfigurationFacade.setContext( null );
    }
}
