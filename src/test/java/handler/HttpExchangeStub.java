package handler;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;


public class HttpExchangeStub extends HttpExchange  {
    String method;
    URI uri;
    Headers headers;
    OutputStream outputStream;
    int responseCode = 0;

    public HttpExchangeStub(String method, String uri) {
        this.method = method;
        this.uri = URI.create(uri);
        List<String> list = new ArrayList<>();
        this.headers = new Headers();
        this.headers.put("Content-Type", list);
        this.outputStream = new ByteArrayOutputStream();
    }

    @Override
    public Headers getRequestHeaders() {
        return null;
    }

    @Override
    public Headers getResponseHeaders() {
        return this.headers;
    }

    @Override
    public URI getRequestURI() {
        return this.uri;
    }

    @Override
    public String getRequestMethod() {
        return this.method;
    }

    @Override
    public HttpContext getHttpContext() {
        return null;
    }

    @Override
    public void close() {

    }

    @Override
    public InputStream getRequestBody() {
        return null;
    }

    @Override
    public OutputStream getResponseBody() {
        return this.outputStream;
    }

    @Override
    public void sendResponseHeaders(int rCode, long responseLength) throws IOException {
        this.responseCode = rCode;
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return null;
    }

    @Override
    public int getResponseCode() {
        return this.responseCode;
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return null;
    }

    @Override
    public String getProtocol() {
        return null;
    }

    @Override
    public Object getAttribute(String name) {
        return null;
    }

    @Override
    public void setAttribute(String name, Object value) {

    }

    @Override
    public void setStreams(InputStream i, OutputStream o) {

    }

    @Override
    public HttpPrincipal getPrincipal() {
        return null;
    }
}
