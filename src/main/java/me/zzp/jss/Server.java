package me.zzp.jss;

import com.sun.net.httpserver.HttpServer;
import me.zzp.jss.scope.Application;

import java.net.InetSocketAddress;

public class Server {
    public static void main(String[] args) throws Exception {
        Application application = new Application();
        HttpServer server = HttpServer.create(new InetSocketAddress(application.getPort()), 0);
        server.createContext("/", new Route(application));
        server.start();
    }
}
