package com.example.demo.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.example.demo.exception.BadRequestCustomException;
import com.example.demo.exception.EErrorCode;
import com.example.demo.service.MongoService;
import com.example.demo.vo.dashboard.ApiResponse;
import com.example.demo.vo.dashboard.StaSrchOpt;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.BasicDBObject;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AccumulatorOperators;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.data.mongodb.core.aggregation.BooleanOperators;
import org.springframework.data.mongodb.core.aggregation.ComparisonOperators;
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 통계 관련 Controller
 * 
 * @author rhwon
 * @version 1.0, 2021.09.28 최초작성
 * @see None
 */

@Api(tags = "대시보드_통계")
@RestController
@RequestMapping("{domainId}")
@Slf4j
public class StatisticsController {
    @Autowired
    MongoService mongoService;

    @Autowired
    MongoTemplate mongoTemplate;
    
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
  
    /**
     * api1 : 데이터 요청 전송량
     */
    @ApiOperation(value = "데이터 요청 전송량", notes = "해당 도메인 내에서 사용된 API가 요청된 전송량을 집계하는 기능")
    @GetMapping("/data_request_transmission")
    public ResponseEntity<?> dataRequestTransmission(
    		@PathVariable(name = "domainId", required = false) String domainId, StaSrchOpt opt) {
        opt.setDomainId(domainId);
        //집계함수
        List<AggregationOperation> conditionList = setSrchCondition(opt);

        //전체 그룹//gwReqTotalLength필드를 합산 = length
        conditionList.add(Aggregation.group().sum("gwReqTotalLength").as("length"));

        //필드 추가//unit필드를 BYTE값으로 추가
        conditionList.add(Aggregation.addFields().addFieldWithValue("unit", "BYTE").build());

        return ResponseEntity.ok(ApiResponse.builder().data(mongoService.getData(conditionList)).build());
    }

    /**
     * api2 : 데이터 응답 전송량
     */
    @ApiOperation(value = "데이터 응답 전송량", notes = "해당 도메인 내에서 사용된 API가 응답된 전송량을 집계하는 기능")
    @GetMapping("/data_response_transmission")
    public ResponseEntity<?> api2(@PathVariable(name = "domainId", required = false) String domainId, StaSrchOpt opt) {
        opt.setDomainId(domainId);
        List<AggregationOperation> conditionList = setSrchCondition(opt);

        //전체 그룹
        conditionList.add(Aggregation.group().sum("gwResTotalLength").as("length"));

        //필드 추가
        conditionList.add(Aggregation.addFields().addFieldWithValue("unit", "BYTE").build());

        return ResponseEntity.ok(ApiResponse.builder().data(mongoService.getData(conditionList)).build());
    }
    
    /**
     * api3 : 데이터 요청 + 응답 전송량(전체)
     */
    @ApiOperation(value = "데이터 요청과 응답 전송에 대한 총 합계량", notes = "해당 도메인 내에서 사용된 API가 요청되고 응답된 전송량을 모두 집계하는 기능")
    @GetMapping("/data_total_transmission")
    public ResponseEntity<?> api3(@PathVariable(name = "domainId", required = false) String domainId, StaSrchOpt opt) {
        opt.setDomainId(domainId);
        List<AggregationOperation> conditionList = setSrchCondition(opt);

        //전체 그룹
        conditionList.add(
            Aggregation.group()
            .sum("gwReqTotalLength").as("reqLength")
            .sum("gwResTotalLength").as("resLength")
        );

        //필드 추가
        conditionList.add(Aggregation.addFields().addFieldWithValue("unit", "BYTE").build());

        //더하기
        conditionList.add(Aggregation.project("unit").andExpression("reqLength + resLength").as("length"));

        return ResponseEntity.ok(ApiResponse.builder().data(mongoService.getData(conditionList)).build());
    }

