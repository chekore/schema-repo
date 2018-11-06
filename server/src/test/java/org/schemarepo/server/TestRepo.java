package org.schemarepo.server;

import java.util.Properties;

import org.junit.Test;
import org.schemarepo.InMemoryRepository;


public class TestRepo {

  @Test
  public void testRepoInit() throws Exception {
    Properties props = new Properties();
    props.setProperty("schema-repo.class", InMemoryRepository.class.getName());
    props.setProperty("schema-repo.jetty.port", "6782");
    props.setProperty("schema-repo.jetty.graceful-shutdown", "10"); // Shutdown quickly for tests

    RepositoryServer server = new RepositoryServer(props);

    server.start();

    server.stop();
  }
}
