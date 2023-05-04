package com.example.demo.controller;

import java.net.URL;
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
import org.springframework.web.util.UriComponentsBuilder;

import com.example.demo.gateway.ServersMap;
import com.example.demo.service.ServiceService;
import com.example.demo.service.UserService;
import com.example.demo.token.JwtUtil;
import com.example.demo.util.RestTemplateUtil;
import com.example.demo.vo.ServiceListVO;
import com.example.demo.vo.ServiceVO;
import com.example.demo.vo.gateway.GatewayConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Api(tags = "서비스 API")
@RestController
@RequestMapping({ "/api/v1/gateway/{domainId}/services", "/api/v1/gateway/services" })
@Slf4j
public class ServiceController {

	private Gson gson = new GsonBuilder().setPrettyPrinting().create();
	
	private final String symbol;
	private final String protocol;
	private final String url;
	private final String adminPath;
	private final String path;

	@Autowired
	ServersMap serversMap;

	@Autowired
	public ServiceController(GatewayConfig config) {
		this.symbol = config.getSymbol();
		this.protocol = config.getProtocol();
		this.url = config.getUrl();
		this.adminPath = config.getAdminPath();
		this.path = config.getPath().getService();
	}

	@Autowired
	private RestTemplateUtil restTemplateUtil;

	private String getAdminUrl() {
		String url = this.serversMap.findOneServer(symbol);
		if (url == null) {
			url = this.url;
		}
		return protocol + "://" + url + "/" + adminPath + "/";
	}

	@Autowired
	private UserService userService;

	@Autowired
	private JwtUtil jwtUtil;

	// 서비스 등록
	@PostMapping()
	public ResponseEntity<?> save(@RequestHeader HttpHeaders headers, @RequestBody Object request,
			@PathVariable(name = "domainId", required = false) String domainId) throws Exception {

		String authToken = headers.getFirst("Authorization");
		if (authToken != null && authToken.startsWith("Bearer ")) {
			String token = authToken.substring(7);

			Jws<Claims> claims = jwtUtil.getClaims(token);// 코드 복호화+서명검증
			boolean isTokenValid = jwtUtil.validateToken(claims);// 토큰 만료시간 검증
			String id = (String) claims.getBody().get("id");// 생성자

			String uuid = userService.uuid(id);
			if (uuid == null) {
				Map<String, Object> map = new HashMap<>();
				HttpStatus status = HttpStatus.UNAUTHORIZED;
				String message = "없는 아이디 입니다. 로그인 후 이용바랍니다.";
				map.put("status", status);
				map.put("message", message);
				return new ResponseEntity<>(map, status);
			}

			String refreshUUID = userService.refreshToken_chk(uuid);
			if (refreshUUID == null) {
				Map<String, Object> map = new HashMap<>();
				HttpStatus status = HttpStatus.UNAUTHORIZED;
				String message = "로그인 가능한 토큰이 없습니다. 토큰을 재발급 하세요.";
				map.put("status", status);
				map.put("message", message);
				return new ResponseEntity<>(map, status);
			}

			headers.setContentType(MediaType.APPLICATION_JSON);
//			Object result = 
//			log.info("================================= service insert:\n" + gson.toJson(result));
//			if (result==HttpStatus.OK) {
//				return new ResponseEntity<>(result,HttpStatus.OK);
//			}else {
				return restTemplateUtil.call(getAdminUrl() + ((domainId == null) ? "" : domainId + "/") + path, HttpMethod.POST, new HttpEntity<>(request, headers));				
		} else {
			Map<String, Object> map = new HashMap<>();
			HttpStatus status = HttpStatus.UNAUTHORIZED;
			String message = "로그인 후에 이용가능합니다.";
			map.put("status", status);
			map.put("message", message);
			return new ResponseEntity<>(map, status);
		}
	}