    /**
     * api4 : API별 데이터 요청 전송량 Top 5 ~ 10
     */
    @GetMapping("/data_request_transmission_per_top_api")
    @ApiOperation(value = "데이터 요청 전송량 상위 1~5위 조회", notes = "해당 도메인 내에서 사용된 API가 요청된 전송량을 집계하여 상위 1위부터 5위까지 조회하는 기능")
    public ResponseEntity<?> api4(@PathVariable(name = "domainId", required = false) String domainId, StaSrchOpt opt) {
        opt.setDomainId(domainId);
        List<AggregationOperation> conditionList = setSrchCondition(opt);

        //전체 그룹
        conditionList.add(Aggregation.group("routeId", "routeName").sum("gwReqTotalLength").as("length"));

        //필드 추가
        conditionList.add(Aggregation.addFields().addFieldWithValue("unit", "BYTE").build());

        //필드 정의
        conditionList.add(
            Aggregation.project("length", "unit")
            .and("_id.routeId").as("routeId")
            .and("_id.routeName").as("routeName")
        );

        //_id제거
        conditionList.add(Aggregation.project().andExclude("_id"));
        
        //정렬
        conditionList.add(Aggregation.sort(Sort.Direction.DESC, "length"));

        //TOP Limit
        conditionList.add(Aggregation.limit(opt.getTopRange()));

        return ResponseEntity.ok(ApiResponse.builder().data(mongoService.getList(conditionList)).build());
    }

    /**
     * api5 : API별 데이터 응답 전송량 Top 5 ~ 10
     */
    @ApiOperation(value = "데이터 응답 전송량 상위 1~5위 조회", notes = "해당 도메인 내에서 사용된 API가 응답된 전송량을 집계하여 상위 1위부터 5위까지 조회하는 기능")
    @GetMapping("/data_reponse_transmission_per_top_api")
    public ResponseEntity<?> api5(@PathVariable(name = "domainId", required = false) String domainId, StaSrchOpt opt) {
        opt.setDomainId(domainId);
        List<AggregationOperation> conditionList = setSrchCondition(opt);

        //전체 그룹
        conditionList.add(Aggregation.group("routeId", "routeName").sum("gwResTotalLength").as("length"));

        //필드 추가
        conditionList.add(Aggregation.addFields().addFieldWithValue("unit", "BYTE").build());

        //필드 정의
        conditionList.add(
            Aggregation.project("length", "unit")
            .and("_id.routeId").as("routeId")
            .and("_id.routeName").as("routeName")
        );

        //_id제거
        conditionList.add(Aggregation.project().andExclude("_id"));
        
        //정렬
        conditionList.add(Aggregation.sort(Sort.Direction.DESC, "length"));

        //TOP Limit
        conditionList.add(Aggregation.limit(opt.getTopRange()));

        return ResponseEntity.ok(ApiResponse.builder().data(mongoService.getList(conditionList)).build());
    }

    /**
     * api6 : API별 데이터 전체(요청+응답) 전송량 Top 5 ~ 10
     */
    @ApiOperation(value = "데이터 요청 및 응답 전송량 상위 1~5위 조회", notes = "해당 도메인 내에서 사용된 API가 요청하고 응답된 전송량을 집계하여 상위 1위부터 5위까지 조회하는 기능")
    @GetMapping("/data_total_transmission_per_top_api")
    public ResponseEntity<?> api6(@PathVariable(name = "domainId", required = false) String domainId, StaSrchOpt opt) {
        opt.setDomainId(domainId);
        List<AggregationOperation> conditionList = setSrchCondition(opt);

        //전체 그룹
        conditionList.add(
            Aggregation.group("routeId", "routeName")
                .sum("gwReqTotalLength").as("reqLength")
                .sum("gwResTotalLength").as("resLength")
        );

        //필드 추가
        conditionList.add(Aggregation.addFields().addFieldWithValue("unit", "BYTE").build());

        //필드 정의
        conditionList.add(
            Aggregation.project("unit")
                .andExpression("reqLength + resLength").as("length")
                .and("_id.routeId").as("routeId")
                .and("_id.routeName").as("routeName")
        );

        //_id제거
        conditionList.add(Aggregation.project().andExclude("_id"));
        
        //정렬
        conditionList.add(Aggregation.sort(Sort.Direction.DESC, "length"));

        //TOP Limit
        conditionList.add(Aggregation.limit(opt.getTopRange()));

        return ResponseEntity.ok(ApiResponse.builder().data(mongoService.getList(conditionList)).build());
    }

    /**
     * api7 : API 호출 건수
     */
    @ApiOperation(value = "API 호출 건수", notes = "API가 호출된 건수를 조회하는 기능")
    @GetMapping("/total_number_of_api_calls")
    public ResponseEntity<?> api7(@PathVariable(name = "domainId", required = false) String domainId, StaSrchOpt opt) {
        opt.setDomainId(domainId);
        List<AggregationOperation> conditionList = setSrchCondition(opt);

        //전체 그룹
        conditionList.add(Aggregation.group().count().as("count"));

        return ResponseEntity.ok(ApiResponse.builder().data(mongoService.getData(conditionList)).build());
    }

