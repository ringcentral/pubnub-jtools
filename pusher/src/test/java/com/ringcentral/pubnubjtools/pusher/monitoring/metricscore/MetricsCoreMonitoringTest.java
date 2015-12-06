package com.ringcentral.pubnubjtools.pusher.monitoring.metricscore;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;


public class MetricsCoreMonitoringTest {

    private static final long DELIVERY_DURATION_MILLIS = 42;

    MetricsCoreMonitoring monitoring = new MetricsCoreMonitoring();

    @Test
    public void testRegisterSuccessfulDeliveryResult() throws Exception {
        monitoring.registerDeliveryResult(true, DELIVERY_DURATION_MILLIS);
        assertEquals(1, monitoring.getSuccessfulDeliveryCounter().getCount());
        assertEquals(0, monitoring.getFailedDeliveryCounter().getCount());
        assertEquals(1, monitoring.getSendingTimer().getCount());
        assertEquals(TimeUnit.MILLISECONDS.toNanos(DELIVERY_DURATION_MILLIS), (long) monitoring.getSendingTimer().getSnapshot().getMean());
    }

    @Test
    public void testRegisterFailedDeliveryResult() throws Exception {
        monitoring.registerDeliveryResult(false, DELIVERY_DURATION_MILLIS);
        assertEquals(0, monitoring.getSuccessfulDeliveryCounter().getCount());
        assertEquals(1, monitoring.getFailedDeliveryCounter().getCount());
        assertEquals(1, monitoring.getSendingTimer().getCount());
        assertEquals(TimeUnit.MILLISECONDS.toNanos(DELIVERY_DURATION_MILLIS), (long)monitoring.getSendingTimer().getSnapshot().getMean());
    }

}