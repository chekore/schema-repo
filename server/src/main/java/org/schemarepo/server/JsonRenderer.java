package org.schemarepo.server;

import java.util.Properties;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.schemarepo.SchemaEntry;
import org.schemarepo.Subject;
import org.schemarepo.json.JsonUtil;


/**
 * Renders as JSON. The actual work is delegated to utility class
 * {@link org.schemarepo.RepositoryUtil}
 */
public class JsonRenderer implements Renderer {

  private final JsonUtil jsonUtil;

  public JsonRenderer(JsonUtil jsonUtil) {
    if (jsonUtil == null) {
      throw new IllegalArgumentException("jsonUtil required");
    }
    this.jsonUtil = jsonUtil;
  }

  @Override
  public String getMediaType() {
    return MediaType.APPLICATION_JSON;
  }

  @Override
  public String renderSubjects(Iterable<Subject> subjects) {
    return jsonUtil.subjectsToJson(subjects);
  }

  @Override
  public String renderSchemas(Iterable<SchemaEntry> schemaEntries) {
    return jsonUtil.schemasToJson(schemaEntries);
  }

  @Override
  public String renderSchemaEntry(SchemaEntry schemaEntry, boolean requestForLatest) {
    return notAcceptable("renderSchemaEntry");
  }

  @Override
  public String renderProperties(Properties props, String comment) {
    return notAcceptable("renderProperties");
  }

  private String notAcceptable(String api) {
    throw new WebApplicationException(Response.status(Response.Status.NOT_ACCEPTABLE)
        .entity(String.format("%s API does not support %s media type", api, getMediaType())).build());
  }
}
