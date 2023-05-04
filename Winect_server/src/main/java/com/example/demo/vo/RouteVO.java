package com.example.demo.vo;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RouteVO {
	private String routeId;//라우트아이디
	private String routeName;//라우트이름
	private String routeDesc;//라우스설명
	private boolean enabled;//
	private boolean isPortalShow;//
	private Date startTime;//API사용시작일자
	private Date deadlineTime;//API사용종료일자
	private List<String> routePath;//라우트경로
	private List<String> protocols;//프로토콜
	private List<String> methods;//메소드
	private String serviceId;//서비스아이디
	private String domainId;//도메인아이디
	private Date updateDate;//수정일자
	private Date createDate;//생성일자
}
