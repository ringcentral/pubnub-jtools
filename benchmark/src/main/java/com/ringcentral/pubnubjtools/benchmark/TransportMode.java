package com.ringcentral.pubnubjtools.benchmark;


import com.codahale.metrics.Counter;
import com.codahale.metrics.Timer;
import com.github.pubnubjtools.pusher.Pusher;
import com.github.pubnubjtools.pusher.PusherBuilder;
import com.github.pubnubjtools.pusher.model.Credentials;
import com.github.pubnubjtools.pusher.model.DeliveryResult;
import com.github.pubnubjtools.pusher.model.Message;
import com.github.pubnubjtools.pusher.model.PubnubException;
import com.github.pubnubjtools.pusher.model.api.Messages;
import com.github.pubnubjtools.pusher.transport.httpclient.ApacheHttpClientTransport;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.RequestManagerConfigurator;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public enum TransportMode {

    PUNUB_JTOOLS_PUSHER {
        @Override
        public Map createContext(int threadCount, Credentials credentials) {
            Map context = new HashMap<>();
            context.put("credentials", credentials);

            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            context.put("executor", executor);


            PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
            cm.setMaxTotal(300);
            cm.setDefaultMaxPerRoute(150);

            CloseableHttpClient httpClient = HttpClients.custom()
                    .setConnectionManager(cm)
                    .setConnectionTimeToLive(5, TimeUnit.MINUTES)
                    .build();


            ApacheHttpClientTransport transport = new ApacheHttpClientTransport(httpClient);
            Pusher pusher = new PusherBuilder().buildSyncPusher(transport);
            context.put("pusher", pusher);
            context.put("perOperationTimer", new Timer());
            context.put("inProgress", new AtomicLong());
            return context;
        }

        @Override
        public void publish(String payload, String channel, Map context) {
            final Credentials credentials = (Credentials) context.get("credentials");
            final Pusher pusher = (Pusher) context.get("pusher");
            final ExecutorService executor = (ExecutorService) context.get("executor");
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        final Message message = Messages.publish().unsecure(payload, payload, channel, credentials);
                        pusher.push(message);
                    } catch (PubnubException e) {
                        // e.printStackTrace();
                    }
                }
            });
        }

        @Override
        public void bindChannelToDevice(final String registrationId, final String channel, Map context) {
            final Credentials credentials = (Credentials) context.get("credentials");
            final Pusher pusher = (Pusher) context.get("pusher");
            final ExecutorService executor = (ExecutorService) context.get("executor");

            executor.execute(new Runnable() {
                @Override
                public void run() {
                    DeliveryResult result = null;
                    try {
                        final Message message = Messages.cloudBinding().bindToApns(registrationId, channel, credentials);
                        result = pusher.push(message);
                    } catch (PubnubException e) {
                        result = e.getDeliveryResult();
                    } finally {

                    }
                }
            });
        }

        @Override
        public void shutdown(Map context) {
            final Pusher pusher = (Pusher) context.get("pusher");
            MetricsCoreMonitoring monitoring = (MetricsCoreMonitoring) pusher.getMonitoring();
            System.out.println("Error count:" + monitoring.getFailedDeliveryAttemptsCounter().getCount());

            final AtomicLong inProgress = (AtomicLong) context.get("inProgress");
            long inProgressSnapshot = inProgress.get();
            while (inProgressSnapshot > 0l) {
                System.out.println("Waiting for " + inProgressSnapshot + " incomplete tasks");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                inProgressSnapshot = inProgress.get();
            }

            ExecutorService executor = (ExecutorService) context.get("executor");
            executor.shutdownNow();
        }

        @Override
        public Timer getTimer(Map context) {
            Pusher pusher = (Pusher) context.get("pusher");
            MetricsCoreMonitoring monitoring = (MetricsCoreMonitoring) pusher.getMonitoring();
            return monitoring.getSendingTimer();
        }
    },

    OFFICIAL_PUNUB_DEFAULT {
        @Override
        public Map createContext(int threadCount, Credentials credentials) {
            Map context = new HashMap<>();
            Pubnub pubnub = new Pubnub(credentials.getPublisherKey(), credentials.getSubscriberKey());
            context.put("pubnub", pubnub);
            Timer timer = new Timer();
            context.put("timer", timer);
            return context;
        }

        @Override
        public void publish(String payload, String channel, Map context) {
            Pubnub pubnub = (Pubnub) context.get("pubnub");
            Timer timer = (Timer) context.get("timer");
            final Timer.Context stopwatch = timer.time();
            Callback callback = new Callback() {
                @Override
                public void successCallback(String channel, Object message) {
                    stopwatch.stop();
                }
                @Override
                public void errorCallback(String channel, Object message) {
                    stopwatch.stop();
                }
            };
            pubnub.publish((String)payload, channel, callback);
        }

        @Override
        public void bindChannelToDevice(String registrationId, String channel, Map context) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void shutdown(Map context) {
            Pubnub pubnub = (Pubnub) context.get("pubnub");
            pubnub.shutdown();
        }
    },

    OFFICIAL_PUNUB_ONE_INSTANCE_WITH_MANY_THREADS_WORKAROUND {
        @Override
        public Map createContext(int threadCount, Credentials credentials) {
            RequestManagerConfigurator.setWorkerCount(threadCount);
            Pubnub pubnub = new Pubnub(credentials.getPublisherKey(), credentials.getSubscriberKey());

            Map context = new HashMap<>();
            context.put("pubnub", pubnub);
            Timer timer = new Timer();
            context.put("timer", timer);

            Counter errors = new Counter();
            context.put("errors", errors);

            return context;
        }

        @Override
        public void publish(String payload, String channel, Map context) {
            Pubnub pubnub = (Pubnub) context.get("pubnub");
            Timer timer = (Timer) context.get("timer");
            final Timer.Context stopwatch = timer.time();
            Callback callback = new Callback() {
                @Override
                public void successCallback(String channel, Object message) {
                    stopwatch.stop();
                }
                @Override
                public void errorCallback(String channel, Object message) {
                    stopwatch.stop();
                    Counter errors = (Counter) context.get("errors");
                    errors.inc();
                }
            };
            pubnub.publish(payload, channel, callback);
        }

        @Override
        public void bindChannelToDevice(String registrationId, String channel, Map context) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void shutdown(Map context) {
            Counter errors = (Counter) context.get("errors");
            System.out.println("Error count:" + errors.getCount());

            Pubnub pubnub = (Pubnub) context.get("pubnub");
            pubnub.shutdown();
        }
    },

    public Timer getTimer(Map context) {
        return (Timer) context.get("timer");
    }

    public abstract Map createContext(int threadCount, Credentials credentials);

    public abstract void publish(String payload, String channel, Map context);

    public abstract void bindChannelToDevice(String registrationId, String channel, Map context);

    public abstract void shutdown(Map context);
}
