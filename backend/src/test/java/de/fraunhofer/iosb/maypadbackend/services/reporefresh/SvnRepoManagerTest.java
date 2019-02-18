package de.fraunhofer.iosb.maypadbackend.services.reporefresh;

import de.fraunhofer.iosb.maypadbackend.config.project.ProjectConfig;
import de.fraunhofer.iosb.maypadbackend.model.Project;
import de.fraunhofer.iosb.maypadbackend.model.repository.Commit;
import de.fraunhofer.iosb.maypadbackend.model.repository.Tag;
import de.fraunhofer.iosb.maypadbackend.util.Tuple;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SvnRepoManagerTest {

    private static final Logger logger = LoggerFactory.getLogger(SpringRunner.class);
    private SvnRepoManager svnRepoManager;

    private List<String> correctBranchList;
    private List<String> correctTagList;

    private Project project;

    @Rule
    public TemporaryFolder projectRootFolder = new TemporaryFolder();

    @Rule
    public TemporaryFolder keyFolder = new TemporaryFolder();

    @Before
    public void setup() throws IOException {
        File repositoryFile = new ClassPathResource("testrepo_svn").getFile();
        project = mock(Project.class);
        when(project.getRepositoryUrl()).thenReturn("file://" + repositoryFile.getAbsolutePath() + "/test_project/");
        logger.info("Found testrepo at " + repositoryFile.getAbsolutePath());
        svnRepoManager = new SvnRepoManager(project);
        svnRepoManager.initRepoManager(keyFolder.getRoot(), projectRootFolder.getRoot());
        // Branches
        correctBranchList = new ArrayList<>();
        correctBranchList.add("branch1");
        correctBranchList.add("branch2");
        correctBranchList.add("trunk");
        // Tags
        correctTagList = new ArrayList<>();
        correctTagList.add("tag1");
        correctTagList.add("tag2");
    }

    @Test
    public void test() throws NoSuchFieldException, IllegalAccessException {
        assertThat(svnRepoManager.cloneRepository()).isEqualTo(true);
        Tuple<ProjectConfig, File> conf = svnRepoManager.getProjectConfig();
        assertThat(conf.getKey()).isNotNull();
        assertThat(conf.getValue().getAbsolutePath()).isEqualTo(
                projectRootFolder.getRoot().getAbsolutePath() + File.separator + "maypad.yaml"
        );
        svnRepoManager.prepareRefresh();
        Field privateConfigField = SvnRepoManager.class.getDeclaredField("projConfig");
        privateConfigField.setAccessible(true);
        assertThat(privateConfigField.get(svnRepoManager)).isNotNull();
        assertThat(svnRepoManager.getMainBranchName()).isEqualTo("trunk");
        Commit c = svnRepoManager.getLastCommit();
        assertThat(c.getMessage()).isEqualTo("Test-Commit-Message");
        List<String> branches = svnRepoManager.getBranchNames();
        assertThat(CollectionUtils.isEqualCollection(branches, correctBranchList)).isEqualTo(true);
        List<Tag> tags = svnRepoManager.getTags();
        List<String> tagNames = new ArrayList<>();
        for (Tag t : tags) {
            tagNames.add(t.getName());
        }
        assertThat(CollectionUtils.isEqualCollection(tagNames, correctTagList));
        assertThat(svnRepoManager.switchBranch("trunk")).isTrue();
    }
}
