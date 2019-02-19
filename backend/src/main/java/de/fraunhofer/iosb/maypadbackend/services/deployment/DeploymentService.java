package de.fraunhofer.iosb.maypadbackend.services.deployment;

import de.fraunhofer.iosb.maypadbackend.dtos.request.DeploymentRequest;
import de.fraunhofer.iosb.maypadbackend.exceptions.httpexceptions.DeploymentRunningException;
import de.fraunhofer.iosb.maypadbackend.exceptions.httpexceptions.NotFoundException;
import de.fraunhofer.iosb.maypadbackend.model.Project;
import de.fraunhofer.iosb.maypadbackend.model.Status;
import de.fraunhofer.iosb.maypadbackend.model.build.Build;
import de.fraunhofer.iosb.maypadbackend.model.deployment.Deployment;
import de.fraunhofer.iosb.maypadbackend.model.deployment.DeploymentType;
import de.fraunhofer.iosb.maypadbackend.model.repository.Branch;
import de.fraunhofer.iosb.maypadbackend.services.ProjectService;
import de.fraunhofer.iosb.maypadbackend.services.build.BuildService;
import de.fraunhofer.iosb.maypadbackend.services.sse.EventData;
import de.fraunhofer.iosb.maypadbackend.services.sse.SseEventType;
import de.fraunhofer.iosb.maypadbackend.services.sse.SseService;
import de.fraunhofer.iosb.maypadbackend.util.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manage the deployment for a project.
 */
@Service
public class DeploymentService {

    private ProjectService projectService;
    private BuildService buildService;
    private SseService sseService;
    private Collection<DeploymentTypeExecutor> executors;
    private Map<Class<? extends DeploymentType>, DeploymentTypeExecutor> deploymentTypeMappings;
    private Map<Tuple<Integer, String>, Integer> runningDeployments;

    private static final Logger logger = LoggerFactory.getLogger(DeploymentService.class);


    /**
     * Triggers a deployment for the given branch.
     *
     * @param id             the id of the project
     * @param ref            the name of the branch
     * @param request        the request that contains the deployment parameters
     * @param deploymentName the name of the deployment type
     */
    public void deployBuild(int id, String ref, DeploymentRequest request, String deploymentName) {
        deployBuild(id, ref, request.isWithBuild(), request.isWithDependencies(), deploymentName);
    }

    /**
     * Triggers a deployment for the given branch.
     *
     * @param id               the id of the project
     * @param ref              the name of the branch
     * @param withBuild        if a build should be triggered
     * @param withDependencies if the the dependencies should be build
     * @param deploymentName   the name of the deployment type
     */
    public void deployBuild(int id, String ref, boolean withBuild, boolean withDependencies, String deploymentName) {
        Project project = projectService.getProject(id);
        Branch branch = project.getRepository().getBranches().get(ref);
        if (branch == null) {
            throw new NotFoundException("BRANCH_NOT_FOUND", "Branch not found.");
        }
        if (!runningDeployments.containsKey(new Tuple<>(id, ref))) {
            DeploymentType deploymentType = branch.getDeploymentType();
            if (!deploymentTypeMappings.containsKey(deploymentType.getClass())) {
                logger.error("No DeploymentTypeExecutor registered for " + deploymentType.getClass());
                throw new RuntimeException("Failed to find DeploymentTypeExecutor for " + deploymentType.getClass());
            }
            if (withBuild) {
                buildService.buildBranch(id, ref, withDependencies, null);
            }
            Build build = buildService.getLatestBuild(branch);
            Deployment deployment = new Deployment(new Date(), build, Status.UNKNOWN);
            branch.getDeployments().add(0, deployment);
            projectService.saveProject(project);
            deployment = getLatestDeployment(branch);
            runningDeployments.put(new Tuple<>(id, ref), deployment.getId());
            deploymentTypeMappings.get(deploymentType.getClass()).deploy(deploymentType, id, ref);
        } else {
            throw new DeploymentRunningException("DEPLOYMENT_RUNNING",
                    String.format("There's already a deployment running for %s", branch.getName()));
        }
    }

