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

import java.util.Collections;
import java.util.Map;

import javax.annotation.Nullable;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

// }}}

/**
 *
 * A default implementation for {@link Notification}s, assuring that the given
 * userInfo cannot be
 * modified by the receivers of the notification.
 *
 */
@Builder
@ToString
public class DefaultNotification
        implements Notification
{

    // {{{ variables

    @Getter @NonNull final String name;
    @Getter @Nullable final Object object;
    @Getter @NonNull final Map<String, Object> userInfo;

    // }}}
    // {{{ constructor

    public DefaultNotification(@NonNull final String name, @Nullable final Object object,
                               @Nullable final Map<String, Object> userInfo)
    {
        this.name = name;
        this.object = object;
        if (userInfo == null) {
            this.userInfo = Collections.emptyMap();
        } else {
            this.userInfo = Collections.unmodifiableMap(userInfo);
        }
    }

    // }}}

}