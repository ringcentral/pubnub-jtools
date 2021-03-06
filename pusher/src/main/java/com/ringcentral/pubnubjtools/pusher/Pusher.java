package com.ringcentral.pubnubjtools.pusher;

import com.ringcentral.pubnubjtools.pusher.model.*;
import com.ringcentral.pubnubjtools.pusher.monitoring.Monitoring;
import com.ringcentral.pubnubjtools.pusher.transport.Transport;

import java.io.IOException;
import java.util.Objects;

public class Pusher {

    private final Transport transport;
    private final PubnubConfig pubnubConfig;
    private final Monitoring monitoring;
    private final WallClock wallClock;
    private final RequestHelper requestHelper;

    public Pusher(Transport transport, PubnubConfig pubnubConfig, Monitoring monitoring) {
        this(transport, pubnubConfig, monitoring, WallClock.INSTANCE, RequestHelper.INSTANCE);
    }

    Pusher(Transport transport, PubnubConfig pubnubConfig, Monitoring monitoring, WallClock wallClock, RequestHelper requestHelper) {
        this.transport = Objects.requireNonNull(transport);
        this.pubnubConfig = Objects.requireNonNull(pubnubConfig);
        this.monitoring = Objects.requireNonNull(monitoring);
        this.wallClock = Objects.requireNonNull(wallClock);
        this.requestHelper = Objects.requireNonNull(requestHelper);
    }

    public DeliveryResult push(Message message) throws PubnubException {
        long startMillis = wallClock.currentTimeMillis();
        Request request = requestHelper.createRequest(message, pubnubConfig);
        DeliveryResult result = push(message, startMillis, request);
        if (result.isFailed()) {
            throw new PubnubException(result);
        }
        monitoring.registerDeliveryResult(result.isSuccess(), result.getDurationMillis());
        return result;
    }

    private DeliveryResult push(Message message, long startMillis, Request request) throws PubnubException {
        try {
            Response response = transport.executeHttpGetRequest(request);
            return new DeliveryResult(message, request, wallClock.currentTimeMillis() - startMillis, response);
        } catch (IOException exception) {
            DeliveryResult deliveryResult = new DeliveryResult(message, request, wallClock.currentTimeMillis() - startMillis, exception);
            throw new PubnubException(deliveryResult);
        }
    }

    public Transport getTransport() {
        return transport;
    }

    public PubnubConfig getPubnubConfig() {
        return pubnubConfig;
    }

}
