package com.neure.agent.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.neure.agent.model.DefaultResponse;
import com.neure.agent.utils.JacksonUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * HttpRequestClient
 *
 * @author tc
 * @date 2024-02-25 15:28
 */
@Slf4j
public class HttpRequestClient {

    private static OkHttpClient httpClient = new OkHttpClient.Builder().build();


    public static <T> DefaultResponse<T> sendGet(String url) {
        return sendGet(url, null, null);
    }

    // 发送GET请求
    public static <T> DefaultResponse<T> sendGet(String url, Map<String, String> urlParams, Map<String, String> headersMap) {
        url = buildURLWithParams(url, urlParams);
        try {
            Request request = new Request.Builder()
                    // 标识为 GET 请求
                    .get()
                    // 设置请求路径
                    .url(url)
                    .headers(mapToHeaders(headersMap))
                    .build();
            Call call = httpClient.newCall(request);
            Response response = call.execute();
            return analysisResponse(response);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return DefaultResponse.Error();

    }

    public static <T> DefaultResponse<T> sendPost(String url, Object body) {
        return sendPost(url, body, null, null);
    }

    // 发送POST请求
    public static <T> DefaultResponse<T> sendPost(String url, Object body, Map<String, String> urlParams, Map<String, String> headersMap) {
        try {
            url = buildURLWithParams(url, urlParams);
            MediaType mediaType = MediaType.parse("application/json; charset=UTF-8");
            RequestBody requestBody = RequestBody.create(JacksonUtils.ObjectToJsonStr(body), mediaType);

            Request request = new Request.Builder()
                    // 标识为 POST 请求
                    .post(requestBody)
                    // 设置请求路径
                    .url(url)
                    .headers(mapToHeaders(headersMap))
                    .build();
            Call call = httpClient.newCall(request);
            Response response = call.execute();
            return analysisResponse(response);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return DefaultResponse.Error();
    }

    public static <T> DefaultResponse<T> sendPut(String url, Object body) {
        return sendPut(url, body, null, null);
    }

    // 发送POST请求
    public static <T> DefaultResponse<T> sendPut(String url, Object body, Map<String, String> urlParams, Map<String, String> headersMap) {
        try {
            url = buildURLWithParams(url, urlParams);
            MediaType mediaType = MediaType.parse("application/json; charset=UTF-8");
            RequestBody requestBody = RequestBody.create(JacksonUtils.ObjectToJsonStr(body), mediaType);
            Request request = new Request.Builder()
                    // 标识为 PUT 请求
                    .put(requestBody)
                    // 设置请求路径
                    .url(url)
                    .headers(mapToHeaders(headersMap))
                    .build();
            Call call = httpClient.newCall(request);
            Response response = call.execute();
            return analysisResponse(response);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return DefaultResponse.Error();
    }

    public static <T> DefaultResponse<T> sendDelete(String url, Object body) {
        return sendDelete(url, body, null, null);
    }

    public static <T> DefaultResponse<T> sendDelete(String url) {
        return sendDelete(url, null, null, null);
    }

    // 发送POST请求
    public static <T> DefaultResponse<T> sendDelete(String url, Object body, Map<String, String> urlParams, Map<String, String> headersMap) {
        try {
            url = buildURLWithParams(url, urlParams);
            MediaType mediaType = MediaType.parse("application/json; charset=UTF-8");
            RequestBody requestBody = RequestBody.create(JacksonUtils.ObjectToJsonStr(body), mediaType);
            Request request = new Request.Builder()
                    // 标识为 PUT 请求
                    .delete(requestBody)
                    .headers(mapToHeaders(headersMap))
                    // 设置请求路径
                    .url(url)
                    .build();
            Call call = httpClient.newCall(request);
            Response response = call.execute();
            return analysisResponse(response);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return DefaultResponse.Error();
    }

    public static String buildURLWithParams(String baseurl, Map<String, String> params) {
        if (params == null) {
            return baseurl;
        }
        StringBuilder urlWithParams = new StringBuilder(baseurl);
        if (!params.isEmpty()) {
            urlWithParams.append("?");
            boolean first = true;
            for (Map.Entry<String, String> param : params.entrySet()) {
                if (first) {
                    first = false;
                } else {
                    urlWithParams.append("&");
                }
                urlWithParams.append(URLEncoder.encode(param.getKey(), StandardCharsets.UTF_8))
                        .append("=")
                        .append(URLEncoder.encode(param.getValue(), StandardCharsets.UTF_8));
            }
        }
        return urlWithParams.toString();
    }

    public static Headers mapToHeaders(Map<String, String> headersMap) {
        Headers.Builder headersBuilder = new Headers.Builder();
        if (headersMap == null || headersMap.isEmpty()) {
            return headersBuilder.build();
        }
        for (Map.Entry<String, String> entry : headersMap.entrySet()) {
            headersBuilder.add(entry.getKey(), entry.getValue());
        }
        return headersBuilder.build();
    }

    @NotNull
    private static <T> DefaultResponse<T> analysisResponse(String responseStr) {

        try {
            DefaultResponse<T> defaultResponse = JacksonUtils.StrToObject(responseStr, new TypeReference<DefaultResponse<T>>() {
            });
            if (defaultResponse.getCode() != 200) {
                log.error(defaultResponse.getMessage());
                return DefaultResponse.Error();
            }
            return defaultResponse;
        } catch (JsonProcessingException e) {
            log.error("response is [ " + responseStr + " ] err:" + e.getMessage());
            return DefaultResponse.Error();
        }
    }

    @NotNull
    private static <T> DefaultResponse<T> analysisResponse(Response response) {
        if (response == null ){
            return DefaultResponse.Error();
        }
        if ( response.code() != 200){
            log.error(response.message());
            return DefaultResponse.buildError(response.message());
        }
        String body = null;
        try {
            body = response.body().string();
        } catch (IOException e) {
            log.error(e.getMessage());
            return DefaultResponse.Error();
        }
        return analysisResponse(body);
    }

}
