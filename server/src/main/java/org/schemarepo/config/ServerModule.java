package org.schemarepo.config;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.StatisticsHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.schemarepo.Repository;
import org.schemarepo.server.AuxiliaryRESTRepository;
import org.schemarepo.server.HumanOrientedRESTRepository;
import org.schemarepo.server.MachineOrientedRESTRepository;

import com.google.inject.Provides;
import com.google.inject.servlet.GuiceFilter;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.servlet.http.HttpServlet;


/**
 * @author zhizhou.ren
 */
public class ServerModule extends JerseyServletModule {
  @Override
  protected void configureServlets() {
    super.configureServlets();
    Map<String, String> initParams = new HashMap<String, String>(1);
    // for debug
    // initParams.put("com.sun.jersey.config.feature.Trace", "true");
    initParams.put("com.sun.jersey.api.json.POJOMappingFeature", "true");
    //bind(Connector.class).to(ServerConnector.class, new Server());
    bind(DefaultServlet.class);
    serve("/*").with(GuiceContainer.class, initParams);
    bind(MachineOrientedRESTRepository.class);
    bind(HumanOrientedRESTRepository.class);
    bind(AuxiliaryRESTRepository.class);
  }

  @Provides
  @Singleton
  public Server provideServer(@Named(Config.JETTY_HOST) String host, @Named(Config.JETTY_PORT) Integer port,
    @Named(Config.JETTY_HEADER_SIZE) Integer headerSize, @Named(Config.JETTY_BUFFER_SIZE) Integer bufferSize,
    @Named(Config.JETTY_STOP_AT_SHUTDOWN) Boolean stopAtShutdown,
    @Named(Config.JETTY_GRACEFUL_SHUTDOWN) Integer gracefulShutdown, Repository repo, Connector connector,
    GuiceFilter guiceFilter, ServletContextHandler handler) {

    HttpConfiguration configuration = new HttpConfiguration();
    configuration.setRequestHeaderSize(headerSize);
    configuration.setResponseHeaderSize(bufferSize);

    HttpConnectionFactory factory = new HttpConnectionFactory(configuration);
    Server server = new Server();
    ServerConnector serverConnector = (ServerConnector) connector;
    serverConnector.addConnectionFactory(factory);
    if (null != host && !host.isEmpty()) {
      serverConnector.setHost(host);
    }
    serverConnector.setPort(port);
    server.setConnectors(new Connector[]{serverConnector});

    // the guice filter intercepts all inbound requests and uses its bindings
    // for servlets
    FilterHolder holder = new FilterHolder(guiceFilter);
    handler.addFilter(holder, "/*", null);
    handler.addServlet(ServerModule.NoneServlet.class, "/");
    handler.setContextPath("/");
    handler.addLifeCycleListener(new ShutDownListener(repo, gracefulShutdown));
    server.setHandler(handler);
    StatisticsHandler statisticsHandler = new StatisticsHandler();
    statisticsHandler.setHandler(server.getHandler());
    server.setHandler(statisticsHandler);
    server.dumpStdErr();
    server.setStopAtShutdown(stopAtShutdown);
    server.setStopTimeout(gracefulShutdown);
    return server;
  }

  private static final class NoneServlet extends HttpServlet {
    private static final long serialVersionUID = 4560115319373180139L;
  }
}
