package org.schemarepo;

/**
 * Static Strings used to communicate a message to the end-user.
 */
public class Message {
  public static final String SCHEMA_WITH_NEWLINE_ERROR =
      "ERROR: One of the schemas for this topic contains a new line and won't be parse-able properly. "
          + "Please use a non-plain text format instead (e.g.: JSON).";

  public static final String SUBJECT_DOES_NOT_EXIST_ERROR = "This subject does not exist.";

  public static final String SCHEMA_DOES_NOT_EXIST_ERROR = "This schema does not exist.";

  public static final String SCHEMA_IS_NOT_LEGAL_SYNTAX_ERROR = "Not a legal schema syntax.";

  public static final String CONTENT_TYPE_ERROR = "Content-Type is not set correctly.";

  public static final String ACCEPT_ERROR = "Accept is not set correctly.";
}
