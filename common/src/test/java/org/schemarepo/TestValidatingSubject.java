package org.schemarepo;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class TestValidatingSubject {
  private static final String ACCEPT = "accept";
  private static final String REJECT = "reject";
  private static final String FOO = "foo";
  private static final String BAR = "bar";
  private static final String BAZ = "baz";
  private static final String ACCEPT_VALIDATOR = "accept.validator";

  private InMemoryRepository repo;

  @Before
  public void setUpRepository() {
    repo = new InMemoryRepository(new ValidatorFactory.Builder().setValidator(ACCEPT_VALIDATOR, new Validator() {
      @Override
      public void validate(String schemaToValidate, Iterable<SchemaEntry> schemasInOrder)
          throws SchemaValidationException {
        // nothing
      }
    }).build());
  }

  @After
  public void tearDownRepository() {
    repo = null;
  }

  @Test
  public void testSuccessfulValidation() throws SchemaValidationException {

    Subject accept = repo.register(ACCEPT, new SubjectConfig.Builder().addValidator(ACCEPT_VALIDATOR).build());

    SchemaEntry foo = accept.registerIfLatest(FOO, null);
    Assert.assertNotNull("failed to register schema", foo);
    SchemaEntry bar = accept.registerIfLatest(BAR, foo);
    Assert.assertNotNull("failed to register schema", bar);
    SchemaEntry none = accept.registerIfLatest("nothing", null);
    Assert.assertNull(none);
    SchemaEntry baz = accept.register(BAZ);
    Assert.assertNotNull("failed to register schema", baz);
  }

  @Test
  public void testValidatorConstruction() {
    Assert.assertNull("Must pass null through Subject.validateWith()",
        Subject.validatingSubject(null, new ValidatorFactory.Builder().build()));
  }

  @Test(expected = SchemaValidationException.class)
  public void testCannotRegister() throws SchemaValidationException {
    Subject reject =
        repo.register(REJECT, new SubjectConfig.Builder().addValidator(ValidatorFactory.REJECT_VALIDATOR).build());
    reject.register(FOO);
  }

  @Test(expected = SchemaValidationException.class)
  public void testCannotRegisterIfLatest() throws SchemaValidationException {
    Subject reject =
        repo.register(REJECT, new SubjectConfig.Builder().addValidator(ValidatorFactory.REJECT_VALIDATOR).build());
    reject.registerIfLatest(FOO, null);
  }
}
