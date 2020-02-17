package com.ymmihw.resource.server.config;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.DelegatingJwtClaimsSetVerifier;
import org.springframework.security.oauth2.provider.token.store.IssuerClaimVerifier;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtClaimsSetVerifier;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class OAuth2ResourceServerConfigRemoteTokenService extends ResourceServerConfigurerAdapter {

  @Override
  public void configure(final HttpSecurity http) throws Exception {
    http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED).and()
        .authorizeRequests().anyRequest().permitAll();
  }

  @Bean
  public TokenStore tokenStore() {
    return new JwtTokenStore(symmetryAccessTokenConverter());
  }

  @Autowired
  private CustomAccessTokenConverter customAccessTokenConverter;

  @Bean
  public JwtClaimsSetVerifier jwtClaimsSetVerifier() {
    return new DelegatingJwtClaimsSetVerifier(
        Arrays.asList(issuerClaimVerifier(), customJwtClaimVerifier()));
  }

  @Bean
  public JwtClaimsSetVerifier customJwtClaimVerifier() {
    return new CustomClaimVerifier();
  }

  @Bean
  public JwtClaimsSetVerifier issuerClaimVerifier() {
    try {
      return new IssuerClaimVerifier(new URL("http://localhost:8081"));
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  @Bean
  @Primary
  public JwtAccessTokenConverter symmetryAccessTokenConverter() {
    JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
    converter.setAccessTokenConverter(customAccessTokenConverter);
    converter.setJwtClaimsSetVerifier(jwtClaimsSetVerifier());
    converter.setSigningKey("123");
    return converter;
  }

  @Bean
  @Primary
  public DefaultTokenServices tokenServices() {
    DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
    defaultTokenServices.setTokenStore(tokenStore());
    return defaultTokenServices;
  }
}
