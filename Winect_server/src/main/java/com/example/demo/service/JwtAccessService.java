package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.demo.token.JwtUtil;

@Service
public class JwtAccessService {
	@Value("${JWT.ACCESS}")
	private String secreKey;

	private Long expiredMs = 1000 * 60 * 60L;

	public String login(String id) {
		return JwtUtil.createJwt(id, secreKey, expiredMs);
	}
}
