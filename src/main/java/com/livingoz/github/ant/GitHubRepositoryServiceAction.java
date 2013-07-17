package com.livingoz.github.ant;

import org.eclipse.egit.github.core.service.RepositoryService;

public abstract class GitHubRepositoryServiceAction extends GitHubServiceAction {

  protected String repositoryUrl = "";

  protected RepositoryService repositoryService;

  public GitHubRepositoryServiceAction() {
    super();
  }

  public RepositoryService getRepositoryService() {
    if (repositoryService == null) {
      repositoryService = new RepositoryService(getGitHubClient());
    }

    return repositoryService;
  }

  public void setRepositoryUrl(String repositoryUrl) {
    this.repositoryUrl = repositoryUrl;
  }

}
