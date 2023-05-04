package com.example.demo.vo;

import java.util.Date;
import java.util.List;

import org.postgresql.util.PGobject;

import com.google.gson.Gson;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoadBalancingVO {
	private String groupId;
	private String groupName;
	private String type;
	private String domainId;
	private Date createTime;
	private Date updateTime;;
	
	private LoadBalancingServiceListVO serviceList;
	
	public void setServiceList(Object LoadBalancingServiceListVO) {
		if(LoadBalancingServiceListVO instanceof PGobject) {
			this.serviceList = new Gson().fromJson(((PGobject) LoadBalancingServiceListVO).getValue(), LoadBalancingServiceListVO.class);
		}else {
			this.serviceList = new Gson().fromJson(new Gson().toJson(LoadBalancingServiceListVO), LoadBalancingServiceListVO.class);
		}
	}
}
