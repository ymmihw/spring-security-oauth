package com.ymmihw.spring.security.oauth2.web.controller;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ymmihw.spring.security.oauth2.web.dto.Foo;

@RestController
@RequestMapping("/foos")
public class FooController {

  @PreAuthorize("#oauth2.hasScope('read')")
  @GetMapping("/{id}")
  public Foo retrieveFoo(@PathVariable("id") Long id) {
    return new Foo(id, RandomStringUtils.randomAlphabetic(6));
  }

}
