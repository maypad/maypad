package de.fraunhofer.iosb.maypadbackend.config;

import de.fraunhofer.iosb.maypadbackend.config.server.ServerConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ServerConfigTest {


    @Autowired
    private ServerConfig serverConfig;

    @Test
    public void testServerConfig() {
        assertEquals(serverConfig.getProjectName(), "cool_name");
        assertEquals(serverConfig.getProjectDescription(), "lorem ipsum dolor sit amet");
        assertEquals(serverConfig.getAddAllBranches(), true);
        assertEquals(serverConfig.getBranchProperties(), null);
    }


}
