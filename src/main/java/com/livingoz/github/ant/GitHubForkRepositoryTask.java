package com.livingoz.github.ant;

import org.apache.tools.ant.BuildException;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryId;

import java.io.IOException;

public class GitHubForkRepositoryTask extends GitHubRepositoryServiceAction {

  String postfix = "";

  @Override
  public void execute() throws BuildException {
    try {
      forkRepository(user, password, repositoryUrl, postfix);
    } catch (IOException e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }
  }

  public Repository forkRepository(String user, String password, String repositoryUrl, String postfix) throws IOException {
    setUser(user);
    setPassword(password);
    setRepositoryUrl(repositoryUrl);
    setPostfix(postfix);

    RepositoryId repositoryToFork = GitHubUtil.parseRepositoryFromUrl(repositoryUrl);

    if (GitHubUtil.repositoryAlreadyExists(getRepositoryService(), user, repositoryToFork, postfix)) {
      throw new RuntimeException("GitHub repository already exists!");
    }

    Repository forkRepository = getRepositoryService().forkRepository(repositoryToFork);

    if (!postfix.isEmpty()) {
      String repositoryName = forkRepository.getName() + postfix;
      forkRepository.setName(repositoryName);
      forkRepository = getRepositoryService().editRepository(forkRepository);
    }

    log("Forking GitHub repository '" + repositoryToFork.generateId() + "' to '" + forkRepository.generateId() + "'");

    return forkRepository;
  }

  public void setPostfix(String postfix) {
    this.postfix = postfix;
  }

}
