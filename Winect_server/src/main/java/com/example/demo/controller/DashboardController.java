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
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.BooleanOperators;
import org.springframework.data.mongodb.core.aggregation.ComparisonOperators;
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Api(tags = "대시보드")
@RestController
@RequestMapping("{domainId}")
@Slf4j
public class DashboardController {
	
    @Autowired
    MongoService mongoService;
    
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * api25 : 오늘 성공건수
     */
    @ApiOperation(value = "오늘 성공한 API 건수", notes = "해당 도메인 내에서 오늘 성공한 API 건수를 조회하는 기능")
    @GetMapping("/success_count_per_today")
    public ResponseEntity<?> api25(
    		@ApiParam(value = "도메인 아이디", required = true, example = "1")
    		@PathVariable(name = "domainId", required = false) String domainId, StaSrchOpt opt) {
        opt.setDomainId(domainId);
        //집계기능 : AggregationOperation
        List<AggregationOperation> conditionList = setSrchCondition(opt);

        //성공건에 대한 조건                       //문자 검색 조건 함수?
        conditionList.add(Aggregation.match(Criteria.where("gwProcessFlag").is("S")));
        conditionList.add(Aggregation.match(Criteria.where("mdProcessFlag").is("S")));

        //전체 그룹
        conditionList.add(Aggregation.group().count().as("count"));

        return ResponseEntity.ok(ApiResponse.builder().data(mongoService.getData(conditionList)).build());
    }

    /**
     * api26 : 오늘 에러건수
     */
    @ApiOperation(value = "오늘 실패한 API 에러건수", notes = "해당 도메인 내에서 오늘 실패한 API 에러건수를 조회하는 기능")
    @GetMapping("/error_count_per_today")
    public ResponseEntity<?> api26(
    		@ApiParam(value = "도메인 아이디", required = true, example = "1")
    		@PathVariable(name = "domainId", required = false) String domainId, StaSrchOpt opt) {
        opt.setDomainId(domainId);
        List<AggregationOperation> conditionList = setSrchCondition(opt);

        //에러건에 대한 조건
        conditionList.add(Aggregation.match(Criteria.where("gwProcessFlag").ne("S")));
        conditionList.add(Aggregation.match(Criteria.where("mdProcessFlag").ne("S")));

        //전체 그룹
        conditionList.add(Aggregation.group().count().as("count"));

        return ResponseEntity.ok(ApiResponse.builder().data(mongoService.getData(conditionList)).build());
    }

    /**
     * api27 : 오늘 성공 + 에러 비율
     */
    @ApiOperation(value = "오늘 성공한 API 성공율과 에러율 조회", notes = "해당 도메인 내에서 오늘 성공한 API 성공율과 에러율 조회하는 기능")
    @GetMapping("/success_rate_and_error_rate_per_today")
    public ResponseEntity<?> api27(
    		@ApiParam(value = "도메인 아이디", required = true, example = "1")
    		@PathVariable(name = "domainId", required = false) String domainId,
    		@ApiParam(value = "StaSrchOpt", required = true, example = "1")
    		StaSrchOpt opt) {
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
        log.info("================================= success rate and error rate per today:\n" + gson.toJson(ResponseEntity.ok(ApiResponse.builder().data(resultMap).build())));
        return ResponseEntity.ok(ApiResponse.builder().data(resultMap).build());
    }

    /**
     * api28 : 오늘 평균 응답시간
     */
    @ApiOperation(value = "오늘 API 평균 응답시간 조회", notes = "해당 도메인 내에서 오늘 API 평균 응답시간을 조회하는 기능")
    @GetMapping("/average_response_time_per_today")
    public ResponseEntity<?> api28(
    		@ApiParam(value = "도메인 아이디", required = true, example = "1")    		
    		@PathVariable(name = "domainId", required = false) String domainId,
    		@ApiParam(value = "StaSrchOpt", required = true, example = "1")
    		StaSrchOpt opt) {
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
        log.info("================================= average response time per today:\n" + gson.toJson(ResponseEntity.ok(ApiResponse.builder().data(mongoService.getData(conditionList)).build())));
        return ResponseEntity.ok(ApiResponse.builder().data(mongoService.getData(conditionList)).build());
    }

    /**
     * api29 : 오늘 트랜잭션 처리량
     */
    @ApiOperation(value = "오늘 트랜젝션 처리량", notes = "해당 도메인 내에서 오늘 트랜젝션 처리량을 조회하는 기능")
    @GetMapping("/traffic_per_today")
    public ResponseEntity<?> api29(
    		@ApiParam(value = "도메인 아이디", required = true, example = "1")    		
    		@PathVariable(name = "domainId", required = false) String domainId,
    		@ApiParam(value = "StaSrchOpt", required = true, example = "1")
    		StaSrchOpt opt) {
        opt.setDomainId(domainId);
        List<AggregationOperation> conditionList = setSrchCondition(opt);

        //전체 그룹
        conditionList.add(Aggregation.group().count().as("traffic").avg("totalLatency").as("avgLatency"));

        //트래픽 계산
        conditionList.add(
            Aggregation.project("traffic")
                .andExpression("avgLatency / 1000").as("sec")
        );

        //TPS 계산
        conditionList.add(
            Aggregation.project("traffic")
                .andExpression("traffic / sec").as("tps")
        );

        //필드 라운드 처리
        conditionList.add(
            Aggregation.project("traffic")
                .andExpression("round(tps, 2)").as("tps")
        );
        log.info("================================= traffic per today:\n" + gson.toJson(ResponseEntity.ok(ApiResponse.builder().data(mongoService.getData(conditionList)).build())));
        return ResponseEntity.ok(ApiResponse.builder().data(mongoService.getData(conditionList)).build());
    }

