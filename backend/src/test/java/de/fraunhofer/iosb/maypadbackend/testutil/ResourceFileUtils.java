package de.fraunhofer.iosb.maypadbackend.testutil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ResourceFileUtils {

    /**
     * Copies a fill from the resources to the given location.
     *
     * @param pathToFile path to the resource
     * @param output destination
     * @throws IOException if files is not present
     */
    public static void copyFileFromResources(String pathToFile, File output) throws IOException {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        InputStream in = classLoader.getResourceAsStream(pathToFile);
        OutputStream out = new FileOutputStream(output);
        int readBytes;
        byte[] buffer = new byte[4096];
        while ((readBytes = in.read(buffer)) > 0) {
            out.write(buffer, 0, readBytes);
        }
        in.close();
        out.close();
    }
}
