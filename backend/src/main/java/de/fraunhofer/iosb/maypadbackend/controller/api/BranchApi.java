package de.fraunhofer.iosb.maypadbackend.controller.api;

import de.fraunhofer.iosb.maypadbackend.dtos.request.BuildRequest;
import de.fraunhofer.iosb.maypadbackend.dtos.request.DeploymentRequest;
import de.fraunhofer.iosb.maypadbackend.dtos.response.BranchResponse;
import de.fraunhofer.iosb.maypadbackend.dtos.response.BuildResponse;
import de.fraunhofer.iosb.maypadbackend.dtos.response.DeploymentResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;

@RequestMapping("/projects/{id}/branches")
public interface BranchApi {

    /**
     * Returns a list of all branches contained in the project associated with the given id.
     * @param id the id of the project
     * @return list of branches as BranchResponse
     */
    @GetMapping
    public List<BranchResponse> getBranches(@PathVariable int id);

    /**
     * Returns the branch with the name ref, if such a branch exists in the project associated with the given id.
     * @param id the id of the project
     * @param ref the name of the branch
     * @return branch with the name ref in project
     */
    @GetMapping("/{ref}")
    public BranchResponse getBranch(@PathVariable int id, @PathVariable String ref);

    /**
     * Returns a list of all builds of the branch specified by ref and id.
     * @param id the id of the project
     * @param ref the name of the branch
     * @return list of all builds as BuildResponse
     */
    @GetMapping("/{ref}/builds")
    public List<BuildResponse> getBuilds(@PathVariable int id, @PathVariable String ref);

    /**
     * Returns a list of all deployments of the branch specified by ref and id.
     * @param id the id of the project
     * @param ref the name of the branch
     * @return list of all deployments as DeploymentResponse
     */
    @GetMapping("/{ref}/deployments")
    public List<DeploymentResponse> getDeployments(@PathVariable int id, @PathVariable String ref);

    /**
     * Triggers a build for the branch specified by ref and id.
     * @param id the id of the project
     * @param ref the name of the Branch
     * @param request the BuildRequest that contains the required parameters
     */
    @PostMapping("/{ref}/builds")
    public void triggerBuild(@PathVariable int id, @PathVariable String ref, @Valid @RequestBody BuildRequest request);

    /**
     * Triggers a deployment for the branch specified by ref and if.
     * @param id the id of the project
     * @param ref the name of the branch
     * @param request the DeploymentRequest that contains the required parameters
     */
    @PostMapping("/{ref}/deployments")
    public void triggerDeployment(@PathVariable int id, @PathVariable String ref,
                                  @Valid @RequestBody DeploymentRequest request);

    /**
     * Signals that the repository associated with the project was updated on the branch specified by ref.
     * @param id the id of the project
     * @param ref the name of the branch
     * @param token the token used to handle the request
     */
    @PostMapping("/{ref}/push")
    public void notifyRepoUpdate(@PathVariable int id, @PathVariable String ref,
                                 @RequestParam(value = "token") String token);

    /**
     * Signals that the running build of the branch specified by id and ref was successful, if such a branch exists.
     * @param id the id of the project
     * @param ref the name of the branch
     * @param token the token used to handle the request
     */
    @PostMapping("/{ref}/builds/sucess")
    public void notifyBuildSuccess(@PathVariable int id, @PathVariable String ref,
                                   @RequestParam(value = "token") String token);

    /**
     * Signals that the running build of the branch specified by id and ref failed, if such a branch exists.
     * @param id the id of the project
     * @param ref the name of the branch
     * @param token the token used to handle the request
     */
    @PostMapping("/{ref}/builds/sucess")
    public void notifyBuildFailure(@PathVariable int id, @PathVariable String ref,
                                   @RequestParam(value = "token") String token);


}