	// 전체조회
	@GetMapping()
	public ResponseEntity<?> list(@RequestHeader HttpHeaders headers, @RequestParam Map<String, String> params,
			@PathVariable(name = "domainId", required = false) String domainId) throws Exception {

		String authToken = headers.getFirst("Authorization");
		if (authToken != null && authToken.startsWith("Bearer ")) {
			String token = authToken.substring(7);

			Jws<Claims> claims = jwtUtil.getClaims(token);// 코드 복호화+서명검증
			boolean isTokenValid = jwtUtil.validateToken(claims);// 토큰 만료시간 검증
			String id = (String) claims.getBody().get("id");// 생성자

			String uuid = userService.uuid(id);
			if (uuid == null) {
				Map<String, Object> map = new HashMap<>();
				HttpStatus status = HttpStatus.UNAUTHORIZED;
				String message = "없는 아이디 입니다. 로그인 후 이용바랍니다.";
				map.put("status", status);
				map.put("message", message);
				return new ResponseEntity<>(map, status);
			}

			String refreshUUID = userService.refreshToken_chk(uuid);
			if (refreshUUID == null) {
				Map<String, Object> map = new HashMap<>();
				HttpStatus status = HttpStatus.UNAUTHORIZED;
				String message = "로그인 가능한 토큰이 없습니다. 토큰을 재발급 하세요.";
				map.put("status", status);
				map.put("message", message);
				return new ResponseEntity<>(map, status);
			}

			headers.setContentType(MediaType.APPLICATION_JSON);

			UriComponentsBuilder builder = UriComponentsBuilder
					.fromHttpUrl(getAdminUrl() + ((domainId == null) ? "" : domainId + "/") + path);
			for (Map.Entry<String, String> entry : params.entrySet()) {
				builder.queryParam(entry.getKey(), entry.getValue());
			}
			log.info("================================= service search:\n" + gson.toJson(restTemplateUtil.callStatusOk(builder, HttpMethod.GET, new HttpEntity<>(headers))));
			return restTemplateUtil.callStatusOk(builder, HttpMethod.GET, new HttpEntity<>(headers));
		} else {
			Map<String, Object> map = new HashMap<>();
			HttpStatus status = HttpStatus.UNAUTHORIZED;
			String message = "로그인 후에 이용가능합니다.";
			map.put("status", status);
			map.put("message", message);
			return new ResponseEntity<>(map, status);
		}
	}

	// 서비스 아이디와 일치하는 서비스 검색
	@GetMapping("/{id}")
	public ResponseEntity<?> findById(@RequestHeader HttpHeaders headers, @PathVariable("id") String id,
			@PathVariable(name = "domainId", required = false) String domainId) throws Exception {

		String authToken = headers.getFirst("Authorization");
		if (authToken != null && authToken.startsWith("Bearer ")) {
			String token = authToken.substring(7);

			Jws<Claims> claims = jwtUtil.getClaims(token);// 코드 복호화+서명검증
			boolean isTokenValid = jwtUtil.validateToken(claims);// 토큰 만료시간 검증
			String tokenId = (String) claims.getBody().get("id");// 생성자

			String uuid = userService.uuid(tokenId);
			if (uuid == null) {
				Map<String, Object> map = new HashMap<>();
				HttpStatus status = HttpStatus.UNAUTHORIZED;
				String message = "없는 아이디 입니다. 로그인 후 이용바랍니다.";
				map.put("status", status);
				map.put("message", message);
				return new ResponseEntity<>(map, status);
			}

			String refreshUUID = userService.refreshToken_chk(uuid);
			if (refreshUUID == null) {
				Map<String, Object> map = new HashMap<>();
				HttpStatus status = HttpStatus.UNAUTHORIZED;
				String message = "로그인 가능한 토큰이 없습니다. 토큰을 재발급 하세요.";
				map.put("status", status);
				map.put("message", message);
				return new ResponseEntity<>(map, status);
			}

			headers.setContentType(MediaType.APPLICATION_JSON);
			log.info("================================= service search one:\n" + gson.toJson(restTemplateUtil.call(getAdminUrl() + ((domainId == null) ? "" : domainId + "/") + path + "/" + id,
					HttpMethod.GET, new HttpEntity<>(headers))));
			return restTemplateUtil.call(getAdminUrl() + ((domainId == null) ? "" : domainId + "/") + path + "/" + id,
					HttpMethod.GET, new HttpEntity<>(headers));
		} else {
			Map<String, Object> map = new HashMap<>();
			HttpStatus status = HttpStatus.UNAUTHORIZED;
			String message = "로그인 후에 이용가능합니다.";
			map.put("status", status);
			map.put("message", message);
			return new ResponseEntity<>(map, status);
		}
	}

