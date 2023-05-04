package com.example.demo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateVO {
	@ApiModelProperty(value = "사용자 아이디", required = true)
	private String userId;
	@ApiModelProperty(value = "사용자 이름", required = true)	
	private String userName;
	@ApiModelProperty(value = "사용자 이메일", required = true)	
	private String userEmail;
	@ApiModelProperty(value = "사용자 전화번호", required = true)	
	private String userPhoneNumber;
	@ApiModelProperty(value = "수정일자", required = true)	
	private String updateTime;
}
