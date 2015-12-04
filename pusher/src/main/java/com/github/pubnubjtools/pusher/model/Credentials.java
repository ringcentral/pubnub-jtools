package com.github.pubnubjtools.pusher.model;

import java.util.Objects;

public class Credentials implements Describable {

    private final String publisherKey;
    private final String subscriberKey;
    private final String secretKey;

    public Credentials(String publisherKey, String subscriberKey, String secretKey) {
        Objects.requireNonNull(publisherKey);
        Objects.requireNonNull(subscriberKey);
        Objects.requireNonNull(secretKey);
        this.publisherKey = publisherKey;
        this.subscriberKey = subscriberKey;
        this.secretKey = secretKey;
    }

    public String getPublisherKey() {
        return publisherKey;
    }

    public String getSubscriberKey() {
        return subscriberKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Credentials that = (Credentials) o;

        if (publisherKey != null ? !publisherKey.equals(that.publisherKey) : that.publisherKey != null) return false;
        if (subscriberKey != null ? !subscriberKey.equals(that.subscriberKey) : that.subscriberKey != null)
            return false;
        return !(secretKey != null ? !secretKey.equals(that.secretKey) : that.secretKey != null);

    }

    @Override
    public int hashCode() {
        int result = publisherKey != null ? publisherKey.hashCode() : 0;
        result = 31 * result + (subscriberKey != null ? subscriberKey.hashCode() : 0);
        result = 31 * result + (secretKey != null ? secretKey.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Credentials{" +
                "publisherKey='" + publisherKey + '\'' +
                ", subscriberKey='" + subscriberKey + '\'' +
                ", secretKey='" + secretKey + '\'' +
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
