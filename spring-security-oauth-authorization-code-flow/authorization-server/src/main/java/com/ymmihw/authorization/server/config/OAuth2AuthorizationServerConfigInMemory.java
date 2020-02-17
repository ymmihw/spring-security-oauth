package com.ymmihw.authorization.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;

@Configuration
@EnableAuthorizationServer
public class OAuth2AuthorizationServerConfigInMemory extends AuthorizationServerConfigurerAdapter {


  @Override
  public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
    oauthServer.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
  }

  @Override
  public void configure(final ClientDetailsServiceConfigurer clients) throws Exception {
    PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    clients.inMemory().withClient("fooClientId").secret(encoder.encode("secret"))
        .authorizedGrantTypes("authorization_code").scopes("foo", "read", "write")
        .redirectUris("http://localhost:8089/", "http://172.16.10.141:8082/").autoApprove(true);
  }

}