	// 서비스와 연관된 라우트 목록
	@GetMapping("/{id}/routes")
	public ResponseEntity<?> searchRouteByServiceId(@RequestHeader HttpHeaders headers,
			@RequestParam Map<String, String> params, @PathVariable("id") String id,
			@PathVariable(name = "domainId", required = false) String domainId) throws Exception {
		String authToken = headers.getFirst("Authorization");
		if (authToken != null && authToken.startsWith("Bearer ")) {
			String token = authToken.substring(7);

			Jws<Claims> claims = jwtUtil.getClaims(token);// 코드 복호화+서명검증
			boolean isTokenValid = jwtUtil.validateToken(claims);// 토큰 만료시간 검증
			String tokenId = (String) claims.getBody().get("id");// 생성자

			String uuid = userService.uuid(tokenId);
			if (uuid == null) {
				Map<String, Object> map = new HashMap<>();
				HttpStatus status = HttpStatus.UNAUTHORIZED;
				String message = "없는 아이디 입니다. 로그인 후 이용바랍니다.";
				map.put("status", status);
				map.put("message", message);
				return new ResponseEntity<>(map, status);
			}

			String refreshUUID = userService.refreshToken_chk(uuid);
			if (refreshUUID == null) {
				Map<String, Object> map = new HashMap<>();
				HttpStatus status = HttpStatus.UNAUTHORIZED;
				String message = "로그인 가능한 토큰이 없습니다. 토큰을 재발급 하세요.";
				map.put("status", status);
				map.put("message", message);
				return new ResponseEntity<>(map, status);
			}

			headers.setContentType(MediaType.APPLICATION_JSON);

			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(
					getAdminUrl() + ((domainId == null) ? "" : domainId + "/") + path + "/" + id + "/routes");
			for (Map.Entry<String, String> entry : params.entrySet()) {
				builder.queryParam(entry.getKey(), entry.getValue());
			}
			System.out.println(params);
			System.out.println(id);
			System.out.println(domainId);
			System.out.println(builder);
			log.info("================================= service search connect route:\n" + gson.toJson(restTemplateUtil.callStatusOk(builder.toUriString(), HttpMethod.GET, new HttpEntity<>(headers))));
			return restTemplateUtil.callStatusOk(builder.toUriString(), HttpMethod.GET, new HttpEntity<>(headers));
		} else {
			Map<String, Object> map = new HashMap<>();
			HttpStatus status = HttpStatus.UNAUTHORIZED;
			String message = "로그인 후에 이용가능합니다.";
			map.put("status", status);
			map.put("message", message);
			return new ResponseEntity<>(map, status);
		}
	}

