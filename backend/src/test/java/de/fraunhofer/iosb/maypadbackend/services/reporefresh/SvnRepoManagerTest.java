package de.fraunhofer.iosb.maypadbackend.services.reporefresh;

import de.fraunhofer.iosb.maypadbackend.config.project.ProjectConfig;
import de.fraunhofer.iosb.maypadbackend.model.Project;
import de.fraunhofer.iosb.maypadbackend.model.repository.Commit;
import de.fraunhofer.iosb.maypadbackend.model.repository.Tag;
import de.fraunhofer.iosb.maypadbackend.services.ProjectService;
import de.fraunhofer.iosb.maypadbackend.services.ProjectgroupService;
import de.fraunhofer.iosb.maypadbackend.util.Tuple;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SvnRepoManagerTest {

    @Value("classpath:testrepo_svn")
    private Resource repositoryResource;

    private static final Logger logger = LoggerFactory.getLogger(SpringRunner.class);
    private File repositoryFile;
    private Project project;
    private SvnRepoManager svnRepoManager;
    private int pgid;

    private List<String> correctBranchList;
    private List<String> correctTagList;

    @Rule
    public TemporaryFolder projectRootFolder = new TemporaryFolder();

    @Rule
    public TemporaryFolder keyFolder = new TemporaryFolder();

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectgroupService projectgroupService;

    @Before
    public void setup() {
        try {
            repositoryFile = repositoryResource.getFile();
            logger.info("Found testrepo at " + repositoryFile.getAbsolutePath());
            pgid = projectgroupService.create("Test_Group").getId();
            project = projectService.create(pgid, "file://" + repositoryFile.getAbsolutePath() + "/test_project/", "SVN");
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
        } catch (IOException ex) {
            logger.error("Could not find test resource testrepo_svn!");
        }
    }

    @Test
    public void test() {
        assertThat(svnRepoManager.cloneRepository()).isEqualTo(true);
        Tuple<ProjectConfig, File> conf = svnRepoManager.getProjectConfig();
        assertThat(conf.getKey()).isNotNull();
        assertThat(conf.getValue().getAbsolutePath()).isEqualTo(
                projectRootFolder.getRoot().getAbsolutePath() + File.separator + "maypad.yaml"
        );
        svnRepoManager.prepareRefresh();
        try {
            Field privateConfigField = SvnRepoManager.class.getDeclaredField("projConfig");
            assertThat(privateConfigField.get(svnRepoManager)).isNotNull();
        } catch (NoSuchFieldException ex) {
            logger.error("Could not find field projConfig in svnRepoManager.");
        } catch (IllegalAccessException ex) {
            logger.error("Could not access field projConfig.");
        }
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

    @After
    public void cleanup() {
        projectgroupService.deleteProjectgroup(pgid);
    }

}
