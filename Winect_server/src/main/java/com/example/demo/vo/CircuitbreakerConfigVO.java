package com.example.demo.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CircuitbreakerConfigVO {
	    private String slidingWindowType;
	    private int slowCallDurationThreshold;
	    private int slowRateThreshold;
	    private int failureRateThreshold;
	    private int waitDurationInOpenState;
	    private int permittedNumberOfCallsInHalfOpenState;
	    private int slidingWindowSize;
	    private int minimumNumberOfCalls;
	    private String fallbackMessage;
}
