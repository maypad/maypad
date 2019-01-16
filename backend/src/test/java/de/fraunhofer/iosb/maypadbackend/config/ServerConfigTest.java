package de.fraunhofer.iosb.maypadbackend.config;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ServerConfigTest {

    @Autowired
    private ServerConfig dbconfig;

    @Test
    public void testWebServerPort() {
        assert(dbconfig.getWebServerPort() == 1337);
    }

    @Test
    public void testReloadRepoSeconds() {
        assert(dbconfig.getReloadRepositoriesSeconds() == 900);
    }

    @Test
    public void testMaxRefreshEnabled() {
        assert(dbconfig.isMaximumRefreshRequestsEnabled());
    }

    @Test
    public void testMaxRefreshSeconds() {
        assert(dbconfig.getMaximumRefreshRequestsSeconds() == 3600);
    }

    @Test
    public void testMaxRefreshRequests() {
        assert(dbconfig.getMaximumRefreshRequests() == 100);
    }

    @Test
    public void testLogLevel() {
        System.out.println(dbconfig.getLogLevel());
        assert(dbconfig.getLogLevel().equals("INFO"));
    }

    @Test
    public void testRepoStoragePath() {
        assert(dbconfig.getRepositoryStoragePath().equals("/home/maypad/repositories"));
    }

    @Test
    public void testSqlUser() {
        assert(dbconfig.getDbUser().equals("my_user"));
    }

    @Test
    public void testSqlPassword() {
        assert(dbconfig.getDbPassword().equals("123456"));
    }

    @Test
    public void testSqlDatabase() {
        assert(dbconfig.getDbDatabase().equals("database"));
    }

    @Test
    public void testSqlHost() {
        assert(dbconfig.getDbHost().equals("127.0.0.1"));
    }

    @Test
    public void testSqlPort() {
        assert(dbconfig.getDbPort() == 7357);
    }


}
