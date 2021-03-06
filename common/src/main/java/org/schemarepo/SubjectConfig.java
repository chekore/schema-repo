package org.schemarepo;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * A {@link SubjectConfig} is effectively a Map<String, String> , with reserved
 * keys and default values for certain keys. <br/>
 * Keys starting with "repo." are reserved.
 *
 */
public class SubjectConfig {
  public static final String VALIDATORS_KEY = "repo.validators";
  private static final SubjectConfig EMPTY = new Builder().build();
  private static final String RESERVED_PREFIX = "repo.";
  private final Map<String, String> conf;
  private final Set<String> validators;

  private SubjectConfig(Map<String, String> conf, Set<String> validators) {
    this.conf = conf;
    this.validators = validators;
  }

  public static SubjectConfig emptyConfig() {
    return EMPTY;
  }

  public String get(String key) {
    return conf.get(key);
  }

  public Set<String> getValidators() {
    return validators;
  }

  public Map<String, String> asMap() {
    return conf;
  }

  @Override
  public int hashCode() {
    return conf.hashCode() * 31 + validators.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    SubjectConfig other = (SubjectConfig) obj;
    if (!validators.equals(other.validators)) {
      return false;
    }
    return conf.equals(other.conf);
  }

  public static class Builder {

    private final HashMap<String, String> conf = new HashMap<String, String>();
    private final HashSet<String> validators = new HashSet<String>();

    public Builder set(Map<String, String> config) {
      for (Map.Entry<String, String> entry : config.entrySet()) {
        set(entry.getKey(), entry.getValue());
      }
      return this;
    }

    public Builder set(String key, String value) {
      if (key.startsWith(RESERVED_PREFIX)) {
        if (VALIDATORS_KEY.equals(key)) {
          setValidators(RepositoryUtil.commaSplit(value));
        } else {
          throw new RuntimeException("SubjectConfig keys starting with '" + RESERVED_PREFIX
              + "' are reserved, failed to set: " + key + " to value: " + value);
        }
      } else {
        conf.put(key, value);
      }
      return this;
    }

    public Builder setValidators(Collection<String> validatorNames) {
      this.validators.clear();
      this.conf.remove(VALIDATORS_KEY);
      if (!validatorNames.isEmpty()) {
        this.validators.addAll(validatorNames);
      }
      // put the config entry even if they specified an empty list of validators. This
      // is explicitly "no validators"
      this.conf.put(VALIDATORS_KEY, RepositoryUtil.commaJoin(validators));
      return this;
    }

    public Builder addValidator(String validatorName) {
      this.validators.add(validatorName);
      this.conf.put(VALIDATORS_KEY, RepositoryUtil.commaJoin(validators));
      return this;
    }

    public SubjectConfig build() {
      return new SubjectConfig(Collections.unmodifiableMap(new HashMap<String, String>(conf)),
          Collections.unmodifiableSet(new HashSet<String>(validators)));
    }
  }
}
