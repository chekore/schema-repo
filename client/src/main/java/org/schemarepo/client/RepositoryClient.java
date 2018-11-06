package org.schemarepo.client;

import org.schemarepo.Repository;


/**
 * A marker interface for all repository clients to be distinguishable from
 * other repository implementations.
 */
public interface RepositoryClient extends Repository {

  /**
   * Return status information about the repository, such as whether it's healthy.
   * Details such as "what" and "representation" are up to the implementing
   * classes.
   * 
   * @return String representation of repository status
   */
  String getStatus();
}
