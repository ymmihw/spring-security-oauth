package com.ymmihw.authorization.server;

import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;

public class DynamicClientRegistrationTest {
    @Test
    public void givenDBUser_whenRevokeToken_thenAuthorized() {
        String accessToken = obtainAccessToken("fooClientIdPassword", "john", "123");

        assertNotNull(accessToken);
    }

    private String obtainAccessToken(String clientId, String username, String password) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("grant_type", "password");
        params.put("client_id", clientId);
        params.put("username", username);
        params.put("password", password);
        Response response = RestAssured.given()
                                       .auth()
                                       .preemptive()
                                       .basic(clientId, "secret")
                                       .and()
                                       .with()
                                       .params(params)
                                       .when()
                                       .post("http://localhost:8081/spring-security-oauth-server/oauth/token");
        return response.jsonPath()
                       .getString("access_token");
    }
}
