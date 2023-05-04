package com.example.demo.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.gateway.ServersMap;
import com.example.demo.service.DomainService;
import com.example.demo.service.UserService;
import com.example.demo.token.JwtUtil;
import com.example.demo.util.RestTemplateUtil;
import com.example.demo.vo.DomainVO;
import com.example.demo.vo.GatewayDomainVO;
import com.example.demo.vo.gateway.GatewayConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Api(tags = "도메인 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/gateway/domains")
@Slf4j
public class DomainController {

	private Gson gson = new GsonBuilder().setPrettyPrinting().create();
	
	@Autowired//GatewayConfig에서 값을 받아 아래와 같이 전역변수로 정의내려 사용하는 것이다.
	public DomainController(GatewayConfig config) {
		this.symbol = config.getSymbol();
		this.protocol = config.getProtocol();
		this.url = config.getUrl();
		this.adminPath = config.getAdminPath();
		this.path = config.getPath().getDomain();
	}
	
	//final로 값이 초기화 되어 있지 않는 상태로 사용하게끔 돕는다.
	private final String symbol;
	private final String protocol;
	private final String url;
	private final String adminPath;
	private final String path;

	@Autowired
	RestTemplateUtil restTemplateUtil;
	
	@Autowired
	private DomainService domainService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private JwtUtil jwtUtil;
	
	@Autowired
	ServersMap serversMap;
	
	//status가 up인 서버 하나
	private String getAdminUrl() {
	    String url = this.serversMap.findOneServer(symbol);
	    if ( url == null ) {
	      url = this.url;
	    }
	    return protocol + "://" + url + "/" + adminPath + "/";
	}
	
