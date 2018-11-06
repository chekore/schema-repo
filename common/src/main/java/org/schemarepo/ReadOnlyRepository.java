package org.schemarepo;

import java.util.ArrayList;

import javax.inject.Inject;


/**
 * ReadOnlyRepository is a {@link Repository} implementation that wraps another
 * {@link Repository} and rejects all operations that can modify the state of
 * the {@link Repository}.<br/>
 * <br/>
 *
 * {@link #register(String, SubjectConfig)}, {@link Subject#register(String)}
 * and {@link Subject#registerIfLatest(String, SchemaEntry)} throw
 * {@link IllegalStateException} if called.
 */
public class ReadOnlyRepository extends DelegatingRepository {

  /**
   * Create a repository that disallows mutations to the underlying repository.
   * 
   * @param repo The repository to wrap
   */
  @Inject
  public ReadOnlyRepository(Repository repo) {
    super(repo);
  }

  @Override
  public Subject register(String subjectName, SubjectConfig config) {
    throw new IllegalStateException("Cannot register a Subject in a ReadOnlyRepository");
  }

  @Override
  public Subject lookup(String subjectName) {
    return Subject.readOnly(repo.lookup(subjectName));
  }

  @Override
  public Iterable<Subject> subjects() {
    ArrayList<Subject> subjects = new ArrayList<Subject>();
    for (Subject sub : repo.subjects()) {
      subjects.add(Subject.readOnly(sub));
    }
    return subjects;
  }
}
