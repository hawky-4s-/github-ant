package com.livingoz.github.ant;

import org.apache.tools.ant.BuildException;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.RepositoryService;

import java.io.IOException;
import java.util.List;

public class GitHubDeleteRepositoryTask extends GitHubRepositoryServiceAction {

  String postfix = "";

  @Override
  public void execute() throws BuildException {
    try {
      deleteRepository(user, password, repositoryUrl, postfix);
    } catch (IOException e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }
  }

  public void deleteRepository(String user, String password, String repositoryUrl, String postfix) throws IOException {
    setUser(user);
    setPassword(password);
    setRepositoryUrl(repositoryUrl);
    setPostfix(postfix);

    RepositoryId repositoryId = GitHubUtil.parseRepositoryFromUrl(repositoryUrl);
    List<Repository> repositories = getRepositoryService().getRepositories(user);

    if (!GitHubUtil.repositoryAlreadyExists(getRepositoryService(), user, repositoryId, postfix)) {
      throw new RuntimeException("Repository '" + repositoryId.generateId() + "' does not exist!");
    }

    if (!postfix.isEmpty()) {
      repositoryId = RepositoryId.createFromId(repositoryId.generateId() + postfix);
    }

    // delete repository here!

    log("Deleted GitHub repository '" + repositoryId.generateId() + "'.");
  }

  public void setPostfix(String postfix) {
    this.postfix = postfix;
  }

}
