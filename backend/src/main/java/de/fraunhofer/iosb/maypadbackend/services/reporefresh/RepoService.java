package de.fraunhofer.iosb.maypadbackend.services.reporefresh;

import de.fraunhofer.iosb.maypadbackend.model.Project;
import de.fraunhofer.iosb.maypadbackend.services.ProjectService;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author Lukas Brosch
 * @version 1.0
 */
@Service
@NoArgsConstructor
public class RepoService {

    private ProjectService projectService;
    private Set<Project> lockedProjects;


    @Autowired
    public RepoService(ProjectService projectService) {
        this.projectService = projectService;
        this.lockedProjects = ConcurrentHashMap.newKeySet();
    }

    public void refreshProject(Project project) {


        projectService.saveProject(project);
    }

    public void initProject(Project project) {

        projectService.saveProject(project);
    }
}
