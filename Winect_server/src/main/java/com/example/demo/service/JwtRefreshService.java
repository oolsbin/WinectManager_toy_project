//package com.example.demo.service;
//
//import java.util.Date;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import com.example.demo.token.JwtUtil;
//
//@Service
//public class JwtRefreshService {
//	@Value("${JWT.REFRESH}")
//	private String secreKey;
//	private Long expiredMs = 3000 * 70 * 60L;
//
//	public String login(String id, String pw) {
//		return JwtUtil.createJwt(id, secreKey, expiredMs);
//	}
//
//	public Date getExpirationDate() {
//  		long expirationMillis = System.currentTimeMillis() + expiredMs;
//    	return new Date(expirationMillis);
//	}
//
//}
