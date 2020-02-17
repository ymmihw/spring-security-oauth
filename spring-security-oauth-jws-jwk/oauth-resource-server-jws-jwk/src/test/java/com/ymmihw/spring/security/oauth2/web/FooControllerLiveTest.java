package com.ymmihw.spring.security.oauth2.web;

import static org.hamcrest.Matchers.isA;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import io.restassured.RestAssured;
import io.restassured.response.Response;

/**
 * This test requires: * oauth-authorization-server-jws-jwk service running in the environment
 *
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class FooControllerLiveTest {

  @Autowired
  private MockMvc mockMvc;

  private static final String TOKEN_URL = "http://localhost:8081/sso-auth-server/oauth/token";
  private static final String RESOURCE_ENDPOINT = "/foos/1";

  @Test
  public void givenAccessToken_whenGetUserResource_thenSuccess() throws Exception {
    String accessToken = obtainAccessToken();

    // Access resources using access token
    this.mockMvc
        .perform(get(RESOURCE_ENDPOINT).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
        .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.name", isA(String.class)));
  }

  private String obtainAccessToken() {
    // get access token
    Map<String, String> params = new HashMap<String, String>();
    params.put("grant_type", "client_credentials");
    params.put("scope", "read");
    Response response = RestAssured.given().auth().basic("bael-client", "bael-secret")
        .formParams(params).post(TOKEN_URL);
    return response.jsonPath().getString("access_token");
  }
}
