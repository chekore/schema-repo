package org.schemarepo.server;

import java.util.Collections;
import java.util.Properties;

import org.schemarepo.Repository;
import org.schemarepo.rest.RESTRepository;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.ws.rs.Path;


/**
 * Subclass of {@link RESTRepository} which supports human-oriented rendering (HTML).
 */
@Singleton
@Path("/schema-repo-browser")
public class HumanOrientedRESTRepository extends RESTRepository {

  /**
   * All parameters are injected by Guice frawework.
   * @param repo backend repository
   */
  @Inject
  public HumanOrientedRESTRepository(Repository repo, Properties properties) {
    super(repo, Collections.singletonList(new HTMLRenderer()));
  }
}
