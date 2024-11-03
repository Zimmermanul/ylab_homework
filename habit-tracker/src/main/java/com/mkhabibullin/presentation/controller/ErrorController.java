package com.mkhabibullin.presentation.controller;


import com.mkhabibullin.presentation.dto.ErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/error")
public class ErrorController {
  
  @RequestMapping("/404")
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorDTO handle404() {
    return new ErrorDTO("Resource not found", System.currentTimeMillis());
  }
  
  @RequestMapping("/500")
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorDTO handle500() {
    return new ErrorDTO("Internal server error", System.currentTimeMillis());
  }
  
  @RequestMapping
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorDTO handleError() {
    return new ErrorDTO("An error occurred", System.currentTimeMillis());
  }
}