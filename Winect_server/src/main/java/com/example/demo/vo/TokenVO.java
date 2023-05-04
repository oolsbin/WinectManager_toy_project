package com.example.demo.vo;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenVO {
	private String userId;
	private String token;
	private Date expiryTime;	
	private Date createTime;
	private Date updateTime;
}
