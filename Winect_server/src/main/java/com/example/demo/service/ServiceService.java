package com.example.demo.service;

import java.util.List;
import java.util.Map;

import com.example.demo.vo.ResponseVO;
import com.example.demo.vo.ServiceListVO;
import com.example.demo.vo.ServiceVO;

public interface ServiceService {
	ServiceVO serviceInsert(ServiceVO vo) throws Exception;
	ServiceVO serviceUpdate(ServiceVO vo) throws Exception;
	int serviceCircuitInsert(ServiceVO vo) throws Exception;
	ServiceVO serviceResponse(String serviceId) throws Exception;
	ServiceVO serviceUnused(String serviceId) throws Exception;
	ServiceVO serviceInfo(Map<String, Object> dataMap) throws Exception;
	List<ServiceListVO> serviceList(Map<String, Object> dataMap) throws Exception;
	List<ServiceListVO> serviceSearchList(Map<String, Object> dataMap) throws Exception;//서비스 검색조회(페이징처리)
	int serviceTotalCnt(Map<String, Object> dataMap) throws Exception;//서비스 totalCount
	ServiceVO serviceCircuitInfo(String serviceId) throws Exception;
	int serviceDelete(Map<String, Object> dataMap) throws Exception;//서비스 삭제
}
