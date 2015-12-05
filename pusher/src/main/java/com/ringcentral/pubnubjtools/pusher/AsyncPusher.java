package com.ringcentral.pubnubjtools.pusher;

import com.ringcentral.pubnubjtools.pusher.model.*;
import com.ringcentral.pubnubjtools.pusher.transport.AsyncTransport;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class AsyncPusher {

    private final AsyncTransport transport;
    private final PubnubConfig pubnubConfig;
    private final WallClock wallClock;
    private final RequestHelper requestHelper;

    public AsyncPusher(AsyncTransport transport, PubnubConfig pubnubConfig) {
        this(transport, pubnubConfig, WallClock.INSTANCE, RequestHelper.INSTANCE);
    }

    AsyncPusher(AsyncTransport transport, PubnubConfig pubnubConfig, WallClock wallClock, RequestHelper requestHelper) {
        this.transport = Objects.requireNonNull(transport);
        this.pubnubConfig = Objects.requireNonNull(pubnubConfig);
        this.wallClock = Objects.requireNonNull(wallClock);
        this.requestHelper = Objects.requireNonNull(requestHelper);
    }

    public CompletableFuture<DeliveryResult> push(Message message) {
        final long startMillis = wallClock.currentTimeMillis();

        CompletableFuture<DeliveryResult> futureResult = new CompletableFuture<>();
        Request request = requestHelper.createRequest(message, pubnubConfig);

        final CompletableFuture<Response> responseFuture = transport.executeHttpGetRequest(request);

        responseFuture.whenComplete((response, throwable) -> {
            if (throwable != null) {
                DeliveryResult deliveryResult = new DeliveryResult(message, request, wallClock.currentTimeMillis() - startMillis, throwable);
                futureResult.complete(deliveryResult);
            } else {
                DeliveryResult deliveryResult = new DeliveryResult(message, request, wallClock.currentTimeMillis() - startMillis, response);
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
