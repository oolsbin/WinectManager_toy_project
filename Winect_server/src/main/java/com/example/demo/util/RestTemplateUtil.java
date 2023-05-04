package com.example.demo.util;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import javax.net.ssl.SSLContext;

import org.apache.http.config.Registry;//
import org.apache.http.config.RegistryBuilder;//
import org.apache.http.conn.socket.ConnectionSocketFactory;//
import org.apache.http.conn.socket.PlainConnectionSocketFactory;//
import org.apache.http.conn.ssl.NoopHostnameVerifier;//
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;//
import org.apache.http.impl.client.CloseableHttpClient;//
import org.apache.http.impl.client.HttpClients;//
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;///
import org.apache.http.ssl.SSLContexts;//
import org.apache.http.ssl.TrustStrategy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.gson.Gson;

@Component
public class RestTemplateUtil {

  /**
   * API 호출 공통 + 리턴 타입
   * @param allPath 전체 경로
   * @param method HttpMethod
   * @param entity HttpEntity
   * @return
   */
  public <T> ResponseEntity<?> call(String allPath, HttpMethod method, HttpEntity<?> entity, Class<T> clazz) {
    if ( !entity.hasBody()) {
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      entity = new HttpEntity<>(new HashMap<String, Object>(), headers);
    }

    Gson gson = new Gson();
    RestTemplate restTemplate = new RestTemplate();
    try {
      ResponseEntity<T> result = restTemplate.exchange(allPath, method, entity, clazz);
      return ResponseEntity.status(result.getStatusCode()).body(result.getBody());
    } catch ( HttpStatusCodeException e ) {
//      e.printStackTrace();
//      Object body = gson.fromJson(e.getResponseBodyAsString(), Object.class);
      return ResponseEntity.status(e.getStatusCode()).contentType(MediaType.APPLICATION_JSON).body(e.getResponseBodyAsString());
    }
  }

  /**
   * API 호출 공통 + 리턴 타입 + 헤더 수정 없음
   * @param allPath 전체 경로
   * @param method HttpMethod
   * @param entity HttpEntity
   * @return
   */
  public <T> ResponseEntity<?> callOriginal(String allPath, HttpMethod method, HttpEntity<?> entity, Class<T> clazz) {
    RestTemplate restTemplate = new RestTemplate();
    try {
      // ResponseEntity<T> result = restTemplate.exchange(allPath, method, entity, clazz);
      return restTemplate.exchange(allPath, method, entity, clazz);
    } catch ( HttpStatusCodeException e ) {

      HashMap rsltMap = new HashMap();
      rsltMap.put("header", e.getResponseHeaders());
      rsltMap.put("body", e.getResponseBodyAsString());

      // HttpHeaders h = e.getResponseHeaders();
      // Object body = gson.fromJson(e.getResponseBodyAsString(), String.class);
      // return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(rsltMap);
      return ResponseEntity.status(e.getRawStatusCode()).contentType(MediaType.APPLICATION_JSON).body(rsltMap);
    }
  }

  /**
   * API 호출 공통 + 리턴 타입 + 헤더 수정 없음
   * @param allPath 전체 경로
   * @param method HttpMethod
   * @param entity HttpEntity
   * @return
   */
  public <T> ResponseEntity<?> callOriginal(URI uri, HttpMethod method, HttpEntity<?> entity, Class<T> clazz) {
    RestTemplate restTemplate = new RestTemplate();
    try {
      // ResponseEntity<T> result = restTemplate.exchange(allPath, method, entity, clazz);
      return restTemplate.exchange(uri, method, entity, clazz);
      // return restTemplate.getForEntity(uri, clazz);
    } catch ( HttpStatusCodeException e ) {

      HashMap rsltMap = new HashMap();
      rsltMap.put("header", e.getResponseHeaders());
      rsltMap.put("body", e.getResponseBodyAsString());

      // HttpHeaders h = e.getResponseHeaders();
      // Object body = gson.fromJson(e.getResponseBodyAsString(), String.class);
      // return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(rsltMap);
      return ResponseEntity.status(e.getRawStatusCode()).contentType(MediaType.APPLICATION_JSON).body(rsltMap);
    }
  }

