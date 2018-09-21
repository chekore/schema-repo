package org.schemarepo.config;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;

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
}
