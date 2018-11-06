package org.schemarepo.server;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.ws.rs.core.MediaType;

import org.schemarepo.SchemaEntry;
import org.schemarepo.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.cache.NullCacheStorage;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;


/**
 * Renders HTML using Freemarker template engine.
 */
public class HTMLRenderer implements Renderer {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private final Configuration cfg;

  public HTMLRenderer() {
    cfg = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
    cfg.setClassForTemplateLoading(getClass(), "/freemarker");
    cfg.setCacheStorage(new NullCacheStorage());
    cfg.setDefaultEncoding("UTF-8");
    cfg.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
  }

  @Override
  public String getMediaType() {
    return MediaType.TEXT_HTML;
  }

  @Override
  public String renderSubjects(Iterable<Subject> subjects) {
    return renderTemplate("Subjects", "subjects", Collections.singletonMap("subjects", wrapIterable(subjects)));
  }

  @Override
  public String renderSchemas(Iterable<SchemaEntry> schemaEntries) {
    return renderTemplate("Schemas", "schemas", Collections.singletonMap("schemas", wrapIterable(schemaEntries)));
  }

  @Override
  public String renderSchemaEntry(SchemaEntry schemaEntry, boolean requestForLatest) {
    return renderTemplate("Schema with ID = " + schemaEntry.getId() + (requestForLatest ? " (latest)" : ""),
        "schemaEntry", Collections.singletonMap("schemaEntry", schemaEntry));
  }

  @Override
  public String renderProperties(Properties props, String comment) {
    return renderTemplate(comment, "properties", Collections.singletonMap("props", props));
  }

  private <E> List<E> wrapIterable(Iterable<E> iterable) {
    List<E> list = new ArrayList<E>();
    for (E elem : iterable) {
      list.add(elem);
    }
    return list;
  }

  private String renderTemplate(String title, String specificTemplate, Map<String, ?> specificData) {
    Map<String, Object> data = new HashMap<String, Object>(specificData);
    data.put("title", title);
    data.put("specificTemplate", specificTemplate);
    StringWriter out = new StringWriter();
    try {
      cfg.getTemplate("wrapper.ftl").process(data, out);
    } catch (Exception e) {
      logger.error("Failed to render template {} with data: {}", specificTemplate, data);
    }
    return out.toString();
  }
}
