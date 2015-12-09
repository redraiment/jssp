package me.zzp.jss.scope;

public class Application {

    /**
     * 服务器端口，默认 8000
     */
    private final int port;

    /**
     * 根目录，默认当前目录。
     */
    private final String root;

    /**
     * 文件名，默认 index.html。
     */
    private final String index;

    /**
     * 字符编码，默认 utf-8。
     */
    private final String encoding;

    public Application() {
        port = Integer.getInteger("port", 8000);
        root = System.getProperty("root", ".");
        index = System.getProperty("index", "index.html");
        encoding = System.getProperty("encoding", "utf-8");
    }

    public int getPort() {
        return port;
    }

    public String getRoot() {
        return root;
    }

    public String getIndex() {
        return index;
    }

    public String getEncoding() {
        return encoding;
    }
}
