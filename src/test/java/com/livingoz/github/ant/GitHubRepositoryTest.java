package com.livingoz.github.ant;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.junit.*;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GitHubRepositoryTest {

  public static final String CAMUNDA_BPM_PLATFORM = "camunda-bpm-platform";

  String user = "hawky-4s-";
  String password = "TiCh!8180?githubcom";
  String repositoryUrl = "git@github.com:camunda/camunda-bpm-platform.git";
  String postfix = "-releasetest";

  @BeforeClass
  public static void startup() {

  }

  @AfterClass
  public static void shutdown() {

  }

  @Test
  public void parseRepositoryFromUrl() throws IOException {
    RepositoryId repositoryId = GitHubUtil.parseRepositoryFromUrl(repositoryUrl);
    assertEquals("camunda/" + CAMUNDA_BPM_PLATFORM, repositoryId.generateId());
  }

  @Test
  public void forkRepositoryFromUrl() throws IOException {
    GitHubForkRepositoryTask gitHubForkRepositoryTask = new GitHubForkRepositoryTask();
    Repository repository = gitHubForkRepositoryTask.forkRepository(user, password, repositoryUrl, postfix);
    assertEquals("hawky-4s-/" + CAMUNDA_BPM_PLATFORM + postfix, repository.generateId());
  }

  @Test
  public void checkBranchOrTagExists() throws IOException {
    String branchOrTagName = "7.0.0-alpha1";
    GitHubCheckForBranchOrTagTask gitHubCheckForBranchOrTagTask = new GitHubCheckForBranchOrTagTask();
    assertTrue(gitHubCheckForBranchOrTagTask.branchOrTagExists(user, password, repositoryUrl, "", branchOrTagName));
  }

  @Test
  public void deleteRepository() throws IOException {
    GitHubClient gitHubClient = new GitHubClient();
    gitHubClient.setCredentials(user, password);

    GitHubUtil.deleteRepository(gitHubClient, RepositoryId.create(user, CAMUNDA_BPM_PLATFORM));
    //GitHubDeleteRepositoryTask gitHubDeleteRepositoryTask = new GitHubDeleteRepositoryTask();
    //gitHubDeleteRepositoryTask.deleteRepository(user, password, repositoryUrl, postfix);
  }

}
