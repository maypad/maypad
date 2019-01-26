package de.fraunhofer.iosb.maypadbackend.dtos.mapper;

import de.fraunhofer.iosb.maypadbackend.dtos.response.BranchResponse;
import de.fraunhofer.iosb.maypadbackend.model.person.Mail;
import de.fraunhofer.iosb.maypadbackend.model.person.Person;
import de.fraunhofer.iosb.maypadbackend.model.repository.Branch;
import de.fraunhofer.iosb.maypadbackend.model.repository.DependencyDescriptor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;


/**
 * Interface for mapping a Branch-Entity to a Branch-Response DTO.
 */
@Mapper(componentModel = "spring", uses = CommitMapper.class)
public interface BranchMapper {
    @Mappings({
            @Mapping(source = "branch", target = "dependencies",
                    qualifiedByName = "toDependencies"),
            @Mapping(source = "branch", target = "members", qualifiedByName = "toMembers"),
            @Mapping(source = "buildType.name", target = "buildWebhook"),
            @Mapping(source = "deploymentType.name", target = "deploymentWebhook"),
            @Mapping(source = "buildSuccessWebhook.url", target = "buildSuccessUrl"),
            @Mapping(source = "buildFailureWebhook.url", target = "buildFailureUrl"),
            @Mapping(source = "branch", target = "mails", qualifiedByName = "toMails")
    })
    public BranchResponse toResponse(Branch branch);

    public List<BranchResponse> toResponseList(List<Branch> branches);

    /**
     * Maps a branch to an array of dependencies (branch-ids).
     *
     * @param branch that should be mapped
     * @return array of dependencies associated with the given branch
     */
    @Named("toDependencies")
    default String[] toDependencies(Branch branch) {
        Set<DependencyDescriptor> dependencies = branch.getDependencies();
        String[] dependenciesArr = new String[dependencies.size()];
        int i = 0;
        for (DependencyDescriptor d : dependencies) {
            String dep = d.getProjectId() + ":" + d.getBranchName();
            dependenciesArr[i] = dep;
            i++;
        }
        return dependenciesArr;
    }

    /**
     * Maps a branch to an array of member names.
     *
     * @param branch that should be mapped
     * @return array of member names associated with the given branch
     */
    @Named("toMembers")
    default String[] toMembers(Branch branch) {
        Set<Person> members = branch.getMembers();
        String[] membersArr = new String[members.size()];
        int i = 0;
        for (Person person : members) {
            membersArr[i] = person.getName();
            i++;
        }
        return membersArr;
    }

    /**
     * Maps a branch to an array of mails.
     *
     * @param branch that should be mapped
     * @return array of mails associated with the given branch
     */
    @Named("toMails")
    default String[] toMails(Branch branch) {
        Set<Mail> mails = branch.getMails();
        String[] mailsArr = new String[mails.size()];
        int i = 0;
        for (Mail mail : mails) {
            mailsArr[i] = mail.getMailAddress();
            i++;
        }
        return mailsArr;
    }
}
