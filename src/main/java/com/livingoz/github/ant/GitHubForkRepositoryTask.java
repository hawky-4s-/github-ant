package com.livingoz.github.ant;

import org.apache.tools.ant.BuildException;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryId;

import java.io.IOException;

public class GitHubForkRepositoryTask extends GitHubRepositoryServiceAction {

  String postfix;

  public GitHubForkRepositoryTask() {
    super();
  }

  @Override
  public void execute() throws BuildException {
    try {
      forkRepository(getCredentials(), repositoryUrl, postfix);
    } catch (IOException e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }
  }

  public Repository forkRepository(Credentials credentials, String repositoryUrl, String postfix) throws IOException {
    setRepositoryUrl(repositoryUrl);
    setPostfix(postfix);

    RepositoryId originalRepository = GitHubUtil.parseRepositoryFromUrl(repositoryUrl);

    // check for original repository
    if (!GitHubUtil.repositoryExists(getRepositoryService(), originalRepository)) {
      throw new RuntimeException("Original Repository '" + originalRepository.generateId() + "' doesn't exist.");
    }

    // check for repository in user space
    RepositoryId forkedRepositoryId = RepositoryId.create(credentials.getUser(), originalRepository.getName());
    if (GitHubUtil.repositoryExists(getRepositoryService(), forkedRepositoryId)) {
      throw new RuntimeException("Repository '" + forkedRepositoryId.generateId() + "' already exists in space '" + credentials.getUser() + "'.");
    }

    // check for renamed repository in user space
    if (postfix != null) {
      RepositoryId renamedRepository = RepositoryId.create(credentials.getUser(), forkedRepositoryId.getName() + postfix);
      if (GitHubUtil.repositoryExists(getRepositoryService(), renamedRepository)) {
        throw new RuntimeException("Renamed repository '" + renamedRepository.generateId() + "' already exists in space '" + credentials.getUser() + "'.");
      }
    }

    // fork
    log("Forking GitHub repository '" + originalRepository.generateId() + "' to '" + forkedRepositoryId.generateId() + "'");
    Repository forkedRepository = getRepositoryService().forkRepository(originalRepository);

    // rename
    if (postfix != null) {
      forkedRepository.setName(forkedRepository.getName() + postfix);
      forkedRepository = getRepositoryService().editRepository(forkedRepository);
      log("Renaming forked GitHub repository '" + forkedRepositoryId.generateId() + "' to '" + forkedRepository.generateId() + "'");
    }

    return forkedRepository;
  }

  public void setPostfix(String postfix) {
    this.postfix = postfix;
  }

}
