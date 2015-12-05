package com.ringcentral.pubnubjtools.pusher.transport.httpclient;

import com.ringcentral.pubnubjtools.pusher.model.Request;
import com.ringcentral.pubnubjtools.pusher.model.Response;
import com.ringcentral.pubnubjtools.pusher.transport.Transport;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClients;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ApacheHttpClientTransport implements Transport {

    private final HttpClient httpClient;

    public ApacheHttpClientTransport() {
        this(HttpClients.createDefault());
    }

    public ApacheHttpClientTransport(HttpClient httpClient) {
        this.httpClient = Objects.requireNonNull(httpClient);
    }

    @Override
    public Response executeHttpGetRequest(Request request) throws IOException {
        Map<String, String> requestHeaders = request.getHeaders();
        HttpGet httpGet = new HttpGet(request.getUrl());
        requestHeaders.forEach((header, value) -> httpGet.addHeader(header, value));

        ResponseHandler<Response> responseHandler = response -> {
            // collect status info
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            String reasonPhrase = statusLine.getReasonPhrase();

            // collect response headers
            Map<String, String> headers = new HashMap<>();
            for(Header header : response.getAllHeaders()) {
                String headerName = header.getName();
                String headerValue = header.getValue();
                headers.put(headerName, headerValue);
            }

            // read response body
            HttpEntity entity = response.getEntity();
            String body = "";
            if (entity != null) {
                ContentType contentType = ContentType.getOrDefault(entity);
                Charset charset = contentType.getCharset();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                entity.writeTo(stream);
                body = stream.toString(charset.name());
            }

            return new Response(statusCode, reasonPhrase, headers, body);
        };

        return httpClient.execute(httpGet, responseHandler);
    }

}
