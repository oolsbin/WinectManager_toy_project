package com.example.demo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.demo.repository.mongo.ManagerLogDocument;


public interface ManagerLogRepository extends MongoRepository<ManagerLogDocument, String> {
  
}
