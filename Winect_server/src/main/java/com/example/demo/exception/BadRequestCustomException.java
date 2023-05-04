package com.example.demo.exception;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestCustomException extends RuntimeException {

  private static final long serialVersionUID = 3932386495596949958L;
  
  private final String msg;
  private final String errorCode;
  
  public BadRequestCustomException(String msg) {
    super(msg);
    this.msg = msg;
    this.errorCode = null;
  }

  public BadRequestCustomException(EErrorCode ec) {
    super(ec.getMsg());
    this.msg = ec.getMsg();
    this.errorCode = ec.getErrorCode();
  }

  public BadRequestCustomException(String msg, String errorCode) {
    super(msg);
    this.msg = msg;
    this.errorCode = errorCode;
  }
}