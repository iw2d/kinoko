package kinoko.util;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class ClassScanner {
    public static Set<Class<?>> getClasses(String packageName) {
        Set<Class<?>> classes = new HashSet<>();
        String path = packageName.replace('.', '/');
        try {
            URL resource = Thread.currentThread().getContextClassLoader().getResource(path);
            if (resource == null) return classes;

            String protocol = resource.getProtocol();

            if ("file".equals(protocol)) {
                // Running from IDE/filesystem
                File dir = new File(resource.toURI());
                if (!dir.exists()) return classes;
                for (File file : Objects.requireNonNull(dir.listFiles())) {
                    if (file.isFile() && file.getName().endsWith(".class")) {
                        String className = packageName + "." + file.getName().replace(".class", "");
                        classes.add(Class.forName(className));
                    }
                }
            } else if ("jar".equals(protocol)) {
                // Running from JAR file (start.bat)
                JarURLConnection connection = (JarURLConnection) resource.openConnection();
                JarFile jarFile = connection.getJarFile();
                Enumeration<JarEntry> entries = jarFile.entries();
                String pathPrefix = packageName.replace('.', '/') + "/";

                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String name = entry.getName();

                    // Check if it's in our package, is a class file, and not a subpackage
                    if (name.startsWith(pathPrefix) && name.endsWith(".class")) {
                        String relativePath = name.substring(pathPrefix.length());
                        // Only include classes directly in this package (not subpackages)
                        if (!relativePath.contains("/") && !relativePath.contains("$")) {
                            String className = name.replace('/', '.').replace(".class", "");
                            classes.add(Class.forName(className));
                        }
                    }
                }
                jarFile.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classes;
    }
}