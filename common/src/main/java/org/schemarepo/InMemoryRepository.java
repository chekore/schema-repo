package org.schemarepo;

import javax.inject.Inject;


/**
 * A {@link Repository} that stores its data in memory and is not persistent.
 * This is useful primarily for testing.
 */
public class InMemoryRepository extends AbstractBackendRepository {

  @Inject
  public InMemoryRepository(ValidatorFactory validators) {
    super(validators);
  }

  @Override
  protected Subject getSubjectInstance(final String subjectName) {
    final Subject subject = subjectCache.lookup(subjectName);
    if (subject == null) {
      throw new IllegalStateException("Unexpected: subject must've been cached by #registerSubjectInBackend");
    }
    return subject;
  }

  @Override
  protected void registerSubjectInBackend(final String subjectName, final SubjectConfig config) {
    cacheSubject(new MemSubject(subjectName, config));
  }

  private static class MemSubject extends Subject {
    private final InMemorySchemaEntryCache schemas = new InMemorySchemaEntryCache();
    private SchemaEntry latest = null;
    private int nextId = 0;
    private SubjectConfig config;

    protected MemSubject(String name, SubjectConfig config) {
      super(name);
      this.config = RepositoryUtil.safeConfig(config);
    }

    @Override
    public SubjectConfig getConfig() {
      return config;
    }

    @Override
    public synchronized SchemaEntry register(String schema)
      throws SchemaValidationException {
      String id = String.valueOf(nextId);
      SchemaEntry toRegister = new SchemaEntry(id, schema);
      SchemaEntry valueInCache = schemas.add(toRegister);
      if (toRegister == valueInCache) {
        // schema is new
        nextId++;
        this.latest = toRegister;
      }
      return valueInCache;
    }

    @Override
    public synchronized SchemaEntry registerIfLatest(String schema, SchemaEntry latest)
      throws SchemaValidationException {
      if (latest == this.latest || (latest != null && latest.equals(this.latest))) {
        return register(schema);
      } else {
        return null;
      }
    }

    @Override
    public SchemaEntry lookupBySchema(String schema) {
      return schemas.lookupBySchema(schema);
    }

    @Override
    public SchemaEntry lookupById(String id) {
      return schemas.lookupById(id);
    }

    @Override
    public synchronized SchemaEntry latest() {
      return latest;
    }

    @Override
    public synchronized Iterable<SchemaEntry> allEntries() {
      return schemas.values();
    }

    @Override
    public boolean integralKeys() {
      return true;
    }
  }
}
