package com.ymmihw.spring.security.oauth2.config;

import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.util.Collections;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;

@Configuration
@EnableAuthorizationServer
public class JwkAuthorizationServerConfiguration {

  private static final String KEY_STORE_FILE = "bael-jwt.jks";
  private static final String KEY_STORE_PASSWORD = "bael-pass";
  private static final String KEY_ALIAS = "bael-oauth-jwt";
  private static final String JWK_KID = "bael-key-id";

  @Bean
  public TokenStore tokenStore(JwtAccessTokenConverter jwtAccessTokenConverter) {
    return new JwtTokenStore(jwtAccessTokenConverter);
  }

  @Bean
  public JwtAccessTokenConverter accessTokenConverter() {
    Map<String, String> customHeaders = Collections.singletonMap("kid", JWK_KID);
    return new JwtCustomHeadersAccessTokenConverter(customHeaders, keyPair());
  }

  @Bean
  public KeyPair keyPair() {
    ClassPathResource ksFile = new ClassPathResource(KEY_STORE_FILE);
    KeyStoreKeyFactory ksFactory = new KeyStoreKeyFactory(ksFile, KEY_STORE_PASSWORD.toCharArray());
    return ksFactory.getKeyPair(KEY_ALIAS);
  }

  @Bean
  public JWKSet jwkSet() {
    RSAKey.Builder builder = new RSAKey.Builder((RSAPublicKey) keyPair().getPublic())
        .keyUse(KeyUse.SIGNATURE).algorithm(JWSAlgorithm.RS256).keyID(JWK_KID);
    return new JWKSet(builder.build());
  }
}
