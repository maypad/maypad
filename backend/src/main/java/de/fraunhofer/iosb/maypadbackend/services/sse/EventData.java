package de.fraunhofer.iosb.maypadbackend.services.sse;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The data object needed for the server sent event.
 */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventData {

    @JsonIgnore
    private String eventId;

    private Integer projectgroupId;
    private Integer projectId;
    private String branch;

    /**
     * Constrcutor for EventData.
     *
     * @param eventId        the id of the event
     * @param projectgroupId the if of the projectgoup
     * @param projectId      the id of the project
     * @param branch         the branch
     */
    private EventData(String eventId, Integer projectgroupId, Integer projectId, String branch) {
        this.eventId = eventId;
        this.projectgroupId = projectgroupId;
        this.projectId = projectId;
        this.branch = branch;
    }

    /**
     * Returns a builder for an EventData object.
     *
     * @return the builder
     */
    public static Builder builder() {
        return new BuilderImpl();
    }

    /**
     * Returns a builder for an EventData object populated with the given eventType.
     *
     * @param eventType the type of the event
     * @return the builder
     */
    public static Builder builder(SseEventType eventType) {
        return new BuilderImpl().eventId(eventType.getEventId());
    }


    public interface Builder {

        /**
         * Sets the eventId.
         *
         * @param id the value of the eventId field
         * @return this builder
         */
        Builder eventId(String id);

        /**
         * Sets the projectgroupId.
         *
         * @param id the value of the projectgroupId field
         * @return this builder
         */
        Builder projectgroupId(Integer id);

        /**
         * Sets the projectId.
         *
         * @param id the value of the projectId field
         * @return this builder
         */
        Builder projectId(Integer id);

        /**
         * Sets the branch.
         *
         * @param ref the value of the branch field
         * @return this builder
         */
        Builder branch(String ref);

        /**
         * Builds the eventdata.
         *
         * @return the built eventdata object
         */
        EventData build();

    }

    private static class BuilderImpl implements Builder {

        private String eventId;

        private Integer projectgroupId;

        private Integer projectId;

        private String branch;

        @Override
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
