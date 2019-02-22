package de.fraunhofer.iosb.maypadbackend.model.repository;

import de.fraunhofer.iosb.maypadbackend.model.Status;
import de.fraunhofer.iosb.maypadbackend.model.build.Build;
import de.fraunhofer.iosb.maypadbackend.model.person.Mail;
import de.fraunhofer.iosb.maypadbackend.model.person.Person;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class BranchTest {

    @Test
    public void compareAndUpdate() {
        List<Person> members = new ArrayList<>();
        List<Mail> mails = new ArrayList<>();
        List<DependencyDescriptor> dependencies = new ArrayList<>();
        members.add(new Person("Peter"));
        mails.add(new Mail("test@mail.de"));
        dependencies.add(new DependencyDescriptor(1, "master"));
        Branch branch = BranchBuilder.create().name("master").description("desc").members(new ArrayList<>())
                .mails(new ArrayList<>()).dependencies(new ArrayList<>()).build();
        Branch compareBranch = BranchBuilder.create().name("masterNew").description("newDesc").members(members)
                .mails(mails).dependencies(dependencies).build();
        branch.compareAndUpdate(compareBranch);
        assertThat(branch.getName()).isEqualTo("masterNew");
        assertThat(branch.getDescription()).isEqualTo("newDesc");
        assertThat(branch.getMails()).isEqualTo(mails); //we have only one element in this list, so we haven't to check the order
        assertThat(branch.getMembers()).isEqualTo(members);
        assertThat(branch.getDependencies()).isEqualTo(dependencies);
    }

    @Test
    public void compareAndUpdateWithNullBranch() {
        Branch branch = BranchBuilder.create().name("master").description("desc").build();
        branch.compareAndUpdate(null);
        assertThat(branch.getName()).isEqualTo("master");
        assertThat(branch.getDescription()).isEqualTo("desc");
    }

    @Test
    public void updateStatus() {
        Branch branch = new Branch();
        List<Build> builds = new ArrayList<>();
        builds.add(new Build(new Date(0), null, Status.SUCCESS));
        builds.add(new Build(new Date(10), null, Status.FAILED));
        branch.setBuilds(builds);
        assertThat(branch.updateStatus()).isEqualTo(Status.SUCCESS);
    }
}