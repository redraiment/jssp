package me.zzp.jss.scope;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public final class Response {
    private final Context context;

    private final ByteArrayOutputStream stdout;

    private int status = 200;

    public Response(Context context) {
        this.context = context;
        stdout = new ByteArrayOutputStream();
    }

    public void write(byte[] values) {
        try {
            stdout.write(values);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void print(String... messages) {
        for (String message :messages) {
            write(message.getBytes());
        }
    }

    public void println(String... messages) {
        print(messages);
        print("\n");
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getContentLength() {
        return stdout.toString().getBytes().length;
    }

    public void setContentType(final String mime) {
        Application application = context.getApplication();
        HttpExchange exchange = context.getExchange();
        String type = mime.startsWith("text/")? mime.concat("; charset=").concat(application.getEncoding()): mime;
        Headers headers = exchange.getResponseHeaders();
        headers.set("Content-Type", type);
    }

    public void setContent(String message) {
        stdout.reset();
        print(message);
    }

    public byte[] getContent() {
        return stdout.toByteArray();
    }
}
