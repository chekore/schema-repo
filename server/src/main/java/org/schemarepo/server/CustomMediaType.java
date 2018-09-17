package org.schemarepo.server;

import javax.ws.rs.core.MediaType;


/**
 * @author zhizhou.ren
 */
public class CustomMediaType extends MediaType {
  /** "application/vnd.schemaregistry.v1+json" */
  public final static String APPLICATION_SCHEMA_REGISTRY_JSON = "application/vnd.schemaregistry.v1+json";
  /** "application/vnd.schemaregistry.v1+json" */
  public final static MediaType APPLICATION_SCHEMA_REGISTRY_JSON_TYPE =
    new MediaType("application", "vnd.schemaregistry.v1+json");
}
