package com.example.demo.controller;

import java.util.HashMap;
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
import com.example.demo.service.UserService;
import com.example.demo.token.JwtUtil;
import com.example.demo.util.RestTemplateUtil;
import com.example.demo.vo.gateway.GatewayConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Api(tags = "라우트 API")
@RestController
@RequiredArgsConstructor
@RequestMapping({"/api/v1/gateway/{domainId}/routes", "/api/v1/gateway/routes"})
@Slf4j
public class RouteController {

	  private final String symbol;
	  private final String protocol;
	  private final String url;
	  private final String adminPath;
	  private final String path;
	  
	  @Autowired
	  ServersMap serversMap;
	  
	  @Autowired
	  public RouteController(GatewayConfig config) {
	    this.symbol = config.getSymbol();
	    this.protocol = config.getProtocol();
	    this.url = config.getUrl();
	    this.adminPath = config.getAdminPath();
	    this.path = config.getPath().getRoute();
	  }

	  @Autowired
	  private RestTemplateUtil restTemplateUtil;

	  private String getAdminUrl() {
		
	    String url = this.serversMap.findOneServer(symbol);
	    if ( url == null ) {
	      url = this.url;
	    }
		return protocol + "://" + url + "/" + adminPath + "/";
	}

	@Autowired
	private UserService userService;

	@Autowired
	private JwtUtil jwtUtil;
	
	private Gson gson = new GsonBuilder().setPrettyPrinting().create();

