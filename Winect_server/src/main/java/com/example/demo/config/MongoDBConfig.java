package com.example.demo.config;

import java.util.Collection;
import java.util.Collections;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories("com.example.demo.repository.mongo")
public class MongoDBConfig extends AbstractMongoClientConfiguration{

  @Autowired
  private Environment env;
  
  @Value("${spring.data.mongodb.host}")
  private String host;
  @Value("${spring.data.mongodb.port}")
  private String port;
  @Value("${spring.data.mongodb.username}")
  private String username;
  @Value("${spring.data.mongodb.password}")
  private String password;
  @Value("${spring.data.mongodb.database}")
  private String database; 

  @Override
  protected String getDatabaseName() {
    return database;
  }

  @Override
  public MongoClient mongoClient() {
	//연결문자열
    ConnectionString connectionString = new ConnectionString("mongodb://"+username+":"+password+"@"+host+":"+port+"/"+database+"");
    MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
      .applyConnectionString(connectionString)
      .build();
      
    return MongoClients.create(mongoClientSettings);
  }

  @Override
  protected Collection<String> getMappingBasePackages() {
    return Collections.singleton("com.example.demo");
  }
}
