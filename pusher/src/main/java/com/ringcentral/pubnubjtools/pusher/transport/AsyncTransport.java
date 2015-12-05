package com.ringcentral.pubnubjtools.pusher.transport;

import com.ringcentral.pubnubjtools.pusher.model.Request;
import com.ringcentral.pubnubjtools.pusher.model.Response;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public interface AsyncTransport {

    CompletableFuture<Response> executeHttpGetRequest(Request request);

}
