package org.schemarepo;

import java.util.HashSet;

import org.junit.Assert;
import org.junit.Test;


public class TestValidatorFactory {

  @Test
  public void test() throws SchemaValidationException {
    Validator foo = new Validator() {
      @Override
      public void validate(String schemaToValidate, Iterable<SchemaEntry> schemasInOrder)
          throws SchemaValidationException {}
    };

    ValidatorFactory fact = new ValidatorFactory.Builder().setValidator("foo", foo).build();
    HashSet<String> fooset = new HashSet<String>();
    fooset.add("foo");
    fooset.add(null); // should ignore
    Assert.assertSame(foo, fact.getValidators(fooset).get(0));
    fact.getValidators(fooset).get(0).validate(null, null);
  }

  @Test(expected = RuntimeException.class)
  public void testInvalidName() {
    new ValidatorFactory.Builder().setValidator("repo.willBreak", null);
  }
}
