package com.example.demo.gateway;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.example.demo.vo.gateway.ServerVO;

@Component
public class ServersMap {
  private Map<String,ServerVO> serversMap = new HashMap<>();
    
  //단건 조회
  public ServerVO getServersMap(String serverId){
    return this.serversMap.get(serverId);
  }

  //목록 조회
  public List<ServerVO> getServersList(){
    List<ServerVO> serversList = new ArrayList<>();
    for(String s : this.serversMap.keySet()){
      serversList.add(this.serversMap.get(s));
    }
    return serversList;
  }
  
  //추가
  public void putServer(String serverId, ServerVO server){
    this.serversMap.put(serverId,server);
  }
  
  //삭제
  public void removeServers(String serverId){
    this.serversMap.remove(serverId);
  }

  //등록된 서버 확인
  public boolean isExistServer(String serverId){
    return this.serversMap.containsKey(serverId);
  }

  //모두 삭제
  public void clearServers(){
    this.serversMap.clear();
  }

  //status가 fail인 서버 목록
  public List<ServerVO> findFailServers(){
    List<ServerVO> serversList = new ArrayList<>();
    for(String key : this.serversMap.keySet()){
    	ServerVO server = getServersMap(key);
      if(!"up".equals(server.getStatus())){
        serversList.add(server);
      }
    }
    return serversList;
  }

  //status가 up인 서버 하나
  public String findOneServer(String target) {
	String serverIp = null;

    for(ServerVO server : this.serversMap.values()) {
      if ( server.getProduct().equals(target) && server.getStatus().equals("up") ) {
        serverIp = server.getServerIP() + ":" + server.getAdminPort();
        break;
      }
    }

    return serverIp;
  }
  
}
