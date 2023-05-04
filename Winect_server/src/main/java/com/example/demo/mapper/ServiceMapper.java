package com.example.demo.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.vo.ServiceListVO;
import com.example.demo.vo.ServiceVO;

@Mapper
public interface ServiceMapper {
	int serviceInsert(ServiceVO vo) throws Exception;//서비스 등록
	int serviceUpdate(ServiceVO vo) throws Exception;//서비스 수정
	int serviceCircuitInsert(ServiceVO vo) throws Exception;//서비스 + 서킷브레이커 등록
	ServiceVO serviceResponse(String serviceId) throws Exception;//서비스가 라우트 내에서 사용되고 있는지???
	ServiceVO serviceUnused(String serviceId) throws Exception;//서비스가 라우트 내에서 사용되고 있는지???
	ServiceVO serviceInfo(Map<String, Object> dataMap) throws Exception;//서비스 조회-----
	List<ServiceListVO> serviceList(Map<String, Object> pageMap) throws Exception;//서비스 페이징처리
	List<ServiceListVO> serviceSearchList(Map<String, Object> dataMap) throws Exception;//서비스 검색조회(페이징처리)
	int serviceTotalCnt(Map<String, Object> dataMap) throws Exception;//서비스 전체 행 개수
	ServiceVO serviceCircuitInfo(String serviceId) throws Exception;//서비스 + 서킷브레이커 조회-----
	int serviceDelete(Map<String, Object> dataMap) throws Exception;//서비스 삭제
}
