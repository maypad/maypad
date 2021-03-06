package de.fraunhofer.iosb.maypadbackend.dtos.request;

public final class ChangeProjectRequestBuilder {
    private ServiceAccountRequest serviceAccount;

    private ChangeProjectRequestBuilder() {
    }

    public static ChangeProjectRequestBuilder create() {
        return new ChangeProjectRequestBuilder();
    }

    public ChangeProjectRequestBuilder serviceAccount(ServiceAccountRequest serviceAccount) {
        this.serviceAccount = serviceAccount;
        return this;
    }

    /**
     * Build the object.
     * @return the built object.
     */
    public ChangeProjectRequest build() {
        ChangeProjectRequest changeProjectRequest = new ChangeProjectRequest();
        changeProjectRequest.setServiceAccount(serviceAccount);
        return changeProjectRequest;
    }
}
