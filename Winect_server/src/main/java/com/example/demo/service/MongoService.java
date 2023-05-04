package com.example.demo.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.stereotype.Service;

@Service
public class MongoService {
    private final String STA_COLLECTION = "statistics_log";

    @Autowired
    MongoTemplate mongoTemplate;

    /**
     * 검색 조건을 받아 List 결과값(다건)
     */
    public List<Object> getList(List<AggregationOperation> conditionList) {
        Aggregation aggregation = Aggregation.newAggregation(conditionList);
        List<Object> rsltObj = mongoTemplate.aggregate(aggregation, STA_COLLECTION, Object.class).getMappedResults();

        return rsltObj;
    }

    /**
     * 검색 조건을 받아 결과값을 Mapper를 이용한 List로 변환
     */
    public List<HashMap<String, Object>> getConvertMapperToList(List<AggregationOperation> conditionList) {
        List<Object> rsltObj = getList(conditionList);
        List<HashMap<String, Object>> resultMap = new ArrayList<HashMap<String, Object>>();

        if(rsltObj != null){
            ObjectMapper mapper = new ObjectMapper();
            TypeReference<List<HashMap<String, Object>>> typeRef = new TypeReference<List<HashMap<String, Object>>>() {};

            resultMap =  mapper.convertValue(rsltObj, typeRef);
        }

        return resultMap;
    }

    /**
     * 검색 조건을 받아 Object 결과값(단건)
     */
    public Object getData(List<AggregationOperation> conditionList) {
        Aggregation aggregation = Aggregation.newAggregation(conditionList);
        Object rsltObj = mongoTemplate.aggregate(aggregation, STA_COLLECTION, Object.class).getUniqueMappedResult();
        System.out.println(rsltObj);
        return rsltObj;
    }

    /**
     * 검색 조건을 받아 결과값을 Mapper를 이용한 Map로 변환
     */
    public HashMap<String, Object> getConvertMapperToMap(List<AggregationOperation> conditionList) {
        Aggregation aggregation = Aggregation.newAggregation(conditionList);
        Object rsltObj = mongoTemplate.aggregate(aggregation, STA_COLLECTION, Object.class).getUniqueMappedResult();
        HashMap<String, Object> resultMap = new HashMap<String, Object>();

        if(rsltObj != null){
            ObjectMapper mapper = new ObjectMapper();
            TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {};

            resultMap = mapper.convertValue(rsltObj, typeRef);
        }

        return resultMap;
    }
}
