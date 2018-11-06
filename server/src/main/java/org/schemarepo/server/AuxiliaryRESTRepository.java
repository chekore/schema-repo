package org.schemarepo.server;

import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.schemarepo.BaseRepository;
import org.schemarepo.Repository;
import org.schemarepo.config.Config;


/**
 * Exposes auxiliary (not part of {@link org.schemarepo.Repository} interface)
 * REST endpoints, such as <pre>/config</pre> and <pre>/status</pre>
 */
@Singleton
@Path("/")
public class AuxiliaryRESTRepository extends BaseRESTRepository {

  private final Properties properties;

  /**
   * Create a {@link AuxiliaryRESTRepository} that wraps a given
   * {@link org.schemarepo.Repository} Typically the wrapped repository is a
   * {@link org.schemarepo.CacheRepository} that wraps a non-caching underlying
   * repository.
   *
   * @param repo       The {@link org.schemarepo.Repository} to wrap.
   * @param properties User-provided properties that were used to configure the
   *                   underlying repository and {@link RepositoryServer}
   */
  @Inject
  public AuxiliaryRESTRepository(Repository repo, Properties properties) {
    super(repo, Arrays.asList(new PlainTextRenderer(), new HTMLRenderer()));
    this.properties = properties != null ? properties : new Properties();
    this.properties.setProperty("schema-repo.start-datetime", new Date().toString());
  }

  @GET
  @Path("/status")
  public Response getStatus() {
    Status status = Status.OK;
    String text = "OK";
    if (repo instanceof BaseRepository) {
      try {
        ((BaseRepository) repo).isValid();
      } catch (IllegalStateException e) {
        status = Status.SERVICE_UNAVAILABLE;
        text = e.getMessage();
      }
    } else {
      text = "N/A";
    }
    return Response.status(status).entity(text + " : " + repo.getClass()).build();
  }

  @GET
  @Path("/config")
  public Response getConfiguration(@HeaderParam("Accept") String mediaType,
      @QueryParam("includeDefaults") boolean includeDefaults) {
    final Properties copyOfProperties = new Properties();
    if (includeDefaults) {
      copyOfProperties.putAll(Config.DEFAULTS);
    }
    copyOfProperties.putAll(properties);
    Renderer r = getRenderer(mediaType);
    return Response.ok(r.renderProperties(copyOfProperties, "Configuration of schema-repo server"), r.getMediaType())
        .build();
  }
}
