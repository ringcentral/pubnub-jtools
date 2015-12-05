package com.ringcentral.pubnubjtools.benchmark;

import com.codahale.metrics.Snapshot;
import com.codahale.metrics.Timer;
import com.github.bucket4j.Bucket;
import com.github.bucket4j.Buckets;
import com.github.pubnubjtools.pusher.model.Credentials;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;

public class Benchmark {

    private final Bucket bucket;
    private final AtomicBoolean stopped = new AtomicBoolean(false);
    private final Duration benchmarkDuration;
    private final TransportMode transportMode;
    private final LoadType loadType;
    private final Map context;

    // measures overhead of workload to be sure that workload generation is not the problem by itself
    private final Timer workloadTimer = new Timer();

    public Benchmark(TransportMode transportMode, int loadRps, int threadCount, Duration benchmarkDuration, Credentials credentials, LoadType loadType) {
        this.bucket = Buckets.withNanoTimePrecision().withLimitedBandwidth(loadRps, TimeUnit.SECONDS, 1, 1).build();
        this.benchmarkDuration = benchmarkDuration;
        this.transportMode = transportMode;
        this.context = transportMode.createContext(threadCount, credentials);
        this.loadType = loadType;
    }

    public Timer execute() {
        try {
            runStopperThread();
            doLoad();

            System.out.println("\nWorkload statistics:");
            printTimer(workloadTimer);

            Timer actualPerformanceTimer = transportMode.getTimer(context);
            System.out.println("\nActual performance statistics:");
            printTimer(actualPerformanceTimer);

            return actualPerformanceTimer;
        } finally {
            transportMode.shutdown(context);
        }
    }

    private void doLoad() {
        while (!stopped.get()) {
            try {
                bucket.consumeSingleToken();
            } catch (InterruptedException e) {
                e.printStackTrace();
                stopped.set(true);
                break;
            }
            try (Timer.Context stopwatch = workloadTimer.time()) {
                loadType.executeStep(transportMode, context);
            }
        }
    }

    private void runStopperThread() {
        new Thread() {
            @Override
            public void run() {
                LockSupport.parkNanos(benchmarkDuration.toNanos());
                stopped.set(true);
            }
        }.start();
    }

    public static void printTimer(Timer timer) {
        Snapshot snapshot = timer.getSnapshot();
        printFormatted("max: %d ms", snapshot.getMax() / 1_000_000);
        printFormatted("min: %d ms", snapshot.getMin() / 1_000_000);
        printFormatted("mean: %.3f ms", snapshot.getMean() / 1_000_000);
        printFormatted("median: %.3f ms", snapshot.getMedian() / 1_000_000);
        printFormatted("75 percentile: %.3f ms", snapshot.get75thPercentile() / 1_000_000);
        printFormatted("95 percentile: %.3f ms", snapshot.get95thPercentile() / 1_000_000);
        printFormatted("99 percentile: %.3f ms", snapshot.get99thPercentile() / 1_000_000);
        printFormatted("Average rate %.3f rps", timer.getMeanRate());
        printFormatted("Total request count %d", timer.getCount());
    }

    private static void printFormatted(String format, Object... params) {
        String formatted = String.format(format, params);
        System.out.println(formatted);
    }

}
