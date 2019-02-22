package de.fraunhofer.iosb.maypadbackend.util;

import com.google.common.base.Charsets;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import static com.google.common.io.Files.asCharSource;

/**
 * Utils for file and directory operations.
 *
 * @version 1.0
 */
public class FileUtil {

    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    /**
     * Check whether write and read access exists.
     *
     * @param file File to check
     * @return true if write and read access exists, else false
     */
    public static boolean hasWriteAccess(File file) {
        return hasReadAccess(file) && file.canWrite();
    }

    /**
     * Delete all files at a location.
     *
     * @param file Location for deleting recursively
     * @return true if files could be deleted, else false
     */
    public static boolean deleteAllFiles(File file) {
        try {
            FileUtils.deleteDirectory(file);
        } catch (IOException e) {
            logger.error("Can't delete files at: " + file.getAbsoluteFile());
            return false;
        }
        return true;
    }

    /**
     * Calc a SHA-256 hash (checksum) from a file.
     *
     * @param input File
     * @return Checksum of the file
     */
    public static String calcSha256(File input) {
        try (InputStream in = new FileInputStream(input)) {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] block = new byte[4096];
            int length;
            while ((length = in.read(block)) > 0) {
                digest.update(block, 0, length);
            }
            return DatatypeConverter.printHexBinary(digest.digest());
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Check whether the given file exists and the current user can read the file.
     *
     * @param file File to check
     * @return true, if the user has access to read, else false
     */
    public static boolean hasReadAccess(File file) {
        if (file == null || !file.exists()) {
            return false;
        }
        return file.canRead();
    }

    /**
     * Check whether the given file exists, the current user can read the file and whether the file is a file.
     *
     * @param file File to check
     * @return true, if the user has access to read, else false
     */
    public static boolean canReadFile(File file) {
        return hasReadAccess(file) && file.isFile();
    }

    /**
     * Get the content of a file.
     *
     * @param file File
     * @return Content of the file
     */
    public static String getFileContent(File file) {
        if (!canReadFile(file)) {
            return null;
        }
        try {
            return asCharSource(file, Charsets.UTF_8).read();
        } catch (IOException e) {
            logger.warn("Can't read file: " + file.getAbsoluteFile());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Checks whether a directory is empty.
     *
     * @param file File to the directory
     * @return true if the directory is empty, else false
     */
    public static boolean isDirectoryEmpty(File file) {
        if (file == null || !file.exists() || !file.isDirectory()) {
            return false;
        }
        return Objects.requireNonNull(file.list()).length == 0;
    }

}