package de.fraunhofer.iosb.maypadbackend.config;

import de.fraunhofer.iosb.maypadbackend.config.project.ProjectConfig;
import de.fraunhofer.iosb.maypadbackend.config.project.ProjectConfigImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProjectConfigTest {

    private ProjectConfig projectConfig;

    @Before
    public void setup() {
        projectConfig = new ProjectConfigImpl(new File("src/test/resources/project.yml"));
    }

    @Test
    public void testServerConfig() {
        assertEquals(projectConfig.getProjectName(), "cool_name");
        assertEquals(projectConfig.getProjectDescription(), "lorem ipsum dolor sit amet");
        assertEquals(projectConfig.getAddAllBranches(), true);
        assertEquals(projectConfig.getBranchProperties(), null);
    }


}
