package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.demo.gateway.ServersMap;
import com.example.demo.util.RestTemplateUtil;
import com.example.demo.vo.gateway.GatewayConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Api(tags = "대시보드_Jvm메모리")
@RestController
@RequestMapping("metrics")
@Slf4j
public class GatewayMetricsController {

	  private final String symbol;
	  private final String protocol;
	  private final String url;
	  private final String path;
	  
	  @Autowired
	  ServersMap serversMap;

	  
	  @Autowired
	  public GatewayMetricsController(GatewayConfig config) {
	    this.symbol = config.getSymbol();
	    this.protocol = config.getProtocol();
	    this.url = config.getUrl();
	    this.path = config.getPath().getMetrics();
	  }

	  @Autowired
	  private RestTemplateUtil restTemplateUtil;

	  private String getAdminUrl() {
	    String url = this.serversMap.findOneServer(symbol);
	    if ( url == null ) {
	      url = this.url;
	    }
	    return protocol + "://" + url + "/";
	  }
	  
	  private Gson gson = new GsonBuilder().setPrettyPrinting().create();
	  
	  /**
	   * 조회
	   */
	  @ApiOperation(value = "metrics info")
	  @GetMapping("/{subpath}")//jvm_memory
	  public ResponseEntity<?> list(@PathVariable("subpath") String subpath) {
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);

	    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getAdminUrl() + path + "/" + subpath);
	    log.info("================================= metrics info :\n" + gson.toJson(restTemplateUtil.callStatusOk(builder.toUriString().replaceAll("_", "."), HttpMethod.GET, new HttpEntity<>(headers))));
	    return restTemplateUtil.callStatusOk(builder.toUriString().replaceAll("_", "."), HttpMethod.GET, new HttpEntity<>(headers));
	  }
	}