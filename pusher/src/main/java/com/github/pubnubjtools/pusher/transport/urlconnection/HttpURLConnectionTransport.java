package com.github.pubnubjtools.pusher.transport.urlconnection;

import com.github.pubnubjtools.pusher.model.Request;
import com.github.pubnubjtools.pusher.model.Response;
import com.github.pubnubjtools.pusher.transport.Transport;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

public class HttpURLConnectionTransport implements Transport {

    private final int requestTimeout;
    private final int connectionTimeout;

    public HttpURLConnectionTransport(int requestTimeout, int connectionTimeout) {
        this.requestTimeout = requestTimeout;
        this.connectionTimeout = connectionTimeout;
    }

    @Override
    public Response executeHttpGetRequest(Request request) throws IOException {
        URL url = new URL(request.getUrl());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        request.getHeaders().forEach((key, value) -> connection.addRequestProperty(key, value));

        connection.setReadTimeout(requestTimeout);
        connection.setConnectTimeout(connectionTimeout);

        int responseCode = connection.getResponseCode();
        String responsePhrase = connection.getResponseMessage();

        Map<String, String> headers = new HashMap<>();
        Map<String, List<String>> rawHeaders = connection.getHeaderFields();
        for (Map.Entry<String, List<String>> entry : rawHeaders.entrySet()) {
            String headerName = entry.getKey();
            List<String> headerValues = entry.getValue();
            if (headerValues == null || headerValues.isEmpty()) {
                headers.put(headerName, null);
            } else {
                String headerValue = headerValues.stream().collect(Collectors.joining(","));
                headers.put(headerName, headerValue);
            }
        }

        InputStream is = null;
        try {
            String encoding = connection.getContentEncoding();
            if (encoding == null || !encoding.equals("gzip")) {
                try {
                    is = connection.getInputStream();
                } catch (IOException e) {
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        throw new IOException("Response status 200 but unable to read response " + e.getMessage());
                    }
                    is = connection.getErrorStream();
                }
            } else {
                try {
                    is = new GZIPInputStream(connection.getInputStream());
                } catch (IOException e) {
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        throw new IOException("Response status 200 but unable to read response " + e.getMessage());
                    }
                    is = connection.getErrorStream();
                }
            }

            String responseBody = readInput(is);
            return new Response(responseCode, responsePhrase, headers, responseBody);
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private static String readInput(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte bytes[] = new byte[1024];

        int n = in.read(bytes);

        while (n != -1) {
            out.write(bytes, 0, n);
            n = in.read(bytes);
        }

        return out.toString();
    }

}
