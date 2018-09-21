package org.schemarepo.client;

import org.junit.Ignore;
import org.junit.Test;


public class TestAvro1124RESTRepositoryClient extends AbstractTestRESTRepositoryClient<Avro1124RESTRepositoryClient> {

  @Override
  protected Avro1124RESTRepositoryClient createClient(String repoUrl) {
    return new Avro1124RESTRepositoryClient(repoUrl);
  }

  @Test
  @Ignore("This test is skipped because we know the old client is broken "
    + "when calling allEntries() for schemas with new lines.")
  public void testAllEntriesMultiLineSchema()
    throws Exception {
    // Skipped
  }
}
