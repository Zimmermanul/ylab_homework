package com.mkhabibullin;

import com.mkhabibullin.infrastructure.config.RootConfig;
import com.mkhabibullin.infrastructure.config.WebConfig;
import jakarta.servlet.FilterRegistration;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRegistration;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * Primary web application initializer that configures the Spring MVC infrastructure.
 * Implements programmatic configuration of the Servlet 3.0+ container in place of web.xml.
 * <p>
 * This initializer:
 * - Creates the root application context
 * - Sets up the dispatcher servlet
 * - Configures default profile
 * - Establishes URL mapping
 */
public class MainWebAppInitializer implements WebApplicationInitializer {
  
  /**
   * Configures the Servlet context programmatically on container startup.
   * <p>
   * Creates and registers:
   * - Root application context with {@link RootConfig}
   * - Development profile as default
   * - Dispatcher servlet with {@link WebConfig}
   * - URL mapping for all requests to the dispatcher
   *
   * @param servletContext The servlet context to be initialized
   * @throws ServletException if any error occurs during initialization
   */
  @Override
  public void onStartup(ServletContext servletContext) throws ServletException {
    AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
    rootContext.register(RootConfig.class);
    servletContext.setInitParameter("spring.profiles.default", "dev");
    servletContext.addListener(new ContextLoaderListener(rootContext));
    AnnotationConfigWebApplicationContext dispatcherContext = new AnnotationConfigWebApplicationContext();
    dispatcherContext.register(WebConfig.class);
    ServletRegistration.Dynamic dispatcher = servletContext.addServlet(
      "dispatcher", new DispatcherServlet(dispatcherContext));
    dispatcher.setLoadOnStartup(1);
    dispatcher.addMapping("/");
    FilterRegistration.Dynamic characterEncodingFilter = servletContext.addFilter(
      "characterEncodingFilter", new CharacterEncodingFilter());
    characterEncodingFilter.setInitParameter("encoding", "UTF-8");
    characterEncodingFilter.setInitParameter("forceEncoding", "true");
    characterEncodingFilter.addMappingForUrlPatterns(null, true, "/*");
  }
}
