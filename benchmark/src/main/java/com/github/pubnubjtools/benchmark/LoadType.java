package com.github.pubnubjtools.benchmark;

import java.util.Map;
import java.util.Random;

public interface LoadType {

    void executeStep(TransportMode transportMode, Map context);

    static LoadType publishMessage(int messageSize) {
        final String channel = "ringcentral-publish-benchmark-" + System.currentTimeMillis();
        return new LoadType() {
            private final String payload;
            {
                StringBuilder payloadBuilder = new StringBuilder();
                for (int i = 0; i < messageSize; i++) {
                    payloadBuilder.append(i % 10);
                }
                this.payload = payloadBuilder.toString();
            }

            @Override
            public void executeStep(TransportMode transportMode, Map context) {
                transportMode. publish(payload, channel, context);
            }
        };
    }


    static LoadType bindChannelToDevice(final String devicePreffix) {
        final Random random = new Random(System.nanoTime());
        return new LoadType() {
            @Override
            public void executeStep(TransportMode transportMode, Map context) {
                StringBuilder registrationId = new StringBuilder(devicePreffix);
                while (registrationId.length() < 64) {
                    registrationId.append(random.nextInt(10));
                }

                String channel = "ringcentral-bindevicetoken-benchmark-" + Math.abs(random.nextLong());
                transportMode.bindChannelToDevice(registrationId.toString(), channel, context);
            }
        };
    }

}