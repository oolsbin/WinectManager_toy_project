package com.example.demo.vo.gateway;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PathVO {
  private String metrics;
  private String service;
  private String route;
  private String consumer;
  private String filter;
  private String domain;
  private String loadBalancing;
  private String certification;
  private String component;
}
