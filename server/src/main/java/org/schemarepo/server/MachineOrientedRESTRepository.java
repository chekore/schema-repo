package org.schemarepo.server;

import java.util.Arrays;

import org.schemarepo.Repository;
import org.schemarepo.json.JsonUtil;
import org.schemarepo.rest.RESTRepository;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.ws.rs.Path;


/**
 * Subclass of {@link RESTRepository} which supports machine-oriented rendering
 * (plain text and JSON).
 */
@Singleton
@Path("/schema-repo")
public class MachineOrientedRESTRepository extends RESTRepository {

  /**
   * All parameters will be injected by Guice framework.
   * @param repo the backend repository
   * @param jsonUtil implementation of JSON utils
   */
  @Inject
  public MachineOrientedRESTRepository(Repository repo, JsonUtil jsonUtil) {
    super(repo, Arrays.asList(new PlainTextRenderer(), new JsonRenderer(jsonUtil), new SchemaJsonRenderer(jsonUtil)));
  }
}
