package de.fraunhofer.iosb.maypadbackend.config;

import de.fraunhofer.iosb.maypadbackend.config.project.ProjectConfig;
import de.fraunhofer.iosb.maypadbackend.config.project.YamlProjectConfig;
import de.fraunhofer.iosb.maypadbackend.config.project.data.BranchProperty;
import de.fraunhofer.iosb.maypadbackend.exceptions.FormatParseException;
import de.fraunhofer.iosb.maypadbackend.testutil.ResourceFileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.springframework.http.HttpMethod;

import java.io.File;
import java.io.FileNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;

public class ProjectConfigTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    /**
     * Setup test.
     */
    @Before
    public void setup() throws Exception {
        ResourceFileUtils.copyFileFromResources("project.yaml",
                new File(temporaryFolder.getRoot().getAbsolutePath() + "/project.yaml"));
    }

    @Test
    public void projectConfigNotFound() throws Exception {
        expectedException.expect(FileNotFoundException.class);
        new YamlProjectConfig(new File(temporaryFolder.getRoot().getAbsolutePath() + "/nofile.yaml"));
    }

    @Test
    public void projectConfigInvalid() throws Exception {
        expectedException.expect(FormatParseException.class);

        File output = new File(temporaryFolder.getRoot().getAbsolutePath() + "/invalid.yaml");
        ResourceFileUtils.copyFileFromResources("invalid.yaml",output);
        new YamlProjectConfig(output);
    }

    @Test
    public void projectConfigValid() throws Exception {
        ProjectConfig projectConfig =
                new YamlProjectConfig(new File(temporaryFolder.getRoot().getAbsolutePath() + "/project.yaml"));
        assertThat(projectConfig.getProjectName()).isEqualTo("cool_name");
        assertThat(projectConfig.getProjectDescription()).isEqualTo("lorem ipsum dolor sit amet");
        assertThat(projectConfig.getAddAllBranches()).isEqualTo(true);
        assertThat(projectConfig.getBranchProperties().size()).isEqualTo(1);
        BranchProperty branch = projectConfig.getBranchProperties().get(0);
        assertThat(branch.getName()).isEqualTo("master");
        assertThat(branch.getDescription()).isEqualTo("Lorem Ipsum");
        assertThat(branch.getMembers().size()).isEqualTo(5);
        assertThat(branch.getMails().size()).isEqualTo(2);
        assertThat(branch.getDeployment().getType()).isEqualTo("webhook");
        assertThat(branch.getDeployment().getName()).isEqualTo("Great-Deployment");
        assertThat(branch.getDeployment().getType()).isEqualTo("webhook");
        assertThat(branch.getDeployment().getUrl()).isEqualTo("https://greatDeployment.com/54321abcd");
        assertThat(branch.getDeployment().getMethod()).isEqualTo(HttpMethod.POST);
        assertThat(branch.getDependsOn().size()).isEqualTo(2);
        assertThat(branch.getBuild().getType()).isEqualTo("webhook");
        assertThat(branch.getBuild().getMethod()).isEqualTo(HttpMethod.POST);
        assertThat(branch.getBuild().getUrl()).isEqualTo("https://greatBuild.com/12345abc");
        assertThat(branch.getBuild().getHeaders().length).isEqualTo(2);
        assertThat(branch.getBuild().getHeaders()[0].getKey()).isEqualTo("key1");
        assertThat(branch.getBuild().getHeaders()[0].getValues().length).isEqualTo(2);
        assertThat(branch.getBuild().getHeaders()[0].getValues()[0]).isEqualTo("value1");
        assertThat(branch.getBuild().getBody()).isEqualTo("{}");
    }


}
