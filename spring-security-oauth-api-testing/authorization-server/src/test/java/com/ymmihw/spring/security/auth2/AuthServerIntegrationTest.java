package com.ymmihw.spring.security.auth2;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;
import com.ymmihw.spring.security.auth2.AuthorizationServer;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AuthorizationServer.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class AuthServerIntegrationTest {
  @Test
  public void whenLoadApplication_thenSuccess() {

  }
}
