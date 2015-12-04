package com.github.pubnubjtools.pusher.model.api;

import com.github.pubnubjtools.pusher.model.api.cloudbinding.BindingChannelToMobileCloudBuilder;
import com.github.pubnubjtools.pusher.model.api.publish.CnannelMessageBuilder;

public class Messages {

    public static CnannelMessageBuilder publish() {
        return CnannelMessageBuilder.INSTANCE;
    }

    public static BindingChannelToMobileCloudBuilder cloudBinding() {
        return BindingChannelToMobileCloudBuilder.INSTANCE;
    }

}
