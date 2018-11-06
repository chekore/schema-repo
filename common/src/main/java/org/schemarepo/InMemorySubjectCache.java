package org.schemarepo;

import java.util.concurrent.ConcurrentHashMap;


/**
 * An unbounded in memory {@link SubjectCache} that never evicts any values.
 */
public class InMemorySubjectCache implements SubjectCache {
  private final ConcurrentHashMap<String, Subject> subjects = new ConcurrentHashMap<String, Subject>();

  @Override
  public Subject lookup(String name) {
    if (name == null) {
      throw new NullPointerException();
    }
    return subjects.get(name);
  }

  @Override
  public Subject add(Subject subject) {
    if (subject == null) {
      return subject;
    }
    Subject prior = subjects.putIfAbsent(subject.getName(), subject);
    return (null != prior) ? prior : subject;
  }

  /**
   * @return All fo the {@link Subject} values in this
   *         {@link InMemorySubjectCache}
   */
  public Iterable<Subject> values() {
    return subjects.values();
  }

  @Override
  public boolean remove(String name) {
    if (name == null) {
      throw new NullPointerException();
    }
    Subject subject = subjects.remove(name);
    if (subject != null) {
      return true;
    } else {
      return false;
    }
  }
}