	  //라우트 저장
	  @PostMapping()
	  public ResponseEntity<?> save(@RequestHeader HttpHeaders headers, @RequestBody Object request, 
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
//	    Object result = 
//	    log.info("================================= route insert:\n" + gson.toJson(result));
//		if (!(result == HttpStatus.OK)) {
//			return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
//		}
		return restTemplateUtil.call(getAdminUrl() + ((domainId==null) ? "" : domainId+"/") + path, HttpMethod.POST, new HttpEntity<>(request, headers));
	} else {
		Map<String, Object> map = new HashMap<>();
		HttpStatus status = HttpStatus.UNAUTHORIZED;
		String message = "로그인 후에 이용가능합니다.";
		map.put("status", status);
		map.put("message", message);
		return new ResponseEntity<>(map, status);
	}
}
	  
	  //라우트 전체조회
	  @GetMapping(produces = "application/json;charset=UTF-8")
	  public ResponseEntity<?> list(@RequestHeader HttpHeaders headers, @RequestParam Map<String,String> params, 
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

	    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getAdminUrl() + ((domainId==null) ? "" : domainId+"/") + path);
	    for (Map.Entry<String, String> entry : params.entrySet()) {
	      builder.queryParam(entry.getKey(), entry.getValue());
	    }
	    
	    ResponseEntity<?> res = restTemplateUtil.callStatusOk(builder, HttpMethod.GET, new HttpEntity<>(headers));
	    
	    if (!res.getStatusCode().is2xxSuccessful()) {
	    	return ResponseEntity.badRequest().body("GW Search failed.").status(400).build();
	    }																					//ResponseEntity.ok(new Gson().toJson(res.getBody())))
	    log.info("================================= route search:\n" + gson.toJson(restTemplateUtil.callStatusOk(builder, HttpMethod.GET, new HttpEntity<>(headers))));
	    return ResponseEntity.ok(new Gson().toJson(res.getBody()));
			} else {
				Map<String, Object> map = new HashMap<>();
				HttpStatus status = HttpStatus.UNAUTHORIZED;
				String message = "로그인 후에 이용가능합니다.";
				map.put("status", status);
				map.put("message", message);
				return new ResponseEntity<>(map, status);
			}
			}
	  
	  //라우트 아이디와 일치하는 라우트 검색
	  @GetMapping(value = "/{id}", produces = "application/json;charset=UTF-8")
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
	    log.info("================================= route search one:\n" + gson.toJson(restTemplateUtil.call(getAdminUrl() + ((domainId==null) ? "" : domainId+"/") + path + "/" + id, HttpMethod.GET, new HttpEntity<>(headers))));
	    return restTemplateUtil.call(getAdminUrl() + ((domainId==null) ? "" : domainId+"/") + path + "/" + id, HttpMethod.GET, new HttpEntity<>(headers));
			} else {
				Map<String, Object> map = new HashMap<>();
				HttpStatus status = HttpStatus.UNAUTHORIZED;
				String message = "로그인 후에 이용가능합니다.";
				map.put("status", status);
				map.put("message", message);
				return new ResponseEntity<>(map, status);
			}
			}
	
	  //특정 라우트와 연관된 서비스 검색
	  @GetMapping("/{id}/services")
	  public ResponseEntity<?> searchServiceByRouteId(@RequestHeader HttpHeaders headers, @PathVariable("id") String id, 
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
//	    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(
//				getAdminUrl() + ((domainId == null) ? "" : domainId + "/") + path + "/" + id + "/services");
//		for (Map.Entry<String, String> entry : params.entrySet()) {
//			builder.queryParam(entry.getKey(), entry.getValue());
//		}
//		System.out.println(params);
//		System.out.println(id);
//		System.out.println(domainId);
//		System.out.println(builder);

//		return restTemplateUtil.callStatusOk(builder.toUriString(), HttpMethod.GET, new HttpEntity<>(headers));
	    log.info("================================= route search connect route:\n" + gson.toJson(restTemplateUtil.call(getAdminUrl() + ((domainId==null) ? "" : domainId+"/") + path + "/" + id + "/services", HttpMethod.GET, new HttpEntity<>(headers))));
	    return restTemplateUtil.call(getAdminUrl() + ((domainId==null) ? "" : domainId+"/") + path + "/" + id + "/services", HttpMethod.GET, new HttpEntity<>(headers));
			} else {
				Map<String, Object> map = new HashMap<>();
				HttpStatus status = HttpStatus.UNAUTHORIZED;
				String message = "로그인 후에 이용가능합니다.";
				map.put("status", status);
				map.put("message", message);
				return new ResponseEntity<>(map, status);
			}
			}
	  
	  //라우트 수정
	  @PutMapping(value= "/{id}", produces = "application/json;charset=UTF-8")
	  public ResponseEntity<?> update(@RequestHeader HttpHeaders headers, @RequestBody Object request, @PathVariable("id") String id, 
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
	    log.info("================================= route update:\n" + gson.toJson(restTemplateUtil.call(getAdminUrl() + ((domainId==null) ? "" : domainId+"/") + path + "/" + id, HttpMethod.PUT, new HttpEntity<>(request, headers))));
	    return restTemplateUtil.call(getAdminUrl() + ((domainId==null) ? "" : domainId+"/") + path + "/" + id, HttpMethod.PUT, new HttpEntity<>(request, headers));
			} else {
				Map<String, Object> map = new HashMap<>();
				HttpStatus status = HttpStatus.UNAUTHORIZED;
				String message = "로그인 후에 이용가능합니다.";
				map.put("status", status);
				map.put("message", message);
				return new ResponseEntity<>(map, status);
			}
}
	
	  //라우트 삭제
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
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    Object result = restTemplateUtil.call(getAdminUrl() + ((domainId==null) ? "" : domainId+"/") + path + "/" + id, HttpMethod.DELETE, new HttpEntity<>(headers));
	    log.info("================================= route delete:\n" + gson.toJson(result));
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
/////////////////////////////////////////////////////////////////////////////////////////////////////////	
//	@Autowired
//	private RouteSevice routeService;
//
//	@Autowired
//	private JwtUtil jwtUtil;
//
//	// route 등록
//	@ApiOperation(value = "라우트 등록", notes = "새 라우트를 등록하는 기능")
//	@PostMapping(value = "route")
//	public ResponseEntity<?> routeInsert(
//			@PathVariable(name = "domainId", required = false) String domainId,
//			@RequestHeader HttpHeaders headers, @RequestBody RouteVO vo) throws Exception {
//
//		String authToken = headers.getFirst("Authorization");
//		if (authToken != null && authToken.startsWith("Bearer ")) {
//			String token = authToken.substring(7);
//
//			Jws<Claims> claims = jwtUtil.getClaims(token);// 코드 복호화+서명검증
//			boolean isTokenValid = jwtUtil.validateToken(claims);// 토큰 만료시간 검증
//			// String id = jwtUtil.getKey(claims);//생성자
//
//			Date date = new Date();
//			vo.setUpdateDate(date);
//			vo.setCreateDate(date);
//			vo.setDomainId(domainId);
//			
//			routeService.routeInsert(vo);
//			RouteVO routeDate = routeService.routeInfo(vo);
//			HttpStatus status = HttpStatus.OK;
//			String message = "저장되었습니다.";
//			Map<String, Object> response = new HashMap<>();
//			response.put("message", message);
//			response.put("status", status);
//			response.put("data", routeDate);
//
//			return new ResponseEntity<>(response, status);
//		}
//		Map<String, Object> map = new HashMap<>();
//		HttpStatus status = HttpStatus.UNAUTHORIZED;
//		String message = "로그인 후에 이용가능합니다.";
//		map.put("status", status);
//		map.put("message", message);
//		return new ResponseEntity<>(map, status);
//	}
//	
//	// path route 조회
//	@ApiOperation(value = "라우트 상세정보 조회", notes = "라우트를 상세정보를 조회하는 기능")
//	@GetMapping(value = "route/{routeId}")
//	public ResponseEntity<?> serviceIdSelect(
//			@PathVariable(name = "domainId", required = false) String domainId,
//			@PathVariable(name = "routeId", required = false) String routeId,
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
//			dataMap.put("routeId", routeId);		
//			
//			RouteVO data = routeService.routePath(dataMap);
//			
//			Map<String, Object> map = new HashMap<>();
//			HttpStatus status = HttpStatus.OK;
//			String message = "라우트를 조회합니다.";
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
//	// route 전체리스트 목록 조회
//	@ApiOperation(value = "라우트 검색 조회", notes = "찾고자 하는 라우트를 검색하거나 전체를 조회하는 기능")
//	@GetMapping(value = "route")
//	public ResponseEntity<?> routeList(
//			@PathVariable(name = "domainId", required = false) String domainId,
////			@PathVariable(name = "routeId", required = false) String routeId,
//			@RequestHeader HttpHeaders headers, @RequestParam String search,
//			@RequestParam String searchType, @RequestParam Integer numOfRows, @RequestParam Integer pageNo)
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
//			Map<String, Object> pageMap = new HashMap<>();
//			pageMap.put("numOfRows", numOfRows);
//			pageMap.put("pageNo", (pageNo - 1) * numOfRows);
//			pageMap.put("domainId", domainId);
//			if (searchType.equals("id")) {
//				pageMap.put("routeId", search);
//			}
//
//			
//			if (searchType.equals("name")) {
//				pageMap.put("routeName", search);
//			}
//
//			if (searchType.equals("path")) {
//				pageMap.put("routePath", search);
//			}
//
//			int totalCnt = routeService.routeTotalCnt(pageMap);
//			List<RouteVO> data = routeService.routeList(pageMap);
//
//			Map<String, Object> map = new HashMap<>();
//			HttpStatus status = HttpStatus.OK;
//			String message = "라우트 목록을 조회합니다.";
//			map.put("status", status);
//			map.put("message", message);
//			map.put("data", data);
//			map.put("numOfRows", numOfRows);
//			map.put("pageNo", pageNo);
//			map.put("totalCount", totalCnt);
//
//			return ResponseEntity.ok(map);
//		}
//		Map<String, Object> map = new HashMap<>();
//		HttpStatus status = HttpStatus.UNAUTHORIZED;
//		String message = "먼저 로그인이 필요합니다.";
//		map.put("status", status);
//		map.put("message", message);
//		return new ResponseEntity<>(map, status);
//	}
//
//	// 라우트 수정
//	@ApiOperation(value = "라우트 수정", notes = "등록된 라우트를 수정하는 기능")
//	@PutMapping(value = "route/{routeId}")
//	public ResponseEntity<?> serviceUpdate(
//			@PathVariable(name = "domainId", required = false) String domainId,
//			@PathVariable(name = "routeId", required = false) String routeId,
//			@RequestHeader HttpHeaders headers, @RequestBody RouteVO vo)
//			throws Exception {
//
//		String authToken = headers.getFirst("Authorization");
//		if (authToken != null && authToken.startsWith("Bearer ")) {
//			String token = authToken.substring(7);
//
//			Jws<Claims> claims = jwtUtil.getClaims(token);// 코드 복호화+서명검증
//			boolean isTokenValid = jwtUtil.validateToken(claims);// 토큰 만료시간 검증
//			// String id = jwtUtil.getKey(claims);//생성자
//			Date date = new Date();
//			vo.setUpdateDate(date);
//			vo.setDomainId(domainId);
//			RouteVO updatedRoute = routeService.routeUpdate(vo);
//			RouteVO routeData = routeService.routeInfo(vo);
//
//			Map<String, Object> map = new HashMap<>();
//			HttpStatus status = HttpStatus.OK;
//			String message = "수정되었습니다.";
//			map.put("status", status);
//			map.put("message", message);
//			map.put("data", routeData);
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
//	// 서비스 삭제
//	@ApiOperation(value = "라우트 삭제", notes = "등록된 라우트를 삭제하는 기능")
//	@DeleteMapping(value = "route/{routeId}")
//	public ResponseEntity<?> serviceDelete(
//			@PathVariable(name = "domainId", required = false) String domainId,
//			@PathVariable(name = "routeId", required = false) String routeId,
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
//			dataMap.put("routeId", routeId);
//			routeService.routeDelete(dataMap);
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
//}
