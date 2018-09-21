package org.schemarepo.client;

import java.util.Properties;

import org.schemarepo.AbstractTestRepository;
import org.schemarepo.InMemoryRepository;
import org.schemarepo.config.Config;
import org.schemarepo.server.RepositoryServer;


public abstract class AbstractTestRepositoryClient<R extends RepositoryClient> extends AbstractTestRepository<R> {

  protected RepositoryServer server;

  @Override
  protected R createRepository()
    throws Exception {
    Properties props = new Properties();
    props.put(Config.REPO_CLASS, InMemoryRepository.class.getName());
    props.put(Config.JETTY_HOST, "localhost");
    props.put(Config.JETTY_PORT, "8123");
    props.put(Config.JETTY_GRACEFUL_SHUTDOWN, "100");
    server = new RepositoryServer(props);
    server.start();
    return createClient("http://localhost:8123/schema-repo/");
  }

  protected abstract R createClient(String repoUrl);

  @Override
  public void tearDownRepository()
    throws Exception {
    server.stop();
    super.tearDownRepository();
  }
}
