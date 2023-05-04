package com.example.demo.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.mapper.RouteMapper;
import com.example.demo.vo.RouteVO;

@Service
public class RouteServiceImpl implements RouteSevice{

	@Autowired
	private RouteMapper routeMapper;

	@Override
	public int routeInsert(RouteVO vo) throws Exception {
		return routeMapper.routeInsert(vo);
	}

	@Override
	public RouteVO routeInfo(RouteVO vo) throws Exception {
		return routeMapper.routeInfo(vo);
	}

	@Override
	public List<RouteVO> routeList(Map<String, Object> pageMap) throws Exception {
		return routeMapper.routeList(pageMap);
	}

	@Override
	public int routeTotalCnt(Map<String, Object> pageMap) throws Exception {
		return routeMapper.routeTotalCnt(pageMap);
	}

	@Override
	@Transactional
	public RouteVO routeUpdate(RouteVO vo) throws Exception {
		
		routeMapper.routeUpdate(vo);
		RouteVO updatedRoute = this.routeInfo(vo);
		
		return updatedRoute;
	}

	@Override
	public int routeDelete(Map<String, Object> dataMap) throws Exception {
		return routeMapper.routeDelete(dataMap);
	}

	@Override
	public RouteVO routePath(Map<String, Object> dataMap) throws Exception {
		return routeMapper.routePath(dataMap);
	}
	
	
}
