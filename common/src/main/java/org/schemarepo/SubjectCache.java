package org.schemarepo;

/**
 * <p>
 * A {@link SubjectCache} is a cache from a string subject name to a
 * {@link Subject}
 * </p>
 * <p>
 * In a {@link Repository} subjects can be cached because they can only be
 * created. However, a {@link Subject} can have its meta-data altered, so this
 * cannot be cached.
 * </p>
 * {@link #add(Subject)} and {@link #lookup(String)} must be thread-safe with
 * respect to each-other.
 */
public interface SubjectCache {
  /**
   * Look up a {@link Subject} by its name. Thread-safe.
   *
   * @param name
   * @throws NullPointerException if the provided name is null
   */
  Subject lookup(String name);

  /**
   * Add or update the {@link Subject} entry in this cache.
   *
   * @param entry
   * @return the {@link Subject} that is in the cache after the call completes. If
   *         the entry already exists this is the pre-existing value, otherwise it
   *         is the value provided. If the value provided is null, returns null.
   *         Thread-safe.
   */
  Subject add(Subject entry);

  /**
   * Remove a {@Subject} by its name. Thread-safe
   * 
   * @param name
   * @throws NullPointerException if the provided is null
   * @return true or false
   */
  boolean remove(String name);
}
