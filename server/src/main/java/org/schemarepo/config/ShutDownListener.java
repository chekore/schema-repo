package org.schemarepo.config;

import java.io.IOException;

import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.util.component.LifeCycle;
import org.schemarepo.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Takes care of calling close() on the repo implementation.
 *
 * These hooks will not get called if stopAtShutdown is set to false, which can be set
 * via the Config.JETTY_STOP_AT_SHUTDOWN property.
 * @author zhizhou.ren
 */
public class ShutDownListener extends AbstractLifeCycle.AbstractLifeCycleListener {
  private final Logger logger = LoggerFactory.getLogger(getClass());
  private final Repository repo;
  private final Integer gracefulShutdown;

  ShutDownListener(Repository repo, Integer gracefulShutdown) {
    this.repo = repo;
    this.gracefulShutdown = gracefulShutdown;
  }

  @Override
  public void lifeCycleStopped(LifeCycle event) {
    logger.info("Waited {} ms to drain requests before closing the repo and exiting. "
        + "This wait time can be adjusted with the {} config property.", gracefulShutdown,
      Config.JETTY_GRACEFUL_SHUTDOWN);

    try {
      repo.close();
      logger.info("Successfully closed the repo.");
    } catch (IOException e) {
      logger.warn("Failed to properly close repo", e);
    }
  }
}
