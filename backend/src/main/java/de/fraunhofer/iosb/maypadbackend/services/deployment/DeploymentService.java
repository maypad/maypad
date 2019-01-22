package de.fraunhofer.iosb.maypadbackend.services.deployment;

import de.fraunhofer.iosb.maypadbackend.model.deployment.DeploymentType;
import de.fraunhofer.iosb.maypadbackend.services.build.BuildService;
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
public class DeploymentService {

    private Collection<? extends  DeploymentTypeExecutor> executors;
    private Map<Class<? extends DeploymentType>, DeploymentTypeExecutor> deploymentTypeMappings;
    private Logger logger = LoggerFactory.getLogger(BuildService.class);

    /**
     * Constructor for DeploymentService.
     * @param executors a collection of all DeploymentTypeExecutor beans
     */
    @Autowired
    public DeploymentService(Collection<? extends DeploymentTypeExecutor> executors) {
        this.executors = executors;
    }

    /**
     * Tries to map a BuildTypeExecutor to every BuildType found in the given package that is
     * annotated with @BuildTypeExec.
     */
    @PostConstruct
    protected void initDeploymentTypeMappings() {
        ClassPathScanningCandidateComponentProvider provider =
                new ClassPathScanningCandidateComponentProvider(false);

        provider.addIncludeFilter(new AssignableTypeFilter(DeploymentType.class));
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
                    if (exec.getClass() == execClass) {
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
