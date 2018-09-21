package org.schemarepo;

import org.junit.Assert;
import org.junit.Test;


public class TestSchemaEntry {
  @Test
  public void testToString() {
    SchemaEntry entry = new SchemaEntry("id", "schema");
    SchemaEntry toAndFrom = new SchemaEntry(entry.toString());
    Assert.assertEquals(entry, toAndFrom);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalid() {
    new SchemaEntry("invalidString");
  }

  @Test
  public void testGetters() {
    String id = "id";
    String sc = "sc";
    SchemaEntry entry = new SchemaEntry(id, sc);
    Assert.assertEquals(id, entry.getId());
    Assert.assertEquals(sc, entry.getSchema());
  }

  @Test
  public void testEqualsAndHashCode() {
    SchemaEntry entry = new SchemaEntry("id", "schema");
    SchemaEntry entry2 = new SchemaEntry("id", "schema");
    SchemaEntry nullId = new SchemaEntry(null, "schema");
    SchemaEntry nullSchema = new SchemaEntry("id", null);
    SchemaEntry idDiffers = new SchemaEntry("iddd", "schema");
    SchemaEntry sDiffers = new SchemaEntry("id", "schemaaaa");
    SchemaEntry allNull = new SchemaEntry(null, null);
    SchemaEntry allNull2 = new SchemaEntry(null, null);

    Assert.assertEquals(entry, entry);
    Assert.assertEquals(entry, entry2);
    Assert.assertEquals(entry2, entry);
    Assert.assertEquals(entry.hashCode(), entry2.hashCode());

    Assert.assertFalse(entry.equals(null));
    Assert.assertFalse(entry.equals(new Object()));

    Assert.assertFalse(entry.equals(nullId));
    Assert.assertFalse(nullId.equals(entry));

    Assert.assertFalse(entry.equals(nullSchema));
    Assert.assertFalse(nullSchema.equals(entry));

    Assert.assertFalse(entry.equals(idDiffers));
    Assert.assertFalse(idDiffers.equals(entry));

    Assert.assertFalse(entry.equals(sDiffers));
    Assert.assertFalse(sDiffers.equals(entry));

    Assert.assertEquals(allNull, allNull2);
    Assert.assertEquals(allNull.hashCode(), allNull2.hashCode());
  }
}
