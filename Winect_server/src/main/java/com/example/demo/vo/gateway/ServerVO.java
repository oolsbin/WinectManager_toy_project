package com.example.demo.vo.gateway;

import javax.persistence.Entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)//파라미터가 없는 protected(접근제한자)로 생성
public class ServerVO extends DateTimeAudit{
  private String serverId;//서버아이디
  private String product;//서버가 사용하는 제품이름
  private String serverIP;//서버IP주소
  private String status;//서버의 상태
  private String adminPort;//서버의 관리포트

  @Builder
  public ServerVO(String serverId, String product, String serverIP, String status, String adminPort){
    this.serverId = serverId;
    this.product = product;
    this.serverIP = serverIP;
    this.status = status;
    this.adminPort = adminPort;
  }
}
