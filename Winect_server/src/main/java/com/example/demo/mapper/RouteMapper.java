package com.example.demo.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.vo.RouteVO;

@Mapper
public interface RouteMapper {
	int routeInsert(RouteVO vo) throws Exception;//라우트정보 저장
	int routeUpdate(RouteVO vo) throws Exception;//라우트 수정
	RouteVO routeInfo(RouteVO vo) throws Exception;//등록된 라우트 정보응답
	RouteVO routePath(Map<String, Object> dataMap) throws Exception;//라우트 path로 검색
	List<RouteVO> routeList(Map<String, Object> pageMap) throws Exception;//라우트 전제조회
	int routeTotalCnt(Map<String, Object> pageMap) throws Exception;//라우트 totalCount
	int routeDelete(Map<String, Object> dataMap) throws Exception;//라우트 삭제
}