	// 수정
	@PutMapping("/{id}")
	public ResponseEntity<?> update(@RequestHeader HttpHeaders headers, @RequestBody Object request,
			@PathVariable("id") String id, @PathVariable(name = "domainId", required = false) String domainId)
			throws Exception {
		String authToken = headers.getFirst("Authorization");
		if (authToken != null && authToken.startsWith("Bearer ")) {
			String token = authToken.substring(7);

			Jws<Claims> claims = jwtUtil.getClaims(token);// 코드 복호화+서명검증
			boolean isTokenValid = jwtUtil.validateToken(claims);// 토큰 만료시간 검증
			String tokenId = (String) claims.getBody().get("id");// 생성자

			String uuid = userService.uuid(tokenId);
			if (uuid == null) {
				Map<String, Object> map = new HashMap<>();
				HttpStatus status = HttpStatus.UNAUTHORIZED;
				String message = "없는 아이디 입니다. 로그인 후 이용바랍니다.";
				map.put("status", status);
				map.put("message", message);
				return new ResponseEntity<>(map, status);
			}

			String refreshUUID = userService.refreshToken_chk(uuid);
			if (refreshUUID == null) {
				Map<String, Object> map = new HashMap<>();
				HttpStatus status = HttpStatus.UNAUTHORIZED;
				String message = "로그인 가능한 토큰이 없습니다. 토큰을 재발급 하세요.";
				map.put("status", status);
				map.put("message", message);
				return new ResponseEntity<>(map, status);
			}

			headers.setContentType(MediaType.APPLICATION_JSON);
			log.info("================================= service update:\n" + gson.toJson(restTemplateUtil.call(getAdminUrl() + ((domainId == null) ? "" : domainId + "/") + path + "/" + id,
					HttpMethod.PUT, new HttpEntity<>(request, headers))));
			return restTemplateUtil.call(getAdminUrl() + ((domainId == null) ? "" : domainId + "/") + path + "/" + id,
					HttpMethod.PUT, new HttpEntity<>(request, headers));
		} else {
			Map<String, Object> map = new HashMap<>();
			HttpStatus status = HttpStatus.UNAUTHORIZED;
			String message = "로그인 후에 이용가능합니다.";
			map.put("status", status);
			map.put("message", message);
			return new ResponseEntity<>(map, status);
		}
	}

	// 삭제
	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@RequestHeader HttpHeaders headers, @PathVariable("id") String id,
			@PathVariable(name = "domainId", required = false) String domainId) throws Exception {
		String authToken = headers.getFirst("Authorization");
		if (authToken != null && authToken.startsWith("Bearer ")) {
			String token = authToken.substring(7);

			Jws<Claims> claims = jwtUtil.getClaims(token);// 코드 복호화+서명검증
			boolean isTokenValid = jwtUtil.validateToken(claims);// 토큰 만료시간 검증
			String tokenId = (String) claims.getBody().get("id");// 생성자

			String uuid = userService.uuid(tokenId);
			if (uuid == null) {
				Map<String, Object> map = new HashMap<>();
				HttpStatus status = HttpStatus.UNAUTHORIZED;
				String message = "없는 아이디 입니다. 로그인 후 이용바랍니다.";
				map.put("status", status);
				map.put("message", message);
				return new ResponseEntity<>(map, status);
			}

			String refreshUUID = userService.refreshToken_chk(uuid);
			if (refreshUUID == null) {
				Map<String, Object> map = new HashMap<>();
				HttpStatus status = HttpStatus.UNAUTHORIZED;
				String message = "로그인 가능한 토큰이 없습니다. 토큰을 재발급 하세요.";
				map.put("status", status);
				map.put("message", message);
				return new ResponseEntity<>(map, status);
			}
			Object result = restTemplateUtil.call(getAdminUrl() + ((domainId == null) ? "" : domainId + "/") + path + "/" + id,
					HttpMethod.DELETE, new HttpEntity<>(headers));
			log.info("================================= service delete:\n" + gson.toJson(result));
			headers.setContentType(MediaType.APPLICATION_JSON);//헤더의 타입을 json형태로 설정
			return ResponseEntity.ok(result);
		} else {
			Map<String, Object> map = new HashMap<>();
			HttpStatus status = HttpStatus.UNAUTHORIZED;
			String message = "로그인 후에 이용가능합니다.";
			map.put("status", status);
			map.put("message", message);
			return new ResponseEntity<>(map, status);
		}
	}

}

///////////////////////////////////////////////////////////////////////////////////////////////////////	

