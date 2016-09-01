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
 * An Notification objects stores information so that it can be sent to other
 * objects by an {@link NotificationCenter}.
 * The notification has a name, an object, and an optional key-value par map.
 * <p>
 * The <i>name</i> is a tag identifying the notification. <br>
 * The <i>object</i> of the notification typically is the object sending the
 * notification. <br>
 * The <i>userInfo</i> can store other related objects, if any, identified by
 * strings.
 *
 */
public interface Notification
{
    /**
     * @return The tag identifying the notification
     */
    @NonNull
    String getName();

    /**
     * @return The object of the notification, typically the object sending the
     *         notification
     */
    @Nullable
    Object getObject();

    /**
     * @return Key-Value pairs of other related objects, if any.
     */
    @NonNull
    Map<String, Object> getUserInfo();
}