    /**
     * api8 : API별 호출 건수 Top 5 ~ 10
     */
    @ApiOperation(value = "API별 호출 건수 상위 1~5위 조회", notes = "해당 도메인 내에서 사용된 API가 호출된 횟수를 집계하여 상위 1위부터 5위까지 조회하는 기능")
    @GetMapping("/number_of_api_calls_per_top_api")
    public ResponseEntity<?> api8(@PathVariable(name = "domainId", required = false) String domainId, StaSrchOpt opt) {
        opt.setDomainId(domainId);
        List<AggregationOperation> conditionList = setSrchCondition(opt);

        //전체 그룹
        conditionList.add(Aggregation.group("routeId", "routeName").count().as("count"));

        //필드 정의
        conditionList.add(
            Aggregation.project("count")
            .and("_id.routeId").as("routeId")
            .and("_id.routeName").as("routeName")
        );

        //_id제거
        conditionList.add(Aggregation.project().andExclude("_id"));

        //정렬
        conditionList.add(Aggregation.sort(Sort.Direction.DESC, "count"));

        //TOP Limit
        conditionList.add(Aggregation.limit(opt.getTopRange()));

        return ResponseEntity.ok(ApiResponse.builder().data(mongoService.getList(conditionList)).build());
    }

    /**
     * api9 : API 오류율
     */
    @ApiOperation(value = "API 오류 비율", notes = "해당 도메인 내에서 사용된 API를 호출했을 때 오류가 발생한 비율을 조회하는 기능")
    @GetMapping("/total_number_of_api_errors")
    public ResponseEntity<?> api9(@PathVariable(name = "domainId", required = false) String domainId, StaSrchOpt opt) {
        opt.setDomainId(domainId);
        List<AggregationOperation> conditionList = setSrchCondition(opt);

        //전체 그룹
        conditionList.add(Aggregation.group("gwProcessFlag", "mdProcessFlag").count().as("count"));

        //그룹
        conditionList.add(
            Aggregation.group()
                .sum("count").as("totalCount")
                .push(
                    new BasicDBObject("gwProcessFlag", "$_id.gwProcessFlag")
                        .append("mdProcessFlag", "$_id.mdProcessFlag")
                        .append("count", "$count")
                ).as("merged")
        );

        //배열 필터
        conditionList.add(
            Aggregation.project("totalCount")
                .and(
                    ArrayOperators.Filter.filter("merged")
                    .as("type")
                    .by(
                        BooleanOperators.Or.or(
                              ComparisonOperators.Ne.valueOf("type.mdProcessFlag").notEqualToValue("S")
                            , ComparisonOperators.Ne.valueOf("type.gwProcessFlag").notEqualToValue("S")
                        )
                    )
                ).as("merged")
        );

        //에러건 합계
        conditionList.add(
            Aggregation.project("totalCount")
            .and(
                AccumulatorOperators.Sum.sumOf("merged.count")
            ).as("count")
        );

        //비율 계산
        conditionList.add(
            Aggregation.project("totalCount", "count")
                .andExpression("round(count / totalCount * 100, 2)").as("rate")
        );

        return ResponseEntity.ok(ApiResponse.builder().data(mongoService.getData(conditionList)).build());
    }

