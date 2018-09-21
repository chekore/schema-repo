package org.schemarepo.server;

import java.util.Properties;

import org.schemarepo.RepositoryUtil;
import org.schemarepo.SchemaEntry;
import org.schemarepo.Subject;

import javax.ws.rs.core.MediaType;


/**
 * Renders as plain text. The actual work is delegated to utility class {@link org.schemarepo.RepositoryUtil}
 */
public class PlainTextRenderer implements Renderer {

  @Override
  public String getMediaType() {
    return MediaType.TEXT_PLAIN;
  }

  @Override
  public String renderSubjects(Iterable<Subject> subjects) {
    return RepositoryUtil.subjectsToString(subjects);
  }

  @Override
  public String renderSchemas(Iterable<SchemaEntry> schemaEntries) {
    return RepositoryUtil.schemasToString(schemaEntries);
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
