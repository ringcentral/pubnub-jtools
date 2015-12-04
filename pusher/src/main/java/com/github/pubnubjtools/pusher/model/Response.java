package com.github.pubnubjtools.pusher.model;

import java.util.Map;

public class Response implements Describable {

    private final int status;
    private final String reasonPhrase;
    private final Map<String, String> headers;
    private final String responseBody;

    public Response(int status, String reasonPhrase, Map<String, String> headers, String body) {
        this.status = status;
        this.reasonPhrase = reasonPhrase;
        this.responseBody = body;
        this.headers = headers;
    }

    public int getStatus() {
        return status;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    @Override
    public String toString() {
        return "Response{" +
                "status=" + status +
                ", reasonPhrase='" + reasonPhrase + '\'' +
                ", headers=" + headers +
                ", responseBody='" + responseBody + '\'' +
                '}';
    }

    @Override
    public String getTraceDescription() {
        return toString();
    }

    @Override
    public String getDebugDescription() {
       return toString();
    }

    @Override
    public String getInfoDescription() {
        return "Response{" +
                "status=" + status +
                ", reasonPhrase='" + reasonPhrase + '\'' +
                ", responseBody='" + responseBody + '\'' +
                '}';
    }

}
