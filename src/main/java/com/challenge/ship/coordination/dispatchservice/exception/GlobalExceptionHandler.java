package com.challenge.ship.coordination.dispatchservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
  public ExceptionResponse handleIllegalArgument(IllegalArgumentException ex) {
    return new ExceptionResponse(ex.getMessage());
  }

  @ExceptionHandler(ShipNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ExceptionResponse handleShipNotFoundException(ShipNotFoundException ex) {
    return new ExceptionResponse(ex.getMessage());
  }
}
