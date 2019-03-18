package de.fraunhofer.iosb.maypadbackend.services.sse;

public enum MessageType {

    INFO("info"),

    ERROR("error"),

    WARNING("warning");

    private String messageType;

    MessageType(String mt) {
        this.messageType = mt;
    }

    public String getMessageType() {
        return messageType;
    }

    @Override
    public String toString() {
        return messageType;
    }

}
