package com.ringcentral.pubnubjtools.pusher.transport;

import com.ringcentral.pubnubjtools.pusher.model.Request;
import com.ringcentral.pubnubjtools.pusher.model.Response;

import java.io.IOException;

public interface Transport {

    Response executeHttpGetRequest(Request request) throws IOException;

}
