package de.fraunhofer.iosb.maypadbackend.testutil;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class EnvironmentUtils {

    private static Map<String, String> ogEnv = null;

    /**
     * Overwrites the environment for the running process.
     *
     * @param newenv the new environment
     * @throws Exception if failed to set new environment
     */
    public static void setEnv(Map<String, String> newenv) throws Exception {
        if (ogEnv == null) {
            ogEnv = System.getenv().entrySet().stream()
                    .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
        }
        Class[] classes = Collections.class.getDeclaredClasses();
        Map<String, String> env = System.getenv();
        for (Class cl : classes) {
            if ("java.util.Collections$UnmodifiableMap".equals(cl.getName())) {
                Field field = cl.getDeclaredField("m");
                field.setAccessible(true);
                Object obj = field.get(env);
                Map<String, String> map = (Map<String, String>) obj;
                map.clear();
                map.putAll(newenv);
            }
        }
        Map<String, String> test = System.getenv();
    }

    /**
     * Recover the original environment. Does nothing when the environment isn't modified.
     *
     * @throws Exception if failed to set new environment
     */
    public static void recoverEnv() throws Exception {
        if (ogEnv != null) {
            setEnv(ogEnv);
        }
    }
}
