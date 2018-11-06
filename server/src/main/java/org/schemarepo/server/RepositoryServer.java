package org.schemarepo.server;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import com.google.inject.Guice;
import com.google.inject.Injector;

import org.eclipse.jetty.server.Server;
import org.schemarepo.config.Config;
import org.schemarepo.config.ConfigModule;
import org.schemarepo.config.ServerModule;
import org.schemarepo.rest.RESTRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A {@link RepositoryServer} is a stand-alone server for running a
 * {@link RESTRepository}. {@link #main(String...)} takes a single argument
 * containing a property file for configuration. <br/>
 * <br/>
 *
 * @author zhizhou.ren
 */
public class RepositoryServer {
  private final Server server;

  /**
   * Constructs an instance of this class, overlaying the default properties with
   * any identically-named properties in the supplied {@link Properties} instance.
   *
   * @param props Property values for overriding the defaults.
   *              <p>
   *              <b><i>Any overriding properties must be supplied as type </i>
   *              <code>String</code><i> or they will not work and the default
   *              values will be used.</i></b>
   *
   */
  public RepositoryServer(Properties props) {
    final Logger logger = LoggerFactory.getLogger(getClass());
    final String julToSlf4jDep = "jul-to-slf4j dependency";
    final String julPropName = Config.LOGGING_ROUTE_JUL_TO_SLF4J;
    if (Boolean.parseBoolean(props.getProperty(julPropName, Config.getDefault(julPropName)))) {
      final String slf4jBridgeHandlerName = "org.slf4j.bridge.SLF4JBridgeHandler";
      try {
        final Class<?> slf4jBridgeHandler =
            Class.forName(slf4jBridgeHandlerName, true, Thread.currentThread().getContextClassLoader());
        slf4jBridgeHandler.getMethod("removeHandlersForRootLogger").invoke(null);
        slf4jBridgeHandler.getMethod("install").invoke(null);
        logger.info("Routing java.util.logging traffic through SLF4J");
      } catch (Exception e) {
        logger.error("Failed to install {}, java.util.logging is unaffected. Perhaps you need to add {}",
            slf4jBridgeHandlerName, julToSlf4jDep, e);
      }
    } else {
      logger.info(
          "java.util.logging is NOT routed through SLF4J. Set {} property to true and add {} if you want otherwise",
          julPropName, julToSlf4jDep);
    }

    Injector injector = Guice.createInjector(new ConfigModule(props), new ServerModule());
    this.server = injector.getInstance(Server.class);
  }

  public static void main(String... args) throws Exception {
    if (args.length != 1) {
      printHelp();
      System.exit(1);
    }
    File config = new File(args[0]);
    if (!config.canRead()) {
      System.err.println("Cannot read file: " + config);
      printHelp();
      System.exit(1);
    }
    Properties props = new Properties();
    props.load(new BufferedInputStream(new FileInputStream(config)));
    RepositoryServer server = new RepositoryServer(props);
    try {
      server.start();
      server.join();
    } finally {
      server.stop();
    }
  }

  private static void printHelp() {
    System.err
        .println("One argument expected containing a configuration " + "properties file.  Default properties are:");
    ConfigModule.printDefaults(System.err);
  }

  public void start() throws Exception {
    server.start();
  }

  private void join() throws InterruptedException {
    server.join();
  }

  public void stop() throws Exception {
    server.stop();
  }
}
