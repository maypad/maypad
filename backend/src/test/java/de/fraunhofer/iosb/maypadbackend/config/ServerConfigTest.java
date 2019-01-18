package de.fraunhofer.iosb.maypadbackend.config;

import de.fraunhofer.iosb.maypadbackend.config.server.ServerConfig;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ServerConfigTest {

    @Autowired
    private ServerConfig serverConfig;

    private static final File MAYPAD_HOME = new File("target/maypadhome");

    @ClassRule
    public static final EnvironmentVariables environmentVariables = new EnvironmentVariables();

    /**
     * Setup MaypadHome for testing.
     * @throws Exception if setup fails
     */
    @BeforeClass
    public static void setupMaypadHome() throws Exception {
        if (MAYPAD_HOME.exists()) {
            for (File f : MAYPAD_HOME.listFiles()) {
                f.delete();
            }
        } else {
            MAYPAD_HOME.mkdir();
        }

        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        InputStream in = classLoader.getResourceAsStream("config.yaml");
        OutputStream out = new FileOutputStream(MAYPAD_HOME.getAbsolutePath() + "/config.yaml");
        int readBytes;
        byte[] buffer = new byte[4096];
        while ((readBytes = in.read(buffer)) > 0) {
            out.write(buffer, 0, readBytes);
        }
        in.close();
        out.close();
        environmentVariables.set("MAYPAD_HOME", MAYPAD_HOME.getAbsolutePath());
    }

    /**
     * Deletes the created MAYPAD_HOME folder.
     */
    @AfterClass
    public static void cleanupMaypadHome() {
        for (File f : MAYPAD_HOME.listFiles()) {
            f.delete();
        }
        MAYPAD_HOME.delete();
    }


    @Test
    public void testLoadProperties() {
        assertThat(serverConfig.getWebServerPort()).isEqualTo(1337);
        assertThat(serverConfig.getReloadRepositoriesSeconds()).isEqualTo(900);
        assertThat(serverConfig.isMaximumRefreshRequestsEnabled()).isEqualTo(true);
        assertThat(serverConfig.getMaximumRefreshRequestsSeconds()).isEqualTo(3600);
        assertThat(serverConfig.getMaximumRefreshRequests()).isEqualTo(100);
        assertThat(serverConfig.getLogLevel()).isEqualTo("INFO");
        assertThat(serverConfig.getRepositoryStoragePath()).isEqualTo("/home/maypad/repositories");
        assertThat(serverConfig.getDbUser()).isEqualTo("my_user");
        assertThat(serverConfig.getDbPassword()).isEqualTo("123456");
        assertThat(serverConfig.getDbDatabase()).isEqualTo("database");
        assertThat(serverConfig.getDbHost()).isEqualTo("127.0.0.1");
        assertThat(serverConfig.getDbPort()).isEqualTo(7357);
        assertThat(serverConfig.getSchedulerPoolSize()).isEqualTo(4);
        assertThat(serverConfig.getWebhookTokenLength()).isEqualTo(24);
    }


}
