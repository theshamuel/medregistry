package com.theshamuel.medreg.model.base.dao.impl;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.theshamuel.medreg.base.dao.BaseRepositoryTest;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.mongo.tests.MongodForTestsFactory;
import java.io.IOException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.springframework.data.mongodb.core.MongoTemplate;


public abstract class BaseRepositoryImplTest implements BaseRepositoryTest {


    private static final String DB_NAME = "medregDBTest";
    protected static MongoCollection collection;
    protected static MongoTemplate template;
    private static MongodForTestsFactory factory = null;
    private static MongoDatabase db;

    @BeforeClass
    public static void initDb() throws IOException {
        factory = MongodForTestsFactory.with(Version.Main.PRODUCTION);
        MongoClient mongo = factory.newMongo();
        db = mongo.getDatabase(DB_NAME);
        template = new MongoTemplate(mongo, DB_NAME);
    }

    public static void initCollection(String collectionName) {
        collection = db.getCollection(collectionName);
    }

    @AfterClass
    public static void closeDb() {
        if (factory != null) {
            factory.shutdown();
        }
    }
}
