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
package org.ops4j.pax.cm.agent.wicket.configuration.edit.properties;

import org.ops4j.lang.NullArgumentException;
import wicket.Localizer;
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
class EditConfigurationItemPanel extends Panel
{
    private static final String WICKET_ID_FORM = "form";

    private final ConfigurationItemDataProvider m_dataProvider;
    private final EditConfigurationItemForm m_form;

    private ConfigurationItem m_configurationItem;

    EditConfigurationItemPanel( String id, ConfigurationItemDataProvider dataProvider )
    {
        super( id );

        NullArgumentException.validateNotNull( dataProvider, "dataProvider" );

        m_configurationItem = new ConfigurationItem();

        m_dataProvider = dataProvider;
        m_dataProvider.setSelectionListener( this );

        m_form = new EditConfigurationItemForm( WICKET_ID_FORM );
        add( m_form );
    }

    public void setConfigurationItem( ConfigurationItem item )
    {
        if( item == null )
        {
            m_configurationItem = new ConfigurationItem();
        }
        else
        {
            m_configurationItem = item;
        }

        m_form.modelChanging();

        m_form.setModelObject( m_configurationItem );
        m_form.setFormMode( item );

        m_form.modelChanged();
    }

    private final class EditConfigurationItemForm extends Form
    {
        private static final String LOCALE_KEY_LABEL = "keyLabel";
        private static final String LOCALE_VALUE_LABEL = "valueLabel";

        private static final String WICKET_ID_KEY_LABEL = "keyLabel";
        private static final String WICKET_ID_KEY = "key";
        private static final String WICKET_ID_VALUE_LABEL = "valueLabel";
        private static final String WICKET_ID_VALUE = "value";

        private static final String WICKET_ID_NEW_BUTTON = "new";
        private static final String WICKET_ID_SAVE_BUTTON = "save";
        private static final String WICKET_ID_DELETE_BUTTON = "delete";

        private final TextField m_keyTextField;
        private final TextField m_valueTextField;
        private final Button m_deleteButton;
        private final Button m_saveButton;

        public EditConfigurationItemForm( String id )
        {
            super( id );

            CompoundPropertyModel cpm = new CompoundPropertyModel( m_configurationItem );
            setModel( cpm );

            EditConfigurationItemPanel editConfigurationItemPanel = EditConfigurationItemPanel.this;
            Localizer localizer = editConfigurationItemPanel.getLocalizer();

            String keyLabelString = localizer.getString( LOCALE_KEY_LABEL, editConfigurationItemPanel, "Key: " );
            Label keyLabel = new Label( WICKET_ID_KEY_LABEL, keyLabelString );
            add( keyLabel );

            m_keyTextField = new TextField( WICKET_ID_KEY );
            m_keyTextField.setEnabled( false );
            add( m_keyTextField );

            String valueLabelString = localizer.getString( LOCALE_VALUE_LABEL, editConfigurationItemPanel, "Value: " );
            Label valueLabel = new Label( WICKET_ID_VALUE_LABEL, valueLabelString );
            add( valueLabel );

            m_valueTextField = new TextField( WICKET_ID_VALUE );
            m_valueTextField.setEnabled( false );
            add( m_valueTextField );

            Button newButton = newNewButton( WICKET_ID_NEW_BUTTON );
            add( newButton );

            m_saveButton = newSaveButton( WICKET_ID_SAVE_BUTTON );
            m_saveButton.setEnabled( false );
            add( m_saveButton );

            m_deleteButton = newDeleteButton( WICKET_ID_DELETE_BUTTON );
            m_deleteButton.setEnabled( false );
            add( m_deleteButton );
        }

        private Button newSaveButton( String wicketIdSaveButton )
        {
            return new Button( wicketIdSaveButton )
            {
                protected void onSubmit()
                {
                    m_dataProvider.saveConfigurationItem( m_configurationItem );
                }
            };
        }

        private Button newDeleteButton( String id )
        {
            Button deleteButton = new Button( id )
            {
                protected void onSubmit()
                {
                    m_dataProvider.deleteSelectedConfigurationItem();
                }
            };
            deleteButton.setDefaultFormProcessing( false );
            return deleteButton;
        }

        private Button newNewButton( String id )
        {
            Button newButton = new Button( id )
            {
                protected void onSubmit()
                {
                    m_dataProvider.newConfigurationItem();
                }
            };
            newButton.setDefaultFormProcessing( false );

            return newButton;
        }

        private void setFormMode( ConfigurationItem item )
        {
            if( item == null )
            {
                setFormToDisableMode();
            }
            else if( item.isNew() )
            {
                setFormToNewMode();
            }
            else
            {
                setFormToEditMode();
            }
        }

        private void setFormToNewMode()
        {
            m_keyTextField.setEnabled( true );
            m_valueTextField.setEnabled( true );
            m_saveButton.setEnabled( true );
            m_deleteButton.setEnabled( true );
        }

        private void setFormToEditMode()
        {
            m_keyTextField.setEnabled( false );
            m_valueTextField.setEnabled( true );
            m_saveButton.setEnabled( true );
            m_deleteButton.setEnabled( true );
        }

        private void setFormToDisableMode()
        {
            m_keyTextField.setEnabled( false );
            m_valueTextField.setEnabled( false );
            m_saveButton.setEnabled( false );
            m_deleteButton.setEnabled( false );
        }
    }
}
