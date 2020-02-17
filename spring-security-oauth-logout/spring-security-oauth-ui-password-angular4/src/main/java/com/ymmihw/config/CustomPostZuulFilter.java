package com.ymmihw.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

@Component
public class CustomPostZuulFilter extends ZuulFilter {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        try {
            InputStream is = ctx.getResponseDataStream();
            String responseBody = IOUtils.toString(is, "UTF-8");
            HttpServletRequest request = ctx.getRequest();
            HttpServletResponse response = ctx.getResponse();
            if (responseBody.contains("refresh_token")) {
                Map<String, Object> responseMap = mapper.readValue(responseBody, new TypeReference<Map<String, Object>>() {
                });
                String refreshToken = responseMap.get("refresh_token")
                                                 .toString();
                responseMap.remove("refresh_token");
                responseBody = mapper.writeValueAsString(responseMap);

                Cookie cookie = new Cookie("refreshToken", refreshToken);
                cookie.setHttpOnly(true);
                cookie.setSecure(true);
                cookie.setPath(request.getContextPath() + "/oauth/token");
                cookie.setMaxAge(2592000); // 30 days
                response.addCookie(cookie);
            }
            String requestMethod = request.getMethod();
            String requestURI = request.getRequestURI();
            if (requestURI.contains("/logout") && requestMethod.equals("DELETE")) {
                Cookie cookie = new Cookie("refreshToken", "");
                cookie.setMaxAge(0);
                cookie.setPath(request.getContextPath() + "/oauth/token");
                response.addCookie(cookie);
            }
            ctx.setResponseBody(responseBody);
        } catch (IOException e) {
            logger.error("Error occured in zuul post filter", e);
        }
        return null;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public int filterOrder() {
        return 10;
    }

    @Override
    public String filterType() {
        return "post";
    }
}