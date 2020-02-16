package com.ymmihw.authorization.server;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class ImplicitAuthorizationServer {
  public static void main(String[] args) {
    new SpringApplicationBuilder(ImplicitAuthorizationServer.class).profiles("implicit").run(args);
  }
}
