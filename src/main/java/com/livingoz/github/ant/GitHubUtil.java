package com.livingoz.github.ant;

import org.eclipse.egit.github.core.IRepositoryIdProvider;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.RepositoryService;

import java.io.IOException;
import java.util.List;

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

  public static boolean repositoryAlreadyExists(RepositoryService repositoryService, String user, RepositoryId repositoryId, String postfix)
      throws IOException {
    RepositoryId repoId = RepositoryId.create(user, repositoryId.getName());
    RepositoryId renamedRepository = RepositoryId.createFromId(repoId.generateId() + postfix);
    List<Repository> repositories = repositoryService.getRepositories(user);
    for (Repository repository : repositories) {
      if (repository.generateId().equals(repoId.generateId())) {
        return true;
      }
      if (postfix != null && !postfix.isEmpty()) {
        if (repository.generateId().equals(renamedRepository.generateId())) {
          return true;
        }
      }
    }

    return false;
  }

  /**
   * Deploy deploy key with given id from given repository
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

}