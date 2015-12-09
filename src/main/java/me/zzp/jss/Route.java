package me.zzp.jss;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import me.zzp.jss.scope.Application;
import me.zzp.jss.scope.Context;
import me.zzp.jss.scope.Response;
import me.zzp.jss.wrapper.ResourceWrapper;
import me.zzp.jss.wrapper.ScopeWrapper;
import me.zzp.jss.wrapper.Wrapper;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

public class Route implements HttpHandler {

    private final Application application;
    private final List<Wrapper> wrappers;

    public Route(Application application) {
        this.application = application;
        wrappers = Arrays.asList(new ScopeWrapper(), new ResourceWrapper());
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Context context = new Context(exchange, application);
        try {
            for (Wrapper wrapper : wrappers) {
                wrapper.wrap(context);
            }

            Response response = context.getResponse();
            exchange.sendResponseHeaders(response.getStatus(), response.getContentLength());

            OutputStream body = exchange.getResponseBody();
            body.write(response.getContent());
            body.close();
        } catch (Exception e) {
            exchange.sendResponseHeaders(500, 0);
        } finally {
            exchange.close();
        }
    }
}
