package com.example.demo.repository;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.repository.mongo.ManagerLogDocument;

@Repository
public interface HistoryRepository extends MongoRepository<ManagerLogDocument, String> {

  List<ManagerLogDocument> findAllByOrderByIdDesc();

}
