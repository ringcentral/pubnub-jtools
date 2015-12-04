package com.github.pubnubjtools.pusher;

import java.util.Map;
import java.util.Objects;

public class PubnubConfig {

    private final String pubnubUrl;
    private final Map<String, String> headers;
    private final Map<String, String> queryParams;

    public PubnubConfig(String pubnubHost, boolean useSsl, Map<String, String> headers, Map<String, String> queryParams) {
        String protocol = useSsl? "https" : "http";
        this.pubnubUrl =  protocol + "://" + pubnubHost;
        this.headers = Objects.requireNonNull(headers);
        this.queryParams = Objects.requireNonNull(queryParams);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public String getPubnubUrl() {
        return pubnubUrl;
    }

    @Override
    public String toString() {
        return "PubnubConfig{" +
                "pubnubUrl='" + pubnubUrl + '\'' +
                ", headers=" + headers +
                ", queryParams=" + queryParams +
                '}';
    }

}
