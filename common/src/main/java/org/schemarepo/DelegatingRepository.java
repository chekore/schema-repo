package org.schemarepo;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Base class for all delegating repositories. Implements decorator pattern.
 */
public abstract class DelegatingRepository extends BaseRepository {

  protected final Repository repo;

  protected DelegatingRepository(final Repository delegate) {
    if (delegate == null) {
      throw new IllegalArgumentException("Delegate repository required");
    }
    this.repo = delegate;
  }

  @Override
  public void isValid() {
    if (repo instanceof BaseRepository) {
      ((BaseRepository) repo).isValid();
    }
  }

  @Override
  public Subject register(final String subjectName, final SubjectConfig config) {
    return repo.register(subjectName, config);
  }

  @Override
  public Subject lookup(final String subjectName) {
    return repo.lookup(subjectName);
  }

  @Override
  public Iterable<Subject> subjects() {
    return repo.subjects();
  }

  @Override
  public boolean delete(String subjectName) {
    return repo.delete(subjectName);
  }

  @Override
  public void close() throws IOException {
    repo.close();
  }

  @Override
  protected Map<String, String> exposeConfiguration() {
    final Map<String, String> properties = new LinkedHashMap<String, String>(super.exposeConfiguration());
    properties.put("DELEGATE", repo.toString());
    return properties;
  }
}