    /**
     * Get the latest deployment from a branch.
     *
     * @param branch Branch to find the last deployment
     * @return Last deployment
     */
    private Deployment getLatestDeployment(Branch branch) {
        return branch.getDeployments().stream().findFirst().orElseThrow(
                () -> new NotFoundException("DEPLOYMENT_NOT_FOUND", "Deployment not found."));
    }

    /**
     * Constructor for DeploymentService.
     *
     * @param projectService the ProjectService used to access projects
     * @param executors      a collection of all DeploymentTypeExecutor beans
     * @param buildService   the BuildService used to build branches
     */
    @Autowired
    public DeploymentService(ProjectService projectService, Collection<DeploymentTypeExecutor> executors,
                             BuildService buildService, SseService sseService) {
        this.projectService = projectService;
        this.executors = executors;
        this.buildService = buildService;
        this.sseService = sseService;
        runningDeployments = new ConcurrentHashMap<>();
    }

    /**
     * Get the deployment with given id.
     *
     * @param branch       Branch with deployments
     * @param deploymentId id of deployment
     * @return Deployment with given id in given branch
     */
    private Deployment getDeployment(Branch branch, int deploymentId) {
        return branch.getDeployments().stream().filter(d -> d.getId() == deploymentId).findAny()
                .orElseThrow(() -> new NotFoundException("DEPLOYMENT_NOT_FOUND", "Deployment not found."));
    }

    /**
     * Updates the status of a running deployment for the given branch, if there is a running deployment.
     *
     * @param id     the id of the project
     * @param ref    the name of the Branch
     * @param status the new status
     */
    public void signalStatus(int id, String ref, Status status) {
        Tuple<Integer, String> branchMapEntry = new Tuple<>(id, ref);
        Branch branch = projectService.getBranch(id, ref);
        if (branch == null) {
            throw new NotFoundException("BRANCH_NOT_FOUND", "Branch not found.");
        }
        if (!runningDeployments.containsKey(branchMapEntry)) {
            throw new NotFoundException("NO_DEPLOYMENT", String.format("No deployment is running for %s", branch.getName()));
        }
        Deployment deployment = getDeployment(branch, runningDeployments.get(branchMapEntry));
        deployment.setStatus(status);
        runningDeployments.remove(branchMapEntry);
        Project project = projectService.getProject(id);
        projectService.saveProject(project);
        sseService.push(EventData.builder(SseEventType.DEPLOYMENT_UPDATE).projectId(id).name(ref).status(status).build());
    }


    /**
     * Tries to map a BuildTypeExecutor to every BuildType found in the given package that is
     * annotated with @BuildTypeExec.
     */
    @PostConstruct
    protected void initDeploymentTypeMappings() {
        ClassPathScanningCandidateComponentProvider provider =
                new ClassPathScanningCandidateComponentProvider(false);

        provider.addIncludeFilter(new AnnotationTypeFilter(DeploymentTypeExec.class));

        deploymentTypeMappings = new ConcurrentHashMap<>();

        Set<BeanDefinition> beans =
                provider.findCandidateComponents("de.fraunhofer.iosb.maypadbackend.model.deployment");
        for (BeanDefinition bdf : beans) {
            try {
                Class<? extends DeploymentType> typeClass = Class.forName(bdf.getBeanClassName())
                        .asSubclass(DeploymentType.class);
                Class<? extends DeploymentTypeExecutor> execClass = typeClass.getAnnotation(DeploymentTypeExec.class).executor();

                for (DeploymentTypeExecutor exec : executors) {
                    if (exec.getClass() == execClass
                            || (AopUtils.isJdkDynamicProxy(exec)
                            && ((Advised) exec).getTargetSource().getTargetClass() == execClass)) {
                        logger.debug("Mapped " + typeClass.getName() + " to " + exec.getClass().getName());
                        deploymentTypeMappings.put(typeClass, exec);
                        break;
                    }
                }
                if (!deploymentTypeMappings.containsKey(typeClass)) {
                    logger.error("Failed to find DeploymentTypeExecutor for " + typeClass.getName());
                    throw new RuntimeException("No DeploymentTypeExecutor found for " + typeClass.getName());
                }
            } catch (ClassNotFoundException e) {
                logger.error("Failed to find DeploymentType Class for" + bdf.getBeanClassName());
                throw new RuntimeException("No class found for" + bdf.getBeanClassName());
            }
        }
    }
}
