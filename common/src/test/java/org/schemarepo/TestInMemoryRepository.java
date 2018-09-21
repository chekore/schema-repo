package org.schemarepo;

public class TestInMemoryRepository extends AbstractTestRepository<InMemoryRepository> {
  @Override
  protected InMemoryRepository createRepository() {
    return new InMemoryRepository(new ValidatorFactory.Builder().build());
  }
}
