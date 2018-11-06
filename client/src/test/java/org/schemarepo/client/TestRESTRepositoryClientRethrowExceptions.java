package org.schemarepo.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Properties;

import org.junit.Test;
import org.schemarepo.config.Config;
import org.schemarepo.json.GsonJsonUtil;


/**
 * Tests {@link RESTRepositoryClient} configured to rethrow any unexpected
 * remote exception encountered while talking to the server.
 */
public class TestRESTRepositoryClientRethrowExceptions extends AbstractTestRESTRepositoryClient<RESTRepositoryClient> {

  @Override
  protected RESTRepositoryClient createClient(String repoUrl) {
    return new RESTRepositoryClient(repoUrl, new GsonJsonUtil(), false);
  }

  @Test
  public void testGetStatus() {
    assertTrue(repo.getStatus().startsWith("OK"));
  }

  @Test
  public void testGetConfig() {
    final String defaultKey = Config.JETTY_HEADER_SIZE;
    Properties properties = repo.getConfiguration(false);
    assertEquals("localhost", properties.getProperty(Config.JETTY_HOST));
    assertNull(properties.getProperty(defaultKey));
    properties = repo.getConfiguration(true);
    assertEquals("localhost", properties.getProperty(Config.JETTY_HOST));
    assertEquals(Config.getDefault(defaultKey), properties.getProperty(defaultKey));
  }
}
