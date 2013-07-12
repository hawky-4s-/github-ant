package com.livingoz.github.ant;

import org.apache.tools.ant.Task;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.RepositoryService;

public abstract class GitHubRepositoryServiceAction extends Task {

  protected String user = "";
  protected String password = "";
  protected String repositoryUrl = "";

  protected RepositoryService repositoryService;

  public RepositoryService getRepositoryService() {
    if (repositoryService != null) {
      return repositoryService;
    }
    if (user != null && !user.isEmpty() && password != null && !password.isEmpty()) {
      GitHubClient gitHubClient = new GitHubClient();
      gitHubClient.setCredentials(user, password);
      return new RepositoryService(gitHubClient);
    }

    throw new IllegalArgumentException("Properties 'user' and 'password' must be set!");
  }

  public void setUser(String user) {
    this.user = user;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setRepositoryUrl(String repositoryUrl) {
    this.repositoryUrl = repositoryUrl;
  }

}
