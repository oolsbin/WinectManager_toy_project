package com.example.demo.service;

import java.util.List;
import java.util.Map;

import com.example.demo.vo.DomainVO;
import com.example.demo.vo.GatewayDomainVO;
import com.example.demo.vo.ResponseVO;
import com.example.demo.vo.ServiceVO;

public interface DomainService {
	int domainInsert(DomainVO vo) throws Exception;//도메인 저장
//	ResponseVO domainResponse(DomainVO vo) throws Exception;
	DomainVO domainInfo(String pkId) throws Exception;//등록된 도메인 정보조회
	List<DomainVO> domainTotal(Map<String, Object> dataMap) throws Exception;//도메인 전체조회
	DomainVO domainUpdate(DomainVO vo) throws Exception;//도메인 수정
	int domainDelete(String domainId) throws Exception;//도메인 삭제
}
