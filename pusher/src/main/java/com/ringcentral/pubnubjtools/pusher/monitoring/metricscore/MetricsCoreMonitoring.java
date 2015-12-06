package com.ringcentral.pubnubjtools.pusher.monitoring.metricscore;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Timer;
import com.ringcentral.pubnubjtools.pusher.monitoring.Monitoring;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class MetricsCoreMonitoring implements Monitoring {

    private final Timer sendingTimer;
    private final Counter failedDeliveryCounter;
    private final Counter successfulDeliveryCounter;

    public MetricsCoreMonitoring(Timer sendingTimer, Counter failedDeliveryCounter, Counter successfulDeliveryCounter) {
        this.sendingTimer = Objects.requireNonNull(sendingTimer);
        this.failedDeliveryCounter = Objects.requireNonNull(failedDeliveryCounter);
        this.successfulDeliveryCounter = Objects.requireNonNull(successfulDeliveryCounter);
    }

    public MetricsCoreMonitoring() {
        this(new Timer(), new Counter(), new Counter());
    }

    @Override
    public void registerDeliveryResult(boolean isSuccess, long sendingDurationMillis) {
        sendingTimer.update(sendingDurationMillis, TimeUnit.MILLISECONDS);
        if (isSuccess) {
            successfulDeliveryCounter.inc();
        } else {
            failedDeliveryCounter.inc();
        }
    }

    public Counter getFailedDeliveryCounter() {
        return failedDeliveryCounter;
    }

    public Counter getSuccessfulDeliveryCounter() {
        return successfulDeliveryCounter;
    }

    public Timer getSendingTimer() {
        return sendingTimer;
    }

}
