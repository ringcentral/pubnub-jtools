package com.ringcentral.pubnubjtools.pusher;

import com.ringcentral.pubnubjtools.pusher.model.*;
import com.ringcentral.pubnubjtools.pusher.monitoring.Monitoring;
import com.ringcentral.pubnubjtools.pusher.transport.AsyncTransport;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class AsyncPusher {

    private final AsyncTransport transport;
    private final PubnubConfig pubnubConfig;
    private final WallClock wallClock;
    private final RequestHelper requestHelper;
    private final Monitoring monitoring;

    public AsyncPusher(AsyncTransport transport, PubnubConfig pubnubConfig, Monitoring monitoring) {
        this(transport, pubnubConfig, monitoring, WallClock.INSTANCE, RequestHelper.INSTANCE);
    }

    AsyncPusher(AsyncTransport transport, PubnubConfig pubnubConfig, Monitoring monitoring, WallClock wallClock, RequestHelper requestHelper) {
        this.transport = Objects.requireNonNull(transport);
        this.pubnubConfig = Objects.requireNonNull(pubnubConfig);
        this.monitoring = Objects.requireNonNull(monitoring);
        this.wallClock = Objects.requireNonNull(wallClock);
        this.requestHelper = Objects.requireNonNull(requestHelper);
    }

    public CompletableFuture<DeliveryResult> push(Message message) {
        final long startMillis = wallClock.currentTimeMillis();

        CompletableFuture<DeliveryResult> futureResult = new CompletableFuture<>();
        Request request = requestHelper.createRequest(message, pubnubConfig);

        final CompletableFuture<Response> responseFuture = transport.executeHttpGetRequest(request);

        responseFuture.whenComplete((response, throwable) -> {
            long durationMillis = wallClock.currentTimeMillis() - startMillis;
            if (throwable != null) {
                DeliveryResult deliveryResult = new DeliveryResult(message, request, durationMillis, throwable);
                monitoring.registerDeliveryResult(false, durationMillis);
                futureResult.complete(deliveryResult);
            } else {
                DeliveryResult deliveryResult = new DeliveryResult(message, request, durationMillis, response);
                monitoring.registerDeliveryResult(deliveryResult.isSuccess(), durationMillis);
                futureResult.complete(deliveryResult);
            }
        });

        return futureResult;
    }

    public AsyncTransport getTransport() {
        return transport;
    }

    public PubnubConfig getPubnubConfig() {
        return pubnubConfig;
    }

}
