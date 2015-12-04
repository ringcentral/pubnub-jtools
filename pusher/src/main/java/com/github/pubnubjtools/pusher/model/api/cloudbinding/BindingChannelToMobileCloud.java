package com.github.pubnubjtools.pusher.model.api.cloudbinding;

import com.github.pubnubjtools.pusher.model.Credentials;
import com.github.pubnubjtools.pusher.model.Message;

import java.util.*;

import static com.github.pubnubjtools.legacy.PubnubCrypto.urlEncode;

public class BindingChannelToMobileCloud implements Message {

    private final String registrationId;
    private final Optional<String> channel;
    private final Credentials credentials;
    private final Cloud cloud;
    private final Operation operation;

    BindingChannelToMobileCloud(String registrationId, Optional<String> channel, Credentials credentials, Cloud cloud, Operation operation) {
        this.registrationId = Objects.requireNonNull(registrationId);
        this.channel = Objects.requireNonNull(channel);
        this.credentials = Objects.requireNonNull(credentials);
        this.cloud = cloud;
        this.operation = operation;
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public Cloud getCloud() {
        return cloud;
    }

    public Operation getOperation() {
        return operation;
    }

    public Optional<String> getChannel() {
        return channel;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    @Override
    public String getPath() {
        String subKey = urlEncode(credentials.getSubscriberKey());
        //             /v1/push/sub-key/<SUB_KEY>/devices/<REG_ID>?OPERATION=<CHANNEL_LIST>&type=<TYPE>
        String path = "/v1/push/sub-key/" + subKey + "/devices/" + registrationId;
        return path;
    }

    @Override
    public Map<String, String> getQueryParameters() {
        Map<String, String> queryParameters = new HashMap<>();
        queryParameters.put("type", cloud.type);
        queryParameters.put(operation.command, channel.orElse(null));
        return queryParameters;
    }

    @Override
    public Map<String, String> getHeaders() {
        return Collections.emptyMap();
    }

    @Override
    public String toString() {
        return "BindingChannelToMobileCloud{" +
                "registrationId='" + registrationId + '\'' +
                ", channel=" + channel +
                ", credentials=" + credentials +
                ", cloud=" + cloud +
                ", operation=" + operation +
                '}';
    }

    @Override
    public String getTraceDescription() {
        return toString();
    }

    @Override
    public String getDebugDescription() {
        return toString();
    }

    @Override
    public String getInfoDescription() {
        return toString();
    }

}
