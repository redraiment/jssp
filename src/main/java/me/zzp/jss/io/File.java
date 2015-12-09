package me.zzp.jss.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class File {

    private final Path path;
    private final String name;
    private final List<String> extensions;

    public File(Path path) {
        this.path = path;
        name = path.getFileName().toString();
        extensions = new ArrayList<>();

        String filename = name.startsWith(".")? name.substring(1): name;
        String[] fragments = filename.split("\\.");
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
