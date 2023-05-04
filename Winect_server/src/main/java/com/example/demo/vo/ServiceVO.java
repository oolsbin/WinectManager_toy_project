package com.example.demo.vo;

import java.util.Date;

import org.postgresql.util.PGobject;

import com.google.gson.Gson;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceVO {
	private String serviceId;//서비스아이디
	private String serviceName;//서비스이름
	private String serviceDesc;//서비스설명
	private int connectTimeout;//연결제한시간
	private int responseTimeout;//응답제한시간
	private int retries;//재시도요청건수
	private String domainId;//도메인아이디
	private boolean useCircuitbreaker;//서킷브레이커사용여부
	private boolean enabled;//활성화
	private boolean unused;//라우트여부
	private Date createTime;//생성일자
	private Date updateTime;//수정일자
	private String targetUrl;//API 타겟주소
	
	private String protocol;//http프로토콜
	private String host;//호스트번호
	private int port;//포트번호
	private String path;//경로
	
	private CircuitbreakerConfigVO circuitbreakerConfig;
	
	public void setCircuitbreakerConfig(Object circuitbreakerConfigVO) {
		if (circuitbreakerConfigVO instanceof PGobject) {
			this.circuitbreakerConfig = new Gson().fromJson(((PGobject) circuitbreakerConfigVO).getValue(), CircuitbreakerConfigVO.class);
		} else {
			this.circuitbreakerConfig = new Gson().fromJson(new Gson().toJson(circuitbreakerConfigVO), CircuitbreakerConfigVO.class);
		}
	}
}
