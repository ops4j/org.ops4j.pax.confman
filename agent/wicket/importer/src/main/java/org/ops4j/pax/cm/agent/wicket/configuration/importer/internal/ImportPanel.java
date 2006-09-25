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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;
import org.ops4j.pax.cm.agent.configuration.PaxConfiguration;
import org.ops4j.pax.cm.agent.configuration.PaxConfigurationFacade;
import wicket.extensions.ajax.markup.html.form.upload.UploadProgressBar;
import wicket.markup.html.basic.Label;
import wicket.markup.html.form.Button;
import wicket.markup.html.form.DropDownChoice;
import wicket.markup.html.form.Form;
import wicket.markup.html.form.upload.FileUpload;
import wicket.markup.html.form.upload.FileUploadField;
import wicket.markup.html.panel.Panel;
import wicket.model.Model;
import wicket.util.lang.Bytes;

/**
 * {@code ImportPanel}
 *
 * @author Edward Yakop
 * @since 0.1.0
 */
final class ImportPanel extends Panel
{
    public static final Bytes MAX_CONFIGURATION_UPLOAD_FILE_SIZE = Bytes.kilobytes( 100 );
    public static final String WICKET_ID_IMPORT_FORM = "importForm";

    private static final Logger m_logger = Logger.getLogger( ImportPanel.class );

    /**
     * Construct an instance of {@code ImportPanel} with the specified {@code wicketId}.
     *
     * @param wicketId The wicket id of this panel. This argument must not be {@code null}.
     *
     * @since 0.1.0
     */
    ImportPanel( String wicketId )
    {
        super( wicketId );

        ImportForm importForm = new ImportForm( WICKET_ID_IMPORT_FORM );
        add( importForm );
    }

    private class ImportForm extends Form
    {
        private static final String WICKET_ID_LABEL_IMPORTER_IDS = "labelImporterIds";
        private static final String WICKET_ID_IMPORTER_IDS = "importerIds";
        private static final String WICKET_ID_IMPORT = "import";
        private static final String WICKET_ID_FILE_INPUT_FIELD = "fileInput";
        private static final String WICKET_ID_PROGRESS_BAR = "progress";
        private static final String WICKET_ID_UPLOAD_BUTTON = "upload";

        private Button m_importButton;
        private byte[] m_importerFileContent;
        private Model m_selectedImporterId;

        private FileUploadField m_fileUploadField;

        /**
         * Constructs a form with no validation.
         *
         * @param id See Component
         */
        public ImportForm( String id )
        {
            super( id );

            setMultiPart( true );

            Label label = new Label( WICKET_ID_LABEL_IMPORTER_IDS, "Import Id:" );
            add( label );

            Set<String> importerIds = ImporterTracker.getImporterIds();
            ArrayList<String> choices = new ArrayList<String>( importerIds );
            DropDownChoice dropDownChoice = new DropDownChoice( WICKET_ID_IMPORTER_IDS, choices );
            m_selectedImporterId = new Model();
            dropDownChoice.setModel( m_selectedImporterId );
            add( dropDownChoice );

            m_importButton = newImportButton();
            add( m_importButton );

            // Add one file input field
            m_fileUploadField = new FileUploadField( WICKET_ID_FILE_INPUT_FIELD );
            add( m_fileUploadField );

            UploadProgressBar uploadProgressBar = new UploadProgressBar( WICKET_ID_PROGRESS_BAR, this );
            add( uploadProgressBar );

            setMaxSize( MAX_CONFIGURATION_UPLOAD_FILE_SIZE );

            Button uploadButton = newUploadButton();
            add( uploadButton );
        }

        private Button newImportButton()
        {
            Button importButton = new Button( WICKET_ID_IMPORT )
            {
                protected void onSubmit()
                {
                    ByteArrayInputStream inputStream = new ByteArrayInputStream( m_importerFileContent );
                    String selectedImportId = (String) m_selectedImporterId.getObject( null );

                    List<PaxConfiguration> paxConfigurations =
                        ImporterTracker.performImport( selectedImportId, inputStream );

                    if( m_logger.isDebugEnabled() )
                    {
                        m_logger.debug( "Number of configuration [" + paxConfigurations.size() + "]" );
                    }

                    for( PaxConfiguration paxConfiguration : paxConfigurations )
                    {
                        if( m_logger.isDebugEnabled() )
                        {
                            m_logger.debug( "Update configuration [" + paxConfiguration.getPid() + "]." );
                        }

                        try
                        {
                            PaxConfigurationFacade.updateConfiguration( paxConfiguration );
                        } catch( IOException e )
                        {
                            e.printStackTrace();  //TODO: Auto-generated, need attention.
                        }
                    }
                }
            };
            importButton.setEnabled( false );
            return importButton;
        }

        private Button newUploadButton()
        {
            return new Button( WICKET_ID_UPLOAD_BUTTON )
            {
                protected void onSubmit()
                {
                    FileUpload upload = m_fileUploadField.getFileUpload();
                    if( upload != null )
                    {
                        m_importerFileContent = upload.getBytes();
                        m_importButton.setEnabled( true );
                    }
                }
            };
        }
    }
}
