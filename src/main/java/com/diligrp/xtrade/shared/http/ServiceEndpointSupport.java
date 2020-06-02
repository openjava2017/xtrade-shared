package com.diligrp.xtrade.shared.http;

import com.diligrp.xtrade.shared.exception.ServiceAccessException;
import com.diligrp.xtrade.shared.util.ObjectUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.stream.Stream;

public abstract class ServiceEndpointSupport {
    private static final int MAX_CONNECT_TIMEOUT_TIME = 10000;

    private static final int MAX_REQUEST_TIMEOUT_TIME = 20000;

    private static final String CONTENT_TYPE = "Content-Type";

    private static final String CONTENT_TYPE_JSON = "application/json;charset=UTF-8";

    private static final String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded;charset=UTF-8";

    private volatile HttpClient httpClient;

    private Object lock = new Object();

    public HttpResult send(String requestUrl, String body) {
        return send(requestUrl, null, body);
    }


    public HttpResult send(String requestUrl, HttpHeader[] headers, String body) {
        if (ObjectUtils.isEmpty(requestUrl)) {
            throw new IllegalArgumentException("Invalid http request url, url=" + requestUrl);
        }

        HttpRequest.Builder request = HttpRequest.newBuilder().uri(URI.create(requestUrl))
                .version(HttpClient.Version.HTTP_2)
                .timeout(Duration.ofMillis(MAX_REQUEST_TIMEOUT_TIME))
                .header(CONTENT_TYPE, CONTENT_TYPE_JSON)
                .POST(HttpRequest.BodyPublishers.ofString(body));
        // Wrap the HTTP headers
        if (headers != null && headers.length > 0) {
            request.headers(Arrays.stream(headers).flatMap(h -> Stream.of(h.param, h.value)).toArray(String[]::new));
        }

        return execute(request.build());
    }

    public HttpResult send(String requestUrl, HttpParam[] params) throws ServiceAccessException {
        return send(requestUrl, null, params);
    }

    public HttpResult send(String requestUrl, HttpHeader[] headers, HttpParam[] params) {
        if (ObjectUtils.isEmpty(requestUrl)) {
            throw new IllegalArgumentException("Invalid http request url, url=" + requestUrl);
        }

        HttpRequest.Builder request = HttpRequest.newBuilder().uri(URI.create(requestUrl))
                .version(HttpClient.Version.HTTP_2)
                .timeout(Duration.ofMillis(MAX_REQUEST_TIMEOUT_TIME))
                .header(CONTENT_TYPE, CONTENT_TYPE_FORM);
        // Wrap the HTTP headers
        if (headers != null && headers.length > 0) {
            String[] heads = Arrays.stream(headers).filter(h -> h != null)
                    .flatMap(h -> Stream.of(h.param, h.value)).toArray(String[]::new);
            request.headers(heads);
        }
        if (params != null && params.length > 0) {
            // [key1, value1, key2, value2] -> key1=value1&key2=value2
            String body = Arrays.stream(params).filter(p -> p != null)
                    .map(p -> "".concat(p.param).concat("=").concat(p.value))
                    .reduce((key, value) -> "".concat(key).concat("&").concat(value)).get();
            request.POST(HttpRequest.BodyPublishers.ofString(body));
        }

        return execute(request.build());
    }

    protected HttpClient getHttpClient() {
        if (httpClient == null) {
            synchronized (lock) {
                // Double check for performance purpose
                if (httpClient == null) {
                    HttpClient.Builder builder = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2)
                            // 认证，默认情况下Authenticator.getDefault()是null值，会报错
//                            .authenticator(Authenticator.getDefault())
                            // 缓存，默认情况下 CookieHandler.getDefault()是null值，会报错
//                            .cookieHandler(CookieHandler.getDefault())
                            .connectTimeout(Duration.ofMillis(MAX_CONNECT_TIMEOUT_TIME))
                            .followRedirects(HttpClient.Redirect.NEVER);
                    // Build SSL
                    Optional<SSLContext> sslContext = buildSSLContext();
                    sslContext.ifPresent(builder::sslContext);
                    Optional<SSLParameters> parameters = buildSslParameters();
                    parameters.ifPresent(builder::sslParameters);
                    // Build thread pool
                    Optional<Executor> executor = buildThreadPool();
                    executor.ifPresent(builder::executor);
                    httpClient = builder.build();
                }
            }
        }

        return httpClient;
    }

    protected Optional<SSLContext> buildSSLContext() {
        return Optional.ofNullable(null);
    }

    protected Optional<SSLParameters> buildSslParameters() {
        return Optional.ofNullable(null);
    }

    protected Optional<Executor> buildThreadPool() {
        return Optional.ofNullable(null);
    }

    private HttpResult execute(HttpRequest request) {
        try {
            HttpResponse<String> response = getHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new ServiceAccessException(1000, "Invalid http status code: " + response.statusCode());
            }

            HttpResult result = HttpResult.create();
            result.statusCode = response.statusCode();
            result.responseText = response.body();
            return result;
        } catch (IOException ioe) {
            throw new ServiceAccessException("Service access exception", ioe);
        } catch (InterruptedException iex) {
            throw new ServiceAccessException("Service access interrupted", iex);
        }
    }

    public static class HttpParam {
        public String param;
        public String value;

        private HttpParam(String param, String value) {
            this.param = param;
            this.value = value;
        }

        public static HttpParam create(String param, String value) {
            return new HttpParam(param, value);
        }
    }

    public static class HttpHeader {
        public String param;
        public String value;

        private HttpHeader(String param, String value) {
            this.param = param;
            this.value = value;
        }

        public static HttpHeader create(String param, String value) {
            return new HttpHeader(param, value);
        }
    }

    public static class HttpResult {
        public int statusCode = -1;
        public String responseText;

        public static HttpResult create() {
            return new HttpResult();
        }
    }
}
