package org.particleframework.http;

import org.particleframework.http.cookie.Cookies;
import org.particleframework.http.util.HttpUtil;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * <p>Common interface for HTTP request implementations</p>
 *
 * @author Graeme Rocher
 * @since 1.0
 */
public interface HttpRequest<B> extends HttpMessage<B> {

    /**
     * @return The {@link Cookies} instance
     */
    Cookies getCookies();
    /**
     * @return The HTTP parameters contained with the URI query string
     */
    HttpParameters getParameters();
    /**
     * @return The request method
     */
    HttpMethod getMethod();

    /**
     * @return The full request URI
     */
    URI getUri();

    /**
     * @return Get the path without any parameters
     */
    URI getPath();

    /**
     * @return Obtain the remote address
     */
    InetSocketAddress getRemoteAddress();

    /**
     * @return Obtain the server address
     */
    InetSocketAddress getServerAddress();
    /**
     * @return The server host name
     */
    String getServerName();

    /**
     * @return Is the request an HTTPS request
     */
    boolean isSecure();

    @Override
    default Optional<Locale> getLocale() {
        return getHeaders().findFirst(HttpHeaders.ACCEPT_LANGUAGE)
                .map((text)-> {
                    int len = text.length();
                    if(len == 0 || (len == 1 && text.charAt(0) == '*')) {
                        return Locale.getDefault().toLanguageTag();
                    }
                    if(text.indexOf(';')>-1) {
                        text = text.split(";")[0];
                    }
                    if(text.indexOf(',')>-1) {
                        text = text.split(",")[0];
                    }
                    return text;
                })
                .map(Locale::forLanguageTag);
    }

    /**
     * @return The request character encoding. Defaults to {@link StandardCharsets#UTF_8}
     */
    default Charset getCharacterEncoding() {
        return HttpUtil.resolveCharset(this).orElse(StandardCharsets.UTF_8);
    }


    /**
     * Return a {@link MutableHttpRequest} that executes an {@link HttpMethod#GET} request for the given URI
     *
     * @param uri The URI
     * @return The {@link MutableHttpRequest} instance
     * @see HttpRequestFactory
     */
    static <T> MutableHttpRequest<T> get(String uri) {
        HttpRequestFactory factory = HttpRequestFactory.INSTANCE.orElseThrow(() ->
                new IllegalStateException("No HTTP client implementation found on classpath")
        );

        return factory.get(uri);
    }

    /**
     * Return a {@link MutableHttpRequest} that executes an {@link HttpMethod#GET} request for the given URI
     *
     * @param uri The URI
     * @param body The body of the request
     * @return The {@link MutableHttpRequest} instance
     * @see HttpRequestFactory
     */
    static <T> MutableHttpRequest<T> post(String uri, T body) {
        Objects.requireNonNull(body, "Argument [body] cannot be null");
        HttpRequestFactory factory = HttpRequestFactory.INSTANCE.orElseThrow(() ->
                new IllegalStateException("No HTTP client implementation found on classpath")
        );

        return factory.post(uri, body);
    }
}