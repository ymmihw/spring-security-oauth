package com.ymmihw.config;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

@Component
public class CustomPreZuulFilter extends ZuulFilter {
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Override
  public Object run() {
    RequestContext ctx = RequestContext.getCurrentContext();
    HttpServletRequest request = ctx.getRequest();
    if (request.getRequestURI().contains("/oauth/token")) {
      byte[] encoded;
      try {
        encoded = Base64.getEncoder().encode("fooClientIdPassword:secret".getBytes("UTF-8"));
        ctx.addZuulRequestHeader("Authorization", "Basic " + new String(encoded));
      } catch (UnsupportedEncodingException e) {
        logger.error("Error occured in pre filter", e);
      }
    }

    if (request.getRequestURI().contains("/foos/")) {
      String accessToken = extractToken(request, "access_token");
      if (accessToken != null) {
        ctx.addZuulRequestHeader("Authorization", "Bearer " + accessToken);
      }
    }
    final String refreshToken = extractToken(request, "refresh_token");
    if (refreshToken != null) {
      final Map<String, String[]> param = new HashMap<String, String[]>();
      param.put("refresh_token", new String[] {refreshToken});
      param.put("grant_type", new String[] {"refresh_token"});

      ctx.setRequest(new CustomHttpServletRequest(request, param));
    }
    return null;
  }

  private String extractToken(HttpServletRequest req, String tokenName) {
    final Cookie[] cookies = req.getCookies();
    if (cookies != null) {
      for (int i = 0; i < cookies.length; i++) {
        if (cookies[i].getName().equalsIgnoreCase(tokenName)) {
          return cookies[i].getValue();
        }
      }
    }
    return null;
  }

  @Override
  public boolean shouldFilter() {
    return true;
  }

  @Override
  public int filterOrder() {
    return -2;
  }

  @Override
  public String filterType() {
    return "pre";
  }
}
