package me.zzp.jss.wrapper;

import com.sun.net.httpserver.HttpExchange;
import me.zzp.jss.io.File;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class ResourceWrapper implements Wrapper {

    private final ScriptEngineManager engines;

    public ResourceWrapper() {
        engines = new ScriptEngineManager();
    }

    private void execute(File file, Context context) {
        ScriptEngine engine = null;
        if (file.hasMinorExtension()) {
            engine = engines.getEngineByName(file.getMajorExtension());
        }

        Application application = context.getApplication();
        Session session = context.getSession();
        Request request = context.getRequest();
        Response response = context.getResponse();
        Page page = context.getPage();

        if (engine == null) {
            response.write(file.getBody());
        } else {
            Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("application", application);
            bindings.put("session", session);
            bindings.put("request", request);
            bindings.put("response", response);
            bindings.put("page", page);
            try {
                engine.eval(new InputStreamReader(new ScriptInputStream(file.getInputStream())), bindings);
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
        if (Files.isDirectory(path)) {
            path = Paths.get(application.getRoot(), request.getPath(), application.getIndex());
        }
        File file = new File(path);
        if (file.exists()) {
            response.setContentType(file.getMimeType());
            execute(file, context);
        } else {
            exchange.sendResponseHeaders(404, 0);
        }
    }
}
