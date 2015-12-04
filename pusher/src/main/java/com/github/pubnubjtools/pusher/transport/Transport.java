package com.github.pubnubjtools.pusher.transport;

import com.github.pubnubjtools.pusher.model.Request;
import com.github.pubnubjtools.pusher.model.Response;

import java.io.IOException;

public interface Transport {

    Response executeHttpGetRequest(Request request) throws IOException;

}
