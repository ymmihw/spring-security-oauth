package com.ymmihw.authorization.server;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import io.restassured.RestAssured;
import io.restassured.response.Response;

public class TokenRevocationLiveTest {

  @Test
  public void whenObtainingAccessToken_thenCorrect() {
    final Response authServerResponse = obtainAccessToken("fooClientId", "john", "123");
    final String accessToken = authServerResponse.jsonPath().getString("access_token");
    assertNotNull(accessToken);

    final Response resourceServerResponse =
        RestAssured.given().header("Authorization", "Bearer " + accessToken)
            .get("http://localhost:8082/spring-security-oauth-resource/foos/100");
    assertThat(resourceServerResponse.getStatusCode(), equalTo(200));
  }

  //

  private Response obtainAccessToken(String clientId, String username, String password) {
    final Map<String, String> params = new HashMap<String, String>();
    params.put("grant_type", "password");
    params.put("client_id", clientId);
    params.put("username", username);
    params.put("password", password);
    return RestAssured.given().auth().preemptive().basic(clientId, "secret").and().with()
        .params(params).when()
        .post("http://localhost:8081/spring-security-oauth-server/oauth/token");
    // response.jsonPath().getString("refresh_token");
    // response.jsonPath().getString("access_token")
  }

  private String obtainRefreshToken(String clientId, final String refreshToken) {
    final Map<String, String> params = new HashMap<String, String>();
    params.put("grant_type", "refresh_token");
    params.put("client_id", clientId);
    params.put("refresh_token", refreshToken);
    final Response response = RestAssured.given().auth().preemptive().basic(clientId, "secret")
        .and().with().params(params).when()
        .post("http://localhost:8081/spring-security-oauth-server/oauth/token");
    return response.jsonPath().getString("access_token");
  }

  private void authorizeClient(String clientId) {
    final Map<String, String> params = new HashMap<String, String>();
    params.put("response_type", "code");
    params.put("client_id", clientId);
    params.put("scope", "read,write");
    RestAssured.given().auth().preemptive().basic(clientId, "secret").and().with().params(params)
        .when().post("http://localhost:8081/spring-security-oauth-server/oauth/authorize");
  }

  @Test
  public void givenUser_whenRevokeToken_thenTokenInvalidError() {
    final Response response = obtainAccessToken("fooClientId", "john", "123");
    authorizeClient("fooClientId");
    String accessToken1 = response.jsonPath().getString("access_token");

    RestAssured.given().header("Authorization", "Bearer " + accessToken1)
        .get("http://localhost:8081/spring-security-oauth-server/tokens");

    final Response response1 = RestAssured.given().header("Authorization", "Bearer " + accessToken1)
        .get("http://localhost:8082/spring-security-oauth-resource/foos/100");
    assertEquals(200, response1.getStatusCode());



    final Response response2 =
        RestAssured.given().header("Authorization", "Bearer " + accessToken1).post(
            "http://localhost:8081/spring-security-oauth-server/tokens/revokeById/" + accessToken1);
    assertEquals(200, response2.getStatusCode());

    final Response response3 = RestAssured.given().header("Authorization", "Bearer " + accessToken1)
        .get("http://localhost:8082/spring-security-oauth-resource/foos/100");
    assertEquals(401, response3.getStatusCode());
  }

  @Test
  public void givenUser_whenRevokeRefreshToken_thenRefreshTokenInvalidError() {
    final Response response = obtainAccessToken("fooClientId", "john", "123");
    authorizeClient("fooClientId");
    String refreshToken = response.jsonPath().getString("refresh_token");
    final String accessToken2 = obtainRefreshToken("fooClientId", refreshToken);
    authorizeClient("fooClientId");
    final Response response1 = RestAssured.given().header("Authorization", "Bearer " + accessToken2)
        .get("http://localhost:8082/spring-security-oauth-resource/foos/100");
    assertEquals(200, response1.getStatusCode());

    final Response revokeRefreshTokenResponse =
        RestAssured.given().header("Authorization", "Bearer " + accessToken2)
            .post("http://localhost:8081/spring-security-oauth-server/tokens/revokeRefreshToken/"
                + refreshToken);
    assertEquals(200, revokeRefreshTokenResponse.getStatusCode());

    final String accessToken3 = obtainRefreshToken("fooClientId", refreshToken);
    authorizeClient("fooClientId");
    final Response response3 = RestAssured.given().header("Authorization", "Bearer " + accessToken3)
        .get("http://localhost:8082/spring-security-oauth-resource/foos/100");
    assertEquals(401, response3.getStatusCode());
  }
}