    /**
     * api10 : API별 오류율 Top 5 ~ 10
     */
    @ApiOperation(value = "API 오류율이 상위 1~5위인 API를 조회", notes = "해당 도메인 내에서 사용된 API를 호출했을 때 오류가 발생한 비율이 상위 1위에서 5위까지의 API를 조회하는 기능")
    @GetMapping("/number_of_api_errors_per_top_api")
    public ResponseEntity<?> api10(@PathVariable(name = "domainId", required = false) String domainId, StaSrchOpt opt) {
        opt.setDomainId(domainId);
        List<AggregationOperation> conditionList = setSrchCondition(opt);

        //전체 그룹
        conditionList.add(Aggregation.group("routeId", "routeName", "gwProcessFlag", "mdProcessFlag").count().as("count"));

        //그룹2
        conditionList.add(
            Aggregation.group("_id.routeId", "_id.routeName")
            .sum("count").as("sum")
            .push(
                new BasicDBObject("count", "$count")
                    .append("gwProcessFlag", "$_id.gwProcessFlag")
                    .append("mdProcessFlag", "$_id.mdProcessFlag")
            ).as("typeCount")
        );

        //필드 정의
        conditionList.add(
            Aggregation.project("sum", "typeCount")
            .and("routeId").as("routeId")
            .and("routeName").as("routeName")
            .andExclude("_id")
        );

        //배열 필터
        conditionList.add(
            Aggregation.project("routeId", "routeName", "sum")
                .and(
                    ArrayOperators.Filter.filter("typeCount")
                    .as("type")
                    .by(
                        BooleanOperators.Or.or(
                              ComparisonOperators.Ne.valueOf("type.mdProcessFlag").notEqualToValue("S")
                            , ComparisonOperators.Ne.valueOf("type.gwProcessFlag").notEqualToValue("S")
                        )
                        // ComparisonOperators
                        //     .valueOf("type.mdProcessFlag")
                        //     .notEqualToValue("S")
                        //     .valueOf("type.gwProcessFlag")
                        //     .notEqualToValue("S")
                    )
                ).as("typeCount")
        );

        conditionList.add(
            Aggregation.project("routeId", "routeName")
            .and(
                AccumulatorOperators.Sum.sumOf("typeCount.count")
            ).as("errorCount")
            .and("sum").as("totalCount")
        );
        
        // //필드 정의
        // ProjectionOperation project = Aggregation.project("routeId", "routeName")
        //     .and(
        //         ArrayOperators.ArrayToObject.arrayValueOfToObject(
        //             org.springframework.data.mongodb.core.aggregation.VariableOperators.Map.itemsOf("typeCount")
        //             .as("type")
        //             .andApply(agg -> new Document("k", "count").append("v", "$$type.count"))
        //         )   
        //     ).as("errorCount")
        //     .and("sum").as("totalCount");
        // conditionList.add(project);

        //필드 정의
        conditionList.add( 
            Aggregation.project("routeId", "routeName", "totalCount")
            .and("errorCount").as("count")
            .andExpression("errorCount / totalCount * 100").as("rate")
        );
        
        //필드 정의
        conditionList.add(
            Aggregation.project("routeId", "routeName", "totalCount", "count")
            .andExpression("round(rate, 2)").as("rate")
        );
        
        //정렬
        conditionList.add(Aggregation.sort(Sort.Direction.DESC, "rate"));

        //TOP Limit
        conditionList.add(Aggregation.limit(opt.getTopRange()));

        return ResponseEntity.ok(ApiResponse.builder().data(mongoService.getList(conditionList)).build());
    }

    /**
     * api11 : API 성공 + 에러율
     */
    @ApiOperation(value = "API 성공율과 에러율 조회", notes = "해당 도메인 내에서 사용된 API의 성공율과 에러율을 조회하는 기능")
    @GetMapping("/total_api_success_rate_and_error_rate")
    public ResponseEntity<?> api11(@PathVariable(name = "domainId", required = false) String domainId, StaSrchOpt opt) {
        opt.setDomainId(domainId);
        List<AggregationOperation> conditionList = setSrchCondition(opt);

        //전체 그룹
        conditionList.add(Aggregation.group("gwProcessFlag", "mdProcessFlag").count().as("count"));

        //성공, 실패 status 필드 정의
        conditionList.add(
            Aggregation.project("count")
                .and(
                    ConditionalOperators.when(
                        BooleanOperators.And.and(
                              ComparisonOperators.Eq.valueOf("_id.gwProcessFlag").equalToValue("S")
                            , ComparisonOperators.Eq.valueOf("_id.mdProcessFlag").equalToValue("S")
                        )
                    )
                    .then("S")
                    .otherwise("E"))
                .as("status")
        );
        
        //성공, 실패 status 합계
        conditionList.add(Aggregation.group("status").sum("count").as("count"));

        //totalCount 합계
        conditionList.add(
            Aggregation.group()
                .sum("count").as("totalCount")
                .push(
                    new BasicDBObject("status", "$_id")
                    .append("count", "$count")
                ).as("merged")
        );

        //unwind로 쪼개기
        conditionList.add(Aggregation.unwind("merged"));

        //비율 계산
        conditionList.add(
            Aggregation.project("totalCount")
                .and("merged.status").as("status")
                .and("merged.count").as("count")
                .andExpression("round(merged.count / totalCount * 100, 2)").as("rate") 
        );

        List<HashMap<String, Object>> resultList = mongoService.getConvertMapperToList(conditionList);
        HashMap<String, Object> resultMap = new HashMap<String, Object>();

        if(resultList != null && resultList.size() > 0){
            for (HashMap<String,Object> map : resultList) {
                if("S".equals(map.get("status"))){
                    resultMap.put("successRate", map);
                } else {
                    resultMap.put("errorRate", map);
                }
            }
        }

        return ResponseEntity.ok(ApiResponse.builder().data(resultMap).build());
    }

