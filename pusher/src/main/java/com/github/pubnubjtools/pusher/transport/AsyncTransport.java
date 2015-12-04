package com.github.pubnubjtools.pusher.transport;

import com.github.pubnubjtools.pusher.model.Request;
import com.github.pubnubjtools.pusher.model.Response;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public interface AsyncTransport {

    CompletableFuture<Response> executeHttpGetRequest(Request request) throws IOException;

}
