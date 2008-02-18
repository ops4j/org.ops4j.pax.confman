/*
 * Copyright 2008 Alin Dreghiciu.
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
package org.ops4j.pax.cm.scanner.directory.internal;

import java.io.File;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.cm.api.ConfigurationManager;
import org.ops4j.pax.cm.api.ServiceConstants;
import org.ops4j.pax.cm.commons.internal.processor.CommandProcessor;
import org.ops4j.pax.cm.domain.ConfigurationSource;
import org.ops4j.pax.cm.domain.Identity;
import org.ops4j.pax.cm.domain.PropertiesSource;
import org.ops4j.pax.cm.scanner.commons.internal.DeleteCommand;
import org.ops4j.pax.cm.scanner.commons.internal.UpdateCommand;
import org.ops4j.pax.swissbox.lifecycle.AbstractLifecycle;

/**
 * Scans directories for properties files.
 * It lists the content of the scanned directory and each found properties file (files with .properties extension) will
 * be considered as a configuration. The file name without extension will be used as pid.
 * If the scanned directory contains directories then each directory will be considerd as containing factory
 * configurations where directory name is the factory pid and each property file contained in that directory is
 * considered a factory configuration with the pid equal ith file name without extension.
 *
 * The scanned directory can point to files that are not directories or do not exist or cannot be read. This fact is
 * verified at each scan. If that's the case it will just not create any configuration. This way of working should be
 * supported as the status of teh scaned directory can change over time (if can be created, removed, permission changed
 * ,...)
 *
 * TODO add support for file name filters (e.g. *.properties)
 * TODO (nice to have) configured directory can contain a properties file that can contain configuration info as interval, fila name filters that override the configured ones. So a config file for he scanner itself.
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 12, 2008
 */
