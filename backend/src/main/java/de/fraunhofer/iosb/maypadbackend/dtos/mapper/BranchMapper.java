package de.fraunhofer.iosb.maypadbackend.dtos.mapper;

import de.fraunhofer.iosb.maypadbackend.dtos.response.BranchResponse;
import de.fraunhofer.iosb.maypadbackend.model.build.BuildType;
import de.fraunhofer.iosb.maypadbackend.model.deployment.DeploymentType;
import de.fraunhofer.iosb.maypadbackend.model.person.Mail;
import de.fraunhofer.iosb.maypadbackend.model.person.Person;
import de.fraunhofer.iosb.maypadbackend.model.repository.Branch;
import de.fraunhofer.iosb.maypadbackend.model.repository.DependencyDescriptor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.util.List;



/**
 * Interface for mapping a Branch-Entity to a Branch-Response DTO.
 */
@Mapper(componentModel = "spring", uses = CommitMapper.class)
public interface BranchMapper {
    @Mappings({
            @Mapping(source = "branch", target = "dependencies",
                    qualifiedByName = "toDependencies"),
            @Mapping(source = "branch", target = "members", qualifiedByName = "toMembers"),
            @Mapping(source = "buildType", target = "buildWebhook"),
            @Mapping(source = "deploymentType", target = "deploymentWebhook"),
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
        List<DependencyDescriptor> dependencies = branch.getDependencies();
        String[] dependenciesArr = new String[dependencies.size()];
        for (int i = 0; i < dependencies.size(); i++) {
            DependencyDescriptor d = dependencies.get(i);
            String dep = d.getProjectId() + ":" + d.getBranchName();
            dependenciesArr[i] = dep;
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
        List<Person> members = branch.getMembers();
        String[] membersArr = new String[members.size()];
        for (int i = 0; i < members.size(); i++) {
            membersArr[i] = members.get(i).getName();
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
        List<Mail> mails = branch.getMails();
        String[] mailsArr = new String[mails.size()];
        for (int i = 0; i < mails.size(); i++) {
            mailsArr[i] = mails.get(i).getMailAddress();
        }
        return mailsArr;
    }

    default String map(DeploymentType deploymentType) {
        return deploymentType.toString();
    }

    default String map(BuildType buildType) {
        return buildType.toString();
    }
}
