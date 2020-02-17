package com.ymmihw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@EnableZuulProxy
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class UiApplication extends SpringBootServletInitializer {

  public static void main(String[] args) {
    SpringApplication.run(UiApplication.class, args);
  }
}