	// 도메인 생성
	@ApiOperation(value = "도메인 등록", notes = "새 도메인을 등록하는 기능")
	@PostMapping(produces = "application/json;charset=UTF-8")
	@Transactional
	public ResponseEntity<?> domainInsert(
			@RequestHeader HttpHeaders headers,
			@RequestBody DomainVO vo)
			throws Exception {

		String authToken = headers.getFirst("Authorization");
		if (authToken != null && authToken.startsWith("Bearer ")) {
			String token = authToken.substring(7);

			Jws<Claims> claims = jwtUtil.getClaims(token);// 코드 복호화+서명검증
			boolean isTokenValid = jwtUtil.validateToken(claims);// 토큰 만료시간 검증
			String id = (String) claims.getBody().get("id");//생성자
			
			String uuid = userService.uuid(id);
			if(uuid==null) {
				Map<String, Object> map = new HashMap<>();
				HttpStatus status = HttpStatus.UNAUTHORIZED;
				String message = "없는 아이디 입니다. 로그인 후 이용바랍니다.";
				map.put("status", status);
				map.put("message", message);
				return new ResponseEntity<>(map, status);
			}
			
			String refreshUUID = userService.refreshToken_chk(uuid);
			if(refreshUUID==null) {
				Map<String, Object> map = new HashMap<>();
				HttpStatus status = HttpStatus.UNAUTHORIZED;
				String message = "로그인 가능한 토큰이 없습니다. 토큰을 재발급 하세요.";
				map.put("status", status);
				map.put("message", message);
				return new ResponseEntity<>(map, status);
			}
			

			Date date = new Date();
			vo.setCreateTime(date);
			vo.setUpdateTime(date);
			vo.setUserId(refreshUUID);


//			domain_vo.setRoleName("domain_admin");

			DomainVO domainInfo = domainService.domainInfo(vo.getDomainId());
			if(!(domainInfo==null)) {
				Map<String, Object> map = new HashMap<>();
				HttpStatus status = HttpStatus.UNAUTHORIZED;
				String message = "이미 사용중인 도메인명 입니다.";
				map.put("status", status);
				map.put("message", message);
				return new ResponseEntity<>(map, status);
			}else {
				GatewayDomainVO gwDomainVO = new GatewayDomainVO();
				gwDomainVO.setDomainId(vo.getDomainId());
				gwDomainVO.setDomainName(vo.getDomainId());
				gwDomainVO.setDomainDesc(vo.getNote());
				gwDomainVO.setEnabled(vo.isEnabled());
			    headers.setContentType(MediaType.APPLICATION_JSON);
			    ResponseEntity<?> res = restTemplateUtil.call(getAdminUrl() + path, HttpMethod.POST, new HttpEntity<>(gwDomainVO, headers));
			    if (!res.getStatusCode().is2xxSuccessful()) {
			    	return res;
			    }
			}
			
			
			String uuId = userService.uuid(id);
			vo.setUserId(uuId);
			domainService.domainInsert(vo);
			DomainVO insertedDomain = domainService.domainInfo(vo.getDomainId());
			
			Map<String, Object> map = new HashMap<>();
			HttpStatus status = HttpStatus.OK;
			String message = "저장되었습니다.";
			map.put("status", status);
			map.put("message", message);
			map.put("data", insertedDomain);
			log.info("================================= domain insert:\n" + gson.toJson(map));
			return new ResponseEntity<>(map, status);
		}else {
		Map<String, Object> map = new HashMap<>();
		HttpStatus status = HttpStatus.UNAUTHORIZED;
		String message = "로그인 후에 이용가능합니다.";
		map.put("status", status);
		map.put("message", message);
		return new ResponseEntity<>(map, status);
		}
	}
	
//    @ApiOperation(value = "도메인 등록", notes = "새 도메인을 등록하는 기능")
//    @PostMapping()
//    @Transactional
//    public ResponseEntity<?> domainInsert(
//    		@RequestHeader HttpHeaders headers,
//    		@RequestBody DomainVO vo) throws Exception {
//    	domainService.domainInsert(vo); // ServiceImpl의 insertDomain 메소드 호출
//
//    	GatewayDomainVO gwDomainVO = new GatewayDomainVO();
//		gwDomainVO.setDomainId(vo.getDomainId());
//		gwDomainVO.setDomainName(vo.getDomainId());
//		gwDomainVO.setDomainDesc(vo.getNote());
//		gwDomainVO.setEnabled(vo.isEnabled());
//    	headers.setContentType(MediaType.APPLICATION_JSON);
//        ResponseEntity<?> res = restTemplateUtil.call(url + path, HttpMethod.POST, new HttpEntity<>(gwDomainVO, headers));
//        if (!res.getStatusCode().is2xxSuccessful()) {
//            throw new RuntimeException("Admin 서버에 도메인 등록에 실패했습니다.");
//        }
//
//    	ResponseVO responseVO = domainService.domainResponse(vo);
//        // DomainDTO 객체의 데이터를 ResponseEntity로 변환하여 반환
//        Map<String, Object> map = new HashMap<>();
//        map.put("status", responseVO.getStatus());
//        map.put("message", responseVO.getMessage());
//        map.put("data", responseVO.getData());
//
//        return ResponseEntity.ok(map);
//    }

	
	//도메인 저장
//	@PostMapping()
//	  public ResponseEntity<?> save(@RequestBody Object request) {
//	    HttpHeaders headers = new HttpHeaders();
//	    headers.setContentType(MediaType.APPLICATION_JSON);
//	    System.out.println(request);
//	    System.out.println(headers);
//	    System.out.println(getAdminUrl());
//	    return restTemplateUtil.call(getAdminUrl() + path, HttpMethod.POST, new HttpEntity<>(request, headers));
//	  }
	
	
	// 도메인 수정
	@ApiOperation(value = "도메인 수정", notes = "등록된 도메인을 수정하는 기능")
	@PutMapping(value = "/{domainId}", produces = "application/json;charset=UTF-8")
	public ResponseEntity<?> domainUpdate(
			@RequestHeader HttpHeaders headers,
			@RequestBody DomainVO vo,
			@PathVariable("domainId") String domainId)
//			@RequestBody Object request)
			throws Exception {
/////////////////////////////////////////////////////////////////////////////////////
		String authToken = headers.getFirst("Authorization");
		if (authToken != null && authToken.startsWith("Bearer ")) {
			String token = authToken.substring(7);

			Jws<Claims> claims = jwtUtil.getClaims(token);// 코드 복호화+서명검증
			boolean isTokenValid = jwtUtil.validateToken(claims);// 토큰 만료시간 검증
			String id = (String) claims.getBody().get("id");//생성자
			
			String uuid = userService.uuid(id);
			if(uuid==null) {
				Map<String, Object> map = new HashMap<>();
				HttpStatus status = HttpStatus.UNAUTHORIZED;
				String message = "없는 아이디 입니다. 로그인 후 이용바랍니다.";
				map.put("status", status);
				map.put("message", message);
				return new ResponseEntity<>(map, status);
			}
			
			String refreshUUID = userService.refreshToken_chk(uuid);
			if(refreshUUID==null) {
				Map<String, Object> map = new HashMap<>();
				HttpStatus status = HttpStatus.UNAUTHORIZED;
				String message = "로그인 가능한 토큰이 없습니다. 토큰을 재발급 하세요.";
				map.put("status", status);
				map.put("message", message);
				return new ResponseEntity<>(map, status);
			}
/////////////////////////////////////////////////////////////////////////////////////
			vo.setDomainId(domainId);
			
			GatewayDomainVO gwDomainVO = new GatewayDomainVO();
			gwDomainVO.setDomainId(vo.getDomainId());
			gwDomainVO.setDomainName(vo.getDomainId());
			gwDomainVO.setDomainDesc(vo.getNote());
			gwDomainVO.setEnabled(vo.isEnabled());
		    headers.setContentType(MediaType.APPLICATION_JSON);
		    restTemplateUtil.call(getAdminUrl() + path + "/" + domainId, HttpMethod.PUT, new HttpEntity<>(gwDomainVO, headers));
		    // 도메인 수정
		    DomainVO domainUpdate = domainService.domainUpdate(vo);
		    
			Map<String, Object> map = new HashMap<>();
			HttpStatus status = HttpStatus.OK;
			String message = "수정되었습니다.";
			map.put("status", status);
			map.put("message", message);
			map.put("data", domainUpdate);
			log.info("================================= domain update:\n" + gson.toJson(map));
			return new ResponseEntity<>(map, status);
//////////////////////////////////////////////////////////////////////////
		}else {
			Map<String, Object> map = new HashMap<>();
			HttpStatus status = HttpStatus.UNAUTHORIZED;
			String message = "로그인 후에 이용가능합니다.";
			map.put("status", status);
			map.put("message", message);
			return new ResponseEntity<>(map, status);
			}
//////////////////////////////////////////////////////////////////////////
//		}
//		Map<String, Object> map = new HashMap<>();
//		HttpStatus status = HttpStatus.UNAUTHORIZED;
//		String message = "로그인 후에 이용가능합니다.";
//		map.put("status", status);
//		map.put("message", message);
//		return new ResponseEntity<>(map, status);
	}
	
