package com.example.demo.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordVO {
	private String userId;
	private String oldPassword;
	private String newPassword;
}
