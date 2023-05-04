package com.example.demo.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.demo.service.JwtAccessService;
import com.example.demo.service.UserService;
import com.example.demo.token.JwtUtil;
import com.example.demo.vo.PasswordVO;
import com.example.demo.vo.TokenVO;
import com.example.demo.vo.UserUpdateVO;
import com.example.demo.vo.UserVO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Api(tags = "사용자관리 API")
@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

	private final JwtAccessService accessService;
//	private final JwtRefreshService refreshService;

	private final UserService userService;

	@Autowired
	private PasswordEncoder passwordEncoder;

//	@Autowired
//	private JwtRefreshService jwtRefreshService;

	@Autowired
	private JwtUtil jwtUtil;
	
	private Gson gson = new GsonBuilder().setPrettyPrinting().create();

	@ApiOperation(value = "사용자 로그인", notes = "로그인 기능(토큰발급)")
	@PostMapping("auths/login")
	public ResponseEntity<?> loginId(@RequestBody UserVO vo) throws Exception {

		if (userService.userId(vo.getUserId()) == null) {
			
			HttpStatus status = HttpStatus.UNAUTHORIZED;
			Map<String, Object> map = new HashMap<>();
			map.put("status", HttpStatus.UNAUTHORIZED);
			map.put("msg", "아이디가 존재하지 않습니다.");
			return new ResponseEntity<>(map, status);
		}
		
		System.out.println(passwordEncoder.matches(vo.getPassword(), userService.userPw(vo.getUserId())));
		
		if (!passwordEncoder.matches(vo.getPassword(), userService.userPw(vo.getUserId()))) {
			HttpStatus status = HttpStatus.UNAUTHORIZED;
			Map<String, Object> map = new HashMap<>();
			map.put("status", HttpStatus.UNAUTHORIZED);
			map.put("msg", "패스워드가 일치하지 않습니다.");
			return new ResponseEntity<>(map, status);
		}

		
		HashMap<String, String> mapToken = new HashMap<String, String>();
		mapToken.put("access", accessService.login(vo.getUserId()));
		UUID uuid = UUID.randomUUID();
		String uuidString = uuid.toString();
		mapToken.put("refresh", uuidString);

		//로그인한 사용자의 id가 user_token에 있는지 확인하고 있으면 삭제
		String UUID = userService.uuid(vo.getUserId());
		if(UUID != null) {
			userService.refreshToken_delete(UUID);
		}
		
		TokenVO tokenVO = new TokenVO();

		tokenVO.setUserId(UUID);
		tokenVO.setToken(uuidString);
		
		Date today = new Date();
		Long expiredMs = 3000 * 70 * 60L;
		Date expiryDate = new Date(today.getTime() + expiredMs);
		tokenVO.setExpiryTime(expiryDate);
		tokenVO.setCreateTime(today);
		tokenVO.setUpdateTime(today);
		//userService.refreshToken_delete(id);////////////////////
		userService.refreshToken(tokenVO);
		userService.userLogin(UUID);
		

		Map<String, Object> response = new HashMap<>();
		response.put("token", mapToken);
		response.put("status", HttpStatus.OK);
		response.put("msg", "로그인 되었습니다."); 
		log.info("================================= login :\n" + gson.toJson(response));
		return ResponseEntity.status(HttpStatus.CREATED).body(response);// 201
	}
	

	@ApiOperation(value = "회원가입", notes = "회원가입 기능")
	@PostMapping(value="auths/register", produces = "application/json;charset=UTF-8")
	public ResponseEntity<?> join(@RequestBody UserVO vo) throws Exception {

		// id 유효성 검사
		String id = vo.getUserId();
		String resultId = userService.userId(id); // 아이디 검색 결과
		if (!(resultId == null)) {// id가 있으면
			HttpStatus status = HttpStatus.UNAUTHORIZED;
			String message = "이미 존재하는 아이디 입니다.";
			log.info("================================= register id error 1 :\n" + gson.toJson(new ResponseEntity<>(message, status)));
			return new ResponseEntity<>(message, status);
		}

		if (id.length() < 4 || id.length() > 12) {
			HttpStatus status = HttpStatus.UNAUTHORIZED;
			String message = "아이디를 4자리 초과 12자리 미만으로 작성해주세요.";
			log.info("================================= register id error 2 :\n" + gson.toJson(new ResponseEntity<>(message, status)));
			return new ResponseEntity<>(message, status);
		}

		String idRegex = "^[a-z0-9]+$";
		Pattern pattern = Pattern.compile(idRegex);
		Matcher matcher = pattern.matcher(id);
		if (!matcher.matches()) {
			// id가 영문 소문자, 숫자로만 구성되어 있지 않음
			HttpStatus status = HttpStatus.UNAUTHORIZED;
			String message = "아이디는 영문 소문자와 숫자만 사용가능합니다.";
			log.info("================================= register id error 3 :\n" + gson.toJson(new ResponseEntity<>(message, status)));
			return new ResponseEntity<>(message, status);
		}

		if (vo.getPassword().length() < 8 || vo.getPassword().length() > 15) {
			HttpStatus status = HttpStatus.UNAUTHORIZED;
			String message = "비밀번호를 8자리 이상 15자리 이하로 작성해주세요.";
			log.info("================================= register password error 1:\n" + gson.toJson(new ResponseEntity<>(message, status)));
			return new ResponseEntity<>(message, status);
		}
		String passwordRegex = "^(?=.*[a-zA-Z])(?=.*\\d|(?=.*[@#$%^&+=!_~-])).{8,15}$";
		Pattern passwordpattern = Pattern.compile(passwordRegex);
		Matcher passwordmatcher = passwordpattern.matcher(vo.getPassword());
		if (!passwordmatcher.matches()) {
			// password가 영문 대소문자, 숫자, 특수문자로만 구성되어 있는지 확인
			HttpStatus status = HttpStatus.UNAUTHORIZED;
			String message = "비밀번호는 문자, 숫자, 특수문자 중 2개이상 작성해야합니다.";
			log.info("================================= register password error 2:\n" + gson.toJson(new ResponseEntity<>(message, status)));
			return new ResponseEntity<>(message, status);
		}

		if (vo.getUserName().isEmpty()) {
			HttpStatus status = HttpStatus.UNAUTHORIZED;
			String message = "이름을 입력해주세요.";
			log.info("================================= register name error :\n" + gson.toJson(new ResponseEntity<>(message, status)));
			return new ResponseEntity<>(message, status);
		}

//		String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$";
//		Pattern emailpattern = Pattern.compile(emailRegex);
//		Matcher emailmatcher = emailpattern.matcher(vo.getEmail());
//		if (!emailmatcher.matches()) {
//			// password가 영문 대소문자, 숫자, 특수문자로만 구성되어 있는지 확인
//			HttpStatus status = HttpStatus.UNAUTHORIZED;
//			String message = "이메일 형식에 맞게 다시 입력해주세요.";
//			return new ResponseEntity<>(message, status);
//		}


//		Date date = new Date();
//		vo.setCreateTime(date+"");
//		vo.setUpdateTime(date+"");
		userService.join(vo);
		String uuid = userService.uuid(vo.getUserId().toString());
		String superId = userService.superId();
		Map<String, Object> vo_map = new HashMap<>();
		vo_map.put("userId", uuid);
		vo_map.put("roleId", superId);
		userService.userRoles(vo_map);
		Map<String, Object> map = new HashMap<>();
		HttpStatus status = HttpStatus.OK;
		String message = "회원가입을 환영합니다.^^";
		map.put("status", status);
		map.put("message", message);
		log.info("================================= register success :\n" + gson.toJson(map));
		return new ResponseEntity<>(map, status);
	};
	
	@ApiOperation(value = "사용자 관리", notes = "사용자 정보를 수정하는 기능")
	@PutMapping("auths/profile")
	public ResponseEntity<?> userProfile(
			@RequestHeader HttpHeaders headers,
			@RequestBody UserUpdateVO vo) throws Exception {

		String authToken = headers.getFirst("Authorization");
		if (authToken != null && authToken.startsWith("Bearer ")) {
			String token = authToken.substring(7);

			Jws<Claims> claims = jwtUtil.getClaims(token);// 코드 복호화+서명검증
			boolean isTokenValid = jwtUtil.validateToken(claims);// 토큰 만료시간 검증
			// String id = jwtUtil.getKey(claims);//생성자

			userService.userProfileUpdate(vo);

			Map<String, Object> map = new HashMap<>();
			HttpStatus status = HttpStatus.OK;
			String message = "회원정보가 수정되었습니다.";
			map.put("status", status);
			map.put("message", message);
			log.info("================================= profile update :\n" + gson.toJson(map));
			return new ResponseEntity<>(map, status);
		}
		Map<String, Object> map = new HashMap<>();
		HttpStatus status = HttpStatus.UNAUTHORIZED;
		String message = "로그인 후에 이용가능합니다.";
		map.put("status", status);
		map.put("message", message);
		
		return new ResponseEntity<>(map, status);
	}
	
	@ApiOperation(value = "비밀번호 변경", notes = "비밀번호를 변경하는 기능")
	@PutMapping("auths/password")
	public ResponseEntity<?> userPassword(
			@RequestHeader HttpHeaders headers,
			@RequestBody PasswordVO vo) throws Exception {
		
		String authToken = headers.getFirst("Authorization");
		if (authToken != null && authToken.startsWith("Bearer ")) {
			String token = authToken.substring(7);
			
			Jws<Claims> claims = jwtUtil.getClaims(token);// 코드 복호화+서명검증
			boolean isTokenValid = jwtUtil.validateToken(claims);// 토큰 만료시간 검증
			String tokenId = (String) claims.getBody().get("id");//생성자
			
			if(!(userService.userPw(tokenId).equals(vo.getNewPassword()))) {
				vo.setUserId(tokenId);
				userService.userPasswordUpdate(vo);
			
				Map<String, Object> map = new HashMap<>();
				HttpStatus status = HttpStatus.OK;
				String message = "비밀번호가 수정되었습니다.";
				map.put("status", status);
				map.put("message", message);
				log.info("================================= password update :\n" + gson.toJson(map));
				return new ResponseEntity<>(map, status);
			}else {
				Map<String, Object> map = new HashMap<>();
				HttpStatus status = HttpStatus.UNAUTHORIZED;
				String message = "기존 비밀번호와 동일한 비밀번호는 사용할 수 없습니다.";
				map.put("status", status);
				map.put("message", message);
				log.info("================================= password error :\n" + gson.toJson(map));
				return new ResponseEntity<>(map, status);
			}
		}
		Map<String, Object> map = new HashMap<>();
		HttpStatus status = HttpStatus.UNAUTHORIZED;
		String message = "로그인 후에 이용가능합니다.";
		map.put("status", status);
		map.put("message", message);
		return new ResponseEntity<>(map, status);
	}
	
	@ApiOperation(value = "사용자 토큰 발급", notes = "새 토큰을 발급하는 기능")
	@PostMapping("auths/refresh")
	public ResponseEntity<?> getUserFromToken(@RequestBody TokenVO vo) throws Exception {
		
		String name = userService.refreshTokenChkId(vo.getToken());
		System.out.println(name);
		
		
		//1) vo.token을 받아서 user_tokens테이블에 해당 데이터가 존재하는지 확인
		if (userService.refreshTokenChkId(vo.getToken()) != null) {
			//2)  vo.token을 받아서 user_tokens테이블에서 user_id추출
			String tokenUserId = userService.tokenUserId(vo.getToken());
			String id = userService.usersTableid(tokenUserId);
			System.out.println(id);
			

			HashMap<String, String> map = new HashMap<String, String>();
			map.put("access", accessService.login(id));
			UUID uuidRan = UUID.randomUUID();
			String uuidString = uuidRan.toString();
			map.put("refresh", uuidString);

			//로그인한 사용자의 id가 user_token에 있는지 확인하고 있으면 삭제
//			String UUID = userService.uuid(id);
			if(id != null) {
				userService.refreshToken_delete(vo.getToken());
			}
			
			TokenVO tokenVO = new TokenVO();

			tokenVO.setToken(uuidString);
			tokenVO.setUserId(name);
			
			Date today = new Date();
			Long expiredMs = 3000 * 70 * 60L;
			Date expiryDate = new Date(today.getTime() + expiredMs);
			tokenVO.setExpiryTime(expiryDate);
			tokenVO.setCreateTime(today);
			tokenVO.setUpdateTime(today);
			//userService.refreshToken_delete(id);////////////////////
			userService.refreshToken(tokenVO);
			
			Map<String, Object> responseMap = new HashMap<>();
			HttpStatus status = HttpStatus.OK;
			String message = "토큰을 재발급 합니다.";
			responseMap.put("status", status);
			responseMap.put("message", message);
			responseMap.put("data", map);
			log.info("================================= refresh token success :\n" + gson.toJson(responseMap));
			return new ResponseEntity<>(responseMap, status);
			
	    	}else {
	    		Map<String, Object> map = new HashMap<>();
    			HttpStatus status = HttpStatus.BAD_REQUEST;
    			String message = "refresh 토큰을 찾을 수 없습니다.";
    			map.put("status", status);
    			map.put("message", message);
    			return new ResponseEntity<>(map, status);
	    	}
	    }

}
