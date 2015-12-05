package com.ringcentral.pubnubjtools.pusher.transport.asynchttpclient;

import com.ringcentral.pubnubjtools.pusher.model.Request;
import com.ringcentral.pubnubjtools.pusher.model.Response;
import com.ringcentral.pubnubjtools.pusher.transport.AsyncTransport;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.nio.client.HttpAsyncClient;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class AsyncApacheHttpClientTransport implements AsyncTransport {

    private final HttpAsyncClient httpClient;

    public AsyncApacheHttpClientTransport() {
        this(HttpAsyncClients.createDefault());
    }

    public AsyncApacheHttpClientTransport(CloseableHttpAsyncClient httpClient) {
        this.httpClient = Objects.requireNonNull(httpClient);
        if (!httpClient.isRunning()) {
            httpClient.start();
        }
    }

    @Override
    public CompletableFuture<Response> executeHttpGetRequest(Request request) {
        Map<String, String> requestHeaders = request.getHeaders();
        HttpGet httpGet = new HttpGet(request.getUrl());
        requestHeaders.forEach(httpGet::addHeader);

        CompletableFuture<Response> futureResult = new CompletableFuture<>();

        FutureCallback<HttpResponse> futureCallback = new FutureCallback<HttpResponse>() {
            @Override
            public void completed(HttpResponse httpResponse) {
                try {
                    // collect status info
                    StatusLine statusLine = httpResponse.getStatusLine();
                    int statusCode = statusLine.getStatusCode();
                    String reasonPhrase = statusLine.getReasonPhrase();

                    // collect httpResponse headers
                    Map<String, String> headers = new HashMap<>();
                    for(Header header : httpResponse.getAllHeaders()) {
                        String headerName = header.getName();
                        String headerValue = header.getValue();
                        headers.put(headerName, headerValue);
                    }

                    // read httpResponse body
                    HttpEntity entity = httpResponse.getEntity();
                    String body = "";
                    if (entity != null) {
                        ContentType contentType = ContentType.getOrDefault(entity);
                        Charset charset = contentType.getCharset();
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();

                        entity.writeTo(stream);

                        body = stream.toString(charset.name());
                    }
                    futureResult.complete(new Response(statusCode, reasonPhrase, headers, body));
                } catch (Throwable e) {
                    futureResult.completeExceptionally(e);
                }
            }
            @Override
            public void failed(Exception e) {
                futureResult.completeExceptionally(e);
            }
            @Override
            public void cancelled() {
                futureResult.cancel(true);
            }
        };

        httpClient.execute(httpGet, futureCallback);
        return futureResult;
    }

}
