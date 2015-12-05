package com.ringcentral.pubnubjtools.pusher.model;

import java.util.Map;

public interface Message extends Describable {

    Map<String, String> getHeaders();

    String getPath();

    Map<String, String> getQueryParameters();

}
