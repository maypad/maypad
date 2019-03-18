package de.fraunhofer.iosb.maypadbackend.services.build;

import de.fraunhofer.iosb.maypadbackend.dtos.request.BuildRequest;
import de.fraunhofer.iosb.maypadbackend.exceptions.httpexceptions.BuildRunningException;
import de.fraunhofer.iosb.maypadbackend.exceptions.httpexceptions.NotFoundException;
import de.fraunhofer.iosb.maypadbackend.model.Project;
import de.fraunhofer.iosb.maypadbackend.model.Status;
import de.fraunhofer.iosb.maypadbackend.model.build.Build;
import de.fraunhofer.iosb.maypadbackend.model.build.BuildReason;
import de.fraunhofer.iosb.maypadbackend.model.build.BuildType;
import de.fraunhofer.iosb.maypadbackend.model.repository.Branch;
import de.fraunhofer.iosb.maypadbackend.services.ProjectService;
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
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manage the builds for a project.
 */
@Service
public class BuildService {

    private ProjectService projectService;
    private SseService sseService;
    private Collection<? extends BuildTypeExecutor> executors;
    private Map<Class<? extends BuildType>, BuildTypeExecutor> buildTypeMappings;
    private Logger logger = LoggerFactory.getLogger(BuildService.class);
    private Map<Tuple<Integer, String>, BuildLock> runningBuilds;
    private DependencyBuildHelper dependencyBuildHelper;

    private static final long BUILD_TIMEOUT_SECONDS = 21600; //6h
    private static final long CHECK_TIMEOUT_INTERVAL = 30000; //in ms

    /**
     * Constructor for BuildService.
     *
     * @param projectService        the ProjectService used to access projects
     * @param sseService            Service for server-sent events
     * @param executors             a collection of all BuildTypeExecutor beans
     * @param dependencyBuildHelper the DependencyBuildHelper
     */
    @Autowired
    public BuildService(ProjectService projectService, Collection<? extends BuildTypeExecutor> executors,
                        DependencyBuildHelper dependencyBuildHelper, SseService sseService) {
        this.projectService = projectService;
        this.sseService = sseService;
        this.executors = executors;
        this.dependencyBuildHelper = dependencyBuildHelper;
        runningBuilds = new ConcurrentHashMap<>();
    }

    /**
     * Triggers a build for the given branch.
     *
     * @param id        the id of the project
     * @param ref       the name of the Branch
     * @param request   the request that contains the build parameters
     * @param buildName the name of the build type (currently not used)
     * @return future
     */
    @Async
    public CompletableFuture<Status> buildBranch(int id, String ref, BuildRequest request, String buildName) {
        return buildBranch(id, ref, request.isWithDependencies(), buildName);
    }

