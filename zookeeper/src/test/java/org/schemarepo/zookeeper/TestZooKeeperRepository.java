package org.schemarepo.zookeeper;

import java.io.IOException;
import java.util.Date;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.test.TestingCluster;
import org.junit.After;
import org.junit.BeforeClass;
import org.schemarepo.AbstractTestPersistentRepository;
import org.schemarepo.ValidatorFactory;
import org.schemarepo.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestZooKeeperRepository extends AbstractTestPersistentRepository<ZooKeeperRepository> {
  private static final String REPO_PATH = "/schema-repo-tests/" + new Date().toString().replace(' ', '_');
  private static final Integer numberOfServersInTestingCluster = 3;
  private static final Logger logger = LoggerFactory.getLogger(TestZooKeeperRepository.class);
  private static TestingCluster testingCluster;
  private static String testingClusterConnectionString;
  private static CuratorFramework zkClient;

  @BeforeClass
  public static void setup() {
    testingCluster = new TestingCluster(numberOfServersInTestingCluster);
    try {
      testingCluster.start();
      testingClusterConnectionString = testingCluster.getConnectString();

      // Sometimes useful for manual debugging:
      // testingClusterConnectionString = "localhost:2181";

      RetryPolicy retryPolicy = new RetryNTimes(Config.getIntDefault(Config.ZK_CURATOR_SLEEP_TIME_BETWEEN_RETRIES),
          Config.getIntDefault(Config.ZK_CURATOR_NUMBER_OF_RETRIES));
      CuratorFrameworkFactory.Builder cffBuilder =
          CuratorFrameworkFactory.builder().connectString(testingClusterConnectionString)
              .sessionTimeoutMs(Config.getIntDefault(Config.ZK_SESSION_TIMEOUT))
              .connectionTimeoutMs(Config.getIntDefault(Config.ZK_CONNECTION_TIMEOUT)).retryPolicy(retryPolicy)
              .defaultData(new byte[0]);

      zkClient = cffBuilder.build();
      zkClient.start();

      try {
        zkClient.blockUntilConnected();
      } catch (Exception e) {
        logger.error("There was an unrecoverable exception during the ZooKeeper session establishment. Aborting.", e);
        throw new RuntimeException(e);
      }
    } catch (Exception e) {
      logger.error("An exception occurred while trying to start the ZK test cluster!", e);
      throw new RuntimeException(e);
    }
  }

  @After
  public void cleanUp() throws IOException {
    try {
      zkClient.delete().deletingChildrenIfNeeded().forPath(REPO_PATH);
    } catch (Exception e) {
      logger.error("An exception occurred while trying to clean up the ZK test cluster!", e);
      throw new RuntimeException(e);
    }
    repo.close();
  }

  @Override
  protected ZooKeeperRepository createRepository() {
    return newRepo(REPO_PATH);
  }

  private ZooKeeperRepository newRepo(String path) {
    return new ZooKeeperRepository(testingClusterConnectionString, path,
        Config.getIntDefault(Config.ZK_CONNECTION_TIMEOUT), Config.getIntDefault(Config.ZK_SESSION_TIMEOUT),
        Config.getIntDefault(Config.ZK_CURATOR_SLEEP_TIME_BETWEEN_RETRIES),
        Config.getIntDefault(Config.ZK_CURATOR_NUMBER_OF_RETRIES), new ValidatorFactory.Builder().build());
  }
}
