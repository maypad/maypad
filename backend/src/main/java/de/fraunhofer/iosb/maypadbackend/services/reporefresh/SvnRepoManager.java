package de.fraunhofer.iosb.maypadbackend.services.reporefresh;

import de.fraunhofer.iosb.maypadbackend.model.Project;
import de.fraunhofer.iosb.maypadbackend.model.repository.Commit;
import de.fraunhofer.iosb.maypadbackend.model.repository.Tag;

import java.io.File;
import java.util.List;

/**
 * Manager for a svn repository.
 *
 * @author Lukas Brosch
 * @version 1.0
 */
public class SvnRepoManager extends RepoManager {

    /**
     * Constructor, prepare the SvnRepoManager.
     *
     * @param project        Project for which the svn-repository is to be managed
     * @param projectRootDir The root directory for the repo files
     */
    public SvnRepoManager(Project project, File projectRootDir) {
        super(project, projectRootDir);
    }

    /**
     * Constructor, prepare the SvnRepoManager.
     *
     * @param project Project for which the svn-repository is to be managed
     */
    public SvnRepoManager(Project project) {
        super(project);
    }

    /**
     * Get the names of all existing branches of the repository.
     *
     * @return List of all branchnames
     */
    @Override
    public List<String> getBranchNames() {
        return null;
    }

    /**
     * Get the name of the main branch.
     *
     * @return Name of the main branch
     */
    @Override
    public String getMainBranchName() {
        return "trunc";
    }

    /**
     * Switches the branch of a repository .
     *
     * @param name Name of the branch
     * @return true, if the switch to other branch was successfully, else false
     */
    @Override
    public boolean switchBranch(String name) {
        return true;
    }


    /**
     * Get all tags of a branch.
     *
     * @return List with tags
     */
    @Override
    public List<Tag> getTags() {
        return null;
    }

    /**
     * Get the last commit.
     *
     * @return The last commit
     */
    @Override
    public Commit getLastCommit() {
        return null;
    }

    /**
     * Clones the repository using the repository URL stored in the project.
     *
     * @return True in success, else false
     */
    @Override
    protected boolean cloneRepository() {
        return true;
    }

}
