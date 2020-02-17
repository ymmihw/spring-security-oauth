package com.ymmihw.spring.security.auth2.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import com.ymmihw.spring.security.auth2.web.dto.Employee;

@Controller
public class EmployeeController {
  private List<Employee> employees = new ArrayList<Employee>();

  @GetMapping("/employee")
  @ResponseBody
  public Optional<Employee> getEmployee(@RequestParam String email) {
    return employees.stream().filter(x -> x.getEmail().equals(email)).findAny();
  }

  @PostMapping(value = "/employee", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public void postMessage(@RequestBody Employee employee) {
    employees.add(employee);
  }

}
