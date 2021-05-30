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
package com.theshamuel.medreg.model.visit.dao.impl;

import com.theshamuel.medreg.model.doctor.entity.Doctor;
import com.theshamuel.medreg.model.visit.dao.VisitOperations;
import com.theshamuel.medreg.model.visit.entity.Visit;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

/**
 * The Visit repository implementation.
 *
 * @author Alex Gladkikh
 */
public class VisitRepositoryImpl implements VisitOperations {

    private final MongoOperations mongo;

    public VisitRepositoryImpl(MongoOperations mongo) {
        this.mongo = mongo;
    }


    @Override
    public List<Visit> findByFilter(String filter) {
        List<Visit> result = Collections.emptyList();
        String[] params = filter.trim().split(";");
        if (params.length > 0) {
            Criteria where = Criteria.where("id").exists(true);
            for (String param : params) {
                String[] tmp = param.split("=");
                if (tmp[0].equals("passport")) {
                    where = where.orOperator(Criteria.where("client.passportSerial")
                                    .regex("^.*".concat(tmp[1].trim()).concat(".*$"), "i"),
                            Criteria.where("client.passportNumber")
                                    .regex("^.*".concat(tmp[1].trim()).concat(".*$"), "i"));
                } else if (tmp[0].equals("dateEvent")) {
                    where = where.and("dateEvent").is(LocalDate.parse(tmp[1].trim()));
                } else {
                    where = where.and(tmp[0]).regex("^.*".concat(tmp[1].trim()).concat(".*$"), "i");
                }
            }
            Query query = Query.query(where)
                    .with(new Sort(new Sort.Order(Sort.Direction.ASC, "dateEvent")));
            result = mongo.find(query, Visit.class);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Visit findByDateTimeEventAndDoctor(Doctor doctor, LocalDate dateEvent,
            LocalTime timeEvent) {
        Criteria where = Criteria.where("doctor").is(doctor).and("dateEvent").is(dateEvent)
                .and("timeEvent").is(timeEvent);
        Query query = Query.query(where);
        return mongo.findOne(query, Visit.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Visit> findByDoctorAndDateEvent(Doctor doctor, LocalDate dateEvent) {
        if (doctor.getId() != null) {
            Criteria where = Criteria.where("doctor._id").is(new ObjectId(doctor.getId()))
                    .and("dateEvent").is(dateEvent);
            Query query = Query.query(where);
            return mongo.find(query, Visit.class);
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Visit> findByDoctorAndBetweenDateEvent(Doctor doctor, LocalDate startDateEvent, LocalDate endDateEvent) {
        if (doctor.getId() != null) {
            Criteria where = Criteria.where("doctor._id").is(new ObjectId(doctor.getId()))
                    .and("dateEvent").gte(startDateEvent).lte(endDateEvent);
            Query query = Query.query(where);
            return mongo.find(query, Visit.class);
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Visit> findByDateEvent(LocalDate dateEvent) {
        Criteria where = Criteria.where("dateEvent").is(dateEvent);
        Query query = Query.query(where);
        return mongo.find(query, Visit.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Visit> findBetweenDateEvent(LocalDate startDateEvent, LocalDate endDateEvent) {
        Criteria where = Criteria.where("dateEvent").gte(startDateEvent).lte(endDateEvent);
        Query query = Query.query(where);
        return mongo.find(query, Visit.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Visit> findAllClientVisits(String clientId, String category) {
        Criteria where = Criteria.where("client._id").is(new ObjectId(clientId))
                .and("services.category").is(category);
        Query query = Query.query(where);
        return mongo.find(query, Visit.class);
    }
}
