package de.fraunhofer.iosb.maypadbackend.config;

import de.fraunhofer.iosb.maypadbackend.config.project.ProjectConfig;
import de.fraunhofer.iosb.maypadbackend.config.project.YamlProjectConfig;
import de.fraunhofer.iosb.maypadbackend.config.project.data.BranchProperty;
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

    /**
     * Setup test.
     */
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
        if (!isLoaded) {
            fail();
        }
        assertThat(projectConfig.getProjectName()).isEqualTo("cool_name");
        assertThat(projectConfig.getProjectDescription()).isEqualTo("lorem ipsum dolor sit amet");
        assertThat(projectConfig.getAddAllBranches()).isEqualTo(true);
        assertThat(projectConfig.getBranchProperties().size()).isEqualTo(1);
        BranchProperty branch = projectConfig.getBranchProperties().get(0);
        assertThat(branch.getName()).isEqualTo("master");
        assertThat(branch.getDescription()).isEqualTo("Lorem Ipsum");
        assertThat(branch.getMembers().size()).isEqualTo(5);
        assertThat(branch.getMails().size()).isEqualTo(2);
        assertThat(branch.getBuild()).isEqualTo("https://greatBuild.com/12345abc");
        assertThat(branch.getDeployment().getType()).isEqualTo("webhook");
        assertThat(branch.getDeployment().getName()).isEqualTo("Great-Deployment");
        assertThat(branch.getDeployment().getArguments()).isEqualTo("https://greatDeployment.com/54321abcd");
        assertThat(branch.getDependsOn().size()).isEqualTo(2);
    }


}
