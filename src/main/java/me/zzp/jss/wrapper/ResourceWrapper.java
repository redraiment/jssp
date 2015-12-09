package me.zzp.jss.wrapper;

import com.sun.net.httpserver.HttpExchange;
import me.zzp.jss.io.Resource;
import me.zzp.jss.io.ScriptInputStream;
import me.zzp.jss.scope.Application;
import me.zzp.jss.scope.Context;
import me.zzp.jss.scope.Page;
import me.zzp.jss.scope.Request;
import me.zzp.jss.scope.Response;
import me.zzp.jss.scope.Session;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class ResourceWrapper implements Wrapper {

    private final ScriptEngineManager engines;

    public ResourceWrapper() {
        engines = new ScriptEngineManager();
    }

    private void execute(Resource resource, Context context) {
        ScriptEngine engine = null;
        if (resource.hasMinorExtension()) {
            engine = engines.getEngineByExtension(resource.getMajorExtension());
        }

        Application application = context.getApplication();
        Session session = context.getSession();
        Request request = context.getRequest();
        Response response = context.getResponse();
        Page page = context.getPage();

        if (engine == null) {
            response.write(resource.getBody());
        } else {
            Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("application", application);
            bindings.put("session", session);
            bindings.put("request", request);
            bindings.put("response", response);
            bindings.put("page", page);
            try {
                engine.eval(new InputStreamReader(new ScriptInputStream(resource.getInputStream())), bindings);
            } catch (ScriptException e) {
                response.setStatus(500);
                response.setContent(e.getMessage());
            }
        }
    }

    @Override
    public void wrap(Context context) throws Exception {
        HttpExchange exchange = context.getExchange();
        Application application = context.getApplication();
        Request request = context.getRequest();
        Response response = context.getResponse();

        Path path = Paths.get(application.getRoot(), request.getPath());
        Resource resource = Resource.find(path, application.getIndex());
        if (resource.exists()) {
            response.setContentType(resource.getMimeType());
            execute(resource, context);
        } else {
            exchange.sendResponseHeaders(404, 0);
        }
    }
}
