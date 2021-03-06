package org.schemarepo;

/**
 * <p>
 * A {@link SchemaEntryCache} is a bi-directional cache from a schema string to
 * a schema id string. In a given {@link Subject} the mapping between a schema
 * and its id is immutable and can be cached. Other aspects of {@link Subject}
 * are not safe to cache.
 * </p>
 * <p>
 * In a {@link Subject} schemas are Strings and can not be null or empty. See
 * {@link RepositoryUtil#validateSchemaOrSubject(String)}
 * </p>
 * {@link #add(SchemaEntry)}, {@link #lookupById(String)}, and
 * {@link #lookupBySchema(String)} must be thread-safe with respect to
 * each-other.
 */
public interface SchemaEntryCache {

  /**
   * Look up a schema entry by its full string form. Thread-safe.
   *
   * @throws NullPointerException If the provided schema is null
   */
  SchemaEntry lookupBySchema(String schema);

  /**
   * Look up a schema entry by its id. Thread-safe.
   *
   * @throws NullPointerException If the provided id is null
   */
  SchemaEntry lookupById(String id);

  /**
   * Add the schema entry to this cache.
   *
   * @param entry schema entry to add. If the provided entry is null, returns
   *              null;
   *
   * @return the {@link SchemaEntry} that is in the cache after the call
   *         completes. If the entry already exists the pre-existing value is
   *         returned, otherwise the value returned is the entry provided. If the
   *         provided entry is null, returns null. Thread-safe.
   */
  SchemaEntry add(SchemaEntry entry);

  /**
   * Creates instances of {@link SchemaEntryCache}<br/>
   * <br/>
   *
   * @see {@link Subject#cacheWith(Subject, SchemaEntryCache)}
   */
  interface Factory {
    /**
     * Create a {@link SchemaEntryCache} instance for use with
     * {@link Subject#cacheWith(Subject, SchemaEntryCache)}.
     *
     * May return null to disable use of a SchemaEntryCache
     */
    SchemaEntryCache createSchemaEntryCache();
  }
}
