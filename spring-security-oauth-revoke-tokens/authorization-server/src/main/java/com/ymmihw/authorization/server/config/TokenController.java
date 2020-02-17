package com.ymmihw.authorization.server.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TokenController {

  @Resource(name = "tokenServices")
  ConsumerTokenServices tokenServices;

  @Resource(name = "tokenStore")
  TokenStore tokenStore;

  @RequestMapping(method = RequestMethod.POST, value = "/tokens/revokeById/{tokenId}")
  @ResponseBody
  public void revokeToken(HttpServletRequest request, @PathVariable String tokenId) {
    tokenServices.revokeToken(tokenId);
  }

  @RequestMapping(method = RequestMethod.GET, value = "/tokens")
  @ResponseBody
  public List<String> getTokens() {
    List<String> tokenValues = new ArrayList<String>();
    Collection<OAuth2AccessToken> tokens = tokenStore.findTokensByClientId("fooClientId");
    if (tokens != null) {
      for (OAuth2AccessToken token : tokens) {
        tokenValues.add(token.getValue());
      }
    }
    return tokenValues;
  }

  @RequestMapping(method = RequestMethod.POST, value = "/tokens/revokeRefreshToken/{tokenId:.*}")
  @ResponseBody
  public String revokeRefreshToken(@PathVariable String tokenId) {
    OAuth2RefreshToken token = () -> tokenId;
    tokenStore.removeRefreshToken(token);
    return tokenId;
  }

}
