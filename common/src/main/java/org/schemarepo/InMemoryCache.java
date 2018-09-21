package org.schemarepo;

/**
 * <p>
 * A {@link InMemoryCache} is an implementation of {@link RepositoryCache}
 * that uses {@link InMemorySubjectCache} and {@link InMemorySchemaEntryCache}
 */
public class InMemoryCache extends RepositoryCache {
  public InMemoryCache() {
    super(new InMemorySubjectCache(), new InMemorySchemaEntryCache.Factory());
  }
}