    /**
     * api30 : 오늘 시간당 평균 응답시간(시계열)
     */
    @ApiOperation(value = "오늘 시간당 평균 응답시간(시계열)", notes = "해당 도메인 내에서 오늘 시간당 평균 응답시간(시계열)을 조회하는 기능")
    @GetMapping("/average_response_time_per_hour_today")
    public ResponseEntity<?> api30(
    		@ApiParam(value = "도메인 아이디", required = true, example = "1")    		
    		@PathVariable(name = "domainId", required = false) String domainId,
    		@ApiParam(value = "StaSrchOpt", required = true, example = "1")
    		StaSrchOpt opt) {
        opt.setDomainId(domainId);
        List<AggregationOperation> conditionList = setSrchCondition(opt);

        //필드 정의
        //9시간을 더하는이유는 Timezone에서 9시간을 더한게 우리나라 시간이기 때문
        conditionList.add(Aggregation.project("totalLatency").andExpression("hour(add(gwReqInTime, 9 * 60 * 60000))").as("hour"));
        
        //그룹
        conditionList.add(Aggregation.group("hour").avg("totalLatency").as("avgLatency"));

        //필드 정의 및 _id제거
        conditionList.add(
            Aggregation.project()
                .and("_id").as("time")
                .andExpression("round(avgLatency, 2)").as("avgLatency")
                .andExclude("_id")
        );
        log.info("================================= average response time per hour today:\n" + gson.toJson(ResponseEntity.ok(ApiResponse.builder().data(mongoService.getList(conditionList)).build())));
        return ResponseEntity.ok(ApiResponse.builder().data(mongoService.getList(conditionList)).build());
    }

    /**
     * api31 : 오늘 호출수 그래프(시계열)
     */
    @ApiOperation(value = "오늘 호출수 그래프(시계열)", notes = "해당 도메인 내에서 오늘 호출수를 그래프(시계열)로 조회하는 기능")
    @GetMapping("/number_of_api_calls_per_hour_today")
    public ResponseEntity<?> api31(
    		@ApiParam(value = "도메인 아이디", required = true, example = "1")    		
    		@PathVariable(name = "domainId", required = false) String domainId,
    		@ApiParam(value = "StaSrchOpt", required = true, example = "1")
    		StaSrchOpt opt) {
        opt.setDomainId(domainId);
        List<AggregationOperation> conditionList = setSrchCondition(opt);

        //필드 정의
        conditionList.add(
            //9시간을 더하는이유는 Timezone에서 9시간을 더한게 우리나라 시간이기 때문
            Aggregation.project("routeId", "routeName")
                .andExpression("hour(add(gwReqInTime, 9 * 60 * 60000))").as("hour")
        );

        //그룹
        conditionList.add(Aggregation.group("routeId", "routeName", "hour").count().as("count"));

        //필드 정의
        conditionList.add(
            Aggregation.project("count")
            .and("_id.routeId").as("routeId")
            .and("_id.routeName").as("routeName")
            .and("_id.hour").as("time")
            .andExclude("_id")
        );
        
        //필드 정의
        conditionList.add(
            Aggregation.group("routeId", "routeName")
                .sum("count").as("totalCount")
                .push(
                    new BasicDBObject("time", "$time")
                    .append("count", "$count")
                ).as("times")
        );
        
        //정렬
        conditionList.add(Aggregation.sort(Sort.Direction.DESC, "totalCount"));
        
        //TOP Limit
        conditionList.add(Aggregation.limit(opt.getTopRange()));

        //필드 정의
        conditionList.add(
            Aggregation.project("times")
                .and("_id.routeId").as("routeId")
                .and("_id.routeName").as("routeName")
                .andExclude("_id")
        );
        log.info("================================= number of api calls per hour today:\n" + gson.toJson(ResponseEntity.ok(ApiResponse.builder().data(mongoService.getList(conditionList)).build())));
        return ResponseEntity.ok(ApiResponse.builder().data(mongoService.getList(conditionList)).build());
    }

