package de.fraunhofer.iosb.maypadbackend.config;

import de.fraunhofer.iosb.maypadbackend.config.server.ServerConfig;
import de.fraunhofer.iosb.maypadbackend.config.server.ServerConfigImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ServerConfigTest {

    private ServerConfig serverConfig;

    @Before
    public void setup() {
        serverConfig = new ServerConfigImpl(new File("src/test/resources/maypad.yml"));
    }

    @Test
    public void testServerConfig() {
        assertEquals(serverConfig.getProjectName(), "cool_name");
        assertEquals(serverConfig.getProjectDescription(), "lorem ipsum dolor sit amet");
        assertEquals(serverConfig.getAddAllBranches(), true);
        assertEquals(serverConfig.getBranchProperties(), null);
    }


}
