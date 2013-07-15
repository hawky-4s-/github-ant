package com.livingoz.github.ant;

import org.apache.tools.ant.BuildException;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryBranch;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.RepositoryTag;

import java.io.IOException;
import java.util.List;

public class GitHubCheckForBranchOrTagTask extends GitHubRepositoryServiceAction {

  String postfix;
  String branchOrTagName;

  @Override
  public void execute() throws BuildException {
    try {
      branchOrTagExists(user, password, repositoryUrl, postfix, branchOrTagName);
    } catch (IOException e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }
  }

  public void branchOrTagExists(String user, String password, String repositoryUrl, String postfix, String branchOrTagName)
      throws IOException {
    setUser(user);
    setPassword(password);
    setRepositoryUrl(repositoryUrl);
    setPostfix(postfix);
    setBranchOrTagName(branchOrTagName);

    RepositoryId repositoryId = GitHubUtil.parseRepositoryFromUrl(repositoryUrl);

    if (postfix != null) {
      repositoryId = RepositoryId.createFromId(repositoryId.generateId() + postfix);
    }

    if (!GitHubUtil.repositoryExists(getRepositoryService(), repositoryId)) {
      throw new RuntimeException("Repository '" + repositoryId.generateId() + "' does not exist!");
    }

    // check for branch first
    List<RepositoryBranch> branches = getRepositoryService().getBranches(repositoryId);
    for (RepositoryBranch branch : branches) {
      if (branch.getName().equals(branchOrTagName)) {
        log("Branch '" + branchOrTagName + "' found in GitHub repository '" + repositoryId.generateId() + "'.");
        return;
      }
    }

    // check for tag when no branch is found
    List<RepositoryTag> tags = getRepositoryService().getTags(repositoryId);
    for (RepositoryTag tag : tags) {
      if (tag.getName().equals(branchOrTagName)) {
        log("Tag '" + branchOrTagName + "' found in GitHub repository '" + repositoryId.generateId() + "'.");
        return;
      }
    }

    throw new BuildException("Branch or Tag with name '" + branchOrTagName + "' does not exists @ '" + repositoryUrl + "'");
  }

  public void setPostfix(String postfix) {
    this.postfix = postfix;
  }

  public void setBranchOrTagName(String branchOrTagName) {
    this.branchOrTagName = branchOrTagName;
  }

}
