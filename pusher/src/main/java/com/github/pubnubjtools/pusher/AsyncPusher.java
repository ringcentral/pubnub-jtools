package com.github.pubnubjtools.pusher;

import com.github.pubnubjtools.pusher.model.*;
import com.github.pubnubjtools.pusher.transport.AsyncTransport;

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
        try {
            final CompletableFuture<Response> responseFuture = transport.executeHttpGetRequest(request);

            responseFuture.whenComplete((response, throwable) -> {
                if (throwable != null) {
                    DeliveryResult deliveryResult = new DeliveryResult(message, request, wallClock.currentTimeMillis() - startMillis, throwable);
                    futureResult.completeExceptionally(new PubnubException(deliveryResult));
                } else {
                    DeliveryResult deliveryResult = new DeliveryResult(message, request, wallClock.currentTimeMillis() - startMillis, response);
                    futureResult.complete(deliveryResult);
                }
            });
        } catch (IOException exception) {
            DeliveryResult deliveryResult = new DeliveryResult(message, request, wallClock.currentTimeMillis() - startMillis, exception);
            futureResult.completeExceptionally(new PubnubException(deliveryResult));
        }

        return futureResult;
    }

    public AsyncTransport getTransport() {
        return transport;
    }

    public PubnubConfig getPubnubConfig() {
        return pubnubConfig;
    }

}
