package de.fraunhofer.iosb.maypadbackend.config;

import de.fraunhofer.iosb.maypadbackend.config.server.ServerConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ServerConfigTest {

    @Autowired
    private ServerConfig dbconfig;

    @Test
    public void testWebServerPort() {
        assertThat(dbconfig.getWebServerPort()).isEqualTo(1337);
    }

    @Test
    public void testReloadRepoSeconds() {
        assertThat(dbconfig.getReloadRepositoriesSeconds()).isEqualTo(900);
    }

    @Test
    public void testMaxRefreshEnabled()
    {
        assertThat(dbconfig.isMaximumRefreshRequestsEnabled()).isEqualTo(true);
    }

    @Test
    public void testMaxRefreshSeconds()
    {
        assertThat(dbconfig.getMaximumRefreshRequestsSeconds()).isEqualTo(3600);
    }

    @Test
    public void testMaxRefreshRequests() {
        assertThat(dbconfig.getMaximumRefreshRequests()).isEqualTo(100);
    }

    @Test
    public void testLogLevel() {
        assertThat(dbconfig.getLogLevel()).isEqualTo("INFO");
    }

    @Test
    public void testRepoStoragePath() {
        assertThat(dbconfig.getRepositoryStoragePath()).isEqualTo("/home/maypad/repositories");
    }

    @Test
    public void testSqlUser() {
        assertThat(dbconfig.getDbUser()).isEqualTo("my_user");
    }

    @Test
    public void testSqlPassword() {
        assertThat(dbconfig.getDbPassword()).isEqualTo("123456");
    }

    @Test
    public void testSqlDatabase() {
        assertThat(dbconfig.getDbDatabase()).isEqualTo("database");
    }

    @Test
    public void testSqlHost() {
        assertThat(dbconfig.getDbHost()).isEqualTo("127.0.0.1");
    }

    @Test
    public void testSqlPort() {
        assertThat(dbconfig.getDbPort()).isEqualTo(7357);
    }


}
