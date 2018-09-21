package org.schemarepo;

public class TestCacheRepository extends AbstractTestRepository<CacheRepository> {

  @Override
  protected CacheRepository createRepository() {
    return new CacheRepository(new InMemoryRepository(new ValidatorFactory.Builder().build()), new InMemoryCache());
  }
}