    /**
     * api32 : 오늘 응답 코드 비율
     */
    @ApiOperation(value = "오늘 응답 코드 비율", notes = "해당 도메인 내에서 오늘 응답 코드 비율을 조회하는 기능")
    @GetMapping("/reponse_code_rate_per_today")
    public ResponseEntity<?> api32(
    		@ApiParam(value = "도메인 아이디", required = true, example = "1")    		
    		@PathVariable(name = "domainId", required = false) String domainId,
    		@ApiParam(value = "StaSrchOpt", required = true, example = "1")
    		StaSrchOpt opt) {
        opt.setDomainId(domainId);
        List<AggregationOperation> conditionList = setSrchCondition(opt);
        
        //그룹
        conditionList.add(Aggregation.group("gwTargetResCode").count().as("count"));
        	
        //필드 정의
        conditionList.add(
            Aggregation.group()
                .sum("count").as("totalCount")
                .push(
                    new BasicDBObject("count", "$count")
                        .append("code", "$_id")
                ).as("codeCount")
        );

        conditionList.add(
            Aggregation.unwind("codeCount")
        );

        conditionList.add(
            Aggregation.project("totalCount")
                .and("codeCount.count").as("count")
                .and("codeCount.code").as("code")
                .andExpression("round(codeCount.count / totalCount * 100, 2)").as("rate") 
        );
        log.info("================================= response code rate per today:\n" + gson.toJson(ResponseEntity.ok(ApiResponse.builder().data(mongoService.getList(conditionList)).build())));
        return ResponseEntity.ok(ApiResponse.builder().data(mongoService.getList(conditionList)).build());
    }

    /**
     * api33 : 오늘 TOP10 호출 API
     */
    @ApiOperation(value = "오늘 호출된 API중 호출된 건수가 높은 상위 1~10위 조회", notes = "해당 도메인 내에서 오늘 호출된 API중 호출된 건수가 높은 상위 10위까지를 조회하는 기능")
    @GetMapping("/number_of_api_calls_per_today")
    public ResponseEntity<?> api33(
    		@ApiParam(value = "도메인 아이디", required = true, example = "1")    		
    		@PathVariable(name = "domainId", required = false) String domainId,
    		@ApiParam(value = "StaSrchOpt", required = true, example = "1")
    		StaSrchOpt opt) {
        opt.setDomainId(domainId);
        List<AggregationOperation> conditionList = setSrchCondition(opt);

        //그룹
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
        log.info("================================= number of api calls per today:\n" + gson.toJson(ResponseEntity.ok(ApiResponse.builder().data(mongoService.getList(conditionList)).build())));
        return ResponseEntity.ok(ApiResponse.builder().data(mongoService.getList(conditionList)).build());
    }

    /**
     * api34 : 오늘 TOP10 컨슈머
     */
    @ApiOperation(value = "오늘 컨슈머 상위 1~10위 조회", notes = "해당 도메인 내에서 오늘 컨슈머 상위 10위까지를 조회하는 기능")
    @GetMapping("/consumer_of_api_calls_per_today")
    public ResponseEntity<?> api34(
    		@ApiParam(value = "도메인 아이디", required = true, example = "1")    		
    		@PathVariable(name = "domainId", required = false) String domainId,
    		@ApiParam(value = "StaSrchOpt", required = true, example = "1")
    		StaSrchOpt opt) {
        opt.setDomainId(domainId);
        List<AggregationOperation> conditionList = setSrchCondition(opt);

        //그룹
        conditionList.add(Aggregation.group("consumerId", "consumerName").count().as("count"));

        //필드 정의
        conditionList.add(
            Aggregation.project("count")
                .and("_id.consumerId").as("consumerId")
                .and("_id.consumerName").as("consumerName")
        );
        
        //_id제거
        conditionList.add(Aggregation.project().andExclude("_id"));  

        //정렬
        conditionList.add(Aggregation.sort(Sort.Direction.DESC, "count"));

        //TOP Limit
        conditionList.add(Aggregation.limit(opt.getTopRange()));
        log.info("================================= consumer of api calls per today:\n" + gson.toJson(ResponseEntity.ok(ApiResponse.builder().data(mongoService.getList(conditionList)).build())));
        return ResponseEntity.ok(ApiResponse.builder().data(mongoService.getList(conditionList)).build());
    }

    /**
     * 검색 조건을 받아 List로 리턴
     * @throws ParseException
     */
    @ApiOperation(value = "검색 조건을 받아 List로 리턴", notes = "검색 조건을 받아 List로 리턴하는 기능")
    public List<AggregationOperation> setSrchCondition(    		
    		@ApiParam(value = "StaSrchOpt", required = true, example = "1")
    		StaSrchOpt opt) {
        List<AggregationOperation> conditionList = new ArrayList<AggregationOperation>();
        
        SimpleDateFormat ymdSdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat ymdhmsSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String today = ymdSdf.format(new Date());
        Date startDate = null;
        Date endDate = null;

        //날짜 조건
        try {
            if((opt.getCreateTime() != null && !opt.getCreateTime().isBlank())){
                startDate = ymdhmsSdf.parse(opt.getCreateTime() + " 00:00:00");
                endDate = ymdhmsSdf.parse(opt.getCreateTime() + " 23:59:59");
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
