package com.example.demo.vo.dashboard;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@ApiModel(value = "StaSrchOpt : 모델이름")
public class StaSrchOpt {
	@ApiModelProperty(value = "전체 트렌젝션 시작일", example = "1", required = true)
    private String startDate;//트렌젝션 전체기준
	@ApiModelProperty(value = "전체 트렌젝션 종료일", example = "1", required = true)
    private String endDate;
	@ApiModelProperty(value = "트렌젝션 통신(생성)날짜", example = "1", required = true)
    private String createTime;
	@ApiModelProperty(value = "데이터 정렬=내림차순", example = "1", required = true)
    private String orderBy = "desc";
	@ApiModelProperty(value = "데이터 정렬", example = "1", required = true)
    private String sortBy;
	@ApiModelProperty(value = "페이지 번호", example = "1", required = true)
    private int currentPage = 1;//페이지 번호
	@ApiModelProperty(value = "페이지에 보여지는 항목 수", example = "1", required = true)
    private int pageSicze = 10;//페이지에 보여지는 항목수
	@ApiModelProperty(value = "상위에 보여질 항목 수", example = "1", required = true)
    private int topRange = 5;//

	@ApiModelProperty(value = "트랜젝션 아이디", example = "1", required = true)
    private String transactionId;//트레이스 아이디
	@ApiModelProperty(value = "도메인 아이디", example = "1", required = true)
    private String domainId;
	@ApiModelProperty(value = "클라이언트 IP번호", example = "1", required = true)
    private String clientIp;
	@ApiModelProperty(value = "서비스 아이디", example = "1", required = true)
    private String serviceId;
	@ApiModelProperty(value = "서비스 이름", example = "1", required = true)
    private String serviceName;
	@ApiModelProperty(value = "라우트 아이디", example = "1", required = true)
    private String routeId;
	@ApiModelProperty(value = "라우트 이름", example = "1", required = true)
    private String routeName;
	@ApiModelProperty(value = "컨슈머 아이디", example = "1", required = true)
    private String consumerId;
	@ApiModelProperty(value = "컨슈머 이름", example = "1", required = true)
    private String consumerName;
	@ApiModelProperty(value = "타켓코드", example = "1", required = true)
    private int gwTargetResCode = 0;
	@ApiModelProperty(value = "gateway에서 요청받는 url", example = "1", required = true)
    private String gwReqPath;//gw받을때
	@ApiModelProperty(value = "gateway로부터 호출하는 url", example = "1", required = true)
    private String gwTargetPath;//호출할때
    
	@ApiModelProperty(value = "메디에이션-프록시 아이디", example = "1", required = true)
    private String proxyId;//메디에이션-->라우트
	@ApiModelProperty(value = "메디에이션-프록시 이름", example = "1", required = true)
    private String proxyName;
	@ApiModelProperty(value = "메디에이션-브릿지 아이디", example = "1", required = true)
    private String bridgeId;//서비스
	@ApiModelProperty(value = "메디에이션-브릿지 이름", example = "1", required = true)
    private String bridgeName;
	@ApiModelProperty(value = "메디에이션-프로세스 아이디", example = "1", required = true)
    private String processId;
	@ApiModelProperty(value = "메디에이션-프로세스 이름", example = "1", required = true)
    private String processName;
	@ApiModelProperty(value = "메디에이션-리모트 아이디", example = "1", required = true)
    private String remoteId;//타켓>리모트
	@ApiModelProperty(value = "메디에이션-리모트 이름", example = "1", required = true)
    private String remoteName;
}
