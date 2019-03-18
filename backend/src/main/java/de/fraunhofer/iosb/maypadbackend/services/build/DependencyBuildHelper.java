package de.fraunhofer.iosb.maypadbackend.services.build;

import de.fraunhofer.iosb.maypadbackend.model.Project;
import de.fraunhofer.iosb.maypadbackend.model.Status;
import de.fraunhofer.iosb.maypadbackend.model.repository.Branch;
import de.fraunhofer.iosb.maypadbackend.model.repository.DependencyDescriptor;
import de.fraunhofer.iosb.maypadbackend.services.ProjectService;
import de.fraunhofer.iosb.maypadbackend.util.Tuple;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Provides method to execute builds starting with least dependent dependency.
 */
@Component
public class DependencyBuildHelper {

    private ProjectService projectService;
    private BuildService buildService;
    private boolean warned; // Warn flag, so that cycle warning prints only once
    private List<BuildNode> nodes; // For cycle detection
    private BuildNode root;

    private static Logger logger = LoggerFactory.getLogger(DependencyBuildHelper.class);


    @Autowired
    @Lazy
    public DependencyBuildHelper(ProjectService projectService, BuildService buildService) {
        this.projectService = projectService;
        this.buildService = buildService;
    }

    /**
     * Runs a build with dependencies. It starts the lowest "layer" of builds
     * first, which are the leaves in the dependency tree. Potential cycles
     * in dependencies are ignored.
     *
     * @return true if all builds were successful, false otherwise.
     */
    public Tuple<Boolean, String> runBuildWithDependencies(int id, String ref) {
        init(id, ref);
        Stack<BuildNode> buildStack = getBuildStack(root);
        int highestLayer = buildStack.peek().getLayer();
        List<BuildNode> currentLayer = new ArrayList<BuildNode>();
        for (int i = highestLayer; i > 0; i--) {
            // Get all branches on that layer, run builds
            currentLayer.clear();
            while (!buildStack.empty() && buildStack.peek().getLayer() == i) {
                currentLayer.add(buildStack.pop());
            }
            Map<DependencyDescriptor, CompletableFuture<Status>> buildFutureMappigns = new HashMap<>();
            currentLayer.forEach(
                    n -> buildFutureMappigns.put(new DependencyDescriptor(n.getProjectId(), n.getBranchRef()),
                            buildService.buildBranch(n.getProjectId(), n.getBranchRef(), false, null))
            );
            try {
                for (Map.Entry<DependencyDescriptor, CompletableFuture<Status>> entry : buildFutureMappigns.entrySet()) {
                    if (entry.getValue().get() != Status.SUCCESS) {
                        return new Tuple<>(false, entry.getKey().toString());
                    }
                }
            } catch (InterruptedException e) {
                logger.warn("Build of project {} interrupted.", id);
                Thread.currentThread().interrupt();
                return new Tuple<>(false, null);
            } catch (ExecutionException e) {
                logger.warn(e.getCause().getMessage());
                return new Tuple<>(false, null);
            }
        }
        return new Tuple<>(true, null);
    }

    private void setNodeChildren(BuildNode node) {
        logger.debug("Dependencies of {}:{}", node.getProjectId(), node.getBranchRef());
        Project project = projectService.getProject(node.getProjectId());
        Branch branch = project.getRepository().getBranches().get(node.getBranchRef());
        for (DependencyDescriptor desc : branch.getDependencies()) {
            logger.debug("- " + desc.getProjectId() + ":" + desc.getBranchName());
            Project pro = projectService.getProject(desc.getProjectId());
            Branch dep = pro.getRepository().getBranches().get(desc.getBranchName());
            boolean cycle = false;
            for (BuildNode n : nodes) {
                if (n.getProjectId() == pro.getId() && n.getBranchRef().equals(dep.getName())) {
                    if (!warned) {
                        logger.warn("Cycles in dependencies detected for project {} on branch {}.",
                                project.getId(), branch.getName());
                        logger.debug("Not adding already existing dependency...");
                        warned = true;
                    }
                    cycle = true;
                }
            }
            if (cycle) {
                cycle = false;
            } else {
                BuildNode child = new BuildNode(desc.getProjectId(), desc.getBranchName());
                child.setParent(node);
                node.addChild(child);
                nodes.add(child);
            }
        }
    }

    private void init(int id, String ref) {
        nodes = new ArrayList<>();
        this.root = new BuildNode(id, ref);
        nodes.add(root);
        warned = false;
    }

    /*
     * Constructs a build-subtree, starting from root.
     * Runs forever if dependency graph is cyclic. Cycle checking
     * is done in setNodeChildren and (hopefully) works correctly.
     */
    private void constructBuildTree(BuildNode root) {
        setNodeChildren(root);
        if (root.getChildren().size() != 0) {
            for (BuildNode child : root.getChildren()) {
                constructBuildTree(child);
            }
        }
    }

    private Stack<BuildNode> getBuildStack(BuildNode root) {
        constructBuildTree(root);
        Stack<BuildNode> ret = new Stack<BuildNode>();
        Queue<BuildNode> queue = new LinkedList<BuildNode>();
        queue.add(root);
        int i = 0;
        while (!queue.isEmpty()) {
            BuildNode current = queue.poll();
            current.setLayer(i);
            ret.push(current);
            queue.addAll(current.getChildren());
            i++;
        }
        return ret;
    }

    /**
     * Node to apply inverted breadth-first-search onto dependency tree.
     */
    @Getter
    private class BuildNode {

        private int projectId;
        private String branchRef;

        private int layer;

        private BuildNode parent;
        private List<BuildNode> children;

        private BuildNode(int projectId, String branchRef) {
            this.projectId = projectId;
            this.branchRef = branchRef;
            this.children = new ArrayList<>();
            this.layer = -1;
        }

        private void addChild(BuildNode child) {
            children.add(child);
        }

        private void setParent(BuildNode parent) {
            this.parent = parent;
        }

        private void setLayer(int layer) {
            this.layer = layer;
        }

    }

}
