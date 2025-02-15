package com.WealthManager.UserInfo.util.counter;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CounterRepository extends MongoRepository<Counter, String> {
    Counter findByCollectionName(String collectionName);
}