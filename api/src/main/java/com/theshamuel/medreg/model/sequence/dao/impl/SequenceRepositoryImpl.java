/**
 * This private project is a project which automatizate workflow in medical center AVESTA
 * (http://avesta-center.com) called "MedRegistry". The "MedRegistry" demonstrates my programming
 * skills to * potential employers.
 * <p>
 * Here is short description: ( for more detailed description please read README.md or go to
 * https://github.com/theshamuel/medregistry )
 * <p>
 * Front-end: JS, HTML, CSS (basic simple functionality) Back-end: Spring (Spring Boot, Spring IoC,
 * Spring Data, Spring Test), JWT library, Java8 DB: MongoDB Tools: git,maven,docker.
 * <p>
 * My LinkedIn profile: https://www.linkedin.com/in/alex-gladkikh-767a15115/
 */
package com.theshamuel.medreg.model.sequence.dao.impl;

import com.theshamuel.medreg.model.sequence.dao.SequenceOperations;
import com.theshamuel.medreg.model.sequence.entity.Sequence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

/**
 * The type Sequence repository.
 */
@Repository
public class SequenceRepositoryImpl implements SequenceOperations {

    private final MongoOperations mongo;

    public SequenceRepositoryImpl(MongoOperations mongo) {
        this.mongo = mongo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized String getNextSequence(String key) {

        Query query = new Query(Criteria.where("code").is(key));

        Update update = new Update();
        update.inc("seq", 1);

        FindAndModifyOptions options = new FindAndModifyOptions();
        options.returnNew(true);

        Sequence seq = mongo.findAndModify(query, update, options, Sequence.class);

        if (seq == null) {
            seq = new Sequence();
            seq.setCode(key);
            seq.setSeq(1);
            mongo.save(seq);
        }

        return seq.toString();

    }

}
