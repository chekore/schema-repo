package org.schemarepo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;


/**
 * An unbounded in memory {@link SchemaEntryCache} that never evicts any values;
 */
public class InMemorySchemaEntryCache implements SchemaEntryCache {

  private final ConcurrentHashMap<String, SchemaEntry> schemaToEntry = new ConcurrentHashMap<String, SchemaEntry>();
  private final ConcurrentHashMap<String, SchemaEntry> idToSchema = new ConcurrentHashMap<String, SchemaEntry>();
  private final LinkedList<SchemaEntry> schemasInOrder = new LinkedList<SchemaEntry>();

  @Override
  public SchemaEntry lookupBySchema(String schema) {
    return schemaToEntry.get(schema);
  }

  @Override
  public SchemaEntry lookupById(String id) {
    return idToSchema.get(id);
  }

  @Override
  public synchronized SchemaEntry add(SchemaEntry entry) {
    if (null == entry) {
      return entry;
    }
    SchemaEntry prior = schemaToEntry.putIfAbsent(entry.getSchema(), entry);
    if (null != prior) {
      entry = prior;
    }
    idToSchema.put(entry.getId(), entry);
    schemasInOrder.push(entry);
    return entry;
  }

  /** return all of the values in this cache **/
  public synchronized Iterable<SchemaEntry> values() {
    return new ArrayList<SchemaEntry>(schemasInOrder);
  }

  public static class Factory implements SchemaEntryCache.Factory {
    @Override
    public SchemaEntryCache createSchemaEntryCache() {
      return new InMemorySchemaEntryCache();
    }
  }
}
