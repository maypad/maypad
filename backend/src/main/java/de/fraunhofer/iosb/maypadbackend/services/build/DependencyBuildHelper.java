package de.fraunhofer.iosb.maypadbackend.services.build;

import de.fraunhofer.iosb.maypadbackend.model.Project;
import de.fraunhofer.iosb.maypadbackend.model.Status;
import de.fraunhofer.iosb.maypadbackend.model.repository.Branch;
import de.fraunhofer.iosb.maypadbackend.model.repository.DependencyDescriptor;
import de.fraunhofer.iosb.maypadbackend.repositories.BranchRepository;
import de.fraunhofer.iosb.maypadbackend.services.ProjectService;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

/**
 * Provides method to execute builds starting with least dependent dependency.
 *
 * @Author Max Willich
 *
 */
@Component
public class DependencyBuildHelper {

    private static Logger logger = LoggerFactory.getLogger(DependencyBuildHelper.class);
    private Branch branch;
    private Project project;
    private boolean warned; // Warn flag, so that cycle warning prints only once
    private List<BuildNode> nodes; // For cycle detection
    private BuildNode root;
    private final int refreshFrequency = 60; // Seconds

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private BuildService buildService;

    /**
     * Constructor for DependencyBuildHelper.
     *
     * @param projectId Id of the project on which to execute the build.
     * @param branchRef Reference of the branch (usually name of branch) to build.
     */
    public DependencyBuildHelper(int projectId, String branchRef) {
        this.project = projectService.getProject(projectId);
        this.branch = project.getRepository().getBranches().get(branchRef);
        nodes = new ArrayList<BuildNode>();
        this.root = new BuildNode(project, branch);
        nodes.add(root);
        warned = false;
    }

    private void setNodeChildren(BuildNode node) {
        for (DependencyDescriptor desc : node.getBranch().getDependencies()) {
            Branch dep = node.getProject().getRepository().getBranches().get(desc.getBranchName());
            Project pro = projectService.getProject(desc.getProjectId());
            boolean cycle = false;
            for (BuildNode n : nodes) {
                if (n.getProject() == pro && n.getBranch() == dep) {
                    if (!warned) {
                        logger.warn("You have cycles in your Maypad dependencies!");
                        logger.error("Not adding already existing dependency...");
                        warned = true;
                    }
                    cycle = true;
                }
            }
            if (cycle) {
                cycle = false;
            } else {
                BuildNode child = new BuildNode(pro, dep);
                child.setParent(node);
                node.addChild(child);
            }
        }
    }

    /**
     * Runs a build with dependencies. It starts the lowest "layer" of builds
     * first, which are the leaves in the dependency tree. Potential cycles
     * in dependencies are ignored.
     *
     * @return true if all builds were successful, false otherwise.
     */
    public boolean runBuildWithDependencies() {
        Stack<BuildNode> buildStack = getBuildStack(root);
        int highestLayer = buildStack.peek().getLayer();
        List<BuildNode> currentLayer = new ArrayList<BuildNode>();
        for (int i = highestLayer; i >= 0; i--) {
            // Get all branches on that layer, run builds
            currentLayer.clear();
            while (buildStack.peek().getLayer() == i) {
                currentLayer.add(buildStack.pop());
            }
            for (BuildNode node : currentLayer) {
                buildService.buildBranch(node.getProject().getId(), node.getBranch().getName(), false, "build");
            }
            // Wait until all builds have finished successfully
            long time = System.currentTimeMillis();
            while (true) {
                // Dont check every cycle, but only every n seconds (60 default).
                if (System.currentTimeMillis() - time < refreshFrequency * 1000) {
                    time = System.currentTimeMillis();
                } else {
                    boolean done = true;
                    for (BuildNode node : currentLayer) {
                        Status status = node.getBranch().getBuildStatus();
                        if (status != Status.SUCCESS) {
                            done = false;
                            if (status == Status.ERROR) {
                                logger.error("Error building project " + node.getProject().getName());
                                logger.error("on branch " + node.getBranch().getName());
                                return false;
                            }
                        }
                    }
                    if (done) {
                        break;
                    }
                }
            }
        }
        return true;
    }

    /*
    * Constructs a build-subtree, starting from root.
    * Runs forever if dependency graph is cyclic. Cycle checking
    * is done in setNodeChildren and (hopefully) works correctly.
     */
    private void constructBuildTree(BuildNode root) {
        setNodeChildren(root);
        if (root.getChildren().size() == 0) {
            return;
        } else {
            for (BuildNode child : root.getChildren()) {
                constructBuildTree(child);
            }
        }
    }

    private Stack<BuildNode> getBuildStack(BuildNode root) {
        Stack<BuildNode> ret = new Stack<BuildNode>();
        Queue<BuildNode> queue = new LinkedList<BuildNode>();
        queue.add(root);
        int i = 0;
        while (!queue.isEmpty()) {
            BuildNode current = queue.poll();
            current.setLayer(i);
            ret.push(current);
            for (BuildNode c : current.getChildren()) {
                queue.add(c);
            }
            i++;
        }
        return ret;
    }

    /**
     * Node to apply inverted breadth-first-search onto dependency tree.
     */
    @Getter
    private class BuildNode {

        private Project project;
        private Branch branch;

        private int layer;

        private BuildNode parent;
        private List<BuildNode> children;

        public BuildNode(Project project, Branch branch) {
            this.project = project;
            this.branch = branch;
            this.children = new ArrayList<BuildNode>();
            this.layer = -1;
        }

        public void addChild(BuildNode child) {
            children.add(child);
        }

        public void setParent(BuildNode parent) {
            this.parent = parent;
        }

        public void setLayer(int layer) {
            this.layer = layer;
        }

    }

}
