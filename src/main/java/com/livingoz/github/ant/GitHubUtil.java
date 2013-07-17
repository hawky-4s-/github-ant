package com.livingoz.github.ant;

import org.apache.tools.ant.Project;
import org.eclipse.egit.github.core.IRepositoryIdProvider;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.RepositoryService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import static org.eclipse.egit.github.core.client.IGitHubConstants.SEGMENT_REPOS;

public class GitHubUtil {

  public static RepositoryId parseRepositoryFromUrl(String repositoryUrl) {
    String prefix = "git@github.com";

    if (repositoryUrl.startsWith(prefix)) {
      String[] parts = repositoryUrl.split(":");

      parts = parts[1].split("/");

      String owner = parts[0];
      String name = parts[1];
      if (name.endsWith(".git")) {
        name = name.replace(".git", "");
      }
      return RepositoryId.create(owner, name);
    }

    return RepositoryId.createFromUrl(repositoryUrl);
  }

  public static boolean repositoryExists(RepositoryService repositoryService, RepositoryId repositoryId)
      throws IOException {
    List<Repository> repositories = repositoryService.getRepositories();

    for (Repository repository : repositories) {
      if (repository.generateId().equals(repositoryId.generateId())) {
        return true;
      }
    }

    return false;
  }

  public static boolean repositoryAlreadyExists(RepositoryService repositoryService, String user, String repositoryName)
      throws IOException {
    RepositoryId repositoryId = RepositoryId.create(user, repositoryName);

    return repositoryExists(repositoryService, repositoryId);
  }

  /**
   * Delete repository with given id
   *
   * @param gitHubClient
   * @param repository
   * @throws IOException
   */
  public static void deleteRepository(GitHubClient gitHubClient, IRepositoryIdProvider repository) throws IOException {
    String repoId = getId(repository);
    StringBuilder uri = new StringBuilder(SEGMENT_REPOS);
    uri.append('/').append(repoId);
    gitHubClient.delete(uri.toString());
  }

  /**
   * Get id for repository
   *
   * @param provider
   * @return non-null id
   */
  public static String getId(IRepositoryIdProvider provider) {
    if (provider == null)
      throw new IllegalArgumentException(
          "Repository provider cannot be null"); //$NON-NLS-1$
    final String id = provider.generateId();
    if (id == null)
      throw new IllegalArgumentException("Repository id cannot be null"); //$NON-NLS-1$
    if (id.length() == 0)
      throw new IllegalArgumentException("Repository id cannot be empty"); //$NON-NLS-1$
    return id;
  }

  public static Properties getProperties(String propertyFileLocation) {
    Properties props = new Properties();

    File homeDir = null;
    if (propertyFileLocation == null) {
      homeDir = new File(System.getProperty("user.home"));
    } else {
      homeDir = new File(propertyFileLocation);
    }

    FileInputStream in = null;
    try {
      in = new FileInputStream(new File(homeDir, ".github"));
      props.load(in);
    }catch (IOException e) {
      // ignore
    } finally {
      try {
        if (in != null) {
          in.close();
        }
      } catch (IOException ioe) {
        // ignore
      }
    }

    return props;
  }

  public static Credentials getCredentials(Project project) {
    Credentials credentials;

    // check userhome/.github
    Properties properties = GitHubUtil.getProperties(null);
    String filePath = project.getProperty("github.ant.filepath");
    if (filePath != null) {
      // check file/.github
      properties = GitHubUtil.getProperties(filePath);
      credentials = new Credentials(properties.getProperty("user", null),
                                    properties.getProperty("password", null),
                                    properties.getProperty("oauth", null));
    } else {
      credentials = new Credentials(project.getProperty("github.ant.user"),
                                    project.getProperty("github.ant.password"),
                                    project.getProperty("github.ant.oauth"));
    }

    return credentials;
  }

  public static GitHubClient createGitHubClientFromCredentials(Credentials credentials) {
    GitHubClient gitHubClient = new GitHubClient();

    String user = credentials.getUser();
    String password = credentials.getPassword();
    String oauthToken = credentials.getOauthToken();

    if (oauthToken != null && !oauthToken.isEmpty()) {
      return gitHubClient.setOAuth2Token(oauthToken);
    } else if (user != null && !user.isEmpty() && password != null && !password.isEmpty()) {
      return gitHubClient.setCredentials(user, password);
    } else {
      throw new RuntimeException("Unable to initialize GitHubClient: missing credentials!");
    }
  }
}