package com.example.demo.vo;


import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseVO {
		private HttpStatus status;
		private String message;
		private Object data;
}
