package com.ringcentral.pubnubjtools.pusher.model;


import java.io.IOException;

public class PubnubException extends IOException {

    private final DeliveryResult deliveryResult;

    public PubnubException(DeliveryResult deliveryResult) {
        super(deliveryResult.getException());
        this.deliveryResult = deliveryResult;
    }

    public DeliveryResult getDeliveryResult() {
        return deliveryResult;
    }

}
