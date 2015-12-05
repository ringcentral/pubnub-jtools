package com.ringcentral.pubnubjtools.pusher.model;


import com.ringcentral.pubnubjtools.pusher.DataProvider;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DeliveryResultTest {

    @Mock Message message;

    Response successResponse = new Response(200, "OK", Collections.emptyMap(), "[1,\"Sent\",\"14492379544296731\"]");
    Response badResponse = new Response(400, "Bad Request", Collections.emptyMap(), "Something wrong");

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testIsSuccess() throws Exception {
        assertTrue(new DeliveryResult(message, DataProvider.SUCCESS_REQUEST, 666, successResponse).isSuccess());
        assertFalse(new DeliveryResult(message, DataProvider.BAD_REQUEST, 666, badResponse).isSuccess());
        assertFalse(new DeliveryResult(message, DataProvider.BAD_REQUEST, 666, new IOException()).isSuccess());
    }

    @Test
    public void testIsFailed() throws Exception {
        assertFalse(new DeliveryResult(message, DataProvider.SUCCESS_REQUEST, 666, successResponse).isFailed());
        assertTrue(new DeliveryResult(message, DataProvider.BAD_REQUEST, 666, badResponse).isFailed());
        assertTrue(new DeliveryResult(message, DataProvider.BAD_REQUEST, 666, new IOException()).isFailed());
    }

    @Test
    public void testHasException() throws Exception {
        assertFalse(new DeliveryResult(message, DataProvider.BAD_REQUEST, 666, badResponse).hasException());
        assertTrue(new DeliveryResult(message, DataProvider.BAD_REQUEST, 666, new IOException()).hasException());
    }

}