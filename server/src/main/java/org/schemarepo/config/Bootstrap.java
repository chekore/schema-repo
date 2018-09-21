package org.schemarepo.config;

import com.google.inject.Singleton;
import com.sun.jersey.spi.container.servlet.ServletContainer;
import com.sun.jersey.spi.container.servlet.WebConfig;

//import io.swagger.jaxrs.config.BeanConfig;
import javax.servlet.ServletException;


/**
 * @author zhizhou.ren
 */
@Singleton
public class Bootstrap extends ServletContainer {
  @Override
  protected void init(WebConfig config)
    throws ServletException {
//    super.init(config);
//    BeanConfig beanConfig = new BeanConfig();
//    beanConfig.setVersion("1.0.0");
//    beanConfig.setSchemes(new String[]{"http"});
//    beanConfig.setHost("localhost:8085");
//    beanConfig.setBasePath("/api");
//    beanConfig.setResourcePackage("org.schemarepo.rest");
//    beanConfig.setScan(true);
  }
}