	//도메인 수정
//	@PutMapping("/{id}")
//	  public ResponseEntity<?> update(@PathVariable("id") String id, @RequestBody Object request) {
//	    HttpHeaders headers = new HttpHeaders();
//	    headers.setContentType(MediaType.APPLICATION_JSON);
//	    
//	    return restTemplateUtil.call(getAdminUrl() + path + "/" + id, HttpMethod.PUT, new HttpEntity<>(request, headers));
//	  }
	
	// 도메인 전체 조회
	@ApiOperation(value = "도메인 조회", notes = "도메인 전체를 조회하는 기능")
	@GetMapping(produces = "application/json;charset=UTF-8")
	public ResponseEntity<?> domainList(
			@RequestHeader HttpHeaders headers
			,@RequestParam String search)
			throws Exception {
		
		String authToken = headers.getFirst("Authorization");
		if (authToken != null && authToken.startsWith("Bearer ")) {
			String token = authToken.substring(7);

			Jws<Claims> claims = jwtUtil.getClaims(token);// 코드 복호화+서명검증
			boolean isTokenValid = jwtUtil.validateToken(claims);// 토큰 만료시간 검증
			String id = (String) claims.getBody().get("id");//생성자
			
			String uuid = userService.uuid(id);
			if(uuid==null) {
				Map<String, Object> map = new HashMap<>();
				HttpStatus status = HttpStatus.UNAUTHORIZED;
				String message = "없는 아이디 입니다. 로그인 후 이용바랍니다.";
				map.put("status", status);
				map.put("message", message);
				return new ResponseEntity<>(map, status);
			}
			
			String refreshUUID = userService.refreshToken_chk(uuid);
			if(refreshUUID==null) {
				Map<String, Object> map = new HashMap<>();
				HttpStatus status = HttpStatus.UNAUTHORIZED;
				String message = "로그인 가능한 토큰이 없습니다. 토큰을 재발급 하세요.";
				map.put("status", status);
				map.put("message", message);
				return new ResponseEntity<>(map, status);
			}

			Map<String, Object> dataMap = new HashMap<>();
			dataMap.put("domainId", search);

			List<DomainVO> data = domainService.domainTotal(dataMap);

			Map<String, Object> map = new HashMap<>();
			HttpStatus status = HttpStatus.OK;
			String message = "도메인 목록을 조회합니다.";
			map.put("status", status);
			map.put("message", message);
			map.put("data", data);
			log.info("================================= domain search:\n" + gson.toJson(map));
			return new ResponseEntity<>(map, status);
		}else {
			Map<String, Object> map = new HashMap<>();
			HttpStatus status = HttpStatus.UNAUTHORIZED;
			String message = "로그인 후에 이용가능합니다.";
			map.put("status", status);
			map.put("message", message);
			return new ResponseEntity<>(map, status);
			}
	}
	
	
	//전체조회
//	@GetMapping()
//	  public ResponseEntity<?> list(@RequestParam Map<String,String> params) {
//	    HttpHeaders headers = new HttpHeaders();
//	    headers.setContentType(MediaType.APPLICATION_JSON);
//
//	    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getAdminUrl() + path);
//	    for (Map.Entry<String, String> entry : params.entrySet()) {
//	      builder.queryParam(entry.getKey(), entry.getValue());
//	    }
//
//	    return restTemplateUtil.callStatusOk(builder, HttpMethod.GET, new HttpEntity<>(headers));
//	  }
	
//	//일치검색
//	@GetMapping("/{id}")
//	  public ResponseEntity<?> findById(@PathVariable("id") String id) {
//	    HttpHeaders headers = new HttpHeaders();
//	    headers.setContentType(MediaType.APPLICATION_JSON);
//	    
//	    return restTemplateUtil.call(getAdminUrl() + path + "/" + id, HttpMethod.GET, new HttpEntity<>(headers));
//	  }
	
