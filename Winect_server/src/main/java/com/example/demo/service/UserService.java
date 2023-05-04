package com.example.demo.service;

import java.util.Map;
import java.util.UUID;

import com.example.demo.vo.PasswordVO;
import com.example.demo.vo.ServiceVO;
import com.example.demo.vo.TokenVO;
import com.example.demo.vo.UserUpdateVO;
import com.example.demo.vo.UserVO;

public interface UserService {
	UserVO login(String id) throws Exception;//로그인
	int userProfileUpdate(UserUpdateVO vo) throws Exception;//회원정보변경
	int userPasswordUpdate(PasswordVO vo) throws Exception;//비밀번호변경
	int userLogin(String uuid) throws Exception;//로그인시간
	int refreshToken(TokenVO vo) throws Exception;//refreshToken발급
	int join(UserVO vo) throws Exception;//회원가입
	int refreshToken_delete(String id) throws Exception;//중복 아이디 토큰 삭제
	int userRoles(Map<String, Object> vo_map) throws Exception;//사용자 super역할 부여
	String usersTableid(String id) throws Exception;//user id
	String userId(String id) throws Exception;//user id
	String userPw(String id) throws Exception;//user pw
	String refreshToken_chk(String refreshToken) throws Exception;//user token검사
	String refreshTokenChkId(String token) throws Exception;//user token검사
	String tokenUserId(String uuid) throws Exception;//token id추출
	String superId() throws Exception;//수퍼아이디 조회
	String uuid(String id) throws Exception;//uuid조회
}
