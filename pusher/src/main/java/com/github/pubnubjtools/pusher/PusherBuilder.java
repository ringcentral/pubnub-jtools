package com.github.pubnubjtools.pusher;

import com.github.pubnubjtools.legacy.PubnubCrypto;
import com.github.pubnubjtools.pusher.transport.AsyncTransport;
import com.github.pubnubjtools.pusher.transport.Transport;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PusherBuilder {

    private static final String DEFAULT_PUBNUB_URL = "pubsub.pubnub.com";

    private static final Map<String, String> DEFAULT_HEADERS = new HashMap<>();
    static {
        // DEFAULT_HEADERS.put("Accept-Encoding", "gzip"); TODO configure gzip response handling
        // headers.put("V", ""); TODO figure out what is it
        // headers.put("User-Agent", ""); let transport library to set this header
    }

    private static final Map<String,String> DEFAULT_PARAMETERS = new HashMap<>();
    static {
        DEFAULT_PARAMETERS.put("pnsdk", PubnubCrypto.urlEncode("pubnub-jtools"));
    }

    private Optional<String> pubnubHost = Optional.empty();
    private Optional<Boolean> useSsl = Optional.empty();
    private Optional<Map<String, String>> httpHeaders = Optional.empty();
    private Optional<Map<String, String>> queryParams = Optional.empty();

    public PusherBuilder withPubnubHost(String pubnubHost) {
        if (this.pubnubHost.isPresent()) {
            throw new IllegalArgumentException("Pubnub URL already configured");
        }
        this.pubnubHost = Optional.of(pubnubHost);
        return this;
    }

    public PusherBuilder useSsl(boolean useSsl) {
        if (this.useSsl.isPresent()) {
            throw new IllegalArgumentException("SSL already configured");
        }
        this.useSsl = Optional.of(useSsl);
        return this;
    }

    public PusherBuilder withHttpHeaders(Map<String, String> httpHeaders) {
        if (this.httpHeaders.isPresent()) {
            throw new IllegalArgumentException("HTTP headers already configured");
        }
        this.httpHeaders = Optional.of(httpHeaders);
        return this;
    }

    public PusherBuilder withQueryParameters(Map<String, String> queryParams) {
        if (this.queryParams.isPresent()) {
            throw new IllegalArgumentException("URL queryParams already configured");
        }
        this.queryParams = Optional.of(queryParams);
        return this;
    }

    public Pusher buildSyncPusher(Transport transport) {
        final PubnubConfig pubnubConfig = getPubnubConfig();
        return new Pusher(transport, pubnubConfig);
    }

    public AsyncPusher buildAsyncPusher(AsyncTransport transport) {
        final PubnubConfig pubnubConfig = getPubnubConfig();
        return new AsyncPusher(transport, pubnubConfig);
    }

    private PubnubConfig getPubnubConfig() {
        final String pubnubHost = this.pubnubHost.orElse(DEFAULT_PUBNUB_URL);
        final boolean useSsl = this.useSsl.orElse(false);
        final Map<String, String> httpHeaders = this.httpHeaders.orElse(DEFAULT_HEADERS);
        final Map<String, String> queryParams = this.queryParams.orElse(DEFAULT_PARAMETERS);
        return new PubnubConfig(pubnubHost, useSsl, httpHeaders, queryParams);
    }

    @Override
    public String toString() {
        return "PusherBuilder{" +
                "pubnubHost=" + pubnubHost +
                ", useSsl=" + useSsl +
                ", httpHeaders=" + httpHeaders +
                ", queryParams=" + queryParams +
                '}';
    }
    
}
