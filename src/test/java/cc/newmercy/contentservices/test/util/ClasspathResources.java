package cc.newmercy.contentservices.test.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;

public final class ClasspathResources {

    private ClasspathResources() { }

    /**
     * Loads a document from the classpath. The resource is in the same package as the given class and has the same name
     * as the class plus the suffix.
     */
    public static String loadClassPathDocument(Class<?> klass, String suffix) {
        StringWriter out = new StringWriter();

        char[] buffer = new char[512];

        InputStreamReader reader = null;

        try {
            reader = new InputStreamReader(klass.getResourceAsStream(klass.getSimpleName() + suffix), "utf-8");

            int count;

            while ((count = reader.read(buffer)) >= 0){
                out.write(buffer, 0, count);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException(klass.getSimpleName() + suffix, e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                }
            }
        }

        return out.toString();
    }
}
