package org.schemarepo;

import java.util.ArrayList;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;


public class TestRepositoryUtil {

  @Test(expected = IllegalArgumentException.class)
  public void validateNullString() {
    RepositoryUtil.validateSchemaOrSubject(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void validateEmptyString() {
    RepositoryUtil.validateSchemaOrSubject("");
  }

  @Test
  public void validateString() {
    RepositoryUtil.validateSchemaOrSubject("org.schemarepo.Something");
  }

  @Test
  public void testSchemasToFromString() {
    SchemaEntry e1 = new SchemaEntry("id1", "s1");
    SchemaEntry e2 = new SchemaEntry("id2", "s2");
    ArrayList<SchemaEntry> empty = new ArrayList<SchemaEntry>();
    ArrayList<SchemaEntry> vals = new ArrayList<SchemaEntry>();
    vals.add(e1);
    vals.add(e2);

    Iterable<SchemaEntry> emptyResult = RepositoryUtil.schemasFromString(RepositoryUtil.schemasToString(empty));
    Iterable<SchemaEntry> emptyResult2 = RepositoryUtil.schemasFromString(null);
    Iterable<SchemaEntry> emptyResult3 = RepositoryUtil.schemasFromString("");
    Assert.assertEquals(empty, emptyResult);
    Assert.assertEquals(emptyResult, emptyResult2);
    Assert.assertEquals(emptyResult, emptyResult3);

    Iterable<SchemaEntry> result = RepositoryUtil.schemasFromString(RepositoryUtil.schemasToString(vals));
    Assert.assertEquals(vals, result);
  }

  @Test
  public void testSubjectsToFromString() {
    Repository r = new InMemoryRepository(new ValidatorFactory.Builder().build());
    Subject s1 = r.register("s1", null);
    Subject s2 = r.register("s2", null);
    ArrayList<Subject> empty = new ArrayList<Subject>();
    ArrayList<Subject> vals = new ArrayList<Subject>();
    vals.add(s1);
    vals.add(s2);

    Iterable<String> emptyResult = RepositoryUtil.subjectNamesFromString(RepositoryUtil.subjectsToString(empty));
    Iterable<String> emptyResult2 = RepositoryUtil.subjectNamesFromString(null);
    Iterable<String> emptyResult3 = RepositoryUtil.subjectNamesFromString("");
    validate(emptyResult, empty);
    Assert.assertEquals(emptyResult, emptyResult2);
    Assert.assertEquals(emptyResult, emptyResult3);

    Iterable<String> result = RepositoryUtil.subjectNamesFromString(RepositoryUtil.subjectsToString(vals));
    validate(result, vals);
  }

  private void validate(Iterable<String> names, Iterable<Subject> subjects) {
    Iterator<String> nameIter = names.iterator();
    for (Subject s : subjects) {
      String name = nameIter.next();
      Assert.assertEquals(s.getName(), name);
    }
  }
}
