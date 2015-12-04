package com.github.pubnubjtools.pusher;

import com.github.pubnubjtools.pusher.model.Request;
import com.github.pubnubjtools.pusher.model.Response;

import java.util.Collections;

public class DataProvider {

    public static final Request SUCCESS_REQUEST = new Request("http://google.com", Collections.emptyMap());
    public static final Request BAD_REQUEST = new Request("http://microsoft.com", Collections.emptyMap());
    public static final Request IO_EXCEPTION_REQUEST = new Request("http://ibm.com", Collections.emptyMap());

    public static final Response SUCCESS_RESPONSE = new Response(200, "OK", Collections.emptyMap(), "[1,\"Sent\",\"14492379544296731\"]");
    public static final Response BAD_RESPONSE = new Response(400, "Bad Request", Collections.emptyMap(), "Something wrong");

}
