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
package com.theshamuel.medreg.model.client.dao.impl;


import com.theshamuel.medreg.model.client.dao.ClientOperations;
import com.theshamuel.medreg.model.client.entity.Client;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

/**
 * The Client repository class.
 *
 * @author Alex Gladkikh
 */
@Repository
public class ClientRepositoryImpl implements ClientOperations {

    private final MongoOperations mongo;

    public ClientRepositoryImpl(MongoOperations mongo) {
        this.mongo = mongo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isUniqueClient(Client client) {
        String passportSerial = client.getPassportSerial().trim();
        String passportNumber = client.getPassportNumber().trim();
        Criteria where = Criteria.where("passportSerial").is(passportSerial).and("passportNumber")
                .is(passportNumber);
        Query query = Query.query(where);
        return mongo.findOne(query, Client.class) == null;
    }

    @Override
    public List<Client> findByFilter(String filter) {
        List<Client> result = Collections.emptyList();
        String[] params = filter.trim().split(";");
        if (params.length > 0) {
            Criteria where = Criteria.where("id").exists(true);
            for (int i = 0; i < params.length; i++) {
                String[] tmp = params[i].split("=");
                if (tmp[0].equals("passport")) {
                    where = where.orOperator(Criteria.where("passportSerial")
                                    .regex("^.*".concat(tmp[1].trim()).concat(".*$"), "i"),
                            Criteria.where("passportNumber")
                                    .regex("^.*".concat(tmp[1].trim()).concat(".*$"), "i"));
                } else {
                    where = where.and(tmp[0]).regex("^.*".concat(tmp[1].trim()).concat(".*$"), "i");
                }
            }
            Query query = Query.query(where)
                    .with(new Sort(new Sort.Order(Sort.Direction.ASC, "surname")));
            result = mongo.find(query, Client.class);
        }
        return result;
    }
    
}
