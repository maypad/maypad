package de.fraunhofer.iosb.maypadbackend.dtos.mapper.request;

import de.fraunhofer.iosb.maypadbackend.dtos.request.ServiceAccountRequest;

import java.util.Optional;

public final class ServiceAccountRequestBuilder {
    private Optional<String> username;
    private Optional<String> password;
    private Optional<String> sshKey;

    private ServiceAccountRequestBuilder() {
    }

    public static ServiceAccountRequestBuilder create() {
        return new ServiceAccountRequestBuilder();
    }

    public ServiceAccountRequestBuilder username(Optional<String> username) {
        this.username = username;
        return this;
    }

    public ServiceAccountRequestBuilder password(Optional<String> password) {
        this.password = password;
        return this;
    }

    public ServiceAccountRequestBuilder sshKey(Optional<String> sshKey) {
        this.sshKey = sshKey;
        return this;
    }

    /**
     * Build the object.
     * @return the built object.
     */
    public ServiceAccountRequest build() {
        ServiceAccountRequest serviceAccountRequest = new ServiceAccountRequest();
        serviceAccountRequest.setUsername(username);
        serviceAccountRequest.setPassword(password);
        serviceAccountRequest.setSshKey(sshKey);
        return serviceAccountRequest;
    }
}
