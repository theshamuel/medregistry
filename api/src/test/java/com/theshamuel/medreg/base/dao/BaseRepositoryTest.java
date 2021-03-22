package com.theshamuel.medreg.base.dao;

public interface BaseRepositoryTest {

    void createTestRecords();


    //Add temporary till upgrade all tests
    default void setMongo() {
    }
}
