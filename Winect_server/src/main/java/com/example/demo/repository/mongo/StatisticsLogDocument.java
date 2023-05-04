package com.example.demo.repository.mongo;

import java.util.Date;

import javax.persistence.Id;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Document(collection = "statistics_log") // 조회할 컬렉션 이름
@Getter
@Setter
public class StatisticsLogDocument {
  //Gateway
  @Id
  private String id;
  private String transactionId;
  private String domainId;
  private String clientIp;
  private String serviceId;
  private String serviceName;
  private String routeId;
  private String routeName;
  private String consumerId;
  private String consumerName;

  //rhwon 조회 제외된 필드
  // private String[] gwReqFilterList;
  // private String[] gwResFilterList;

  private String gwReqMethod;
  private String gwTargetMethod;
  private String gwReqPath;
  private String gwTargetPath;
  private int gwTargetResCode = -1;

  //rhwon 추가된 필드
  private Long gwReqHeaderLength = 0L;
  private Long gwReqBodyLength = 0L;
  private Long gwReqTotalLength = 0L;
  private Long gwResHeaderLength = 0L;
  private Long gwResBodyLength = 0L;
  private Long gwResTotalLength = 0L;

  //rhwon 삭제된 필드
  // private Long gwReqLength = 0L;
  // private Long gwResLength = 0L;

  private Date gwReqInTime;
  private String gwReqInFlag = "N";
  // private Date gwReqOutTime;
  // private String gwReqOutFlag = "N";
  // private Date gwResInTime;
  // private String gwResInFlag = "N";
  private Date gwResOutTime;
  private String gwResOutFlag = "N";
  private Long totalLatency = 0L;
  private Long gwTargetLatency = 0L;
  private String gwProcessFlag = "N";
  private String gwErrorMsg = "";

  //Mediation
  private String proxyId = "";
  private String proxyName = "";
  private String bridgeId = "";
  private String bridgeName = "";

  //rhwon 조회 제외된 필드
  // private String processId = "";
  // private String processName = "";
  // private String remoteId = "";
  // private String remoteName = "";
  private String mdReqMethod = "";
  private String mdTargetMethod = "";
  private String mdReqPath = "";
  private String mdTargetPath = "";

  //rhwon 추가된 필드
  private String mdTargetProtocol = "";

  private int mdTargetResCode = -1;

  //rhwon 삭제된 필드
  // private Long mdReqLength = 0L;
  // private Long mdReqTransformLength = 0L;
  // private Long mdResLength = 0L;
  // private Long mdResTransformLength = 0L;

  //rhwon 추가된 필드
  // private Long mdReqHeaderLength = 0L;
  // private Long mdReqBodyLength = 0L;
  // private Long mdReqTotalLenghth = 0L;
  private Long mdReqTransHeaderLength = 0L;
  private Long mdReqTransBodyLength = 0L;
  private Long mdReqTransTotalLength = 0L;
  private Long mdResHeaderLength = 0L;
  private Long mdResBodyLength = 0L;
  private Long mdResTotalLength = 0L;
  // private Long mdResTransHeaderLength = 0L;
  // private Long mdResTransBodyLength = 0L;
  // private Long mdResTransTotalLength = 0L;


  // private Date mdReqInTime;
  // private String mdReqInFlag = "N";
  private Date mdReqOutTime;
  private String mdReqOutFlag = "N";
  private Date mdResInTime;
  private String mdResInFlag = "N";
  // private Date mdResOutTime;
  // private String mdResOutFlag = "N";
  private Long mdTargetLatency = 0L;
  private String mdProcessFlag = "N";
  private String mdErrorMsg = "";

  private String gwReqContentType;
  private String gwResContentType;
  private String mdReqContentType;
  private String mdResContentType;

  public StatisticsLogDocument(){}

