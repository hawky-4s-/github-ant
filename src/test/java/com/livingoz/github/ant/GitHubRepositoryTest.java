package com.livingoz.github.ant;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GitHubRepositoryTest {

  String user = "hawky-4s-";
  String password = "TiCh!8180?githubcom";
  String repositoryUrl = "git@github.com:camunda/camunda-bpm-platform.git";
  String postfix = "-releasetest";

  @Before
  public void setup() {

  }

  @After
  public void cleanup() {

  }

  @Test
  public void parseRepositoryFromUrl() throws IOException {
    RepositoryId repositoryId = GitHubUtil.parseRepositoryFromUrl(repositoryUrl);
    assertEquals("camunda/camunda-bpm-platform", repositoryId.generateId());
  }

  @Test
  public void forkRepositoryFromUrl() throws IOException {
    GitHubForkRepositoryTask gitHubForkRepositoryTask = new GitHubForkRepositoryTask();
    Repository repository = gitHubForkRepositoryTask.forkRepository(user, password, repositoryUrl, postfix);
    assertEquals("hawky-4s-/camunda-bpm-platform-releasetest", repository.generateId());
  }

  @Test
  public void checkBranchOrTagExists() throws IOException {
    GitHubForkRepositoryTask gitHubForkRepositoryTask = new GitHubForkRepositoryTask();
    Repository repository = gitHubForkRepositoryTask.forkRepository(user, password, repositoryUrl, "");
    assertEquals("hawky-4s-/camunda-bpm-platform", repository.generateId());

    String branchOrTagName = "7.0.0-alpha1";
    GitHubCheckForBranchOrTagTask gitHubCheckForBranchOrTagTask = new GitHubCheckForBranchOrTagTask();
    assertTrue(gitHubCheckForBranchOrTagTask.branchOrTagExists(user, password, repositoryUrl, "", branchOrTagName));
  }

  @Test
  public void deleteRepository() throws IOException {
    GitHubClient gitHubClient = new GitHubClient();
    gitHubClient.setCredentials(user, password);

    GitHubUtil.deleteRepository(gitHubClient, RepositoryId.create(user, "camunda-bpm-platform"));
    //GitHubDeleteRepositoryTask gitHubDeleteRepositoryTask = new GitHubDeleteRepositoryTask();
    //gitHubDeleteRepositoryTask.deleteRepository(user, password, repositoryUrl, postfix);
  }

}