//	@Autowired
//	private ServiceService serviceService;
//
//	@Autowired
//	private JwtUtil jwtUtil;
//
//	// 서비스 등록
//	@ApiOperation(value = "서비스 등록", notes = "새로운 서비스 api를 등록하는 기능")
//	@PostMapping(value = "service")
//	public ResponseEntity<?> serviceInsert(
//			@PathVariable(name = "domainId", required = false) String domainId,
//			@RequestHeader HttpHeaders headers,
//			@RequestBody ServiceVO vo)
//			throws Exception {
//		
//		System.out.println(vo);
//		System.out.println(domainId);
//
//		String authToken = headers.getFirst("Authorization");
//		if (authToken != null && authToken.startsWith("Bearer ")) {
//			String token = authToken.substring(7);
//
//			Jws<Claims> claims = jwtUtil.getClaims(token);// 코드 복호화+서명검증
//			boolean isTokenValid = jwtUtil.validateToken(claims);// 토큰 만료시간 검증
//			// String id = jwtUtil.getKey(claims);//생성자
//
//			ServiceVO id = serviceService.serviceResponse(vo.getServiceId());
//			if(!(id==null)) {
//				Map<String, Object> map = new HashMap<>();
//				HttpStatus status = HttpStatus.BAD_REQUEST;
//				String message = "이미 존재하는 서비스 아이디 입니다.";
//				map.put("status", status);
//				map.put("message", message);
//
//				return new ResponseEntity<>(map, status);
//			}
//			
//			vo.setDomainId(domainId);
//			if(vo.getTargetUrl()!=null) {
//				// 서비스 생성
//				ServiceVO insertedService = serviceService.serviceInsert(vo);				
//			
//				Map<String, Object> map = new HashMap<>();
//				HttpStatus status = HttpStatus.OK;
//				String message = "서비스가 등록되었습니다.";
//				map.put("status", status);
//				map.put("message", message);
//				map.put("data", insertedService);
//				return new ResponseEntity<>(map, status);
//			}
//			
//			String[] strings = {vo.getProtocol(), "://", vo.getHost(), ":", vo.getPort()+"", vo.getPath()};
//			String concatenatedString = String.join("", strings);
//			vo.setTargetUrl(concatenatedString);
//			vo.setDomainId(domainId);
//			System.out.println(concatenatedString);
//			serviceService.serviceInsert(vo);
//			ServiceVO insertedService = serviceService.serviceResponse(vo.getServiceId());
//
//			String urlString = insertedService.getTargetUrl();
//	        
//	        try {
//	            URL url = new URL(urlString);
//	            
//	            String protocol = url.getProtocol(); // 프로토콜 (ex: https)
//	            String host = url.getHost(); // 호스트 (ex: 121.167.52.69)
//	            int port = url.getPort(); // 포트 (ex: 29101)
//	            String path = url.getPath(); // 경로 (ex: /user/register)
//	            
//	            vo.setProtocol(protocol);
//	            vo.setHost(host);
//	            vo.setPort(port);
//	            vo.setPath(path);
//
//	            System.out.println("Protocol: " + protocol);
//	            System.out.println("Host: " + host);
//	            System.out.println("Port: " + port);
//	            System.out.println("Path: " + path);
//	        } catch (Exception e) {
//	            e.printStackTrace();
//	        }
//
//			Map<String, Object> map = new HashMap<>();
//			HttpStatus status = HttpStatus.OK;
//			String message = "저장되었습니다.";
//			map.put("status", status);
//			map.put("message", message);
//			map.put("data", vo);
//
//			return new ResponseEntity<>(map, status);
//		}
//		Map<String, Object> map = new HashMap<>();
//		HttpStatus status = HttpStatus.UNAUTHORIZED;
//		String message = "로그인 후에 이용가능합니다.";
//		map.put("status", status);
//		map.put("message", message);
//		return new ResponseEntity<>(map, status);
//	}
//
//	// 서비스 수정
//	@ApiOperation(value = "서비스 수정", notes = "등록된 서비스 내용을 수정하는 기능")
//	@PutMapping(value = "service/{serviceId}")
//	public ResponseEntity<?> serviceUpdate(
//			@PathVariable(name = "domainId", required = false) String domainId,
//			@PathVariable(name = "serviceId", required = false) String serviceId,
//			@RequestHeader HttpHeaders headers, @RequestBody ServiceVO vo)
//			throws Exception {
//
//		String authToken = headers.getFirst("Authorization");
//		if (authToken != null && authToken.startsWith("Bearer ")) {
//			String token = authToken.substring(7);
//
//			Jws<Claims> claims = jwtUtil.getClaims(token);// 코드 복호화+서명검증
//			boolean isTokenValid = jwtUtil.validateToken(claims);// 토큰 만료시간 검증
//			// String id = jwtUtil.getKey(claims);//생성자
//			vo.setDomainId(domainId);
//			ServiceVO updatedService = serviceService.serviceUpdate(vo);
//
//			Map<String, Object> map = new HashMap<>();
//			HttpStatus status = HttpStatus.OK;
//			String message = "수정되었습니다.";
//			map.put("status", status);
//			map.put("message", message);
//			map.put("data", updatedService);
//
//			return new ResponseEntity<>(map, status);
//		}
//		Map<String, Object> map = new HashMap<>();
//		HttpStatus status = HttpStatus.UNAUTHORIZED;
//		String message = "로그인 후에 이용가능합니다.";
//		map.put("status", status);
//		map.put("message", message);
//		return new ResponseEntity<>(map, status);
//	}
//
//	// path service 조회
//	@ApiOperation(value = "서비스 상세정보 조회", notes = "서비스 상세정보를 조회하는 기능")
//	@GetMapping(value = "service/{serviceId}")
//	public ResponseEntity<?> serviceIdSelect(
//			@PathVariable(name = "domainId", required = false) String domainId,
//			@PathVariable(name = "serviceId", required = false) String serviceId,
//			@RequestHeader HttpHeaders headers
//			)throws Exception {
//		
//		String authToken = headers.getFirst("Authorization");
//		if (authToken != null && authToken.startsWith("Bearer ")) {
//			String token = authToken.substring(7);
//			
//			Jws<Claims> claims = jwtUtil.getClaims(token);// 코드 복호화+서명검증
//			boolean isTokenValid = jwtUtil.validateToken(claims);// 토큰 만료시간 검증
//			// String id = jwtUtil.getKey(claims);//생성자
//			
//			Map<String, Object> dataMap = new HashMap<>();
//			dataMap.put("domainId", domainId);
//			dataMap.put("serviceId", serviceId);
//			dataMap.put("domainId", domainId);
//			
//			
//			ServiceVO data = serviceService.serviceInfo(dataMap);
//			
//			Map<String, Object> map = new HashMap<>();
//			HttpStatus status = HttpStatus.OK;
//			String message = "서비스를 조회합니다.";
//			map.put("status", status);
//			map.put("message", message);
//			map.put("data", data);
//
//			return new ResponseEntity<>(map, status);
//		}
//		Map<String, Object> map = new HashMap<>();
//		HttpStatus status = HttpStatus.UNAUTHORIZED;
//		String message = "먼저 로그인이 필요합니다.";
//		map.put("status", status);
//		map.put("message", message);
//		return new ResponseEntity<>(map, status);
//	}
//	
//	//서비스 검색조회
//	@ApiOperation(value = "서비스 검색 조회", notes = "찾고자 하는 서비스를 검색하거나 전체를 조회하는 기능")
//	@GetMapping(value = "service")
//	public ResponseEntity<?> serviceSelectList(
//			@PathVariable(name = "domainId", required = false) String domainId,
////			@PathVariable(name = "serviceId", required = false) String serviceId,
//			@RequestHeader HttpHeaders headers,
//			@RequestParam String search,
//			@RequestParam String searchType,
//			@RequestParam Integer numOfRows,
//			@RequestParam Integer pageNo
//			)throws Exception {
//
//		String authToken = headers.getFirst("Authorization");
//		if (authToken != null && authToken.startsWith("Bearer ")) {
//			String token = authToken.substring(7);
//
//			Jws<Claims> claims = jwtUtil.getClaims(token);// 코드 복호화+서명검증
//			boolean isTokenValid = jwtUtil.validateToken(claims);// 토큰 만료시간 검증
//			// String id = jwtUtil.getKey(claims);//생성자
//
//			Map<String, Object> dataMap = new HashMap<>();
//			dataMap.put("numOfRows", numOfRows);
//			dataMap.put("pageNo", (pageNo - 1) * numOfRows);
//			dataMap.put("domainId", domainId);
//			
//			
//			if (searchType.equals("id")) {
//				dataMap.put("serviceId", search);
//			}
//
//			if (searchType.equals("name")) {
//				dataMap.put("serviceName", search);
//			}
//
//			if (searchType.equals("url")) {
//				dataMap.put("targetUrl", search);
//				System.out.println(dataMap);
//			}
//
//			
//			int totalCnt = serviceService.serviceTotalCnt(dataMap);
//			List<ServiceListVO> data = serviceService.serviceSearchList(dataMap);
//
//			Map<String, Object> map = new HashMap<>();
//			HttpStatus status = HttpStatus.OK;
//			String message = "검색된 서비스 목록을 조회합니다.";
//			map.put("status", status);
//			map.put("message", message);
//			map.put("data", data);
//			map.put("numOfRows", numOfRows);
//			map.put("pageNo", pageNo);
//			map.put("totalCount", totalCnt);
//
//			return new ResponseEntity<>(map, status);
//		}
//		Map<String, Object> map = new HashMap<>();
//		HttpStatus status = HttpStatus.UNAUTHORIZED;
//		String message = "먼저 로그인이 필요합니다.";
//		map.put("status", status);
//		map.put("message", message);
//		return new ResponseEntity<>(map, status);
//	}
//
//	// 서비스 삭제
//	@ApiOperation(value = "서비스 삭제", notes = "등록된 서비스를 삭제하는 기능")
//	@DeleteMapping(value = "service/{serviceId}")
//	public ResponseEntity<?> serviceDelete(
//			@PathVariable(name = "domainId", required = false) String domainId,
//			@PathVariable(name = "serviceId", required = false) String serviceId,
//			@RequestHeader HttpHeaders headers)
//			throws Exception {
//
//		String authToken = headers.getFirst("Authorization");
//		if (authToken != null && authToken.startsWith("Bearer ")) {
//			String token = authToken.substring(7);
//
//			Jws<Claims> claims = jwtUtil.getClaims(token);// 코드 복호화+서명검증
//			boolean isTokenValid = jwtUtil.validateToken(claims);// 토큰 만료시간 검증
//			// String id = jwtUtil.getKey(claims);//생성자
//			
//			Map<String, Object> dataMap = new HashMap<>();
//			dataMap.put("domainId", domainId);
//			dataMap.put("serviceId", serviceId);
//			serviceService.serviceDelete(dataMap);
//
//			Map<String, Object> map = new HashMap<>();
//			HttpStatus status = HttpStatus.OK;
//			String message = "삭제되었습니다.";
//			map.put("status", status);
//			map.put("message", message);
//
//			return new ResponseEntity<>(map, status);
//		}
//		Map<String, Object> map = new HashMap<>();
//		HttpStatus status = HttpStatus.UNAUTHORIZED;
//		String message = "로그인 후에 이용가능합니다.";
//		map.put("status", status);
//		map.put("message", message);
//		return new ResponseEntity<>(map, status);
//	}
//	
//	//서비스 수정
//	@PutMapping(value="service/modification")
//	public ResponseEntity<?> serviceUpdate(
//			@RequestBody ServiceVO vo) throws Exception{
//		
//		//서비스 생성
//		ServiceVO updatedService = serviceService.serviceUpdate(vo);
//
//		Map<String, Object> map = new HashMap<>();
//		HttpStatus status = HttpStatus.OK;
//		String message = "수정되었습니다.";
//		map.put("status", status);
//		map.put("message", message);
//		map.put("data", updatedService);
//
//		return new ResponseEntity<>(map, status);
//	}
//}
