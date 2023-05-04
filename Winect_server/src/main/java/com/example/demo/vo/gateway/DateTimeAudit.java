package com.example.demo.vo.gateway;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class DateTimeAudit implements Serializable {
  private static final long serialVersionUID = 4225542024952736573L;//버전의 호환성을 위함

  protected LocalDateTime createTime;

  protected LocalDateTime updateTime;
}
