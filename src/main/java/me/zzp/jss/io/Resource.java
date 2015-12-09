package me.zzp.jss.io;

import javax.script.ScriptEngineFactory;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;

public final class Resource {

    private final static Set<String> scripts;

    static {
        scripts = new HashSet<>();
        ServiceLoader.load(ScriptEngineFactory.class).forEach(factory -> {
            scripts.addAll(factory.getExtensions());
        });
    }

    public static Resource find(Path path, String defaultFileName) {
        if (Files.isDirectory(path)) {
            path = path.resolve(defaultFileName);
        }
        if (!Files.exists(path)) {
            Path root = path.getParent();
            String filename = path.getFileName().toString();
            for (String extension : scripts) {
                Path guess = root.resolve(String.format("%s.%s", filename, extension));
                if (Files.exists(guess)) {
                    path = guess;
                    break;
                }
            }
        }
        return new Resource(path);
    }

    private final Path path;
    private final String name;
    private final List<String> extensions;

    public Resource(Path path) {
        this.path = path;
        name = path.getFileName().toString();
        extensions = new ArrayList<>();

        String filename = name.startsWith(".")? name.substring(1): name;
        String[] fragments = filename.toLowerCase().split("\\.");
        for (int i = 1; i < fragments.length; i++) {
            extensions.add(fragments[i]);
        }
    }

    public boolean exists() {
        return Files.exists(path);
    }

    /* extensions  */

    public List<String> getExtensions() {
        return extensions;
    }

    public String getExtension() {
        return extensions.isEmpty()? "": extensions.get(0);
    }

    public String getMajorExtension() {
        return extensions.isEmpty()? "": extensions.get(extensions.size() - 1);
    }

    public boolean hasMinorExtension() {
        return extensions.size() > 1;
    }

    public String getMinorExtension() {
        return hasMinorExtension()? extensions.get(extensions.size() - 2): "";
    }

    public String getMimeType() {
        return Mime.get(getExtension());
    }

    /* io */

    public InputStream getInputStream() {
        try {
            return Files.newInputStream(path);
        } catch (IOException e) {
            return null;
        }
    }

    public byte[] getBody() {
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            return new byte[0];
        }
    }
}
