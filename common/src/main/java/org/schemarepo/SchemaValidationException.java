package org.schemarepo;

public class SchemaValidationException extends Exception {
  private static final long serialVersionUID = -3915576082651907606L;

  public SchemaValidationException(String msg) {
    super(msg);
  }
}
