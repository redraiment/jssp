package me.zzp.jss.scope;

import com.sun.net.httpserver.HttpExchange;

public final class Context {

    private HttpExchange exchange;

    private Application application;
    private Session session;
    private Request request;
    private Response response;
    private Page page;

    public Context() {
    }

    public Context(HttpExchange exchange) {
        this.exchange = exchange;
    }

    public Context(HttpExchange exchange, Application application) {
        this(exchange);
        this.application = application;
    }

    public HttpExchange getExchange() {
        return exchange;
    }

    public void setExchange(HttpExchange exchange) {
        this.exchange = exchange;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }
}
