package de.fraunhofer.iosb.maypadbackend.config;

import de.fraunhofer.iosb.maypadbackend.config.project.ProjectConfig;
import de.fraunhofer.iosb.maypadbackend.config.project.YamlProjectConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProjectConfigTest {

    private ProjectConfig projectConfig;
    private boolean isLoaded;

    private Logger logger = LoggerFactory.getLogger(ProjectConfigTest.class);

    @Before
    public void setup() {
        File f = new File("src/test/resources/project.yaml");
        try {
            projectConfig = new YamlProjectConfig(f);
            isLoaded = true;
        } catch (IOException e) {
            logger.error(e.getMessage());
            isLoaded = false;
        }
    }

    @Test
    public void testServerConfig() {
        if (!isLoaded) { fail(); }
        assertThat(projectConfig.getProjectName()).isEqualTo("cool_name");
        assertThat(projectConfig.getProjectDescription()).isEqualTo("lorem ipsum dolor sit amet");
        assertThat(projectConfig.getAddAllBranches()).isEqualTo(true);
        assertThat(projectConfig.getBranchProperties()).isEqualTo(null);
    }


}
