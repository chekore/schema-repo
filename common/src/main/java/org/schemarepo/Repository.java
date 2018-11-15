package org.schemarepo;

import java.io.Closeable;


/**
 * A {@link Repository} is a collection of {@link Subject}s. A {@link Subject} can be looked up by
 * name on a {@link Repository}, or registered.<br/>
 * <br/>
 * Registration of a {@link Subject} in a {@link Repository} is done via
 * {@link #register(String, SubjectConfig)}, which requires the subject name and its configuration.
 * The configuration is a map of configuration keys to configuration values, both of which are
 * Strings.
 */
public interface Repository extends Closeable {

  /**
   * Attempt to create a Subject with the given name and validator.
   *
   * @param subjectName The name of the subject. Must not be null.
   * @param config      The subject configuration. May be null.
   * @return The newly created Subject, or an equivalent one if already created. Does not return null.
   * @throws NullPointerException if subjectName is null
   */
  Subject register(String subjectName, SubjectConfig config);

  /**
   * Returns the subject if it exists, null otherwise.
   *
   * @param subjectName the subject name
   * @return The subject if it exists, null otherwise.
   */
  Subject lookup(String subjectName);

  /**
   * List all subjects. Does not return null.
   */
  Iterable<Subject> subjects();

  /**
   * Attempt to delete a Subject with the given name and validator.
   * 
   * @param subjectName The name of the subject. Must not be null.
   * @return true or false
   */
  boolean delete(String subjectName);
}
