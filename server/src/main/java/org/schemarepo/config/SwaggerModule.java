package org.schemarepo.config;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.servlet.ServletModule;


/**
 * @author zhizhou.ren
 */
public class SwaggerModule extends ServletModule {
  @Override
  protected void configureServlets() {
    super.configureServlets();
    Map<String, String> params = new HashMap<String, String>();
    params.put("com.sun.jersey.config.property.packages",
      "io.swagger.jaxrs.json;io.swagger.jaxrs.listing;org.schemarepo.rest");
    serve("/api/*").with(Bootstrap.class, params);
  }
}