	// 도메인 삭제
	@ApiOperation(value = "도메인 삭제", notes = "등록된 도메인을 삭제하는 기능")
	@DeleteMapping(value = "/{domainId}", produces = "application/json;charset=UTF-8")
	public ResponseEntity<?> domainDelete(
			@RequestHeader HttpHeaders headers,
			@PathVariable("domainId") String domainId)
			throws Exception {

		String authToken = headers.getFirst("Authorization");
		if (authToken != null && authToken.startsWith("Bearer ")) {
			String token = authToken.substring(7);

			Jws<Claims> claims = jwtUtil.getClaims(token);// 코드 복호화+서명검증
			boolean isTokenValid = jwtUtil.validateToken(claims);// 토큰 만료시간 검증
			String id = (String) claims.getBody().get("id");//생성자
			
			String uuid = userService.uuid(id);
			if(uuid==null) {
				Map<String, Object> map = new HashMap<>();
				HttpStatus status = HttpStatus.UNAUTHORIZED;
				String message = "없는 아이디 입니다. 로그인 후 이용바랍니다.";
				map.put("status", status);
				map.put("message", message);
				return new ResponseEntity<>(map, status);
			}
			
			String refreshUUID = userService.refreshToken_chk(uuid);
			if(refreshUUID==null) {
				Map<String, Object> map = new HashMap<>();
				HttpStatus status = HttpStatus.UNAUTHORIZED;
				String message = "로그인 가능한 토큰이 없습니다. 토큰을 재발급 하세요.";
				map.put("status", status);
				map.put("message", message);
				return new ResponseEntity<>(map, status);
			}

			domainService.domainDelete(domainId);
			headers.setContentType(MediaType.APPLICATION_JSON);
		    restTemplateUtil.call(getAdminUrl() + path + "/" + domainId, HttpMethod.DELETE, new HttpEntity<>(headers));

			Map<String, Object> map = new HashMap<>();
			HttpStatus status = HttpStatus.OK;
			String message = "삭제되었습니다.";
			map.put("status", status);
			map.put("message", message);
			log.info("================================= domain delete:\n" + gson.toJson(map));
			return new ResponseEntity<>(map, status);
		}else {
			Map<String, Object> map = new HashMap<>();
			HttpStatus status = HttpStatus.UNAUTHORIZED;
			String message = "로그인 후에 이용가능합니다.";
			map.put("status", status);
			map.put("message", message);
			return new ResponseEntity<>(map, status);
			}
	}
	
}	
