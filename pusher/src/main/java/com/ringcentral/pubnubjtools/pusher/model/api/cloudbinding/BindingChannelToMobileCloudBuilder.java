package com.ringcentral.pubnubjtools.pusher.model.api.cloudbinding;

import com.ringcentral.pubnubjtools.pusher.model.Credentials;

import java.util.Optional;

public class BindingChannelToMobileCloudBuilder {

    public static final BindingChannelToMobileCloudBuilder INSTANCE = new BindingChannelToMobileCloudBuilder();

    public BindingChannelToMobileCloud bindToCloud(Cloud cloud, String deviceToken, String channel, Credentials credentials) {
        // TODO implement validation of device token format
        return new BindingChannelToMobileCloud(deviceToken, Optional.of(channel), credentials, cloud, Operation.ADD);
    }

    public BindingChannelToMobileCloud bindToApns(String deviceToken, String channel, Credentials credentials) {
        // TODO implement validation of device token format
        return new BindingChannelToMobileCloud(deviceToken, Optional.of(channel), credentials, Cloud.APNS, Operation.ADD);
    }

    public BindingChannelToMobileCloud unbindFromApns(String deviceToken, String channel, Credentials credentials) {
        // TODO implement validation of device token format
        return new BindingChannelToMobileCloud(deviceToken, Optional.of(channel), credentials, Cloud.APNS, Operation.REMOVE);
    }

    public BindingChannelToMobileCloud unbindAllFromApns(String deviceToken, Credentials credentials) {
        // TODO implement validation of device token format
        return new BindingChannelToMobileCloud(deviceToken, Optional.empty(), credentials, Cloud.APNS, Operation.REMOVE);
    }

    public BindingChannelToMobileCloud bindToGcm(String registrationId, String channel, Credentials credentials) {
        // TODO implement validation of registrationId format
        return new BindingChannelToMobileCloud(registrationId, Optional.of(channel), credentials, Cloud.GCM, Operation.ADD);
    }

    public BindingChannelToMobileCloud unbindFromGcm(String registrationId, String channel, Credentials credentials) {
        // TODO implement validation of registrationId format
        return new BindingChannelToMobileCloud(registrationId, Optional.of(channel), credentials, Cloud.GCM, Operation.REMOVE);
    }

    public BindingChannelToMobileCloud unbindAllFromGcm(String registrationId, Credentials credentials) {
        // TODO implement validation of registrationId format
        return new BindingChannelToMobileCloud(registrationId, Optional.empty(), credentials, Cloud.GCM, Operation.REMOVE);
    }

}
