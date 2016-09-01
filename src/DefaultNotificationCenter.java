// {{{ copyright
/**
 * The MIT License (MIT)
 * Copyright (c) 2016 Pascal Bihler
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */
// }}}

// {{{ imports

import java.lang.ref.WeakReference;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.annotation.Nullable;

import lombok.Getter;
import lombok.NonNull;

// }}}

/**
 * A concrete implementation of the {@link WeakNotificationCenter}.
 *
 */
public class DefaultNotificationCenter
        implements WeakNotificationCenter
{
    // {{{ constants

    // Reserved key, since concurrent Hash table does not allow null as key
    private static final String ANY_NOTIFICATION_NAME = "###RESERVERD_KEY_FOR_ANY_NAME###".intern();

    // }}}
    // {{{ variables

    protected final static DefaultNotificationCenter instance = new DefaultNotificationCenter();

    final ConcurrentHashMap<String, Queue<ObserverReference>> observers = new ConcurrentHashMap<String, Queue<ObserverReference>>();

    // }}}

    /* **************************************************************************
     * Static Methods
     * **************************************************************************
     */

    // {{{ Singleton access method

    @NonNull
    public static DefaultNotificationCenter instance()
    {
        return instance;
    }

    // }}}
    // {{{ createUserInfo

    @NonNull
    /**
     * Creates a key-value map suitable for use with <i>userInfo</i> of a
     * {@link Notification} from the given
     * arguments.
     *
     * @return A map build out of the given arguments, or {@code null} when
     *         there were no arguments given.
     */
    public static Map<String, Object> createUserInfo(final Object... userInfo)
    {
        final Map<String, Object> map = new HashMap<String, Object>();
        for (int i = 0; i < userInfo.length; i += 2) {
            final String key = "" + userInfo[i];
            final Object value = i + 1 < userInfo.length ? userInfo[i + 1] : null;
            if (map.containsKey(key)) throw new IllegalArgumentException("Duplicate key " + key + " in userInfo");
            map.put(key, value);
        }
        return map;
    }

    // }}}

    /* **************************************************************************
     * Add Observer
     * **************************************************************************
     */

    // {{{ addObserver(Observer,String,Object)

    @Override
    public void addObserver(@NonNull final Observer observer, @Nullable final String notificationName,
                            @Nullable final Object notificationSender)
    {
        addObserverReference(new StrongObserverReference(observer, notificationSender), notificationName);
    }

    // }}}
    // {{{ addObserver(Observer,String)

    /**
     * see {@link #addObserver(Observer, String, Object)}
     */
    public void addObserver(@NonNull final Observer observer, @Nullable final String notificationName)
    {
        addObserver(observer, notificationName, null);
    }

    // }}}
    // {{{ addObserver(Observer)

    /**
     * see {@link #addObserver(Observer, String, Object)}
     */
    public void addObserver(@NonNull final Observer observer)
    {
        addObserver(observer, null);
    }

    // }}}
    // {{{ addWeakObserver(Observer,String,Object)

    @Override
    public void addWeakObserver(@NonNull final Observer observer, @Nullable final String notificationName,
                                @Nullable final Object notificationSender)
    {
        addObserverReference(new WeakObserverReference(observer, notificationSender), notificationName);
    }

    // }}}
    // {{{ addWeakObserver(Observer,String)

    /**
     * see {@link #addWeakObserver(Observer, String, Object)}
     */
    public void addWeakObserver(@NonNull final Observer observer, @Nullable final String notificationName)
    {
        addWeakObserver(observer, notificationName, null);
    }

    // }}}
    // {{{ addWeakObserver(Observer)

    /**
     * see {@link #addWeakObserver(Observer, String, Object)}
     */
    public void addWeakObserver(@NonNull final Observer observer)
    {
        addWeakObserver(observer, null);
    }

    // }}}
    // {{{ addObserverReference

    private void addObserverReference(@NonNull final ObserverReference observerReference,
                                      @Nullable String notificationName)
    {
        if (notificationName == null) {
            notificationName = ANY_NOTIFICATION_NAME;
        }
        assert notificationName != null;

        Queue<ObserverReference> observerSet = this.observers.get(notificationName);
        if (observerSet == null) {
            final Queue<ObserverReference> newObserverSet = new ConcurrentLinkedQueue<ObserverReference>();
            observerSet = this.observers.putIfAbsent(notificationName, newObserverSet);
            if (observerSet == null) {
                observerSet = newObserverSet;
            }
        }
        assert observerSet != null;

        observerSet.add(observerReference);
    }

    // }}}

    /* **************************************************************************
     * Remove Observer
     * **************************************************************************
     */

    // {{{ removeObserver(Observer,String,Object)

    @Override
    public void removeObserver(@NonNull final Observer observer, @Nullable final String notificationName,
                               @Nullable final Object notificationSender)
    {
        if (notificationName != null) {
            removeObserverWithName(observer, notificationName, notificationSender);
        } else {
            // iterate through all names...
            for (final String name : this.observers.keySet()) {
                removeObserverWithName(observer, name, notificationSender);
            }
        }
    }

    // }}}
    // {{{ removeObserver(Observer,String)

    /**
     * see {@link #removeObserver(Observer, String, Object)}
     */
    public void removeObserver(@NonNull final Observer observer, @Nullable final String notificationName)
    {
        removeObserver(observer, notificationName, null);
    }

    // }}}
    // {{{ removeObserver(Observer)

    @Override
    public void removeObserver(@NonNull final Observer observer)
    {
        removeObserver(observer, null);
    }

    // }}}
    // {{{ removeObserverWithName

    private void removeObserverWithName(@NonNull final Observer observer, @NonNull final String notificationName,
                                        @Nullable final Object notificationSender)
    {

        final Queue<ObserverReference> queue = this.observers.get(notificationName);

        if (queue != null) {
            removeObserverFromQueue(queue, observer, notificationSender);

            if (queue.isEmpty()) { // entries might be gone...
                this.observers.remove(notificationName, queue); // cleanup
            }
        }
    }

    // }}}
    // {{{ removeObserverFromQueue

    private void removeObserverFromQueue(@NonNull final Queue<ObserverReference> queue,
                                         @NonNull final Observer targetObserver,
                                         @Nullable final Object notificationSender)
    {
        final Iterator<ObserverReference> iterator = queue.iterator();
        try {
            ObserverReference reference;
            while ((reference = iterator.next()) != null) {
                final Observer observer = reference.get();

                if (observer == null) {
                    iterator.remove(); // reference is dead -> cleanup
                    continue;
                }

                if (observer != targetObserver) {
                    continue; // we don't look for this observer
                }

                if (notificationSender != null) {
                    final WeakReference<Object> senderReference = reference.getSenderReference();
                    if (senderReference == null) {
                        continue;
                    } else {
                        final Object expectedSender = senderReference.get();
                        if (expectedSender != null && notificationSender != expectedSender) {
                            // this is not the expected sender...
                            continue;
                        }
                    }
                }

                // its the one to remove...

                iterator.remove();
            }
        } catch (final NoSuchElementException ex) {
            // end of iterator
        }

    }

    // }}}

    /* **************************************************************************
     * Post Notification
     * **************************************************************************
     */

    // {{{ postNotification(Notification)

    @Override
    public void postNotification(@NonNull final Notification notification)
    {
        final String notificationName = notification.getName();

        dispatchNotifications(notificationName, notification);
        dispatchNotifications(ANY_NOTIFICATION_NAME, notification);

    }

    // }}}
    // {{{ postNotification(String,Object,Map)

    @Override
    public void postNotification(@NonNull final String notificationName, @Nullable final Object notificationSender,
                                 @Nullable final Map<String, Object> userInfo)
    {
        final DefaultNotification notification = DefaultNotification.builder().name(notificationName)
                .object(notificationSender).userInfo(userInfo).build();
        postNotification(notification);
    }

    // }}}
    // {{{ postNotification(String,Object,Object...)

    /**
     * Creates a new notification and sends it to the receiver.
     *
     * @param notificationName
     *            The name of the notification.
     * @param notificationSender
     *            The object posting the notification.
     * @param userInfo
     *            Information about the the notification. May be null.
     */
    public void postNotification(@NonNull final String notificationName, @Nullable final Object notificationSender,
                                 final Object... userInfo)
    {
        postNotification(notificationName, notificationSender, createUserInfo(userInfo));
    }

    // }}}
    // {{{ postNotification(String,Object)

    @Override
    public void postNotification(@NonNull final String notificationName, @Nullable final Object notificationSender)
    {
        postNotification(notificationName, notificationSender, (Map<String, Object>) null);
    }

    // }}}
    // {{{ postNotification(String)

    /**
     * see {@link #postNotification(String, Object)}
     */
    public void postNotification(@NonNull final String notificationName)
    {
        postNotification(notificationName, null);
    }

    // }}}
    // {{{ dispatchNotifications(String,Notification)

    private void dispatchNotifications(@NonNull final String notificationName, @NonNull final Notification notification)
    {
        final Queue<ObserverReference> queue = this.observers.get(notificationName);

        if (queue != null) {
            dispatchNotifications(queue, notification);

            if (queue.isEmpty()) { // entries might be gone...
                this.observers.remove(notificationName, queue); // cleanup
            }
        }
    }

    // }}}
    // {{{ dispatchNotifications(Queue,Notifications)

    private void dispatchNotifications(@NonNull final Queue<ObserverReference> queue,
                                       @NonNull final Notification notification)
    {
        final Iterator<ObserverReference> iterator = queue.iterator();
        try {
            ObserverReference reference;
            while ((reference = iterator.next()) != null) {
                final Observer observer = reference.get();

                if (observer == null) {
                    iterator.remove(); // reference is dead -> cleanup
                    continue;
                }

                final Object notificationSender = notification.getObject();
                if (notificationSender != null) {
                    final WeakReference<Object> senderReference = reference.getSenderReference();
                    if (senderReference != null) {
                        final Object expectedSender = senderReference.get();
                        if (expectedSender == null) {
                            iterator.remove(); // reference is dead -> cleanup
                            continue;
                        }
                        if (notificationSender != expectedSender) {
                            continue;
                        }
                    }
                }

                observer.receivedNotification(notification);
            }
        } catch (final NoSuchElementException ex) {
            // end of iterator
        }
    }

    // }}}

    /* **************************************************************************
     * Internal Helper Classes
     * **************************************************************************
     */

    // {{{ ObserverReference

    static interface ObserverReference
    {
        @Nullable
        Observer get();

        @Nullable
        WeakReference<Object> getSenderReference();
    }

    // }}}
    // {{{ WeakObserverReference

    private static class WeakObserverReference
            extends WeakReference<Observer>
            implements ObserverReference
    {
        @Getter @Nullable final WeakReference<Object> senderReference;

        public WeakObserverReference(@NonNull final Observer observer, @Nullable final Object sender)
        {
            super(observer);
            this.senderReference = sender == null ? null : new WeakReference<Object>(sender);
        }
    }

    // }}}
    // {{{ StrongObserverReference

    private static class StrongObserverReference
            implements ObserverReference
    {
        @NonNull final Observer observer;
        @Getter @Nullable final WeakReference<Object> senderReference;

        @Override
        public Observer get()
        {
            return observer;
        }

        public StrongObserverReference(@NonNull final Observer observer, @Nullable final Object sender)
        {
            this.observer = observer;
            this.senderReference = sender == null ? null : new WeakReference<Object>(sender);
        }
    }

    // }}}

}
