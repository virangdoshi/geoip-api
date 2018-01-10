package com.s24.geoip.web;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.catalina.valves.AccessLogValve;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.GenericFilterBean;

/**
 * Disables Logging for Healthchecks
 */
@Configuration
public class TomcatLoggingConfiguration {

    private static final String MARKER = "ignore-access-log";

    @Bean
    public FilterRegistrationBean loggingExcludeFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new GenericFilterBean() {
            @Override
            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                    throws IOException, ServletException {
                request.setAttribute(MARKER, Boolean.TRUE);
                chain.doFilter(request, response);
            }
        });

        registration.addUrlPatterns("/0.0.0.1");
        return registration;
    }

    @Bean
    public EmbeddedServletContainerCustomizer servletContainerCustomizer() {
        return new EmbeddedServletContainerCustomizer() {

            @Override
            public void customize(ConfigurableEmbeddedServletContainer container) {
                if (container instanceof TomcatEmbeddedServletContainerFactory) {
                    TomcatEmbeddedServletContainerFactory tomcat = (TomcatEmbeddedServletContainerFactory) container;

                    tomcat.getEngineValves().stream()
                            .filter(valve -> (valve instanceof AccessLogValve))
                            .findFirst()
                            .ifPresent(valve -> ((AccessLogValve) valve).setConditionUnless(MARKER));
                }
            }
        };
    }
}
