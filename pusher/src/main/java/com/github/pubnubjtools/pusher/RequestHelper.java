package com.github.pubnubjtools.pusher;

import com.github.pubnubjtools.legacy.PubnubCrypto;
import com.github.pubnubjtools.pusher.model.Message;
import com.github.pubnubjtools.pusher.model.Request;

import java.util.HashMap;
import java.util.Map;

public class RequestHelper {

    public static final RequestHelper INSTANCE = new RequestHelper();

    private RequestHelper() {}

    public Request createRequest(Message message, PubnubConfig pubnubConfig) {
        // merge headers
        Map<String, String> requestHeaders = combine(pubnubConfig.getHeaders(), message.getHeaders());

        // build path
        String path = message.getPath();

        StringBuilder requestUrl = new StringBuilder(pubnubConfig.getPubnubUrl());
        requestUrl.append(path);

        // build query string
        Map<String, String> queryParams = combine(pubnubConfig.getQueryParams(), message.getQueryParameters());
        if (!queryParams.isEmpty()) {
            requestUrl.append('?');
            boolean first = true;
            for (Map.Entry<String, String> queryParamEntry : queryParams.entrySet()) {
                if (!first) {
                    requestUrl.append('&');
                }
                first = false;

                String queryParamName = queryParamEntry.getKey();
                requestUrl.append(queryParamName);

                String queryParamValue = queryParamEntry.getValue();
                if (queryParamValue != null) {
                    requestUrl.append('=');
                    String encodedQueryParamValue = PubnubCrypto.urlEncode(queryParamValue);
                    requestUrl.append(encodedQueryParamValue);
                }
            }
        }

        return new Request(requestUrl.toString(), requestHeaders);
    }

    private static Map<String, String> combine(Map<String, String> map1, Map<String, String> map2) {
        if (map1.isEmpty()) {
            return map2;
        }
        if (map2.isEmpty()) {
            return map1;
        }
        Map<String, String> result = new HashMap<>(map1);
        result.putAll(map2);
        return result;
    }

}
