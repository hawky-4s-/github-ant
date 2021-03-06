package com.livingoz.github.ant;

import org.apache.tools.ant.BuildFileTest;

public class GitHubAntTaskTest extends BuildFileTest {

  public GitHubAntTaskTest() {
    super();
  }

  public void setUp () {
    configureProject("src/test/resources/com/livingoz/github/ant/build.xml");
  }

  public void testForkRepository() {
    executeTarget("forkRepository");
  }

  public void testForkRepositoryWithPostfix() {
    executeTarget("forkRepositoryWithPostfix");
  }

  public void testCheckForBranchOrTag() {
    executeTarget("checkForBranchOrTag");
  }

  public void testDeleteRepository() {
    executeTarget("deleteRepository");
  }

}
