package com.ringcentral.pubnubjtools.pusher;

import com.ringcentral.pubnubjtools.pusher.model.*;
import com.ringcentral.pubnubjtools.pusher.monitoring.Monitoring;
import com.ringcentral.pubnubjtools.pusher.transport.Transport;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;


import static com.ringcentral.pubnubjtools.pusher.DataProvider.*;
import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.when;

public class PusherTest {

    private static final long REQUEST_TIME_MILLIS = 42;

    @Mock WallClock wallClock;
    @Mock RequestHelper requestHelper;
    @Mock PubnubConfig config;
    @Mock Transport transport;
    @Mock Monitoring monitoring;
    @Mock Message message;

    Pusher pusher;

    @Before
    public void setupMocks() {
        MockitoAnnotations.initMocks(this);
        when(wallClock.currentTimeMillis()).thenReturn(1000l, 1000l + REQUEST_TIME_MILLIS);

        pusher = new Pusher(transport, config, monitoring, wallClock, requestHelper);
    }

    @Test
    public void testPushSuccessRequest() throws Exception {
        setupTransport(SUCCESS_REQUEST, SUCCESS_RESPONSE);
        pushAndVerifyResult(SUCCESS_REQUEST, SUCCESS_RESPONSE, null, REQUEST_TIME_MILLIS, true);
    }

    @Test
    public void testPushBadRequest() throws Exception {
        setupTransport(BAD_REQUEST, BAD_RESPONSE);
        pushAndVerifyResult(BAD_REQUEST, BAD_RESPONSE, null, REQUEST_TIME_MILLIS, false);
    }

    @Test
    public void testPushRequestFailedWithException() throws Exception {
        final IOException ioException = new IOException();
        setupTransport(BAD_REQUEST, ioException);
        pushAndVerifyResult(BAD_REQUEST, null, ioException, REQUEST_TIME_MILLIS, false);
    }

    private void setupTransport(Request request, Response response) throws IOException {
        when(requestHelper.createRequest(message, config)).thenReturn(request);
        when(transport.executeHttpGetRequest(request)).thenReturn(response);
    }

    private void setupTransport(Request request, IOException ioException) throws IOException {
        when(requestHelper.createRequest(message, config)).thenReturn(request);
        when(transport.executeHttpGetRequest(request)).thenThrow(ioException);
    }

    private void pushAndVerifyResult(Request request, Response response, Exception exception, long durationMillis, boolean success) throws PubnubException {
        DeliveryResult result;
        if (success) {
            result = pusher.push(message);
        } else {
            try {
                pusher.push(message);
                throw new IllegalStateException("exception should be thrown");
            } catch (PubnubException e) {
                result = e.getDeliveryResult();
            }
        }

        assertSame(request, result.getRequest());
        assertSame(response, result.getResponse());
        assertSame(exception, result.getException());
        assertEquals(durationMillis, result.getDurationMillis());
        assertEquals(success, result.isSuccess());
    }

}