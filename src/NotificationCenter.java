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

import java.util.Map;

import javax.annotation.Nullable;

import lombok.NonNull;

// }}}

/**
 * An NotificationCenter is a general notification dispatch table.
 * <p>
 * Typically, you use the singleton {@link DefaultNotificationCenter#instance()}
 * to post notifications within your program.
 *
 */
public interface NotificationCenter
{

    /**
     * Adds the given observer to the dispatch table, with optional criteria:
     * notification name and sender.
     * <p>
     * The observer is retained by the dispatch table, so in order to remove the
     * strong reference and let the observer (and the objects referenced there)
     * be released, {@link #removeObserver(Observer)} or
     * {@link #removeObserver(Observer, String, Object)} must be called.
     *
     * @param observer
     *            The observer receiving the notifications
     * @param notificationName
     *            The tag of the notification which should be delivered to the
     *            observer
     * @param notificationSender
     *            The object which should be set in the notification in order to
     *            receive the
     *            notification. The specified object is not retained.
     */
    public abstract void addObserver(@NonNull final Observer observer, @Nullable final String notificationName,
                                     @Nullable final Object notificationSender);

    /**
     * Removes all the entries containing the given observer from the dispatch
     * table.
     *
     * @param observer
     *            The observer to remove
     */
    public abstract void removeObserver(@NonNull final Observer observer);

    /**
     * Removes the entries containing the given observer from the dispatch table
     * if the specified criteria match.
     *
     * @param observer
     *            Observer to remove
     * @param notificationName
     *            If not <code>null</code>, only entries that specify this
     *            notification name are removed
     * @param notificationSender
     *            If not <code>null</code>, only entries that specify this
     *            notification object are removed
     */
    public abstract void removeObserver(@NonNull final Observer observer, @Nullable final String notificationName,
                                        @Nullable final Object notificationSender);

    /**
     * Sends a given notification to the receiver.
     *
     * @param notification
     *            The notification to post. It's userInfo must not be changed
     *            after posting.
     *
     */
    public abstract void postNotification(@NonNull final Notification notification);

    /**
     * Creates a new notification and sends it to the receiver.
     *
     * @param notificationName
     *            The name of the notification.
     * @param notificationSender
     *            The object posting the notification.
     */
    public abstract void postNotification(@NonNull final String notificationName,
                                          @Nullable final Object notificationSender);

    /**
     * Creates a new notification and sends it to the receiver.
     *
     * @param notificationName
     *            The name of the notification.
     * @param notificationSender
     *            The object posting the notification.
     * @param userInfo
     *            Information about the the notification. May be null. Must not
     *            be changed after posting.
     */
    public abstract void postNotification(@NonNull final String notificationName,
                                          @Nullable final Object notificationSender,
                                          @Nullable final Map<String, Object> userInfo);

}