    /**
     * Triggers a build for the given branch.
     *
     * @param id               the id of the project
     * @param ref              the name of the Branch
     * @param withDependencies if the dependencies should be build
     * @param buildName        the name of the build type (currently not used)
     * @return future
     */
    @Async
    public CompletableFuture<Status> buildBranch(int id, String ref, boolean withDependencies, String buildName) {
        logger.info("Start build of project {}, branch {}", id, ref);
        Project project = projectService.getProject(id);
        Branch branch = project.getRepository().getBranches().get(ref);
        if (!runningBuilds.containsKey(new Tuple<>(id, ref))) {
            BuildType buildType = branch.getBuildType();

            if (!buildTypeMappings.containsKey(buildType.getClass())) {
                logger.error("No BuildTypeExecutor registered for " + buildType.getClass());
                throw new RuntimeException("Failed to find BuildTypeExecutor for " + buildType.getClass());
            }

            if (branch.getLastCommit() == null) {
                throw new NotFoundException("NO_COMMIT", String.format("Nothing to build on %s.", branch.getName()));
            }

            Build build = new Build(new Date(), branch.getLastCommit(), Status.UNKNOWN);
            branch.getBuilds().add(0, build);
            branch = projectService.saveProject(project).getRepository().getBranches().get(ref);
            build = getLatestBuild(branch);
            BuildLock lock = new BuildLock(build.getId());
            runningBuilds.put(new Tuple<>(id, ref), lock);
            if (withDependencies) {
                logger.info("Build of project {}, branch{} is running with dependencies", id, ref);
                try {
                    Tuple<Boolean, String> dependencyBuildStatus = dependencyBuildHelper.runBuildWithDependencies(id, ref);
                    if (!dependencyBuildStatus.getKey()) {
                        logger.debug("Build of dependencies failed for project {}.", id);
                        signalStatus(id, ref, Status.FAILED, BuildReason.DEPENDENCY_BUILD_FAILED, dependencyBuildStatus.getValue());
                        return CompletableFuture.completedFuture(Status.FAILED);
                    }
                } catch (Exception e) {
                    logger.error("Build of project {}, branch {} failed");
                    signalStatus(id, ref, Status.FAILED, BuildReason.BUILD_FAILED, null);
                    Thread.currentThread().interrupt();
                    return CompletableFuture.completedFuture(Status.FAILED);
                }
            }

            buildTypeMappings.get(buildType.getClass()).build(buildType, id, ref);
            try {
                lock.acquire();
            } catch (InterruptedException e) {
                logger.warn("Build of project {} interrupted.", id);
                signalStatus(id, ref, Status.FAILED, BuildReason.BUILD_FAILED, null);
                Thread.currentThread().interrupt();
                return CompletableFuture.completedFuture(Status.FAILED);
            }
            branch = projectService.getBranch(id, ref);
            logger.info("Build of project {}, branch {} done", id, ref);
            return CompletableFuture.completedFuture(getBuild(branch, build.getId()).getStatus());
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
        return branch.getBuilds().stream().findFirst().orElseThrow(
                () -> new NotFoundException("NO_BUILD",
                        String.format("There's no build for branch %s.", branch.getName())));
    }

    /**
     * Get the build in a branch with given id.
     *
     * @param branch  the branch
     * @param buildId id of the build
     * @return Build in branch with given id
     */
    private Build getBuild(Branch branch, int buildId) {
        return branch.getBuilds().stream().filter(b -> b.getId() == buildId).findFirst()
                .orElseThrow(() -> new NotFoundException("BUILD_NOT_FOUND", "Build not found."));
    }

    /**
     * Updates the status of a running build for the given branch, if there is a running build. If status is RUNNING no
     * update is required.
     *
     * @param id     the id of the project
     * @param ref    the name of the Branch
     * @param status the new status
     */
    public void signalStatus(int id, String ref, Status status) {
        signalStatus(id, ref, status, null, null);
    }

    /**
     * Updates the status of a running build for the given branch, if there is a running build. If status is RUNNING no
     * update is required.
     *
     * @param id               the id of the project
     * @param ref              the name of the Branch
     * @param status           the new status
     * @param reason           provides additional information about the build
     * @param reasonDependency provides additional information about the dependency that failed
     */
    public void signalStatus(int id, String ref, Status status, BuildReason reason, String reasonDependency) {
        Tuple<Integer, String> branchMapEntry = new Tuple<>(id, ref);
        Project project = projectService.getProject(id);
        Branch branch = project.getRepository().getBranches().get(ref);
        if (!runningBuilds.containsKey(branchMapEntry)) {
            throw new NotFoundException("NO BUILD", String.format("No Build is running for %s", branch.getName()));
        }
        BuildLock lock = runningBuilds.get(branchMapEntry);
        Build build = getBuild(branch, lock.getBuildId());
        build.setStatus(status);
        if (status == Status.FAILED || status == Status.SUCCESS || status == Status.TIMEOUT) {
            lock.release();
            runningBuilds.remove(branchMapEntry);
            if (reason != null) {
                build.setReason(reason);
                build.setReasonDependency(reasonDependency);
            }
        }
        logger.debug(String.format("Build %d changed its status to %s on (project %d, branch %s)", build.getId(),
                status, id, ref));
        projectService.saveProject(project);
        projectService.statusPropagation(project.getId());
        if (status == Status.FAILED || status == Status.SUCCESS || status == Status.TIMEOUT || status == Status.RUNNING) {
            sseService.push(EventData.builder(SseEventType.BUILD_UPDATE).projectId(id).name(ref).status(status).build());
        }
    }

    /**
     * Checks if a build is running for too long in the specified interval.
     */
    @Scheduled(fixedDelay = CHECK_TIMEOUT_INTERVAL, initialDelay = CHECK_TIMEOUT_INTERVAL)
    public void timeoutRunningBuilds() {
        Date current = new Date();
        for (Map.Entry<Tuple<Integer, String>, BuildLock> entry : runningBuilds.entrySet()) {
            Project project = projectService.getProject(entry.getKey().getKey());
            Branch branch = project.getRepository().getBranches().get(entry.getKey().getValue());
            Build build = getBuild(branch, entry.getValue().getBuildId());
            if ((current.getTime() - build.getTimestamp().getTime()) / 1000 >= BUILD_TIMEOUT_SECONDS) {
                signalStatus(project.getId(), branch.getName(), Status.TIMEOUT);
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
                    if (exec.getClass() == execClass
                            || (AopUtils.isJdkDynamicProxy(exec)
                            && ((Advised) exec).getTargetSource().getTargetClass() == execClass)) {
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
