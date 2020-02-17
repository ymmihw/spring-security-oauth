package com.ymmihw.spring.security.auth2;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import io.restassured.RestAssured;
import io.restassured.response.Response;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = ResourceServer.class)
public class OAuthMvcTest {

  @Autowired
  private WebApplicationContext wac;

  @Autowired
  private FilterChainProxy springSecurityFilterChain;

  private MockMvc mockMvc;

  private static final String CLIENT_ID = "fooClientIdPassword";
  private static final String CLIENT_SECRET = "secret";

  private static final String CONTENT_TYPE = "application/json;charset=UTF-8";

  private static final String EMAIL = "jim@yahoo.com";
  private static final String NAME = "Jim";

  @Before
  public void setup() {
    this.mockMvc =
        MockMvcBuilders.webAppContextSetup(this.wac).addFilter(springSecurityFilterChain).build();
  }

  private String obtainAccessToken(String username, String password) throws Exception {
    final Map<String, String> params = new HashMap<String, String>();
    params.put("grant_type", "password");
    params.put("client_id", CLIENT_ID);
    params.put("username", username);
    params.put("password", password);
    Response response = RestAssured.given().auth().preemptive().basic(CLIENT_ID, CLIENT_SECRET)
        .and().with().params(params).when()
        .post("http://localhost:8081/spring-security-oauth-server/oauth/token");

    return response.jsonPath().getString("access_token");
  }

  @Test
  public void givenNoToken_whenGetSecureRequest_thenUnauthorized() throws Exception {
    mockMvc.perform(get("/employee").param("email", EMAIL)).andExpect(status().isUnauthorized());
  }

  @Test
  public void givenInvalidRole_whenGetSecureRequest_thenForbidden() throws Exception {
    final String accessToken = obtainAccessToken("john", "123");
    System.out.println("token:" + accessToken);
    mockMvc
        .perform(
            get("/employee").header("Authorization", "Bearer " + accessToken).param("email", EMAIL))
        .andExpect(status().isForbidden());
  }

  @Test
  public void givenToken_whenPostGetSecureRequest_thenOk() throws Exception {
    final String accessToken = obtainAccessToken("tom", "111");

    String employeeString = "{\"email\":\"" + EMAIL + "\",\"name\":\"" + NAME + "\",\"age\":30}";

    mockMvc
        .perform(post("/employee").header("Authorization", "Bearer " + accessToken)
            .contentType(CONTENT_TYPE).content(employeeString).accept(CONTENT_TYPE))
        .andExpect(status().isCreated());

    mockMvc
        .perform(get("/employee").param("email", EMAIL)
            .header("Authorization", "Bearer " + accessToken).accept(CONTENT_TYPE))
        .andExpect(status().isOk()).andExpect(content().contentType(CONTENT_TYPE))
        .andExpect(jsonPath("$.name", is(NAME)));

  }

}
