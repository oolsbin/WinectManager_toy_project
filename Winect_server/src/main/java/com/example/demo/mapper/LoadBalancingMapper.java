package com.example.demo.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.vo.LoadBalancingVO;

@Mapper
public interface LoadBalancingMapper {
	int LoadInsert(LoadBalancingVO vo) throws Exception;//로드벨런싱 등록
}
