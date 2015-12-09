package me.zzp.jss.io;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public final class Mime {
    private final static Map<String, String> mapping;

    static {
        mapping = new HashMap<>();

        Scanner scanner = new Scanner(ClassLoader.getSystemResourceAsStream("mime.properties"));
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] pair = line.split("=", 2);
            if (pair.length == 2 && !pair[0].isEmpty() && !pair[1].isEmpty()) {
                mapping.put(pair[0], pair[1]);
            }
        }
        if (!mapping.containsKey("*")) {
            mapping.put("*", "");
        }
    }

    private Mime() {
    }

    public static String get(final String name) {
        String extension = name.startsWith(".")? name.substring(1): name;
        return mapping.containsKey(extension)? mapping.get(extension): mapping.get("*");
    }
}
