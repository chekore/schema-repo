package org.schemarepo.config;

import java.util.HashMap;
import java.util.Map;

import com.sun.jersey.guice.JerseyServletModule;


public class SwaggerModule extends JerseyServletModule {
  @Override
  protected void configureServlets() {
    super.configureServlets();
    Map<String, String> params = new HashMap<String, String>();
    params.put("com.sun.jersey.config.property.packages",
      "io.swagger.jaxrs.json;io.swagger.jaxrs.listing;org.schemarepo.server");
    serve("/api/*").with(Bootstrap.class, params);
  }
}