  /**
   * API 호출 공통
   * @param allPath 전체 경로
   * @param method HttpMethod
   * @param entity HttpEntity
   * @return
   */
  public ResponseEntity<?> call(String allPath, HttpMethod method, HttpEntity<?> entity) {
    return call(allPath, method, entity, Object.class);
  }

  /**
   * API 호출 공통
   * @param allPath 전체 경로
   * @param method HttpMethod
   * @param entity HttpEntity
   * @return
   */
  public ResponseEntity<?> call(UriComponents allPath, HttpMethod method, HttpEntity<?> entity) {
    return call(allPath.toUriString(), method, entity, Object.class);
  }

  /**
   * API 호출 공통 + 리턴 타입 + 오류코드 200으로 통일
   * @param allPath 전체 경로
   * @param method HttpMethod
   * @param entity HttpEntity
   * @return
   */
  public ResponseEntity<?> callStatusOk(String allPath, HttpMethod method, HttpEntity<?> entity) {
    if ( !entity.hasBody()) {
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      entity = new HttpEntity<>(new HashMap<String, Object>(), headers);
    }

    Gson gson = new Gson();
    RestTemplate restTemplate = new RestTemplate();
    try {
      ResponseEntity<?> result = restTemplate.exchange(allPath, method, entity, Object.class);
      return ResponseEntity.status(result.getStatusCode()).body(result.getBody());
    } catch ( HttpStatusCodeException e ) {
//      JsonElement element = gson.fromJson(e.getResponseBodyAsString(), JsonElement.class);
//      JsonObject object = element.getAsJsonObject();
//      object.addProperty("status", e.getRawStatusCode());

      return ResponseEntity.status(e.getStatusCode()).contentType(MediaType.APPLICATION_JSON).body(e.getResponseBodyAsString());
    }
  }

  /**
   * API 호출 공통 + 리턴 타입 + 오류코드 200으로 통일
   * @param uri 전체 경로 URI 형식으로
   * @param method HttpMethod
   * @param entity HttpEntity
   * @return
   */
  public ResponseEntity<?> callStatusOk(URI uri, HttpMethod method, HttpEntity<?> entity) {
    if ( !entity.hasBody()) {
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      entity = new HttpEntity<>(new HashMap<String, Object>(), headers);
    }

    Gson gson = new Gson();
    RestTemplate restTemplate = new RestTemplate();
    try {
      ResponseEntity<?> result = restTemplate.exchange(uri, method, entity, Object.class);
      return ResponseEntity.status(result.getStatusCode()).body(result.getBody());
    } catch ( HttpStatusCodeException e ) {
//      JsonElement element = gson.fromJson(e.getResponseBodyAsString(), JsonElement.class);
//      JsonObject object = element.getAsJsonObject();
//      object.addProperty("status", e.getRawStatusCode());

      return ResponseEntity.status(e.getStatusCode()).contentType(MediaType.APPLICATION_JSON).body(e.getResponseBodyAsString());
    }
  }

  /**
   * API 호출 공통 + 리턴 타입 + 오류코드 200으로 통일, UTF-8로 인코딩
   * @param builder 전체 경로 빌더로 받음
   * @param method HttpMethod
   * @param entity HttpEntity
   * @return
   */
  public ResponseEntity<?> callStatusOk(UriComponentsBuilder builder, HttpMethod method, HttpEntity<?> entity) {
    if ( !entity.hasBody()) {
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      entity = new HttpEntity<>(new HashMap<String, Object>(), headers);
    }
    
    Gson gson = new Gson();
    RestTemplate restTemplate = new RestTemplate();
    try {
      ResponseEntity<?> result = restTemplate.exchange(builder.build().encode(StandardCharsets.UTF_8).toUri(), method, entity, Object.class);
      return ResponseEntity.status(result.getStatusCode()).body(result.getBody());
    } catch ( HttpStatusCodeException e ) {
//      JsonElement element = gson.fromJson(e.getResponseBodyAsString(), JsonElement.class);
//      JsonObject object = element.getAsJsonObject();
//      object.addProperty("status", e.getRawStatusCode());

      return ResponseEntity.status(e.getStatusCode()).contentType(MediaType.APPLICATION_JSON).body(e.getResponseBodyAsString());
    }
  }

