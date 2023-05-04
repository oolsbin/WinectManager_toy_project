package com.example.demo.vo;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceCircuitVO {
	    private String serviceId;
	    private String serviceName;
	    private String serviceDesc;
	    private int connectTimeout;
	    private int responseTimeout;
	    private int retries;
	    private boolean enabled;
	    private boolean useCircuitbreaker;
	    private String targetUrl;
	    
	    private String protocol;//http프로토콜
	    private String domainId;//도메인아이디
	    private boolean unused;//라우트여부
		private Date createTime;//생성일자
		private Date updateTime;//수정일자
		 
		
	    private String slidingWindowType;//count,time
	    private int slowCallDurationThreshold;
	    private int slowRateThreshold;
	    private int failureRateThreshold;
	    private int waitDurationInOpenState;
	    private int permittedNumberOfCallsInHalfOpenState;
	    private int slidingWindowSize;
	    private int minimumNumberOfCalls;
	    private String fallbackMessage;//응답메세지

}
