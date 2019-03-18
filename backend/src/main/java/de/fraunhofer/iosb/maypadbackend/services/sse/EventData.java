package de.fraunhofer.iosb.maypadbackend.services.sse;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import de.fraunhofer.iosb.maypadbackend.model.Status;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The data object needed for the server-sent events.
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
    private String name;
    private MessageType messageType;
    private String event;

    /**
     * Constructor for EventData.
     *
     * @param eventId        the id of the event
     * @param projectgroupId the if of the projectgoup
     * @param projectId      the id of the project
     * @param name           the branch
     * @param messageType    the message type (e.g. error or info)
     */
    private EventData(String eventId, Integer projectgroupId, Integer projectId, String name, MessageType messageType, String event) {
        this.eventId = eventId;
        this.projectgroupId = projectgroupId;
        this.projectId = projectId;
        this.name = name;
        this.messageType = messageType;
        this.event = event;
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
         * Sets the name.
         *
         * @param ref the value of the name field
         * @return this builder
         */
        Builder name(String ref);

        /**
         * Sets the status.
         *
         * @param type the value of the type field.
         * @return this builder
         */
        Builder type(MessageType type);

        /**
         * Sets detailed message (for example for stack traces).
         *
         * @param event The message.
         * @return This builder.
         */
        Builder event(String event);

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

        private String name;

        private MessageType messageType;

        private String event;

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
        public Builder name(String ref) {
            this.name = ref;
            return this;
        }

        @Override
        public Builder event(String event) {
            this.event = event;
            return this;
        }

        @Override
        public Builder type(MessageType type) {
            this.messageType = type;
            return this;
        }

        @Override
        public EventData build() {
            return new EventData(this.eventId, this.projectgroupId, this.projectId, this.name, this.messageType, this.event);
        }
    }
}
