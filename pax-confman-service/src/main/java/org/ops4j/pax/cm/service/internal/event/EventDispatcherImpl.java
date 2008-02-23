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
package org.ops4j.pax.cm.service.internal.event;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.cm.service.internal.event.Event;
import org.ops4j.pax.cm.service.internal.di.RequiresConfigurationProducer;
import org.ops4j.pax.cm.service.internal.di.RequiresEventDispatcher;

/**
 * TODO Add JavaDoc
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 22, 2008
 */
public class EventDispatcherImpl
    implements EventDispatcher
{

    /**
     * Logger.
     */
    private static final Log LOG = LogFactory.getLog( EventDispatcherImpl.class );

    /**
     * Event handlers repository.
     */
    private final EventHandlerRepository m_eventHandlerRepository;
    /**
     * Events queue.
     */
    private final BlockingQueue<Event> m_events;
    /**
     * Dispatcher thread.
     */
    private Thread m_dispatcherThread;
    /**
     * True as long as dispatcher thread is active.
     */
    private boolean m_dispatcherThreadActive;

    /**
     * Constructor.
     *
     * @param eventHandlerRepository event handler repository
     *
     * @throws NullArgumentException - If event handlr repository is null
     */
    public EventDispatcherImpl( final EventHandlerRepository eventHandlerRepository )
    {
        NullArgumentException.validateNotNull( eventHandlerRepository, "Event handler repository" );

        m_eventHandlerRepository = eventHandlerRepository;
        m_events = new LinkedBlockingQueue<Event>();
    }

    /**
     * @see EventDispatcher#fireEvent(org.ops4j.pax.cm.service.internal.event.Event)
     */
    public void fireEvent( final Event event )
    {
        m_events.offer( event );
    }

    /**
     * Starts events dispatching if not already sarted.
     */
    public synchronized void start()
    {
        if( !m_dispatcherThreadActive )
        {
            m_dispatcherThreadActive = true;
            m_dispatcherThread = new Thread( new Dispatcher(), "Pax ConfMan Event Dispatcher" );
            m_dispatcherThread.start();
        }
    }

    /**
     * Stops events dispatching if not already stopped.
     */
    public synchronized void stop()
    {
        if( m_dispatcherThreadActive )
        {
            m_dispatcherThreadActive = false;
            m_dispatcherThread.interrupt();
            m_dispatcherThread = null;
        }
    }

    /**
     * Sets the required fields from the context. Kind of rudimentary dependency injection.
     *
     * @param eventHandler event handler
     *
     * @return same event handler as received
     */
    private EventHandler<? extends Event> prepareHandler( final EventHandler<? extends Event> eventHandler )
    {
        if( eventHandler instanceof RequiresConfigurationProducer )
        {
            // TODO set depenency
            throw new IllegalStateException( "Cannot set required dependency" );
        }
        if( eventHandler instanceof RequiresEventDispatcher )
        {
            // TODO set depenency
            throw new IllegalStateException( "Cannot set required dependency" );
        }
        return eventHandler;
    }

    /**
     * Dispacher thread.
     */
    private class Dispatcher
        implements Runnable
    {

        public void run()
        {
            while( !m_dispatcherThreadActive )
            {
                try
                {
                    final Event event = m_events.take();
                    try
                    {
                        LOG.trace( "Handling event " + event );
                        prepareHandler( m_eventHandlerRepository.getEventHandlerForEvent( event ) ).handle( event );
                    }
                    catch( Exception ignore )
                    {
                        // don't let dispatcher thread die
                        LOG.error( ignore.getMessage() );
                    }
                }
                catch( InterruptedException ignore )
                {
                    // ignore
                }
            }
        }
    }

}