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
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

// }}}

public class DefaultNotificationCenterTest
{

    DefaultNotificationCenter center;

    @Mock Observer observer;
    @Captor ArgumentCaptor<Notification> captor;

    int callCount = 0;
    Observer countingObserver = new Observer(){

        @Override
        public void receivedNotification(Notification notification)
        {
            callCount++;
        }
    };

    @Before
    public void setUp() throws Exception
    {
        MockitoAnnotations.initMocks(this);

        center = DefaultNotificationCenter.instance();
        center.observers.clear(); // make sure no observers are registered
    }

    @Test
    public void testInstanceIsSingleton()
    {
        NotificationCenter newCenter = DefaultNotificationCenter.instance();

        assertSame(center, newCenter);
    }

    @Test
    public void testCreateUserInfo() throws Exception
    {
        final Map<String, Object> userInfo = DefaultNotificationCenter.createUserInfo("Key", "Value", 1, "int key",
                true, "boolean key", null, "null key", "xkey");

        assertEquals(5, userInfo.size());
        assertEquals("{xkey=null, 1=int key, Key=Value, true=boolean key, null=null key}", userInfo.toString());
    }

    @Test
    public void testCreateUserInfoNullArguments() throws Exception
    {
        final Map<String, Object> userInfo = DefaultNotificationCenter.createUserInfo();

        assertEquals(0, userInfo.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateUserInfoDuplicateKeys() throws Exception
    {
        final Map<String, Object> userInfo = DefaultNotificationCenter.createUserInfo("key", "value",
                "key", "other value");

        assertEquals(0, userInfo.size());
    }

    @Test
    public void testAddObserver() throws Exception
    {
        center.addObserver(observer);

        center.postNotification("Some event");
        center.postNotification("Some other event");
        center.postNotification("Some event",this);
        center.postNotification("Some other event",this);

        verify(observer,times(4)).receivedNotification(captor.capture());

        assertEquals("Some event", captor.getAllValues().get(0).getName());
        assertEquals("Some other event", captor.getAllValues().get(1).getName());
        assertEquals("Some event", captor.getAllValues().get(2).getName());
        assertEquals("Some other event", captor.getAllValues().get(3).getName());
    }

    @Test
    public void testAddObserverWithName() throws Exception
    {
        center.addObserver(observer,"Some event");

        center.postNotification("Some event");
        center.postNotification("Some other event");
        center.postNotification("Some event",this);
        center.postNotification("Some other event",this);

        verify(observer,times(2)).receivedNotification(captor.capture());

        assertEquals("Some event", captor.getAllValues().get(0).getName());
        assertEquals("Some event", captor.getAllValues().get(1).getName());
    }

    @Test
    public void testAddObserverWithSender() throws Exception
    {
        Object that = new Object();
        center.addObserver(observer,null,that);

        center.postNotification("Some event",this);
        center.postNotification("Some other event", this);
        center.postNotification("Some event",that);
        center.postNotification("Some other event",that);

        verify(observer,times(2)).receivedNotification(captor.capture());

        assertEquals("Some event", captor.getAllValues().get(0).getName());
        assertEquals(that, captor.getAllValues().get(0).getObject());
        assertEquals("Some other event", captor.getAllValues().get(1).getName());
        assertEquals(that, captor.getAllValues().get(1).getObject());
    }

    @Test
    public void testAddObserverWithNameAndSender() throws Exception
    {
        Object that = new Object();
        center.addObserver(observer,"Some event",that);

        center.postNotification("Some event",this);
        center.postNotification("Some other event", this);
        center.postNotification("Some event",that);
        center.postNotification("Some other event",that);

        verify(observer,times(1)).receivedNotification(captor.capture());

        assertEquals("Some event", captor.getAllValues().get(0).getName());
        assertEquals(that, captor.getAllValues().get(0).getObject());
    }

    @Test
    public void testAddObserverWithSenderDoesNotRetainSender() throws Exception
    {
        Object that = new Object();
        center.addObserver(observer,"Some event",that);

        // is the sender reference set?
        assertEquals(that,center.observers.elements().nextElement().peek().getSenderReference().get());

        that = null;
        System.gc();

        assertNull(center.observers.elements().nextElement().peek().getSenderReference().get());

        // trigger implicit observer check
        center.postNotification("Some event",this);

        // is observer removed?
        assertEquals(0,center.observers.size());
    }

    @Test
    public void testAddWeakObserver() throws Exception
    {
        center.addWeakObserver(countingObserver,"Some event");

        center.postNotification("Some event");

        assertEquals(1,callCount);

        countingObserver = null;
        System.gc();

        center.postNotification("Some event");

        assertEquals(1,callCount);
    }

    @Test
    public void testRemoveObserver() throws Exception
    {

        center.addObserver(observer);
        center.addObserver(observer, "Test");
        center.addObserver(observer, null, this);
        center.addObserver(observer, "Test", this);
        center.addObserver(countingObserver);
        center.addObserver(countingObserver, "Test");
        center.addObserver(countingObserver, null, this);
        center.addObserver(countingObserver, "Test", this);

        center.removeObserver(observer);

        center.postNotification("Test",this);

        verify(observer, times(0)).receivedNotification(captor.capture());

        assertEquals(4, callCount);
    }

    @Test
    public void testRemoveObserverWithName() throws Exception
    {
        center.addObserver(observer);
        center.addObserver(observer, "Test");
        center.addObserver(observer, null, this);
        center.addObserver(observer, "Test", this);
        center.addObserver(countingObserver);
        center.addObserver(countingObserver, "Test");
        center.addObserver(countingObserver, null, this);
        center.addObserver(countingObserver, "Test", this);

        center.removeObserver(observer, "Test");

        center.postNotification("Test",this);

        verify(observer, times(2)).receivedNotification(captor.capture());

        assertEquals(4, callCount);
    }

    @Test
    public void testRemoveObserverWithSender() throws Exception
    {

        center.addObserver(observer);
        center.addObserver(observer, "Test");
        center.addObserver(observer, null, this);
        center.addObserver(observer, "Test", this);
        center.addObserver(countingObserver);
        center.addObserver(countingObserver, "Test");
        center.addObserver(countingObserver, null, this);
        center.addObserver(countingObserver, "Test", this);

        center.removeObserver(observer, null, this);

        center.postNotification("Test",this);

        verify(observer, times(2)).receivedNotification(captor.capture());

        assertEquals(4, callCount);
    }

    @Test
    public void testRemoveObserverWithNameAndSender() throws Exception
    {

        center.addObserver(observer);
        center.addObserver(observer, "Test");
        center.addObserver(observer, null, this);
        center.addObserver(observer, "Test", this);
        center.addObserver(countingObserver);
        center.addObserver(countingObserver, "Test");
        center.addObserver(countingObserver, null, this);
        center.addObserver(countingObserver, "Test", this);

        center.removeObserver(observer, "Test", this);

        center.postNotification("Test",this);

        verify(observer, times(3)).receivedNotification(captor.capture());

        assertEquals(4, callCount);
    }




    @Test
    public void testPostNotification() throws Exception
    {
        center.addObserver(observer);

        center.postNotification("Some event");

        verify(observer).receivedNotification(captor.capture());

        assertEquals("Some event", captor.getValue().getName());
        assertNull(captor.getValue().getObject());
        assertNotNull(captor.getValue().getUserInfo());
        assertEquals(0,captor.getValue().getUserInfo().size());
    }

    @Test
    public void testPostNotificationWithSender() throws Exception
    {
        center.addObserver(observer);

        center.postNotification("Some event", this);

        verify(observer).receivedNotification(captor.capture());

        assertEquals("Some event", captor.getValue().getName());
        assertEquals(this, captor.getValue().getObject());
        assertEquals(0,captor.getValue().getUserInfo().size());
    }

    @Test
    public void testPostNotificationWithUserInfo() throws Exception
    {
        center.addObserver(observer);

        final Map<String, Object> userInfo = DefaultNotificationCenter.createUserInfo("foo","bar");

        center.postNotification("Some event", null, userInfo);

        verify(observer).receivedNotification(captor.capture());

        assertEquals("Some event", captor.getValue().getName());
        assertNull(captor.getValue().getObject());
        assertEquals(userInfo,captor.getValue().getUserInfo());
    }

    @Test
    public void testPostNotificationWithSenderAndUserInfo() throws Exception
    {
        Object o = new Object();

        center.addObserver(observer);

        final Map<String, Object> userInfo = DefaultNotificationCenter.createUserInfo("foo","bar");

        center.postNotification("Some event", o, userInfo);

        verify(observer).receivedNotification(captor.capture());

        assertEquals("Some event", captor.getValue().getName());
        assertEquals(o, captor.getValue().getObject());
        assertEquals(userInfo,captor.getValue().getUserInfo());
    }

    @Test
    public void testPostNotificationWithUserInfoUsingStrings() throws Exception
    {
        center.addObserver(observer);

        center.postNotification("Some event", null, "foo","bar");

        verify(observer).receivedNotification(captor.capture());

        assertEquals("Some event", captor.getValue().getName());
        assertNull(captor.getValue().getObject());
        assertEquals(1,captor.getValue().getUserInfo().size());
        assertEquals("bar",captor.getValue().getUserInfo().get("foo"));
    }

    @Test
    public void testPostNotificationWithSenderAndUserInfoUsingStrings() throws Exception
    {
        Object o = new Object();

        center.addObserver(observer);

        center.postNotification("Some event", o, "foo", "bar");

        verify(observer).receivedNotification(captor.capture());

        assertEquals("Some event", captor.getValue().getName());
        assertEquals(o, captor.getValue().getObject());
        assertEquals(1,captor.getValue().getUserInfo().size());
        assertEquals("bar",captor.getValue().getUserInfo().get("foo"));
    }

    @Test
    public void testPostNotificationWithCustomNotification() throws Exception
    {
        final Object o = new Object();
        final HashMap<String, Object> hashMap = new HashMap<String,Object>();

        Notification not = new Notification() {

            @Override
            public String getName()
            {
                return "bla";
            }

            @Override
            public Object getObject()
            {
                return o;
            }

            @Override
            public Map<String, Object> getUserInfo()
            {
                return hashMap;
            }

        };

        center.addObserver(observer,"bla", o);

        center.postNotification(not);

        verify(observer).receivedNotification(captor.capture());

        assertEquals("bla", captor.getValue().getName());
        assertEquals(o, captor.getValue().getObject());
        assertEquals(hashMap, captor.getValue().getUserInfo());
    }


}
