package com.ringcentral.pubnubjtools.pusher.model.api.cloudbinding;

public enum Cloud {

    APNS("apns", "Apple Push Notification Service"),
    GCM("gcm", "Google Cloud Messaging"),
    MPNS("mpns", "Microsoft Push Notification Service");

    final String type;
    final String description;

    Cloud(String type, String description) {
        this.type = type;
        this.description = description;
    }

    @Override
    public String toString() {
        return type;
    }

}
