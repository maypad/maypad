package de.fraunhofer.iosb.maypadbackend.services.reporefresh;

import de.fraunhofer.iosb.maypadbackend.model.Project;
import de.fraunhofer.iosb.maypadbackend.model.serviceaccount.KeyServiceAccount;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * Handles the file for a SSH-key.
 *
 * @version 1.0
 */
@AllArgsConstructor
public class KeyFileManager {

    private File projectRootDir;
    private Project project;
    private static final Logger logger = LoggerFactory.getLogger(KeyFileManager.class);

    /**
     * Create a SSH pem File.
     *
     * @return SSH File with key
     */
    protected File getSshFile() {
        File keyDir = new File(projectRootDir.getAbsolutePath() + File.separator + "keys" + File.separator);
        if (!keyDir.isDirectory() && !keyDir.exists()) {
            logger.info("Folder for keys doesn't exists. So, create it.");
            if (!keyDir.mkdirs()) {
                logger.error("Can't create folder " + keyDir.getAbsolutePath());
                return null;
            }
        }
        File keyFile = new File(keyDir.getAbsolutePath() + File.separator + project.getId());
        if (keyFile.exists()) {
            return keyFile;
        }

        //Create new file for the key
        if (!(project.getServiceAccount() instanceof KeyServiceAccount)) {
            logger.warn("Project with id " + project.getId() + " hasn't an serviceaccount with a key.");
            return null;
        }
        KeyServiceAccount serviceAccount = (KeyServiceAccount) project.getServiceAccount();
        //empty line after key is needed for API, if rsa key is only in one line.
        // So: mind. 2 lines for the key is needed (or one line with a following empty line)
        List<String> lines = Arrays.asList("-----BEGIN RSA PRIVATE KEY-----", serviceAccount.getSshKey(), "",
                "-----END RSA PRIVATE KEY-----");
        Path file = Paths.get(keyFile.getAbsolutePath());
        try {
            Files.write(file, lines, Charset.forName("UTF-8"));
        } catch (IOException e) {
            logger.error("Can't write key file for project with id " + project.getId());
            return null;
        }
        return keyFile;
    }

    /**
     * Delete the SSH key file, if exists.
     */
    protected void deleteSshFile() {
        deleteSshFile(projectRootDir, project.getId());
    }

    /**
     * Delete the SSH key file, if exists.
     *
     * @param projectRootDir File to the project root dir
     * @param id             if of the project
     */
    protected static void deleteSshFile(File projectRootDir, int id) {
        File keyFile = new File(projectRootDir.getAbsolutePath() + File.separator + "keys" + File.separator + id);
        if (keyFile.exists()) {
            if (!keyFile.delete()) {
                logger.error("Can't delete key file for project with id " + id);
            }
        }
    }


}
