package com.ymmihw.spring.security.auth2;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;
import com.ymmihw.spring.security.auth2.ResourceServer;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ResourceServer.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class ResourceServerIntegrationTest {

  @Test
  public void whenLoadApplication_thenSuccess() {

  }
}
