package com.ymmihw.spring.security.oauth2.web;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class JwkSetRestControllerIntegrationTest {

  @Autowired
  private MockMvc mvc;

  @Test
  public void givenContextWithKeyStore_whenRequestJWKSetEndpoint_thenRetrieveJWKSetWithConfiguredKey()
      throws Exception {
    // @formatter:off
        this.mvc.perform(get("/.well-known/jwks.json"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.keys[0].kid", is("bael-key-id")));
        // @formatter:on
  }
}