    /**
     * api12 : 타겟 시스템 API 성공 + 에러율
     */
    @ApiOperation(value = "타겟 시스템 API의 성공율과 에러율 조회", notes = "해당 도메인 내에서 사용된 타겟 시스템 API의 성공율과 에러율을 조회하는 기능")
    @GetMapping("/target_system_total_api_success_rate_and_error_rate")
    public ResponseEntity<?> api12(@PathVariable(name = "domainId", required = false) String domainId, StaSrchOpt opt) {
        opt.setDomainId(domainId);
        List<AggregationOperation> conditionList = setSrchCondition(opt);

        //전체 그룹
        conditionList.add(Aggregation.group("mdResInFlag").count().as("count"));

        //성공, 실패 status 필드 정의
        conditionList.add(
            Aggregation.project("count")
                .and(
                    ConditionalOperators.when(
                        BooleanOperators.And.and(
                              ComparisonOperators.Eq.valueOf("_id").equalToValue("S")
                        )
                    )
                    .then("S")
                    .otherwise("E"))
                .as("status")
        );
        
        //성공, 실패 status 합계
        conditionList.add(Aggregation.group("status").sum("count").as("count"));

        //totalCount 합계
        conditionList.add(
            Aggregation.group()
                .sum("count").as("totalCount")
                .push(
                    new BasicDBObject("status", "$_id")
                    .append("count", "$count")
                ).as("merged")
        );

        //unwind로 쪼개기
        conditionList.add(Aggregation.unwind("merged"));

        //비율 계산
        conditionList.add(
            Aggregation.project("totalCount")
                .and("merged.status").as("status")
                .and("merged.count").as("count")
                .andExpression("round(merged.count / totalCount * 100, 2)").as("rate") 
        );

        List<HashMap<String, Object>> resultList = mongoService.getConvertMapperToList(conditionList);
        HashMap<String, Object> resultMap = new HashMap<String, Object>();

        if(resultList != null && resultList.size() > 0){
            for (HashMap<String,Object> map : resultList) {
                if("S".equals(map.get("status"))){
                    resultMap.put("successRate", map);
                } else {
                    resultMap.put("errorRate", map);
                }
            }
        }

        return ResponseEntity.ok(ApiResponse.builder().data(resultMap).build());
    }

    /**
     * api13 : API 전체 평균 응답시간
     */
    
    @ApiOperation(value = "API 전체 평균 응답시간", notes = "해당 도메인 내에서 사용된 API 전체 평균 응답시간을 조회하는 기능")
    @GetMapping("/total_api_average_response_time")
    public ResponseEntity<?> api13(@PathVariable(name = "domainId", required = false) String domainId, StaSrchOpt opt) {
        opt.setDomainId(domainId);
        List<AggregationOperation> conditionList = setSrchCondition(opt);

        //전체 그룹
        conditionList.add(Aggregation.group().avg("totalLatency").as("avgValue"));

        //필드 추가
        conditionList.add(Aggregation.addFields().addFieldWithValue("unit", "ms").build());

        //필드 라운드 처리
        conditionList.add(
            Aggregation.project("unit")
            .andExpression("round(avgValue, 2)").as("averageResponseTime")
        );

        return ResponseEntity.ok(ApiResponse.builder().data(mongoService.getData(conditionList)).build());
    }

    /**
     * api14 : API별 평균 응답시간 Top 5 ~ 10
     */
    @ApiOperation(value = "API 전체 평균 응답시간이 짧은 상위 1위~5위 조회", notes = "해당 도메인 내에서 사용된 API 전체 평균 응답시간이 짧은 순서대로 상위 1위부터 5위까지 조회하는 기능")
    @GetMapping("/average_response_time_per_top_api")
    public ResponseEntity<?> api14(@PathVariable(name = "domainId", required = false) String domainId, StaSrchOpt opt) {
        opt.setDomainId(domainId);
        List<AggregationOperation> conditionList = setSrchCondition(opt);

        //전체 그룹
        conditionList.add(Aggregation.group("routeId", "routeName").avg("totalLatency").as("avgValue"));

        //필드 추가
        conditionList.add(Aggregation.addFields().addFieldWithValue("unit", "ms").build());

        //필드 정의
        conditionList.add(
            Aggregation.project("unit")
            .andExpression("round(avgValue, 2)").as("averageResponseTime")
            .and("_id.routeId").as("routeId")
            .and("_id.routeName").as("routeName")
        );

        //_id제거
        conditionList.add(Aggregation.project().andExclude("_id"));
        
        //정렬
        conditionList.add(Aggregation.sort(Sort.Direction.ASC, "averageResponseTime"));

        //TOP Limit
        conditionList.add(Aggregation.limit(opt.getTopRange()));

        return ResponseEntity.ok(ApiResponse.builder().data(mongoService.getList(conditionList)).build());
    }

