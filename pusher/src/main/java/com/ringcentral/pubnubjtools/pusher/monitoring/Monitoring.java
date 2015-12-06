package com.ringcentral.pubnubjtools.pusher.monitoring;

public interface Monitoring {

    Monitoring DISABLED_MONITORING = new Monitoring() {
        @Override
        public void registerDeliveryResult(boolean isSuccess, long sendingDurationMillis) {
            // do nothing
        }
    };

    void registerDeliveryResult(boolean isSuccess, long sendingDurationMillis);

}
