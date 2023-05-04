package com.example.demo.service;

import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.mapper.UserMapper;
import com.example.demo.vo.PasswordVO;
import com.example.demo.vo.TokenVO;
import com.example.demo.vo.UserUpdateVO;
import com.example.demo.vo.UserVO;

@Service
public class UserServiceImpl implements UserService{

	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Override
	public UserVO login(String id) throws Exception{
		return userMapper.login(id);
	}
	
	@Override
	public int refreshToken(TokenVO vo) throws Exception {
		return userMapper.refreshToken(vo);
	}

	@Override
	public int join(UserVO vo) throws Exception {
		String encodedPassword = passwordEncoder.encode(vo.getPassword());
		vo.setPassword(encodedPassword);
		return userMapper.join(vo);
	}
	
	@Override
	public String userId(String id) throws Exception {
		return userMapper.userId(id);
	}

	@Override
	public String userPw(String id) throws Exception {
		return userMapper.userPw(id);
	}
	
	@Override
	public String refreshToken_chk(String id) throws Exception {
		return userMapper.refreshToken_chk(id);
	}

	@Override
	public int refreshToken_delete(String id) throws Exception {
		return userMapper.refreshToken_delete(id);
	}

	@Override
	public int userProfileUpdate(UserUpdateVO vo) throws Exception {
		return userMapper.userProfileUpdate(vo);
	}

	@Override
	public int userPasswordUpdate(PasswordVO vo) throws Exception {
		String encodedPassword = passwordEncoder.encode(vo.getNewPassword());
		vo.setNewPassword(encodedPassword);
		return userMapper.userPasswordUpdate(vo);
	}

	@Override
	public String superId() throws Exception {
		return userMapper.superId();
	}

	@Override
	public String uuid(String id) throws Exception {
		return userMapper.uuid(id);
	}

	@Override
	public int userRoles(Map<String, Object> vo_map) throws Exception {
		return userMapper.userRoles(vo_map);
	}

	@Override
	public int userLogin(String uuid) throws Exception {
		return userMapper.userLogin(uuid);
	}

	@Override
	public String tokenUserId(String uuid) throws Exception {
		return userMapper.tokenUserId(uuid);
	}

	@Override
	public String refreshTokenChkId(String token) throws Exception {
		return userMapper.refreshTokenChkId(token);
	}

	@Override
	public String usersTableid(String id) throws Exception {
		return userMapper.usersTableid(id);
	}

}
