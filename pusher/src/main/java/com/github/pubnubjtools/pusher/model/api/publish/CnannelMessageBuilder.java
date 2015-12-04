package com.github.pubnubjtools.pusher.model.api.publish;

import com.github.pubnubjtools.pusher.model.Credentials;
import com.github.pubnubjtools.pusher.model.EncryptionParameters;

import java.util.Optional;

public class CnannelMessageBuilder {

    public static final CnannelMessageBuilder INSTANCE = new CnannelMessageBuilder();

    public ChannelMessage unsecure(Object payload, String serializedPayload, String channel, Credentials credentials) {
        return new ChannelMessage(payload, serializedPayload, channel, credentials, Optional.empty(), false);
    }

    public ChannelMessage encrypted(Object payload, String serializedPayload, String channel, Credentials credentials, EncryptionParameters encryptionParameters) {
        return new ChannelMessage(payload, serializedPayload, channel, credentials, Optional.of(encryptionParameters), false);
    }

    public ChannelMessage signed(Object payload, String serializedPayload, String channel, Credentials credentials) {
        return new ChannelMessage(payload, serializedPayload, channel, credentials, Optional.empty(), true);
    }

    public ChannelMessage encryptedAndSigned(Object payload, String serializedPayload, String channel, Credentials credentials, EncryptionParameters encryptionParameters) {
        return new ChannelMessage(payload, serializedPayload, channel, credentials, Optional.of(encryptionParameters), true);
    }

}
