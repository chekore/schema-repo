package org.schemarepo;

/**
 * A {@link DelegatingSubject} is a Subject that delegates work to an underlying
 * {@link Subject}.
 *
 * Specific implementations may override various methods.
 *
 */
public abstract class DelegatingSubject extends Subject {
  private final Subject delegate;

  /**
   * A {@link DelegatingSubject} delegates work to a provided Subject.
   **/
  protected DelegatingSubject(Subject delegate) {
    super(delegate.getName());
    this.delegate = delegate;
  }

  @Override
  public SchemaEntry register(String schema) throws SchemaValidationException {
    return delegate.register(schema);
  }

  @Override
  public SchemaEntry registerIfLatest(String schema, SchemaEntry latest) throws SchemaValidationException {
    return delegate.registerIfLatest(schema, latest);
  }

  @Override
  public SchemaEntry lookupBySchema(String schema) {
    return delegate.lookupBySchema(schema);
  }

  @Override
  public SchemaEntry lookupById(String id) {
    return delegate.lookupById(id);
  }

  @Override
  public SchemaEntry latest() {
    return delegate.latest();
  }

  @Override
  public Iterable<SchemaEntry> allEntries() {
    return delegate.allEntries();
  }

  @Override
  public SubjectConfig getConfig() {
    return delegate.getConfig();
  }

  @Override
  public boolean integralKeys() {
    return delegate.integralKeys();
  }
}
