package de.fraunhofer.iosb.maypadbackend.config;

import de.fraunhofer.iosb.maypadbackend.config.server.ServerConfig;
import de.fraunhofer.iosb.maypadbackend.testutil.EnvironmentUtils;
import de.fraunhofer.iosb.maypadbackend.testutil.ResourceFileUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(ServerConfigTest.SpringRunnerTestConfig.class)
@SpringBootTest
public class ServerConfigTest  {

    @Autowired
    private ServerConfig serverConfig;

    /**
     * Recover environment.
     *
     * @throws Exception if cleanup fails
     */
    @AfterClass
    public static void cleanupEnv() throws Exception {
        SpringRunnerTestConfig.cleanup();
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
        assertThat(serverConfig.getDomain()).isEqualTo("maypad.de");
    }


    public static final class SpringRunnerTestConfig extends SpringJUnit4ClassRunner {

        private static File maypadHome;

        private static void setupMaypadHome() throws Exception {
            maypadHome = Files.createTempDirectory("maypad_home").toFile();

            File frontend = new File(maypadHome.getAbsolutePath() + "/frontend/index.html");
            frontend.mkdirs();
            frontend.createNewFile();
            File config = new File(maypadHome.getAbsolutePath() + "/config.yaml");
            ResourceFileUtils.copyFileFromResources("config.yaml", config);
            Map<String, String> env = new HashMap(System.getenv());
            env.put("MAYPAD_HOME", maypadHome.getAbsolutePath());
            EnvironmentUtils.setEnv(env);
        }

        private static void cleanup() throws Exception {
            FileUtils.deleteDirectory(maypadHome);
            EnvironmentUtils.recoverEnv();
        }

        public SpringRunnerTestConfig(Class<?> clazz) throws Exception {
            super(clazz);
            setupMaypadHome();
        }

    }
}
