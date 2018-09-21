package org.schemarepo.config;

import java.util.concurrent.Executor;

import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.Scheduler;

import com.google.inject.Inject;
import com.google.inject.Singleton;


/**
 * @author zhizhou.ren
 */
@Singleton
public class CustomServerConnector extends ServerConnector {

  @Inject
  public CustomServerConnector(Server server) {
    super(server);
  }

  @Inject
  public CustomServerConnector(Server server, int acceptors, int selectors) {
    super(server, acceptors, selectors);
  }

  @Inject
  public CustomServerConnector(Server server, int acceptors, int selectors, ConnectionFactory... factories) {
    super(server, acceptors, selectors, factories);
  }

  @Inject
  public CustomServerConnector(Server server, ConnectionFactory... factories) {
    super(server, factories);
  }

  @Inject
  public CustomServerConnector(Server server, SslContextFactory sslContextFactory) {
    super(server, sslContextFactory);
  }

  @Inject
  public CustomServerConnector(Server server, int acceptors, int selectors, SslContextFactory sslContextFactory) {
    super(server, acceptors, selectors, sslContextFactory);
  }

  @Inject
  public CustomServerConnector(Server server, SslContextFactory sslContextFactory, ConnectionFactory... factories) {
    super(server, sslContextFactory, factories);
  }

  @Inject
  public CustomServerConnector(Server server, Executor executor, Scheduler scheduler, ByteBufferPool bufferPool,
    int acceptors, int selectors, ConnectionFactory... factories) {
    super(server, executor, scheduler, bufferPool, acceptors, selectors, factories);
  }
}
