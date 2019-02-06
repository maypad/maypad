package de.fraunhofer.iosb.maypadbackend.services.sse;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventData {

    @JsonIgnore
    private String eventId;

    private Integer projectgroupId;
    private Integer projectId;
    private String branch;

    public EventData(String eventId, Integer projectgroupId, Integer projectId, String branch) {
        this.eventId = eventId;
        this.projectgroupId = projectgroupId;
        this.projectId = projectId;
        this.branch = branch;
    }

    public static Builder builder() {
        return new BuilderImpl();
    }

    public static interface Builder {

        Builder eventId(String id);

        Builder projectgroupId(Integer id);

        Builder projectId(Integer id);

        Builder branch(String ref);

        EventData build();
    }

    private static class BuilderImpl implements Builder {

        private String eventId;

        private Integer projectgroupId;

        private Integer projectId;

        private String branch;

        public Builder eventId(String id) {
            this.eventId = id;
            return this;
        }

        @Override
        public Builder projectgroupId(Integer id) {
            this.projectgroupId = id;
            return this;
        }

        @Override
        public Builder projectId(Integer id) {
            this.projectId = id;
            return this;
        }

        @Override
        public Builder branch(String ref) {
            this.branch = ref;
            return this;
        }

        @Override
        public EventData build() {
            return new EventData(this.eventId, this.projectgroupId, this.projectId, this.branch);
        }
    }
}
