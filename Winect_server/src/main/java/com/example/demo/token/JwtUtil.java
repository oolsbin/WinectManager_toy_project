package com.example.demo.token;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.demo.exception.TokenExpiredException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;

@Component
public class JwtUtil {
	@Value("${JWT.REFRESH}")
	private String refreshKey;
	
	@Value("${JWT.ACCESS}")
	private String accessKey;
	
	@SuppressWarnings("deprecation")
	public static String createJwt(String id, String accessKey, Long expiredMs) {
		Claims claims = Jwts.claims();//claims: 클라이언트에 대한 정보
		claims.put("id", id);
		
		//token 생성
		return Jwts.builder()
				.setClaims(claims)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + expiredMs))
				.signWith(SignatureAlgorithm.HS256, accessKey)
				.compact();		
	}

	// String으로 된 accessToken의 코드를 복호화하여 signature 검증
	public Jws<Claims> getClaims(String accessToken) {
		try {
			return Jwts.parser().setSigningKey(accessKey).parseClaimsJws(accessToken);
		} catch (ExpiredJwtException e) {
			// Handle expired JWT
			HttpStatus status = HttpStatus.UNAUTHORIZED;
			String message = "사용자 토큰 기한이 만료되었습니다.";
			throw new TokenExpiredException(status, message);
		}
	}
	  
	// accessToken 토큰 남은 기한 검증
	public boolean validateToken(Jws<Claims> claims) {
	    return !claims.getBody()
	                  .getExpiration()
	                  .before(new Date());
	  }
	  
	// 토큰을 통해 Payload의 ID를 취득
	public String getKey(Jws<Claims> claims) {
	    return claims.getBody()
	                 .getId();
	  }
	
}
