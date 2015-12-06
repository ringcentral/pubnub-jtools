package com.ringcentral.pubnubjtools.pusher;

import com.ringcentral.pubnubjtools.legacy.PubnubCrypto;
import com.ringcentral.pubnubjtools.pusher.monitoring.Monitoring;
import com.ringcentral.pubnubjtools.pusher.transport.AsyncTransport;
import com.ringcentral.pubnubjtools.pusher.transport.Transport;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public abstract class PusherBuilder<T, P> {

    private static final String DEFAULT_PUBNUB_URL = "pubsub.pubnub.com";

    private static final Map<String, String> DEFAULT_HEADERS = new HashMap<>();
    static {
        // DEFAULT_HEADERS.put("Accept-Encoding", "gzip"); TODO configure gzip response handling
        // headers.put("V", ""); TODO figure out what is it
        // headers.put("User-Agent", ""); let transport library to set this header
    }

    private static final Map<String, String> DEFAULT_PARAMETERS = new HashMap<>();
    static {
        DEFAULT_PARAMETERS.put("pnsdk", PubnubCrypto.urlEncode("pubnub-jtools"));
    }

    protected T transport;
    protected Optional<Monitoring> monitoring = Optional.empty();
    private Optional<String> pubnubHost = Optional.empty();
    private Optional<Boolean> useSsl = Optional.empty();
    private Optional<Map<String, String>> httpHeaders = Optional.empty();
    private Optional<Map<String, String>> queryParams = Optional.empty();

    private PusherBuilder(T transport) {
        this.transport = Objects.requireNonNull(transport);
    }

    public abstract P build();

    public static PusherBuilder<Transport, Pusher> forTransport(Transport transport) {
        return new PusherBuilder<Transport, Pusher>(transport) {
            @Override
            public Pusher build() {
                final PubnubConfig pubnubConfig = getPubnubConfig();
                Monitoring monitoring = this.monitoring.orElse(Monitoring.DISABLED_MONITORING);
                return new Pusher(transport, pubnubConfig, monitoring);
            }
        };
    }

    public static PusherBuilder<AsyncTransport, AsyncPusher> forTransport(AsyncTransport transport) {
        return new PusherBuilder<AsyncTransport, AsyncPusher>(transport) {
            @Override
            public AsyncPusher build() {
                final PubnubConfig pubnubConfig = getPubnubConfig();
                Monitoring monitoring = this.monitoring.orElse(Monitoring.DISABLED_MONITORING);
                return new AsyncPusher(transport, pubnubConfig, monitoring);
            }
        };
    }

    public PusherBuilder withMonitoring(Monitoring monitoring) {
        this.monitoring = Optional.of(monitoring);
        return this;
    }

    public PusherBuilder withPubnubHost(String pubnubHost) {
        this.pubnubHost = Optional.of(pubnubHost);
        return this;
    }

    public PusherBuilder useSsl(boolean useSsl) {
        this.useSsl = Optional.of(useSsl);
        return this;
    }

    public PusherBuilder withHttpHeaders(Map<String, String> httpHeaders) {
        this.httpHeaders = Optional.of(httpHeaders);
        return this;
    }

    public PusherBuilder withQueryParameters(Map<String, String> queryParams) {
        this.queryParams = Optional.of(queryParams);
        return this;
    }

    PubnubConfig getPubnubConfig() {
        final String pubnubHost = this.pubnubHost.orElse(DEFAULT_PUBNUB_URL);
        final boolean useSsl = this.useSsl.orElse(false);
        final Map<String, String> httpHeaders = this.httpHeaders.orElse(DEFAULT_HEADERS);
        final Map<String, String> queryParams = this.queryParams.orElse(DEFAULT_PARAMETERS);
        return new PubnubConfig(pubnubHost, useSsl, httpHeaders, queryParams);
    }

    @Override
    public String toString() {
        return "PusherBuilder{" +
                "transport=" + transport +
                ", pubnubHost=" + pubnubHost +
                ", useSsl=" + useSsl +
                ", httpHeaders=" + httpHeaders +
                ", queryParams=" + queryParams +
                '}';
    }

}
