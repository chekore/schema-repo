package org.schemarepo.server;

import java.util.Properties;

import org.schemarepo.RepositoryUtil;
import org.schemarepo.SchemaEntry;
import org.schemarepo.Subject;
import org.schemarepo.json.JsonUtil;


/**
 * @author zhizhou.ren
 */
public class SchemaJsonRenderer implements Renderer {
  private final JsonUtil jsonUtil;

  public SchemaJsonRenderer(JsonUtil jsonUtil) {
    if (jsonUtil == null) {
      throw new IllegalArgumentException("jsonUtil required");
    }
    this.jsonUtil = jsonUtil;
  }

  @Override
  public String getMediaType() {
    return CustomMediaType.APPLICATION_SCHEMA_REGISTRY_JSON;
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
    return requestForLatest ? schemaEntry.toString() : schemaEntry.getSchema();
  }

  @Override
  public String renderProperties(Properties props, String comment) {
    return RepositoryUtil.propertiesToString(props, comment);
  }
}
