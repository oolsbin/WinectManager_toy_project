package com.example.demo.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.vo.DomainVO;
import com.example.demo.vo.ServiceVO;

@Mapper
public interface DomainMapper {
	int domainInsert(DomainVO vo) throws Exception;//도메인 저장
	DomainVO domainInfo(String pkId) throws Exception;//등록된 도메인 정보조회
	List<DomainVO> domainTotal(Map<String, Object> dataMap) throws Exception;//도메인 전체조회
	int domainUpdate(DomainVO vo) throws Exception;//도메인 수정
	int domainDelete(String domainId) throws Exception;//도메인 삭제
}
