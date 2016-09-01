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

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

// }}}

public class DefaultNotificationTest
{
    DefaultNotification notification;

    @Before
    public void setUp() throws Exception
    {
        final Map<String, Object> userInfo = DefaultNotificationCenter.createUserInfo("Foo", "Bar");

        notification = new DefaultNotification("Test", this, userInfo);
    }

    @Test
    public void testGetName()
    {
        assertEquals("Test", notification.getName());
    }

    @Test
    public void testGetObject()
    {
        assertEquals(this, notification.getObject());
    }

    @Test
    public void testGetUserInfo()
    {
        final Map<String, Object> userInfo = notification.getUserInfo();

        assertNotNull(userInfo);
        assertEquals(1,userInfo.size());
        assertEquals("Bar",userInfo.get("Foo"));
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testUserInfoUnmodifieable()
    {
        final Map<String, Object> userInfo = notification.getUserInfo();

        userInfo.put("Blubber", "Schnitzel");
    }

    @Test
    public void testUserInfoNotNull()
    {
        DefaultNotification notificationWithoutUserInfo = new DefaultNotification("Test", this, null);
        final Map<String, Object> userInfo = notificationWithoutUserInfo.getUserInfo();

        assertNotNull(userInfo);
        assertEquals(0,userInfo.size());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testNameNotNull()
    {
        new DefaultNotification(null, this, null);
    }

}