    /**
     * api15 : API별 타겟 시스템 평균 응답시간 Top 5 ~ 10
     */
    @ApiOperation(value = "API별 타겟 시스템 평균 응답시간이 짧은 상위 1위~5위 조회", notes = "해당 도메인 내에서 사용된 API별 타겟 시스템 평균 응답시간이 짧은 순서대로 상위 1위부터 5위까지 조회하는 기능")
    @GetMapping("/target_system_average_response_time_per_top_api")
    public ResponseEntity<?> api15(@PathVariable(name = "domainId", required = false) String domainId, StaSrchOpt opt) {
        opt.setDomainId(domainId);
        List<AggregationOperation> conditionList = setSrchCondition(opt);

        //mdTargetLatency가 null이 아닌것
        conditionList.add(
            Aggregation.match(
                Criteria.where("mdTargetLatency").ne(null)
            )
        );

        //전체 그룹
        conditionList.add(Aggregation.group("routeId", "routeName").avg("mdTargetLatency").as("avgValue"));

        //필드 추가
        conditionList.add(Aggregation.addFields().addFieldWithValue("unit", "ms").build());

        //필드 정의
        conditionList.add(
            Aggregation.project("unit")
            .andExpression("round(avgValue, 2)").as("averageResponseTime")
            .and("_id.routeId").as("routeId")
            .and("_id.routeName").as("routeName")
        );

        //_id제거
        conditionList.add(Aggregation.project().andExclude("_id"));
        
        //정렬
        conditionList.add(Aggregation.sort(Sort.Direction.ASC, "averageResponseTime"));

        //TOP Limit
        conditionList.add(Aggregation.limit(opt.getTopRange()));

        return ResponseEntity.ok(ApiResponse.builder().data(mongoService.getList(conditionList)).build());
    }

    /**
     * 검색 조건을 받아 List로 리턴
     */
    @ApiOperation(value = "검색 조건을 받아 List로 리턴", notes = "검색 조건을 받아 List로 리턴")
    public List<AggregationOperation> setSrchCondition(StaSrchOpt opt) {
        List<AggregationOperation> conditionList = new ArrayList<AggregationOperation>();
        
        SimpleDateFormat ymdSdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat ymdhmsSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String today = ymdSdf.format(new Date());
        Date startDate = null;
        Date endDate = null;
        System.out.println(opt.toString());
        //날짜 조건
        try {
            if((opt.getStartDate() != null && !opt.getStartDate().isBlank()) && (opt.getEndDate() != null) && !opt.getEndDate().isBlank()){
                startDate = ymdhmsSdf.parse(opt.getStartDate());
                endDate = ymdhmsSdf.parse(opt.getEndDate());
            } else {
                //조건 없을시 오늘 날짜로 셋팅
                startDate = ymdhmsSdf.parse(today + " 00:00:00");
                endDate = ymdhmsSdf.parse(today + " 23:59:59");
            }
        } catch (ParseException e) {
            // e.printStackTrace();
            throw new BadRequestCustomException(EErrorCode.ERROR_WC001);
        }

        conditionList.add(
            Aggregation.match(
                Criteria.where("gwReqInTime")
                .gte(startDate)
                .lte(endDate)
            )
        );

        //도메인 조건
        if(opt.getDomainId() != null && !opt.getDomainId().isBlank()){
            conditionList.add(
                Aggregation.match(
                    Criteria.where("domainId").is(opt.getDomainId())
                )
            );
        }

        //컨슈머 조건
        if(opt.getConsumerId() != null && !opt.getConsumerId().isBlank()){
            conditionList.add(
                Aggregation.match(
                    Criteria.where("consumerId").is(opt.getConsumerId())
                )
            );
        }

        //라우트 조건
        if(opt.getRouteId() != null && !opt.getRouteId().isBlank()){
            conditionList.add(
                Aggregation.match(
                    Criteria.where("routeId").is(opt.getRouteId())
                )
            );
        }

        return conditionList;
  }
}
