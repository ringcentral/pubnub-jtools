package com.github.pubnubjtools.pusher.model.api.publish;

import com.github.pubnubjtools.legacy.PubnubCrypto;
import com.github.pubnubjtools.pusher.model.Credentials;
import com.github.pubnubjtools.pusher.model.EncryptionParameters;
import com.github.pubnubjtools.pusher.model.Message;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.github.pubnubjtools.legacy.PubnubCrypto.*;

public class ChannelMessage implements Message {

    private final Object payload;
    private final String serializedPayload;
    private final String channel;
    private final Credentials credentials;
    private final Optional<EncryptionParameters> encryptionParameters;
    private final boolean signatureRequired;
    private final String path;

    ChannelMessage(Object payload, String serializedPayload, String channel, Credentials credentials, Optional<EncryptionParameters> encryptionParameters, boolean signatureRequired) {
        this.payload = payload;
        this.serializedPayload = Objects.requireNonNull(serializedPayload);
        this.channel = Objects.requireNonNull(channel);
        this.credentials = Objects.requireNonNull(credentials);
        this.encryptionParameters = Objects.requireNonNull(encryptionParameters);

        if (signatureRequired) {
            // TODO it is hard to understand through reverse-engineering when signature is required.
            // TODO I do not like to meet with live people, but it is time to ask Pubnub developers.
            throw new UnsupportedOperationException("Signing does not implemented yet");
        }
        this.signatureRequired = Objects.requireNonNull(signatureRequired);
        this.path = buildPath();
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public Object getPayload() {
        return payload;
    }

    public Optional<EncryptionParameters> getEncryptionParameters() {
        return encryptionParameters;
    }

    public String getChannel() {
        return channel;
    }

    public String getSerializedPayload() {
        return serializedPayload;
    }

    @Override
    public String getPath() {
        return path;
    }

    private String buildPath() {
        String pubKey = urlEncode(credentials.getPublisherKey());
        String subKey = urlEncode(credentials.getSubscriberKey());
        String signature = "0";
        String channel = urlEncode(this.channel);

        String payload = toJson(serializedPayload);
        if (encryptionParameters.isPresent()) {
            payload = encrypt(payload, encryptionParameters.get());
        }
        payload = urlEncode(payload);

        String callback = "0";

        //             /publish/pubKey/subKey/signature/channel/callback/payload
        String path = "/publish/" + pubKey + "/" + subKey + "/" + signature + "/" + channel + "/" + callback + "/" + payload;
        return path;
    }

    private String encrypt(String payload, EncryptionParameters encryptionParameters) {
        String cipherKey = encryptionParameters.getCipherKey();
        String initializationVector = encryptionParameters.getInitializationVector();
        try {
            PubnubCrypto encryptor = new PubnubCrypto(cipherKey, initializationVector);
            String encryptedPayload = encryptor.encrypt(payload);
            return "\"" + encryptedPayload + "\"";
        } catch (Exception e) {
            String msg = "Unable to encrypt message using parameters " + encryptionParameters.getTraceDescription();
            throw new RuntimeException(msg, e);
        }
    }

    @Override
    public Map<String, String> getQueryParameters() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, String> getHeaders() {
        return Collections.emptyMap();
    }

    @Override
    public String toString() {
        return "ChannelMessage{" +
                "path='" + path + '\'' +
                ", payload=" + payload +
                ", serializedPayload='" + serializedPayload + '\'' +
                ", channel='" + channel + '\'' +
                ", credentials=" + credentials +
                ", encryptionParameters=" + encryptionParameters +
                ", signatureRequired=" + signatureRequired +
                '}';
    }

    @Override
    public String getTraceDescription() {
        return toString();
    }

    @Override
    public String getDebugDescription() {
        return "ChannelMessage{" +
                "payload=" + payload +
                ", channel='" + channel + '\'' +
                ", encryptionParameters=" + encryptionParameters +
                ", signatureRequired=" + signatureRequired +
                '}';
    }

    @Override
    public String getInfoDescription() {
        return getDebugDescription();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChannelMessage that = (ChannelMessage) o;

        if (signatureRequired != that.signatureRequired) return false;
        if (payload != null ? !payload.equals(that.payload) : that.payload != null) return false;
        if (serializedPayload != null ? !serializedPayload.equals(that.serializedPayload) : that.serializedPayload != null)
            return false;
        if (channel != null ? !channel.equals(that.channel) : that.channel != null) return false;
        if (credentials != null ? !credentials.equals(that.credentials) : that.credentials != null) return false;
        return !(encryptionParameters != null ? !encryptionParameters.equals(that.encryptionParameters) : that.encryptionParameters != null);

    }

    @Override
    public int hashCode() {
        int result = payload != null ? payload.hashCode() : 0;
        result = 31 * result + (serializedPayload != null ? serializedPayload.hashCode() : 0);
        result = 31 * result + (channel != null ? channel.hashCode() : 0);
        result = 31 * result + (credentials != null ? credentials.hashCode() : 0);
        result = 31 * result + (encryptionParameters != null ? encryptionParameters.hashCode() : 0);
        result = 31 * result + (signatureRequired ? 1 : 0);
        return result;
    }

}
