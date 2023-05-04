package com.example.demo.vo.dashboard;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse {

  // 기본
  // private final String timestamp;

  // 성공시
  private final Object data;
  private final String next;

  // 실패시
  private final String msg;
  private final String errorCode;
  private final String path;
  private final String cause;
  
  @Builder
  public ApiResponse(Object data, String next, String msg, String errorCode, String path, String cause) {
    // this.timestamp = Instant.now().toString();
    this.data = data;
    this.next = next;
    this.msg = msg;
    this.errorCode = errorCode;
    this.path = path;
    this.cause = cause;
  }
  
}
