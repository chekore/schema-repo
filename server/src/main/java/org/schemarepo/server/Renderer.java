package org.schemarepo.server;

import java.util.Properties;

import org.schemarepo.SchemaEntry;
import org.schemarepo.Subject;


/**
 * Responsible for rendering various repository items (such as subjects and
 * individual schemas) based on a particular presentation technology (such as
 * plain text, JSON, HTML, etc).
 */
public interface Renderer {

  /**
   * @return MediaType to send to the client
   */
  String getMediaType();

  String renderSubjects(Iterable<Subject> subjects);

  String renderSchemas(Iterable<SchemaEntry> schemaEntries);

  String renderSchemaEntry(SchemaEntry schemaEntry, boolean requestForLatest);

  String renderProperties(Properties props, String comment);
}
