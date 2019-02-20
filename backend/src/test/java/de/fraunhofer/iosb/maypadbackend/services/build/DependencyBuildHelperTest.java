package de.fraunhofer.iosb.maypadbackend.services.build;

import de.fraunhofer.iosb.maypadbackend.model.Project;
import de.fraunhofer.iosb.maypadbackend.model.ProjectBuilder;
import de.fraunhofer.iosb.maypadbackend.model.Status;
import de.fraunhofer.iosb.maypadbackend.model.repository.Branch;
import de.fraunhofer.iosb.maypadbackend.model.repository.BranchBuilder;
import de.fraunhofer.iosb.maypadbackend.model.repository.DependencyDescriptor;
import de.fraunhofer.iosb.maypadbackend.model.repository.Repository;
import de.fraunhofer.iosb.maypadbackend.services.ProjectService;
import de.fraunhofer.iosb.maypadbackend.util.Tuple;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DependencyBuildHelperTest {

    @Mock
    BuildService buildService;

    @Mock
    ProjectService projectService;

    @InjectMocks
    DependencyBuildHelper dependencyBuildHelper;

    @Test
    public void runBuildWithDependenciesValid() {
        Branch branchDependency1 = BranchBuilder.create() //depends on project
                .name("stable")
                .dependencies(Arrays.asList(new DependencyDescriptor(1, "master")))
                .build();
        Repository repositoryDependency1 = new Repository();
        repositoryDependency1.setBranches(Stream.of(new Object[][] {
                {"stable", branchDependency1}
        }).collect(Collectors.toMap(d -> (String) d[0], d -> (Branch) d[1])));

        Branch branchDependency2 = BranchBuilder.create() //depends on itself
                .name("master")
                .dependencies(Arrays.asList(new DependencyDescriptor(3, "master")))
                .build();
        Repository repositoryDependency2 = new Repository();
        repositoryDependency2.setBranches(Stream.of(new Object[][] {
                {"master", branchDependency2}
        }).collect(Collectors.toMap(d -> (String) d[0], d -> (Branch) d[1])));

        Branch branch = BranchBuilder.create()
                .name("master")
                .dependencies(Arrays.asList(new DependencyDescriptor(2, "stable"),
                        new DependencyDescriptor(3, "master")))
                .build();
        Repository repository = new Repository();
        repository.setBranches(Stream.of(new Object[][] {
                {"master", branch}
        }).collect(Collectors.toMap(d -> (String) d[0], d -> (Branch) d[1])));

        Project project = ProjectBuilder.create()
                .id(1)
                .repository(repository)
                .build();
        Project projectDependency1 = ProjectBuilder.create()
                .id(2)
                .repository(repositoryDependency1)
                .build();
        Project projectDependency2 = ProjectBuilder.create()
                .id(3)
                .repository(repositoryDependency2)
                .build();

        when(projectService.getProject(1)).thenReturn(project);
        when(projectService.getProject(2)).thenReturn(projectDependency1);
        when(projectService.getProject(3)).thenReturn(projectDependency2);
        when(buildService.buildBranch(anyInt(), anyString(), anyBoolean(), eq(null)))
                .thenReturn(CompletableFuture.completedFuture(Status.SUCCESS));

        InOrder inOrderVerifier = inOrder(buildService);

        assertThat(dependencyBuildHelper.runBuildWithDependencies(1, "master"))
                .isEqualTo(new Tuple<>(true, null));

        inOrderVerifier.verify(buildService).buildBranch(3, "master", false, null);
        inOrderVerifier.verify(buildService).buildBranch(2, "stable", false, null);

        inOrderVerifier.verifyNoMoreInteractions();
    }

    @Test
    public void runBuildWithDependenciesFailure() {
        Branch branchDependency1 = BranchBuilder.create() //depends on project3
                .name("stable")
                .dependencies(Arrays.asList(new DependencyDescriptor(3, "master")))
                .build();
        Repository repositoryDependency1 = new Repository();
        repositoryDependency1.setBranches(Stream.of(new Object[][] {
                {"master", branchDependency1}
        }).collect(Collectors.toMap(d -> (String) d[0], d -> (Branch) d[1])));

        Branch branchDependency2 = BranchBuilder.create() //depends on itself
                .name("master")
                .dependencies(Arrays.asList(new DependencyDescriptor(3, "master")))
                .build();
        Repository repositoryDependency2 = new Repository();
        repositoryDependency2.setBranches(Stream.of(new Object[][] {
                {"master", branchDependency2}
        }).collect(Collectors.toMap(d -> (String) d[0], d -> (Branch) d[1])));

        Branch branch = BranchBuilder.create()
                .name("master")
                .dependencies(Arrays.asList(new DependencyDescriptor(2, "master")))
                .build();
        Repository repository = new Repository();
        repository.setBranches(Stream.of(new Object[][] {
                {"master", branch}
        }).collect(Collectors.toMap(d -> (String) d[0], d -> (Branch) d[1])));

        Project project = ProjectBuilder.create()
                .id(1)
                .repository(repository)
                .build();
        Project projectDependency1 = ProjectBuilder.create()
                .id(2)
                .repository(repositoryDependency1)
                .build();
        Project projectDependency2 = ProjectBuilder.create()
                .id(3)
                .repository(repositoryDependency2)
                .build();

        when(projectService.getProject(1)).thenReturn(project);
        when(projectService.getProject(2)).thenReturn(projectDependency1);
        when(projectService.getProject(3)).thenReturn(projectDependency2);
        when(buildService.buildBranch(anyInt(), anyString(), anyBoolean(), eq(null)))
                .thenReturn(CompletableFuture.completedFuture(Status.FAILED));

        InOrder inOrderVerifier = inOrder(buildService);

        assertThat(dependencyBuildHelper.runBuildWithDependencies(1, "master"))
                .isEqualTo(new Tuple<>(false, "3:master"));

        inOrderVerifier.verify(buildService).buildBranch(3, "master", false, null);
        inOrderVerifier.verifyNoMoreInteractions();
    }
}
