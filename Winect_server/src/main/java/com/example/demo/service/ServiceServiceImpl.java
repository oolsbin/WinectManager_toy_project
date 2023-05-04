
package com.example.demo.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.mapper.ServiceMapper;
import com.example.demo.vo.ServiceListVO;
import com.example.demo.vo.ServiceVO;

@Service
public class ServiceServiceImpl implements ServiceService{

	@Autowired
	private ServiceMapper serviceMapper;

	
	@Override
	@Transactional
	public ServiceVO serviceInsert(ServiceVO vo) throws Exception {
		
		String url = vo.getTargetUrl();
		
		int index = url.indexOf(":"); // ":" 문자의 위치 찾기
		if (index == -1) {
			vo.setProtocol("http"); // 기본값으로 설정
		} else {
			String method = url.substring(0, index);
			vo.setProtocol(method);
		}
		
		//route에서 service를 사용하고 있는지 확인
		if (this.serviceUnused(vo.getServiceId())==null) {
			vo.setUnused(true);
		}else {
			vo.setUnused(false);
		}
		
		Date date = new Date();
		vo.setCreateTime(date);
		vo.setUpdateTime(date);
		System.out.println(vo);
		//서비스 생성
		serviceMapper.serviceInsert(vo);
		//서비스 조회
		ServiceVO insertedService = this.serviceResponse(vo.getServiceId());
		
		return insertedService;
	}
	
	@Override
	@Transactional
	public ServiceVO serviceUpdate(ServiceVO vo) throws Exception {
		
		String url = vo.getTargetUrl();
		
		int index = url.indexOf(":"); // ":" 문자의 위치 찾기
		if (index == -1) {
			vo.setProtocol("http"); // 기본값으로 설정
		} else {
			String method = url.substring(0, index);
			vo.setProtocol(method);
		}
		
		
		if (this.serviceUnused(vo.getServiceId())==null) {
			vo.setUnused(true);
		}else {
			vo.setUnused(false);
		}
		
		Date date = new Date();
		vo.setUpdateTime(date);

		//서비스 생성
		serviceMapper.serviceUpdate(vo);
		//서비스 조회
		ServiceVO updatedService = this.serviceResponse(vo.getServiceId());
		
		return updatedService;
	}
	
	@Override
	public int serviceCircuitInsert(ServiceVO vo) throws Exception {
		return serviceMapper.serviceCircuitInsert(vo);
	}

	@Override
	public ServiceVO serviceUnused(String serviceId) throws Exception {
		return serviceMapper.serviceUnused(serviceId);
	}

	@Override
	public ServiceVO serviceInfo(Map<String, Object> dataMap) throws Exception {
		return serviceMapper.serviceInfo(dataMap);
	}
	
	@Override
	public List<ServiceListVO> serviceList(Map<String, Object> pageMap) throws Exception {
		return serviceMapper.serviceList(pageMap);
	}
	
	@Override
	public int serviceTotalCnt(Map<String, Object> dataMap) throws Exception {
		return serviceMapper.serviceTotalCnt(dataMap);
	}
	
	@Override
	public List<ServiceListVO> serviceSearchList(Map<String, Object> dataMap) throws Exception {
		return serviceMapper.serviceSearchList(dataMap);
	}
	
	@Override
	public int serviceDelete(Map<String, Object> dataMap) throws Exception {
		return serviceMapper.serviceDelete(dataMap);
	}
	
	@Override
	public ServiceVO serviceResponse(String serviceId) throws Exception {
		return serviceMapper.serviceResponse(serviceId);
	}
	
	///////////////////////////////////////////////////////////
	
	@Override
	public ServiceVO serviceCircuitInfo(String serviceId) throws Exception {
		return serviceMapper.serviceCircuitInfo(serviceId);
	}

	



	
}
