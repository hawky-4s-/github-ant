package com.livingoz.github.ant;

import org.apache.tools.ant.BuildException;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryId;

import java.io.IOException;

public class GitHubForkRepositoryTask extends GitHubRepositoryServiceAction {

  String postfix;

  @Override
  public void execute() throws BuildException {
    try {
      forkRepository(user, password, repositoryUrl, postfix);
    } catch (IOException e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }
  }

  public void forkRepository(String user, String password, String repositoryUrl, String postfix) throws IOException {
    setUser(user);
    setPassword(password);
    setRepositoryUrl(repositoryUrl);
    setPostfix(postfix);

    RepositoryId originalRepository = GitHubUtil.parseRepositoryFromUrl(repositoryUrl);

    // check for original repository
    if (GitHubUtil.repositoryExists(getRepositoryService(), originalRepository)) {
      throw new RuntimeException("Original Repository '" + originalRepository.generateId() + "' doesn't exist.");
    }

    // check for repository in user space
    RepositoryId forkedRepositoryId = RepositoryId.create(user, originalRepository.getName());
    if (GitHubUtil.repositoryExists(getRepositoryService(), forkedRepositoryId)) {
      throw new RuntimeException("Repository '" + forkedRepositoryId.generateId() + "' already exists in space '" + user + "'.");
    }

    // check for renamed repository in user space
    if (postfix != null) {
      RepositoryId renamedRepository = RepositoryId.create(user, forkedRepositoryId.getName() + postfix);
      if (GitHubUtil.repositoryExists(getRepositoryService(), renamedRepository)) {
        throw new RuntimeException("Renamed repository '" + renamedRepository.generateId() + "' already exists in space '" + user + "'.");
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
  }

  public void setPostfix(String postfix) {
    this.postfix = postfix;
  }

}
