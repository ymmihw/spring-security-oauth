package com.ymmihw.authorization.server;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class PasswordAuthorizationServer {
  public static void main(String[] args) {
    new SpringApplicationBuilder(PasswordAuthorizationServer.class).profiles("password").run(args);
  }
}
