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
    public void testLoadProperties() {
        assertThat(dbconfig.getWebServerPort()).isEqualTo(1337);
        assertThat(dbconfig.getReloadRepositoriesSeconds()).isEqualTo(900);
        assertThat(dbconfig.isMaximumRefreshRequestsEnabled()).isEqualTo(true);
        assertThat(dbconfig.getMaximumRefreshRequestsSeconds()).isEqualTo(3600);
        assertThat(dbconfig.getMaximumRefreshRequests()).isEqualTo(100);
        assertThat(dbconfig.getLogLevel()).isEqualTo("INFO");
        assertThat(dbconfig.getRepositoryStoragePath()).isEqualTo("/home/maypad/repositories");
        assertThat(dbconfig.getDbUser()).isEqualTo("my_user");
        assertThat(dbconfig.getDbPassword()).isEqualTo("123456");
        assertThat(dbconfig.getDbDatabase()).isEqualTo("database");
        assertThat(dbconfig.getDbHost()).isEqualTo("127.0.0.1");
        assertThat(dbconfig.getDbPort()).isEqualTo(7357);
    }


}
