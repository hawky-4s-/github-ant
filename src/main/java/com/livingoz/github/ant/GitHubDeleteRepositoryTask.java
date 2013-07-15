package com.livingoz.github.ant;

import org.apache.tools.ant.BuildException;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.RepositoryService;

import java.io.IOException;
import java.util.List;

public class GitHubDeleteRepositoryTask extends GitHubRepositoryServiceAction {

  String postfix;

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

    // check for repository in user space
    RepositoryId userRepository = RepositoryId.create(user, repositoryId.getName());
    if (!GitHubUtil.repositoryExists(getRepositoryService(), userRepository)) {
      throw new RuntimeException("Repository '" + userRepository.generateId() + "' doesn't exists in space '" + user + "'.");
    }


    if (postfix != null) {
      repositoryId = RepositoryId.createFromId(repositoryId.generateId() + postfix);
    }



    List<Repository> repositories = getRepositoryService().getRepositories();

    if (!GitHubUtil.repositoryExists(getRepositoryService(), repositoryId)) {
      throw new RuntimeException("Repository '" + repositoryId.generateId() + "' does not exist!");
    }



    // delete repository here!

    log("Deleted GitHub repository '" + repositoryId.generateId() + "'.");
  }

  public void setPostfix(String postfix) {
    this.postfix = postfix;
  }

}
