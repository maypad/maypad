package de.fraunhofer.iosb.maypadbackend.model.build;

import de.fraunhofer.iosb.maypadbackend.model.Status;
import de.fraunhofer.iosb.maypadbackend.model.repository.Commit;

import java.util.Date;

public final class BuildBuilder {
    private int id;
    private Date timestamp;
    private Commit commit;
    private Status status;

    private BuildBuilder() {
    }

    public static BuildBuilder create() {
        return new BuildBuilder();
    }

    public BuildBuilder id(int id) {
        this.id = id;
        return this;
    }

    public BuildBuilder timestamp(Date timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public BuildBuilder commit(Commit commit) {
        this.commit = commit;
        return this;
    }

    public BuildBuilder status(Status status) {
        this.status = status;
        return this;
    }

    /**
     * Build the object.
     * @return the built object.
     */
    public Build build() {
        Build build = new Build();
        build.setId(id);
        build.setTimestamp(timestamp);
        build.setCommit(commit);
        build.setStatus(status);
        return build;
    }
}
