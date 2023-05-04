package com.example.demo.service;

import com.example.demo.vo.LoadBalancingVO;

public interface LoadBalancingService {
	int LoadInsert(LoadBalancingVO vo) throws Exception;//로드벨런싱 등록
}
