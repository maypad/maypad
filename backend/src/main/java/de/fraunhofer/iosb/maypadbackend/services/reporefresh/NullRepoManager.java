package de.fraunhofer.iosb.maypadbackend.services.reporefresh;

import de.fraunhofer.iosb.maypadbackend.config.project.ProjectConfig;
import de.fraunhofer.iosb.maypadbackend.model.Project;
import de.fraunhofer.iosb.maypadbackend.model.person.Author;
import de.fraunhofer.iosb.maypadbackend.model.person.Mail;
import de.fraunhofer.iosb.maypadbackend.model.repository.Commit;
import de.fraunhofer.iosb.maypadbackend.model.repository.Tag;
import de.fraunhofer.iosb.maypadbackend.util.Tuple;

import java.io.File;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Dummy for an unknown type of a repository.
 *
 * @author Lukas Brosch
 * @version 1.0
 */
public class NullRepoManager extends RepoManager {

    /**
     * Constructor, prepare the NullRepoManager.
     *
     * @param project Project for which this null-repository is to be managed
     */
    public NullRepoManager(Project project) {
        super(project);
    }

    /**
     * Get the names of all existing branches of the repository.
     *
     * @return List of all branchnames
     */
    @Override
    public List<String> getBranchNames() {
        return new LinkedList<>();
    }

    /**
     * Switches the branch of a repository .
     *
     * @param name Name of the branch
     * @return true, if the switch to other branch was successfully, else false
     */
    @Override
    public boolean switchBranch(String name) {
        //we haven't a repo, so we do nothing here
        return true;
    }

    /**
     * Get the readme file of branch.
     *
     * @return Input of readme file
     */
    @Override
    public String getReadme() {
        // no repo, no readme
        return "";
    }

    /**
     * Get all tags of a branch.
     *
     * @return List with tags
     */
    @Override
    public List<Tag> getTags() {
        return new LinkedList<>();
    }

    /**
     * Get the last commit.
     *
     * @return The last commit
     */
    @Override
    public Commit getLastCommit() {
        return new Commit("", "", new Date(0), new Author("", new Mail("")));
    }

    /**
     * Clones the repository using the repository URL stored in the project.
     *
     * @return True in success, else false
     */
    @Override
    protected boolean cloneRepository() {
        //we have no valid repo, so we haven't to clone something
        return true;
    }

    /**
     * Returns a configuration with path that contains all values of the project configuration from the repository.
     *
     * @return Tuple of projectconfig and path to config
     */
    @Override
    public Tuple<ProjectConfig, File> getProjectConfig() {
        //no repo, no projectconfig
        return null;
    }
}
