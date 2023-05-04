package com.example.demo.vo;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DomainVO {
	private String domainId;//도메인 아이디
	private String domainName;//도메인 이름
	private String userId;//domain_admin
	private Date createTime;//생성일자
	private Date updateTime;//수정일자
	private String note;//도메인설명-payLoad
	private boolean enabled;//활성화여부-payLoad
	private String domainIcon;//도메인아이콘-payLoad
	private String domainColor;//도메인색깔-payLoad
	private boolean useGateway;//게이트웨이사용여부-payLoad
	private boolean useMediation;//메이에이션(중개?)사용여부-payLoad
}