package de.fraunhofer.iosb.maypadbackend.model.repository;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DependencyDescriptorTest {

    @Test
    public void dependencyDescriptortoString() {
        assertThat((new DependencyDescriptor(42, "master")).toString()).isEqualTo("42:master");
    }
}