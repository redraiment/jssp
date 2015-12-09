package me.zzp.jss.scope;

import com.sun.net.httpserver.HttpExchange;

import java.net.URI;

public final class Request {
    // headers
    // cookies
    // parameters
    private String method;
    private String path;
    private String query;

    public Request(Context context) {
        HttpExchange exchange = context.getExchange();
        method = exchange.getRequestMethod();
        URI uri = exchange.getRequestURI();
        path = uri.getPath();
        query = uri.getQuery();
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
