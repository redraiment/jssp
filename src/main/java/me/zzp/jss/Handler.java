package me.zzp.jss;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Handler implements HttpHandler {

    private final ScriptEngineManager manager;
    private final String prefix;
    private final Map<String, String> mime;
    private String defaultMime = "";

    public Handler(String prefix) {
        this.prefix = prefix;
        manager = new ScriptEngineManager();

        mime = new HashMap<>();
        Properties defines = new Properties();
        try {
            defines.load(ClassLoader.getSystemResourceAsStream("mime.properties"));
        } catch (IOException e) {
        }
        defines.stringPropertyNames().forEach(key -> {
            if (key.equalsIgnoreCase("*")) {
                defaultMime = defines.getProperty(key);
            } else {
                mime.put(key, defines.getProperty(key));
            }
        });
    }

    private String[] getExtensions(Path path) {
        String filename = path.getFileName().toString();
        if (filename.startsWith(".")) {
            filename = filename.substring(1);
        }
        String[] fragments = filename.split("\\.");
        if (fragments.length >= 3) {
            return new String[] {fragments[fragments.length - 2], fragments[fragments.length - 1]};
        } else if (fragments.length >= 2) {
            return new String[] {fragments[fragments.length - 1], ""};
        } else {
            return new String[] {"", ""};
        }
    }

    private String getMime(String extension) {
        return mime.containsKey(extension)? mime.get(extension): defaultMime;
    }

    private byte[] eval(Path path, String language) throws IOException, ScriptException {
        ScriptEngine engine = null;
        if (language != null && !language.isEmpty()) {
            engine = manager.getEngineByName(language);
        }

        if (engine == null) {
            return Files.readAllBytes(path);
        }

        Response response = new Response();
        Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.put("response", response);
        engine.eval(new InputStreamReader(new ScriptInputStream(Files.newInputStream(path))), bindings);

        return response.getBody();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        URI uri = exchange.getRequestURI();
        String filename = uri.getPath();
        Path path = Paths.get(prefix, filename);
        if (Files.isDirectory(path)) {
            path = Paths.get(prefix, filename, "index.html");
        }
        try {
            if (Files.isRegularFile(path)) {
                String[] extensions = getExtensions(path);

                String type = getMime(extensions[0]);
                if (type.startsWith("text/")) {
                    type = type.concat("; charset=utf-8");
                }
                Headers headers = exchange.getResponseHeaders();
                headers.set("Content-Type", type);

                byte[] body = eval(path, extensions[1]);
                exchange.sendResponseHeaders(200, body.length);

                OutputStream out = exchange.getResponseBody();
                out.write(body);
                out.close();
            } else {
                exchange.sendResponseHeaders(404, 0);
            }
        } catch (ScriptException e) {
            exchange.sendResponseHeaders(500, 0);
        }
        exchange.close();
    }
}
