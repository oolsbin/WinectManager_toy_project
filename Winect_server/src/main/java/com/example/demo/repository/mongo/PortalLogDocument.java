package com.example.demo.repository.mongo;

import java.util.Date;

import javax.persistence.Id;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Document(collection = "portal_log") // 조회할 컬렉션 이름
@Getter
@Setter
public class PortalLogDocument {
  @Id
  private String id;
  private Date inTime;          // 들어온 시간
  private Date outTime;         // 나간 시간
  private String path;          // 접속 url
  private String type;          // 메뉴 타입 (service, route, filter ...)
  private String clientIp;      // client ip
  private String userId;
  private String userid;
  private String action;        // method (get, post, put, delete)
  // private String reqHeader;     // 요청 Header
  private String reqBody;       // 요청 Body
  // private String resHeader;     // 응답 Header
  private String resBody;       // 응답 Body
  private int resCode = -1;
  private String errorMsg;

  private String documentType = "portal";

  public PortalLogDocument(){}

  @Builder
  public PortalLogDocument(String id, Date inTime, Date outTime, String path, String type, String clientIp, String userId, String action, String reqBody, String resBody, int resCode, String errorMsg){
    this.id = id;
    this.inTime = inTime;
    this.outTime = outTime;
    this.path = path;
    this.type = type;
    this.clientIp = clientIp;
    this.userId = userId;
    this.action = action;
    this.reqBody = reqBody;
    this.resBody = resBody;
    this.resCode = resCode;
    this.errorMsg = errorMsg;
    this.documentType = "portal";
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getUserid() {
    return userid;
  }

  public void setUserid(String userid) {
    this.userid = userid;
  }



}
