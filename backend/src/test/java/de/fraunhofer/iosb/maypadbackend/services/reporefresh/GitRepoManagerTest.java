package de.fraunhofer.iosb.maypadbackend.services.reporefresh;

import de.fraunhofer.iosb.maypadbackend.model.Project;
import de.fraunhofer.iosb.maypadbackend.model.ProjectBuilder;
import de.fraunhofer.iosb.maypadbackend.model.repository.Repository;
import de.fraunhofer.iosb.maypadbackend.model.repository.RepositoryType;
import de.fraunhofer.iosb.maypadbackend.model.serviceaccount.KeyServiceAccount;
import de.fraunhofer.iosb.maypadbackend.model.serviceaccount.UserServiceAccount;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LsRemoteCommand;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.TransportCommand;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.eclipse.jgit.lib.ObjectIdRef;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.RefDatabase;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GitRepoManagerTest {

    @Mock
    private Git git;
    @Mock
    private org.eclipse.jgit.lib.Repository gitRepository;
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    private Project project;
    private GitRepoManager gitRepoManager;

    @Before
    public void setup() throws Exception {
        project = ProjectBuilder.create().id(1).repositoryUrl("https://maypadrepo.git").repository(new Repository(RepositoryType.GIT)).build();
        gitRepoManager = new GitRepoManager(project, git);
        gitRepoManager.initRepoManager(folder.newFolder(), folder.newFolder());
    }

    @Test
    public void getBranches() throws Exception {
        Collection<Ref> result = new ArrayList<>();
        result.add(new ObjectIdRef.PeeledNonTag(Ref.Storage.NEW, "refs/heads/master", null));
        result.add(new ObjectIdRef.PeeledNonTag(Ref.Storage.NEW, "refs/heads/testbranch", null));
        LsRemoteCommand lsRemoteCommand = Mockito.mock(LsRemoteCommand.class);
        when(git.lsRemote()).thenReturn(lsRemoteCommand);
        when(lsRemoteCommand.setHeads(true)).thenReturn(lsRemoteCommand);
        when(lsRemoteCommand.call()).thenReturn(result);
        List<String> branchNames = gitRepoManager.getBranchNames();
        assertThat(branchNames.size()).isEqualTo(2);
        assertThat(branchNames.contains("master")).isTrue();
        assertThat(branchNames.contains("testbranch")).isTrue();
    }

    @Test
    public void getBranchesWithUserServiceAccount() throws Exception {
        UserServiceAccount serviceAccount = Mockito.mock(UserServiceAccount.class);
        project.setServiceAccount(serviceAccount);
        when(serviceAccount.getUsername()).thenReturn("username");
        when(serviceAccount.getPassword()).thenReturn("password");
        Collection<Ref> result = new ArrayList<>();
        result.add(new ObjectIdRef.PeeledNonTag(Ref.Storage.NEW, "refs/heads/master", null));
        LsRemoteCommand lsRemoteCommand = Mockito.mock(LsRemoteCommand.class);
        when(git.lsRemote()).thenReturn(lsRemoteCommand);
        when(lsRemoteCommand.setHeads(true)).thenReturn(lsRemoteCommand);
        when(lsRemoteCommand.call()).thenReturn(result);
        List<String> branchNames = gitRepoManager.getBranchNames();
        assertThat(branchNames.size()).isEqualTo(1);
        assertThat(branchNames.contains("master")).isTrue();
        verify(((TransportCommand) lsRemoteCommand)).setCredentialsProvider(any(UsernamePasswordCredentialsProvider.class));
    }

    @Test
    public void getBranchesWithKeyServiceAccount() throws Exception {
        KeyServiceAccount serviceAccount = Mockito.mock(KeyServiceAccount.class);
        project.setServiceAccount(serviceAccount);
        Collection<Ref> result = new ArrayList<>();
        result.add(new ObjectIdRef.PeeledNonTag(Ref.Storage.NEW, "refs/heads/master", null));
        LsRemoteCommand lsRemoteCommand = Mockito.mock(LsRemoteCommand.class);
        when(git.lsRemote()).thenReturn(lsRemoteCommand);
        when(lsRemoteCommand.setHeads(true)).thenReturn(lsRemoteCommand);
        when(lsRemoteCommand.call()).thenReturn(result);
        List<String> branchNames = gitRepoManager.getBranchNames();
        assertThat(branchNames.size()).isEqualTo(1);
        assertThat(branchNames.contains("master")).isTrue();
        verify(((TransportCommand) lsRemoteCommand)).setTransportConfigCallback(any(TransportConfigCallback.class));
    }

    @Test
    public void getBranchesWithGitError() throws Exception {
        LsRemoteCommand lsRemoteCommand = Mockito.mock(LsRemoteCommand.class);
        when(git.lsRemote()).thenReturn(lsRemoteCommand);
        when(lsRemoteCommand.setHeads(true)).thenReturn(lsRemoteCommand);
        when(lsRemoteCommand.call()).thenThrow(new WrongRepositoryStateException("Invalid state"));
        List<String> branchNames = gitRepoManager.getBranchNames();
        assertThat(branchNames.size()).isEqualTo(0);
    }

    @Test
    public void getBranchesNoBranchesExists() throws Exception {
        LsRemoteCommand lsRemoteCommand = Mockito.mock(LsRemoteCommand.class);
        when(git.lsRemote()).thenReturn(lsRemoteCommand);
        when(lsRemoteCommand.setHeads(true)).thenReturn(lsRemoteCommand);
        when(lsRemoteCommand.call()).thenReturn(null);
        List<String> branchNames = gitRepoManager.getBranchNames();
        assertThat(branchNames.size()).isEqualTo(0);
    }

    @Test
    public void switchBranchWithoutRepo() {
        when(git.getRepository()).thenReturn(null);
        assertThat(gitRepoManager.switchBranch("master")).isFalse();
    }

    @Test
    public void switchBranchToCurrentBranch() throws Exception {
        when(git.getRepository()).thenReturn(gitRepository);
        when(gitRepository.getBranch()).thenReturn("master");
        PullCommand pullCommand = Mockito.mock(PullCommand.class);
        when(git.pull()).thenReturn(pullCommand);
        ResetCommand resetCommand = Mockito.mock(ResetCommand.class);
        when(git.reset()).thenReturn(resetCommand);
        when(resetCommand.setMode(any())).thenReturn(resetCommand);
        when(pullCommand.setStrategy(any())).thenReturn(pullCommand);
        assertThat(gitRepoManager.switchBranch("master")).isTrue();
        verify(pullCommand).call();
    }

    @Test
    public void switchBranchToOtherBranch() throws Exception {
        when(git.getRepository()).thenReturn(gitRepository);
        when(gitRepository.getBranch()).thenReturn("master");
        PullCommand pullCommand = Mockito.mock(PullCommand.class);
        when(git.pull()).thenReturn(pullCommand);
        when(pullCommand.setStrategy(any())).thenReturn(pullCommand);

        RefDatabase db = Mockito.mock(RefDatabase.class);
        when(gitRepository.getRefDatabase()).thenReturn(db);
        Ref ref = Mockito.mock(Ref.class);
        when(db.exactRef(anyString())).thenReturn(ref);

        CheckoutCommand checkoutCommand = Mockito.mock(CheckoutCommand.class);
        when(git.checkout()).thenReturn(checkoutCommand);
        when(checkoutCommand.setName("testbranch")).thenReturn(checkoutCommand);
        when(checkoutCommand.setCreateBranch(anyBoolean())).thenReturn(checkoutCommand);

        assertThat(gitRepoManager.switchBranch("testbranch")).isTrue();
        verify(pullCommand).call();
    }

    @Test
    public void switchBranchToOtherBranchWithError() throws Exception {
        when(git.getRepository()).thenReturn(gitRepository);
        when(gitRepository.getBranch()).thenReturn("master");

        when(git.getRepository()).thenReturn(gitRepository);
        when(gitRepository.getBranch()).thenReturn("master");

        RefDatabase db = Mockito.mock(RefDatabase.class);
        when(gitRepository.getRefDatabase()).thenReturn(db);
        when(db.exactRef(anyString())).thenThrow(IOException.class);

        assertThat(gitRepoManager.switchBranch("testbranch")).isFalse();
    }

    @Test
    public void switchBranchWithoutDetectCurrentBranch() throws Exception {
        when(git.getRepository()).thenReturn(gitRepository);
        when(git.getRepository().getBranch()).thenThrow(new IOException());
        PullCommand pullCommand = Mockito.mock(PullCommand.class);
        when(git.pull()).thenReturn(pullCommand);
        when(pullCommand.setStrategy(any())).thenReturn(pullCommand);

        RefDatabase db = Mockito.mock(RefDatabase.class);
        when(gitRepository.getRefDatabase()).thenReturn(db);
        Ref ref = Mockito.mock(Ref.class);
        when(db.exactRef(anyString())).thenReturn(ref);

        CheckoutCommand checkoutCommand = Mockito.mock(CheckoutCommand.class);
        when(git.checkout()).thenReturn(checkoutCommand);
        when(checkoutCommand.setName("testbranch")).thenReturn(checkoutCommand);
        when(checkoutCommand.setCreateBranch(anyBoolean())).thenReturn(checkoutCommand);

        assertThat(gitRepoManager.switchBranch("testbranch")).isTrue();
        verify(pullCommand).call();
    }
}
