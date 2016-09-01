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

import javax.annotation.Nullable;

import lombok.NonNull;

// }}}

/**
 * A {@link NotificationCenter} that allows to register observer, which are not
 * strongly retained.
 *
 */
public interface WeakNotificationCenter
        extends NotificationCenter
{
    /**
     * Adds the given observer to the dispatch table, as
     * {@link #addObserver(Observer, String, Object)}
     * <p>
     * The observer is not retained by the dispatch table, so as soon as there
     * are no other strong references to the observer object, the garbage
     * collect can remove the object, and the observer is removed from the
     * dispatch table.
     *
     */
    public abstract void addWeakObserver(@NonNull final Observer observer, @Nullable final String notificationName,
                                         @Nullable final Object notificationSender);

}
