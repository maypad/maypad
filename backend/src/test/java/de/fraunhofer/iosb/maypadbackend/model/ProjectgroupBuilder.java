package de.fraunhofer.iosb.maypadbackend.model;

import java.util.List;

public final class ProjectgroupBuilder {
    private int id;
    private String name;
    private Status buildStatus;
    private List<Project> projects;

    private ProjectgroupBuilder() {
    }

    public static ProjectgroupBuilder create() {
        return new ProjectgroupBuilder();
    }

    public ProjectgroupBuilder id(int id) {
        this.id = id;
        return this;
    }

    public ProjectgroupBuilder name(String name) {
        this.name = name;
        return this;
    }

    public ProjectgroupBuilder buildStatus(Status buildStatus) {
        this.buildStatus = buildStatus;
        return this;
    }

    public ProjectgroupBuilder projects(List<Project> projects) {
        this.projects = projects;
        return this;
    }

    /**
     * Build the object.
     * @return the built object.
     */
    public Projectgroup build() {
        Projectgroup projectgroup = new Projectgroup();
        projectgroup.setId(id);
        projectgroup.setName(name);
        projectgroup.setBuildStatus(buildStatus);
        projectgroup.setProjects(projects);
        return projectgroup;
    }
}
