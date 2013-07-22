package com.livingoz.github.ant;

import org.apache.tools.ant.BuildException;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryId;

import java.io.IOException;
import java.util.List;

public class GitHubDeleteRepositoryTask extends GitHubRepositoryServiceAction {

  String postfix;

  public GitHubDeleteRepositoryTask() {
    super();
  }

  @Override
  public void execute() throws BuildException {
    try {
      deleteRepository(getCredentials(), repositoryUrl, postfix);
    } catch (IOException e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }
  }

  public void deleteRepository(Credentials credentials, String repositoryUrl, String postfix) throws IOException {
    setRepositoryUrl(repositoryUrl);
    setPostfix(postfix);

    boolean repositoryExists = true;

    RepositoryId repositoryId = GitHubUtil.parseRepositoryFromUrl(repositoryUrl);

    // check for repository in user space
    RepositoryId userRepository = RepositoryId.create(credentials.getUser(), repositoryId.getName());
    if (!GitHubUtil.repositoryExists(getRepositoryService(), userRepository)) {
      log("Repository '" + userRepository.generateId() + "' doesn't exists in space '" + credentials.getUser() + "'.");
      repositoryExists = false;
    }

    if (postfix != null) {
      repositoryId = RepositoryId.createFromId(repositoryId.generateId() + postfix);
    }

    if (!GitHubUtil.repositoryExists(getRepositoryService(), repositoryId)) {
      log("Repository '" + repositoryId.generateId() + "' does not exist!");
      repositoryExists = false;
    }

    // delete repository here!
    if (repositoryExists) {
      GitHubUtil.deleteRepository(getGitHubClient(), repositoryId);
      log("Deleted GitHub repository '" + repositoryId.generateId() + "'.");
    }


  }

  public void setPostfix(String postfix) {
    this.postfix = postfix;
  }

}
