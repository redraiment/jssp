package me.zzp.jss;

import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;

public class Application {
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", new Handler(args.length == 0? ".": args[0]));
        server.start();
    }
}
