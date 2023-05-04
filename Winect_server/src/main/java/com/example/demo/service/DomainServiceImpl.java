package com.example.demo.service;

import java.util.Date;
import java.util.List;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.demo.mapper.DomainMapper;
import com.example.demo.util.RestTemplateUtil;
import com.example.demo.vo.DomainVO;
import com.example.demo.vo.GatewayDomainVO;
import com.example.demo.vo.ResponseVO;

@Service
public class DomainServiceImpl implements DomainService{

	@Autowired
	private DomainMapper domainMapper;


	@Override
	public int domainInsert(DomainVO vo)throws Exception {
		return domainMapper.domainInsert(vo);
	}
	
//	@Override
//	public int domainInsert(DomainVO vo) throws Exception {
//		Date date = new Date();
//		vo.setCreateTime(date);
//		vo.setUpdateTime(date);
//		vo.setUserId("29f4c20a-24d7-47bf-81eb-6da7d6a21e4c");
//		DomainVO domainInfo = domainMapper.domainInfo(vo.getDomainId());
//		if (!(domainInfo == null)) {
//			throw new RuntimeException("이미 사용중인 도메인명 입니다.");
//		} else {
//			//web도메인 저장
//			return domainMapper.domainInsert(vo);
//		}
//	}
//
//	@Override
//	public ResponseVO domainResponse(DomainVO vo) throws Exception {
//		int result = domainInsert(vo);
//		ResponseVO responseVO = new ResponseVO();
//		if (result > 0) {
//			DomainVO insertedDomain = domainMapper.domainInfo(vo.getDomainId());
//			responseVO.setStatus(HttpStatus.OK);
//			responseVO.setMessage("저장되었습니다.");
//			responseVO.setData(insertedDomain);
//		} else {
//			responseVO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
//			responseVO.setMessage("저장 실패");
//		}
//		return responseVO;
//	}

	
	
	@Override
	public DomainVO domainInfo(String domainId) throws Exception {
		return domainMapper.domainInfo(domainId);
	}

	@Override
	public List<DomainVO> domainTotal(Map<String, Object> dataMap) throws Exception {
		return domainMapper.domainTotal(dataMap);
	}

	@Override
	public DomainVO domainUpdate(DomainVO vo) throws Exception {
		
		Date date = new Date();
		vo.setUpdateTime(date);
		
		domainMapper.domainUpdate(vo);
		
		DomainVO updatedDomain = this.domainInfo(vo.getDomainId());
		return updatedDomain;
	}
	
	@Override
	public int domainDelete(String domainId) throws Exception {
		return domainMapper.domainDelete(domainId);
	}
	
}