  /**
   * API 호출 https
   * @param allPath 전체 경로
   * @param method HttpMethod
   * @param entity HttpEntity
   * @return
   */
  public <T> ResponseEntity<?> callHttps(String allPath, HttpMethod method, HttpEntity<?> entity, Class<T> clazz) throws Exception {
    // CloseableHttpClient httpClient
    //   = HttpClients.custom()
    //     .setSSLHostnameVerifier(new NoopHostnameVerifier())
    //     .build();
    // HttpComponentsClientHttpRequestFactory requestFactory
    //   = new HttpComponentsClientHttpRequestFactory();
    // requestFactory.setHttpClient(httpClient);

    TrustStrategy acceptingTrustStrategy = (cert, authType) -> true;
    SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
    SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext,
      NoopHostnameVerifier.INSTANCE);

    Registry<ConnectionSocketFactory> socketFactoryRegistry =
      RegistryBuilder.<ConnectionSocketFactory> create()
      .register("https", sslsf)
      .register("http", new PlainConnectionSocketFactory())
      .build();

    BasicHttpClientConnectionManager connectionManager =
      new BasicHttpClientConnectionManager(socketFactoryRegistry);
    CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslsf)
      .setConnectionManager(connectionManager).build();

    HttpComponentsClientHttpRequestFactory requestFactory =
      new HttpComponentsClientHttpRequestFactory(httpClient);

    if ( !entity.hasBody()) {
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      entity = new HttpEntity<>(new HashMap<String, Object>(), headers);
    }

    Gson gson = new Gson();
    RestTemplate restTemplate = new RestTemplate(requestFactory);
    try {
      ResponseEntity<T> result = restTemplate.exchange(allPath, method, entity, clazz);
      return ResponseEntity.status(result.getStatusCode()).body(result.getBody());
    } catch ( HttpStatusCodeException e ) {
//      Object body = gson.fromJson(e.getResponseBodyAsString(), Object.class);
      return ResponseEntity.status(e.getStatusCode()).contentType(MediaType.APPLICATION_JSON).body(e.getResponseBodyAsString());
    }
  }

  /**
   * API 호출 공통 https
   * @param allPath 전체 경로
   * @param method HttpMethod
   * @param entity HttpEntity
   * @return
   */
  public ResponseEntity<?> callHttps(String allPath, HttpMethod method, HttpEntity<?> entity) throws Exception {
    return callHttps(allPath, method, entity, Object.class);
  }

  /**
   * API 호출 공통 https
   * @param allPath 전체 경로
   * @param method HttpMethod
   * @param entity HttpEntity
   * @return
   */
  public ResponseEntity<?> callHttps(UriComponents allPath, HttpMethod method, HttpEntity<?> entity) throws Exception {
    return callHttps(allPath.toUriString(), method, entity, Object.class);
  }

  /**
   * API 호출 getForEntity
   * entity를 받지않는다.
   * @author  Jangjh
   * @version 1.0
   * @date    2021-05-14
   */

  public <T> ResponseEntity<?> callObject(String path, Class<T> clazz) throws Exception {
    Gson gson = new Gson();
//    JsonObject
//    ObjectMapper oMapper = new ObjectMapper();
//    oMapper.
    
    RestTemplate restTemplate = new RestTemplate();
    try {
      ResponseEntity<T> result = restTemplate.getForEntity(path,clazz);
      return ResponseEntity.status(result.getStatusCode()).body(result.getBody());
    } catch ( HttpStatusCodeException e ) {
//      Object body = gson.fromJson(e.getResponseBodyAsString(), Object.class);
      return ResponseEntity.status(e.getStatusCode()).contentType(MediaType.APPLICATION_JSON).body(e.getResponseBodyAsString());
    }
  }

  public ResponseEntity<?> callObject(String path) throws Exception {
    return callObject(path,Object.class);
  }
}