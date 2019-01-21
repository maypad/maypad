package de.fraunhofer.iosb.maypadbackend.services.build;

import de.fraunhofer.iosb.maypadbackend.dtos.request.BuildRequest;
import de.fraunhofer.iosb.maypadbackend.model.build.BuildType;
import de.fraunhofer.iosb.maypadbackend.model.repository.Branch;
import de.fraunhofer.iosb.maypadbackend.model.repository.DependencyDescriptor;
import de.fraunhofer.iosb.maypadbackend.services.ProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BuildService {

    private ProjectService projectService;
    private Collection<? extends BuildTypeExecutor> executors;
    private Map<Class<? extends BuildType>, BuildTypeExecutor> buildTypeMappings;
    private Logger logger = LoggerFactory.getLogger(BuildService.class);

    /**
     * Constructor for BuildService.
     * @param projectService the ProjectService used to access projects
     * @param executors a collection of all BuildTypeExecutor beans
     */
    @Autowired
    public BuildService(ProjectService projectService, Collection<? extends BuildTypeExecutor> executors) {
        this.projectService = projectService;
        this.executors = executors;
    }

    /**
     * Triggers a build for the given branch.
     * @param branch the branch the should be built
     * @param request the request that contains the build parameters
     * @param buildName the name of the build type (currently not used)
     */
    public void buildBranch(Branch branch, BuildRequest request, String buildName) {
        buildBranch(branch, request.isWithDependencies(), buildName);
    }

    /**
     * Triggers a build for the given branch.
     * @param branch the branch the should be build
     * @param withDependencies if the dependencies should be build
     * @param buildName the name of the build type (currently not used)
     */
    public void buildBranch(Branch branch, boolean withDependencies, String buildName) {
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
        buildTypeMappings.get(buildType.getClass()).build(buildType);
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
