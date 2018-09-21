package org.schemarepo;

import javax.inject.Inject;


/**
 * <p>
 * A {@link RepositoryCache} composes a {@link SubjectCache} with a
 * {@link SchemaEntryCache.Factory}, using
 * {@link Subject#cacheWith(Subject, SchemaEntryCache)} to wrap {@link Subject}
 * instances prior to insertion into the {@link SubjectCache}.
 */
public class RepositoryCache implements SubjectCache {

  private final SubjectCache subjects;
  private final SchemaEntryCache.Factory entryCacheFactory;

  @Inject
  public RepositoryCache(SubjectCache subjects, SchemaEntryCache.Factory entryCacheFactory) {
    this.subjects = subjects;
    this.entryCacheFactory = entryCacheFactory;
  }

  @Override
  public Subject add(Subject entry) {
    return subjects.add(Subject.cacheWith(entry, entryCacheFactory.createSchemaEntryCache()));
  }

  @Override
  public Subject lookup(String name) {
    return subjects.lookup(name);
  }
}
