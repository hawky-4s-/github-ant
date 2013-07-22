package com.livingoz.github.ant;

import org.apache.tools.ant.Project;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.junit.*;

import java.io.IOException;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GitHubRepositoryTest {

  public static final String CAMUNDA_BPM_PLATFORM = "camunda-bpm-platform";

  String repositoryUrl = "git@github.com:camunda/camunda-bpm-platform.git";
  String postfix = "-releasetest";

  @BeforeClass
  public static void startup() {
  }

  @AfterClass
  public static void shutdown() {
  }

  @Test
  public void resolveCredentialsFromPath() {
    Project project = new Project();
    project.setProperty("github.ant.filepath", "src/test/resources/");
    Credentials credentials = GitHubUtil.getCredentials(project);

    assertEquals("testuser", credentials.getUser());
    assertEquals("testpassword", credentials.getPassword());
  }

  @Test
  public void resolveCredentialsFromCmdLine() {
    Project project = new Project();
    project.setProperty("github.ant.user", "testuser_cmdline");
    project.setProperty("github.ant.password", "testpassword_cmdline");
    Credentials credentials = GitHubUtil.getCredentials(project);

    assertEquals("testuser_cmdline", credentials.getUser());
    assertEquals("testpassword_cmdline", credentials.getPassword());
  }

  @Test
  public void checkCredentialsInitializationPrecedence() {
    // oauth > user + password
    Project project = new Project();
    project.setProperty("github.ant.user", "testuser_cmdline");
    project.setProperty("github.ant.password", "testpassword_cmdline");
    project.setProperty("github.ant.oauth", "testoauth_cmdline");

    Credentials credentials = GitHubUtil.getCredentials(project);
    GitHubClient gitHubClient = GitHubUtil.createGitHubClientFromCredentials(credentials);

    assertNull(gitHubClient.getUser());
  }

  @Test
  public void parseRepositoryFromUrl() throws IOException {
    RepositoryId repositoryId = GitHubUtil.parseRepositoryFromUrl(repositoryUrl);
    assertEquals("camunda/" + CAMUNDA_BPM_PLATFORM, repositoryId.generateId());
  }

  @Test
  public void forkRepositoryFromUrl() throws IOException {
    GitHubForkRepositoryTask gitHubForkRepositoryTask = new GitHubForkRepositoryTask();

    cleanRepository(gitHubForkRepositoryTask, "hawky-4s-", CAMUNDA_BPM_PLATFORM);

    Repository repository = gitHubForkRepositoryTask.forkRepository(gitHubForkRepositoryTask.getCredentials(), repositoryUrl, postfix);
    assertEquals("hawky-4s-/" + CAMUNDA_BPM_PLATFORM + postfix, repository.generateId());
  }

  @Test
  public void checkBranchOrTagExists() throws IOException {
    String branchOrTagName = "7.0.0-alpha1";
    GitHubCheckForBranchOrTagTask gitHubCheckForBranchOrTagTask = new GitHubCheckForBranchOrTagTask();
    assertTrue(gitHubCheckForBranchOrTagTask.branchOrTagExists(repositoryUrl, "", branchOrTagName));
  }

  @Ignore
  @Test
  public void deleteRepository() throws IOException {
    Credentials credentials = GitHubUtil.getCredentials(null);
    GitHubClient gitHubClient = GitHubUtil.createGitHubClientFromCredentials(credentials);

    GitHubUtil.deleteRepository(gitHubClient, RepositoryId.create(gitHubClient.getUser(), CAMUNDA_BPM_PLATFORM));
  }

  private void cleanRepository(GitHubRepositoryServiceAction gitHubRepositoryServiceAction, String user, String repositoryName) throws IOException {
    if (GitHubUtil.repositoryExists(gitHubRepositoryServiceAction.getRepositoryService(), user, repositoryName)) {
      GitHubUtil.deleteRepository(gitHubRepositoryServiceAction.getGitHubClient(),
          RepositoryId.create(gitHubRepositoryServiceAction.getGitHubClient().getUser(), repositoryName));
    }
  }
}
