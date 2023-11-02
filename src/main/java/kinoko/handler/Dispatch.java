package kinoko.handler;

import kinoko.server.Client;
import kinoko.server.InHeader;
import kinoko.server.InPacket;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

public final class Dispatch {
    public static final Map<InHeader, Method> HANDLER_MAP = new HashMap<>();

    public static Method getHandler(InHeader header) {
        return HANDLER_MAP.get(header);
    }

    public static void registerHandlers() {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        String packageName = Handler.class.getPackageName();

        URL packageUrl = classLoader.getResource(packageName.replace(".", "/"));
        Objects.requireNonNull(packageUrl, String.format("Could not find package : \"%s\"", packageName));

        File packageDirectory = new File(packageUrl.getFile());
        for (String className : getAllClassNames(packageName, packageDirectory)) {
            final Class<?> clazz;
            try {
                clazz = Class.forName(className);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            for (Method method : clazz.getDeclaredMethods()) {
                if (!method.isAnnotationPresent(Handler.class)) {
                    continue;
                }
                if (method.getParameterCount() != 2 || method.getParameterTypes()[0] != Client.class || method.getParameterTypes()[1] != InPacket.class) {
                    throw new RuntimeException(String.format("Incorrect parameters for handler method \"%s\"", method.getName()));
                }
                Handler annotation = method.getAnnotation(Handler.class);
                for (InHeader header : annotation.value()) {
                    if (HANDLER_MAP.containsKey(header)) {
                        throw new RuntimeException(String.format("Multiple handlers found for InHeader \"%s\"", header.name()));
                    }
                    HANDLER_MAP.put(header, method);
                }
            }
        }
    }

    private static List<String> getAllClassNames(String prefix, File directory) {
        List<String> classNames = new ArrayList<>();
        File[] files = directory.listFiles();
        Objects.requireNonNull(files, String.format("Not a directory : \"%s\"", directory.getName()));
        for (File file : files) {
            String fileName = String.format("%s.%s", prefix, file.getName());
            if (file.isDirectory()) {
                classNames.addAll(getAllClassNames(fileName, file));
            } else {
                classNames.add(fileName.replace(".class", ""));
            }
        }
        return classNames;
    }
}
