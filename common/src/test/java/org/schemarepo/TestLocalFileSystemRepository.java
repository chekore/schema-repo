package org.schemarepo;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertTrue;


public class TestLocalFileSystemRepository extends AbstractTestPersistentRepository<LocalFileSystemRepository> {
  private static final String TEST_PATH = "target/test/TestLocalFileSystemRepository-paths/";
  private static final String REPO_PATH = "target/test/TestLocalFileSystemRepository/";

  @BeforeClass
  public static void setup() {
    rmDir(new File(TEST_PATH));
    rmDir(new File(REPO_PATH));
  }

  private static void rmDir(File dir) {
    if (!dir.exists() || !dir.isDirectory()) {
      return;
    }
    for (String filename : dir.list()) {
      File entry = new File(dir, filename);
      if (entry.isDirectory()) {
        rmDir(entry);
      } else {
        entry.delete();
      }
    }
    dir.delete();
  }

  @After
  public void cleanUp()
    throws Exception {
    LoggerFactory.getLogger(getClass()).debug("Closing");
    getRepo().close();
    // see https://github.com/schema-repo/schema-repo/issues/12
    if (System.getProperty("os.name", "").toLowerCase().contains("windows")) {
      System.gc();
      Thread.sleep(100);
    }
    // Clean up the repo's content
    rmDir(new File(REPO_PATH));
  }

  @Override
  protected LocalFileSystemRepository createRepository() {
    return newRepo(REPO_PATH);
  }

  private LocalFileSystemRepository newRepo(String path) {
    return new LocalFileSystemRepository(path, new ValidatorFactory.Builder().build());
  }

  @Test
  public void testPathHandling()
    throws SchemaValidationException {
    String paths[] = new String[]{"data", "data/", "/tmp/file_repo", "/tmp/file_repo/", "/tmp/file_repo/"};

    for (String path : paths) {
      LocalFileSystemRepository r = newRepo(TEST_PATH + path);
      try {
        File expected = new File(TEST_PATH, path);
        assertTrue("Expected directory not created: " + expected.getAbsolutePath() + " for path: " + path,
          expected.exists());
      } finally {
        r.close();
        // should be ok to call close twice
        r.close();
      }
    }
    // verify idempotent
    newRepo(TEST_PATH + "/tmp/repo").close();
    newRepo(TEST_PATH + "/tmp/repo").close();
  }

  @Test(expected = RuntimeException.class)
  public void testInvalidDir()
    throws IOException {
    String badPath = TEST_PATH + "/bad";
    new File(TEST_PATH).mkdirs();
    new File(badPath).createNewFile();
    LocalFileSystemRepository r = newRepo(badPath);
    r.close();
  }

  @Test(expected = IllegalStateException.class)
  public void testCantUseClosedRepo() {
    LocalFileSystemRepository r = newRepo(TEST_PATH + "/tmp/repo");
    r.close();
    r.lookup("nothing");
  }
}
