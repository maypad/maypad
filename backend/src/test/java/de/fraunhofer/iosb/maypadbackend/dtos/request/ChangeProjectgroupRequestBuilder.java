package de.fraunhofer.iosb.maypadbackend.dtos.request;

public final class ChangeProjectgroupRequestBuilder {
    private String name;

    private ChangeProjectgroupRequestBuilder() {
    }

    public static ChangeProjectgroupRequestBuilder create() {
        return new ChangeProjectgroupRequestBuilder();
    }

    public ChangeProjectgroupRequestBuilder name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Build the object.
     * @return the built object.
     */
    public ChangeProjectgroupRequest build() {
        ChangeProjectgroupRequest changeProjectgroupRequest = new ChangeProjectgroupRequest();
        changeProjectgroupRequest.setName(name);
        return changeProjectgroupRequest;
    }
}
