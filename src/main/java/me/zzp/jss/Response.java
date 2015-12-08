package me.zzp.jss;

import java.io.StringWriter;

public final class Response {
    private final StringWriter stdout;

    public Response() {
        stdout = new StringWriter();
    }

    public void print(String... messages) {
        for (String message :messages) {
            stdout.write(message);
        }
    }

    public void println(String... messages) {
        print(messages);
        print("\n");
    }

    public byte[] getBody() {
        return stdout.toString().getBytes();
    }
}