  @Builder
  public StatisticsLogDocument(
      String id
    , String transactionId
    , String domainId
    , String clientIp
    , String serviceId
    , String serviceName
    , String routeId
    , String routeName
    , String consumerId
    , String consumerName
    , String gwReqMethod
    , String gwTargetMethod
    , String gwReqPath
    , String gwTargetPath
    , int gwTargetResCode
    , Long gwReqHeaderLength
    , Long gwReqBodyLength
    , Long gwReqTotalLength
    , Long gwResHeaderLength
    , Long gwResBodyLength
    , Long gwResTotalLength
    , Date gwReqInTime
    , String gwReqInFlag
    , Date gwResOutTime
    , String gwResOutFlag
    , Long totalLatency
    , Long gwTargetLatency
    , String gwProcessFlag
    , String gwErrorMsg
    , String proxyId
    , String proxyName
    , String bridgeId
    , String bridgeName
    , String mdReqMethod
    , String mdTargetMethod
    , String mdReqPath
    , String mdTargetPath
    , String mdTargetProtocol
    , int mdTargetResCode
    , Long mdReqTransHeaderLength
    , Long mdReqTransBodyLength
    , Long mdReqTransTotalLength
    , Long mdResHeaderLength
    , Long mdResBodyLength
    , Long mdResTotalLength
    , Date mdReqOutTime
    , String mdReqOutFlag
    , Date mdResInTime
    , String mdResInFlag
    , Long mdTargetLatency
    , String mdProcessFlag
    , String mdErrorMsg
    , String gwReqContentType
    , String gwResContentType
    , String mdReqContentType
    , String mdResContentType
  ) {
    this.id = id;
    this.transactionId = transactionId;
    this.domainId = domainId;
    this.clientIp = clientIp;
    this.serviceId = serviceId;
    this.serviceName = serviceName;
    this.routeId = routeId;
    this.routeName = routeName;
    this.consumerId = consumerId;
    this.consumerName = consumerName;
    this.gwReqMethod = gwReqMethod;
    this.gwTargetMethod = gwTargetMethod;
    this.gwReqPath = gwReqPath;
    this.gwTargetPath = gwTargetPath;
    this.gwTargetResCode = gwTargetResCode;
    this.gwReqHeaderLength = gwReqHeaderLength;
    this.gwReqBodyLength = gwReqBodyLength;
    this.gwReqTotalLength = gwReqTotalLength;
    this.gwResHeaderLength = gwResHeaderLength;
    this.gwResBodyLength = gwResBodyLength;
    this.gwResTotalLength = gwResTotalLength;
    this.gwReqInTime = gwReqInTime;
    this.gwReqInFlag = gwReqInFlag;
    this.gwResOutTime = gwResOutTime;
    this.gwResOutFlag = gwResOutFlag;
    this.totalLatency = totalLatency;
    this.gwTargetLatency = gwTargetLatency;
    this.gwProcessFlag = gwProcessFlag;
    this.gwErrorMsg = gwErrorMsg;
    this.proxyId = proxyId;
    this.proxyName = proxyName;
    this.bridgeId = bridgeId;
    this.bridgeName = bridgeName;
    this.mdReqMethod = mdReqMethod;
    this.mdTargetMethod = mdTargetMethod;
    this.mdReqPath = mdReqPath;
    this.mdTargetPath = mdTargetPath;
    this.mdTargetProtocol = mdTargetProtocol;
    this.mdTargetResCode = mdTargetResCode;
    this.mdReqTransHeaderLength = mdReqTransHeaderLength;
    this.mdReqTransBodyLength = mdReqTransBodyLength;
    this.mdReqTransTotalLength = mdReqTransTotalLength;
    this.mdResHeaderLength = mdResHeaderLength;
    this.mdResBodyLength = mdResBodyLength;
    this.mdResTotalLength = mdResTotalLength;
    this.mdReqOutTime = mdReqOutTime;
    this.mdReqOutFlag = mdReqOutFlag;
    this.mdResInTime = mdResInTime;
    this.mdResInFlag = mdResInFlag;
    this.mdTargetLatency = mdTargetLatency;
    this.mdProcessFlag = mdProcessFlag;
    this.mdErrorMsg = mdErrorMsg;
    this.gwReqContentType = gwReqContentType;
    this.gwResContentType = gwResContentType;
    this.mdReqContentType = mdReqContentType;
    this.mdResContentType = mdResContentType;
  }

}
