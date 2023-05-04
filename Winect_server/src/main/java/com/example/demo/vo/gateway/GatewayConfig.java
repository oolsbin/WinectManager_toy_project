package com.example.demo.vo.gateway;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component//Autowired로 다른 클래스에서 사용 가능하다.
//@EnableConfigurationProperties//이거는 @ConfigurationProperties적용된 class를 bean객체로 처리하는 것
@ConfigurationProperties(prefix = "gateway")
@Getter
@Setter
//Gateway의 정보를 담는 컴포넌트로
//gateway가 프리픽스로 사용되고 그에 대한 프로퍼티(속성값)들을 저장한다.
public class GatewayConfig {
  private String symbol;
  private String protocol;
  private String url;
  private String adminPath;
  private String apiDefHost;
  private PathVO path;//API의 경로정보를 담고 있는 객체

//  @Getter
//  @Setter
//  public static class Path {
//    private String metrics;
//    private String service;
//    private String route;
//    private String consumer;
//    private String filter;
//    private String domain;
//    private String loadBalancing;
//    private String certification;
//    private String component;
//  }
}
