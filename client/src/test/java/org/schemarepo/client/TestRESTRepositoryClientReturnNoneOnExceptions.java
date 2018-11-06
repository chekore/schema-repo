package org.schemarepo.client;

import org.schemarepo.json.GsonJsonUtil;


/**
 * Tests {@link org.schemarepo.client.RESTRepositoryClient} configured to return
 * a 'none' (e.g. null or empty collection) values in case of any remote errors
 * encountered while talking to the server.
 */
public class TestRESTRepositoryClientReturnNoneOnExceptions
    extends AbstractTestRESTRepositoryClient<RESTRepositoryClient> {

  @Override
  protected RESTRepositoryClient createClient(String repoUrl) {
    return new RESTRepositoryClient(repoUrl, new GsonJsonUtil(), true);
  }
}
