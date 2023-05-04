package com.example.demo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserVO {
	@ApiModelProperty(value = "사용자 아이디", required = true)
	private String userId;
	@ApiModelProperty(value = "사용자 패스워드", required = true)
	private String password;
	@ApiModelProperty(value = "사용자 이름", required = true)	
	private String userName;
	@ApiModelProperty(value = "로그인시간", required = true)
	private String loginTime;
	@ApiModelProperty(value = "패스워드시간", required = true)
	private String passwordTime;
	@ApiModelProperty(value = "생성일자", required = true)
	private String createTime;
	@ApiModelProperty(value = "수정일자", required = true)	
	private String updateTime;
	
//	private String email;
//	private String phoneNumber;
	
}
