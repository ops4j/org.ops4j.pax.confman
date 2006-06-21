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

import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.cm.agent.wicket.WicketApplicationConstant;
import org.ops4j.pax.cm.agent.configuration.PaxConfiguration;
import org.ops4j.pax.cm.agent.wicket.configuration.ConfigurationPropertiesEditorContent;
import org.ops4j.pax.cm.agent.wicket.configuration.edit.properties.DefaultConfigurationPropertiesEditor;
import org.ops4j.pax.wicket.service.Content;
import org.ops4j.pax.wicket.service.DefaultPageContainer;
import org.osgi.framework.BundleContext;
import wicket.Component;

public final class EditConfigurationPageContainer extends DefaultPageContainer
{
    private static final Logger m_logger = Logger.getLogger( EditConfigurationPageContainer.class );

    public EditConfigurationPageContainer( BundleContext bundleContext, String containmentId, String applicationName )
    {
        super( bundleContext, containmentId, applicationName );
    }

    Component createConfigurationPropertiesEditor( PaxConfiguration configuration )
        throws IllegalArgumentException
    {
        NullArgumentException.validateNotNull( configuration, "configuration" );
        Map<String, List<Content>> children = getChildren();

        String keyConfigProps = WicketApplicationConstant.Configuration.Edit.DESTINATION_ID_CONFIGURATION_PROPERTIES;
        List<Content> configurationProperties = children.get( keyConfigProps );

        Component configurationPropertiesEditor = null;
        if( configurationProperties != null )
        {
            for( Content cnt : configurationProperties )
            {
                if( !( cnt instanceof ConfigurationPropertiesEditorContent ) )
                {
                    m_logger.warn(
                        "Content provider [" + cnt + "] is not an instance of [ConfigurationPropertiesEditorContent]."
                    );
                    continue;
                }

                ConfigurationPropertiesEditorContent content = (ConfigurationPropertiesEditorContent) cnt;
                if( content.isAbleToHandleConfiguration( configuration ) )
                {
                    configurationPropertiesEditor = content.createComponent( configuration );
                }
            }
        }

        if( configurationPropertiesEditor == null )
        {
            configurationPropertiesEditor = new DefaultConfigurationPropertiesEditor(
                WicketApplicationConstant.Configuration.Edit.COMPONENT_CONFIGURATION_PROPERTIES, configuration
            );
        }

        return configurationPropertiesEditor;
    }
}