class DirectoryScanner
    extends AbstractLifecycle
{

    /**
     * Logger.
     */
    private static final Log LOG = LogFactory.getLog( DirectoryScanner.class );
    /**
     * Known mime types (very limited).
     */
    private static final FileNameMap MIME_TYPES = URLConnection.getFileNameMap();

    /**
     * Commands processor.
     */
    private final CommandProcessor<ConfigurationManager> m_processor;
    /**
     * Scanned file system directory. Cannot be null.
     */
    private final File m_directory;
    /**
     * Map between absolute file name and information about processed file.
     */
    private final Map<String, ProcessedFile> m_processed;
    /**
     * Interval of time in milliseconds between scanning the target directory.
     */
    private final Long m_interval;
    /**
     * Scanning thread. Null if not active.
     */
    private Thread m_scanningThread;
    /**
     * Signal sent to scanning thread in order to stop it.
     */
    private boolean m_stopSignal;

    /**
     * Constructor.
     *
     * @param processor commands processor
     * @param directory file system directory to be scanned
     * @param interval  interval of time in milliseconds between scanning the target directory.
     *                  If null a default 2000 milliseconds will be used.
     *
     * @throws NullArgumentException - If commands processor is null
     *                               - If directory is null
     */
    DirectoryScanner( final CommandProcessor<ConfigurationManager> processor,
                      final File directory,
                      final Long interval )
    {
        NullArgumentException.validateNotNull( processor, "Commands processor" );
        NullArgumentException.validateNotNull( directory, "Scanned directory" );

        m_processor = processor;
        m_directory = directory;
        m_processed = new HashMap<String, ProcessedFile>();
        m_interval = interval == null ? 2000L : interval;
        m_stopSignal = false;
    }

    /**
     * Scans the specified directory. If the factory pid is null it will also scan subdirectories for factory
     * configurations.
     *
     * @param directory    directory to be scanned
     * @param factoryPid   factory pid. if null configurations files found into the directory will be considered managed
     *                     service configurations, otherwise will be considered Managed Service Factories.
     * @param scannedFiles set of absolute files names for files scanned on last run
     */
    private void scan( final File directory,
                       final String factoryPid,
                       final Set<String> scannedFiles )
    {
        //LOG.debug( "Scanning " + m_directory.getAbsoluteFile() );
        if( directory != null && directory.isDirectory() && directory.canRead() )
        {
            final File[] contents = directory.listFiles();
            for( File file : contents )
            {
                if( file.isDirectory() )
                {
                    if( factoryPid == null )
                    {
                        scan( file, file.getName(), scannedFiles );
                    }
                }
                else if( file.canRead() )
                {
                    String fileName = file.getName();
                    // find out the id by removing extension
                    String id = fileName;
                    if( id.contains( "." ) )
                    {
                        id = id.substring( 0, id.lastIndexOf( "." ) );
                    }
                    if( id.trim().length() == 0 )
                    {
                        // we may have files that start with .
                        continue;
                    }
                    // check if did not already configured this file and the file was not modified
                    scannedFiles.add( file.getAbsolutePath() );
                    ProcessedFile processed = m_processed.get( file.getAbsolutePath() );
                    if( processed != null && processed.lastModified == file.lastModified() )
                    {
                        continue;
                    }
                    String mimeType = MIME_TYPES.getContentTypeFor( fileName );
                    if( mimeType == null && fileName.contains( "." ) )
                    {
                        mimeType = "extension/" + fileName.substring( fileName.lastIndexOf( "." ) );
                    }
                    if( mimeType != null )
                    {
                        // create configuration metadata
                        final Dictionary<String, String> metadata = new Hashtable<String, String>();
                        metadata.put( ServiceConstants.INFO_AGENT, "org.ops4j.pax.cm.scanner.directory" );
                        metadata.put( ServiceConstants.MIME_TYPE, mimeType );
                        // create configuration source
                        final Identity identity;
                        if( factoryPid == null )
                        {
                            identity = new Identity( id, null );
                        }
                        else
                        {
                            identity = new Identity( factoryPid, id, null );
                        }
                        // and add it to be process
                        m_processor.add(
                            new UpdateCommand(
                                new ConfigurationSource( identity, new PropertiesSource( file, metadata ) )
                            )
                        );
                        m_processed.put( file.getAbsolutePath(), new ProcessedFile( identity, file.lastModified() ) );
                    }
                }
            }
        }
    }

    /**
     * Removes files that are not in the set of files to retain.
     *
     * @param retainFiles set of absolute file names of the files to be retained
     */
    private void removeDiff( final Set<String> retainFiles )
    {
        final Set<String> removable = new HashSet<String>( m_processed.keySet() );
        removable.removeAll( retainFiles );
        for( String fileName : removable )
        {
            m_processor.add( new DeleteCommand( m_processed.get( fileName ).m_identity ) );
            m_processed.remove( fileName );
        }
    }

    /**
     * Starts directory scanning.
     */
    protected synchronized void onStart()
    {
        if( m_scanningThread == null )
        {
            LOG.debug( "Starting " + this );
            m_scanningThread = new Thread(
                new Scanner(), "Pax ConfMan - DirectoryScanner - " + m_directory.getAbsolutePath()
            );
            m_scanningThread.start();
        }
    }

    /**
     * Stops directory scanning.
     */
    protected synchronized void onStop()
    {
        if( m_scanningThread != null )
        {
            m_stopSignal = true;
            m_scanningThread.interrupt();
            m_scanningThread = null;
            LOG.debug( "Stopped " + this );
        }
    }

    /**
     * Getter.
     *
     * @return scanned directory
     */
    public File getDirectory()
    {
        return m_directory;
    }

    /**
     * Scanning thread.
     */
    private class Scanner
        implements Runnable
    {

        public void run()
        {
            while( !m_stopSignal )
            {
                try
                {
                    final Set<String> scannedFiles = new HashSet<String>();
                    scan( m_directory, null, scannedFiles );
                    removeDiff( scannedFiles );
                    Thread.sleep( m_interval );
                }
                catch( InterruptedException ignore )
                {
                    // ignore
                }
                catch( Throwable ignore )
                {
                    // catch everything as we should not die if something goes wrong during scanning
                    LOG.error( "Exception while scanning " + m_directory.getAbsolutePath(), ignore );
                }
            }
            m_stopSignal = false;
        }

    }

    @Override
    public String toString()
    {
        return new StringBuilder()
            .append( this.getClass().getSimpleName() )
            .append( "{" )
            .append( m_directory.getAbsolutePath() )
            .append( "}" )
            .toString();
    }

    /**
     * Information about processed file.
     */
    private static class ProcessedFile
    {

        final Identity m_identity;
        final Long lastModified;

        ProcessedFile( final Identity identity, final Long lastModified )
        {
            this.m_identity = identity;
            this.lastModified = lastModified;
        }

    }

}