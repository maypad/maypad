package de.fraunhofer.iosb.maypadbackend.services.build;

import de.fraunhofer.iosb.maypadbackend.dtos.request.BuildRequest;
import de.fraunhofer.iosb.maypadbackend.exceptions.httpexceptions.BuildRunningException;
import de.fraunhofer.iosb.maypadbackend.exceptions.httpexceptions.NotFoundException;
import de.fraunhofer.iosb.maypadbackend.model.Status;
import de.fraunhofer.iosb.maypadbackend.model.build.Build;
import de.fraunhofer.iosb.maypadbackend.model.build.BuildType;
import de.fraunhofer.iosb.maypadbackend.model.repository.Branch;
import de.fraunhofer.iosb.maypadbackend.model.repository.DependencyDescriptor;
import de.fraunhofer.iosb.maypadbackend.repositories.BranchRepository;
import de.fraunhofer.iosb.maypadbackend.services.ProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@EnableScheduling
public class BuildService {

    private ProjectService projectService;
    private BranchRepository branchRepository;
    private Collection<? extends BuildTypeExecutor> executors;
    private Map<Class<? extends BuildType>, BuildTypeExecutor> buildTypeMappings;
    private Logger logger = LoggerFactory.getLogger(BuildService.class);
    private Map<Branch, Build> runningBuilds;

    private static final long buildTimeoutSeconds = 21600; //6h

    /**
     * Constructor for BuildService.
     *
     * @param projectService the ProjectService used to access projects
     * @param executors      a collection of all BuildTypeExecutor beans
     */
    @Autowired
    public BuildService(ProjectService projectService, BranchRepository branchRepository,
                        Collection<? extends BuildTypeExecutor> executors) {
        this.projectService = projectService;
        this.branchRepository = branchRepository;
        this.executors = executors;
        runningBuilds = new ConcurrentHashMap<>();
    }

    /**
     * Triggers a build for the given branch.
     *
     * @param branch    the branch the should be built
     * @param request   the request that contains the build parameters
     * @param buildName the name of the build type (currently not used)
     */
    public void buildBranch(Branch branch, BuildRequest request, String buildName) {
        buildBranch(branch, request.isWithDependencies(), buildName);
    }

    /**
     * Triggers a build for the given branch.
     *
     * @param branch           the branch the should be build
     * @param withDependencies if the dependencies should be build
     * @param buildName        the name of the build type (currently not used)
     */
    public void buildBranch(Branch branch, boolean withDependencies, String buildName) {
        if (runningBuilds.containsKey(branch)) {
            BuildType buildType = branch.getBuildType();
            if (!buildTypeMappings.containsKey(buildType.getClass())) {
                logger.error("No BuildTypeExecutor registered for " + buildType.getClass());
                throw new RuntimeException("Failed to find BuildTypeExecutor for " + buildType.getClass());
            }
            if (withDependencies) {
                for (DependencyDescriptor dd : branch.getDependencies()) {
                    buildBranch(projectService.getBranch(dd.getProjectId(), dd.getBranchName()), false, buildName);
                }
            }
            if (branch.getLastCommit() == null) {
                throw new NotFoundException("NO_COMMIT", String.format("Nothing to build on %s.", branch.getName()));
            }
            Build build = new Build(new Date(), branch.getLastCommit(), Status.RUNNING);
            branch.getBuilds().add(build);
            runningBuilds.put(branch, build);
            buildTypeMappings.get(buildType.getClass()).build(buildType);
        } else {
            throw new BuildRunningException("BUILD_RUNNING", String.format("There's already a build running for %s.",
                    branch.getName()));
        }

    }

    /**
     * Returns the latest build of a given branch.
     *
     * @param branch the branch
     * @return the latest build on the given branch
     */
    public Build getLatestBuild(Branch branch) {
        int buildCount = branch.getBuilds().size();
        if (buildCount == 0) {
            throw new NotFoundException("NO_BUILD", String.format("There's no build for branch %s.", branch.getName()));
        }
        return branch.getBuilds().stream().skip(branch.getBuilds().size() - 1).findFirst().get();
    }

    /**
     * Updates the status of a running build for the given branch, if there is a running build. If status is RUNNING no
     * update is required.
     *
     * @param branch the branch
     * @param status the new status
     */
    public void signalStatus(Branch branch, Status status) {
        if (!runningBuilds.containsKey(branch)) {
            throw new NotFoundException("NO BUILD", String.format("No Build is running for %s", branch.getName()));
        }

        if (status != Status.RUNNING) {
            runningBuilds.get(branch).setStatus(status);
            runningBuilds.remove(branch);
            branchRepository.saveAndFlush(branch);
        }
    }

    /**
     * Checks every 5min if a build is running for too long.
     */
    @Scheduled(fixedDelay = 30000)
    public void timeoutRunningBuilds() {
        Date current = new Date();
        for (Branch key : runningBuilds.keySet()) {
            if ((current.getTime() - runningBuilds.get(key).getTimestamp().getTime()) / 1000 >= buildTimeoutSeconds) {
                runningBuilds.get(key).setStatus(Status.TIMEOUT);
                runningBuilds.remove(key);
                branchRepository.saveAndFlush(key);
            }
        }
    }

    /**
     * Tries to map a BuildTypeExecutor to every BuildType found in the given package that is
     * annotated with @BuildTypeExec.
     */
    @PostConstruct
    protected void initBuildTypeMappings() {
        ClassPathScanningCandidateComponentProvider provider =
                new ClassPathScanningCandidateComponentProvider(false);

        provider.addIncludeFilter(new AssignableTypeFilter(BuildType.class));
        provider.addIncludeFilter(new AnnotationTypeFilter(BuildTypeExec.class));

        buildTypeMappings = new ConcurrentHashMap<>();

        Set<BeanDefinition> beans =
                provider.findCandidateComponents("de.fraunhofer.iosb.maypadbackend.model.build");
        for (BeanDefinition bdf : beans) {
            try {
                Class<? extends BuildType> typeClass = Class.forName(bdf.getBeanClassName())
                        .asSubclass(BuildType.class);
                Class<? extends BuildTypeExecutor> execClass = typeClass.getAnnotation(BuildTypeExec.class).executor();

                for (BuildTypeExecutor exec : executors) {
                    if (exec.getClass() == execClass) {
                        logger.debug("Mapped " + typeClass.getName() + " to " + exec.getClass().getName());
                        buildTypeMappings.put(typeClass, exec);
                        break;
                    }
                }
                if (!buildTypeMappings.containsKey(typeClass)) {
                    logger.error("Failed to find BuildTypeExecutor for " + typeClass.getName());
                    throw new RuntimeException("No BuildTypeExecutor found for " + typeClass.getName());
                }
            } catch (ClassNotFoundException e) {
                logger.error("Failed to find BuildType Class for" + bdf.getBeanClassName());
                throw new RuntimeException("No class found for" + bdf.getBeanClassName());
            }
        }
    }
}
