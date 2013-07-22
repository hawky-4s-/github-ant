package com.livingoz.github.ant;

import org.apache.tools.ant.Task;
import org.eclipse.egit.github.core.client.GitHubClient;

public abstract class GitHubServiceAction extends Task {

  private final GitHubClient gitHubClient;
  private Credentials credentials;

  GitHubServiceAction() {
    credentials = GitHubUtil.getCredentials(getProject());
    gitHubClient = GitHubUtil.createGitHubClientFromCredentials(credentials);
  }

  public GitHubClient getGitHubClient() {
    return gitHubClient;
  }

  public Credentials getCredentials() {
    return credentials;
  }

  public void setCredentials(Credentials credentials) {
    this.credentials = credentials;
  }

}
