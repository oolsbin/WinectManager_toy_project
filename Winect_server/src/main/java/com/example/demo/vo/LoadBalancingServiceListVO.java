package com.example.demo.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoadBalancingServiceListVO {
	private String serviceId;
	private String serviceName;
	private String weight;
}
