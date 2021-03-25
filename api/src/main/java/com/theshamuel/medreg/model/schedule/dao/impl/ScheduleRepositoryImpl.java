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
package com.theshamuel.medreg.model.schedule.dao.impl;


import com.theshamuel.medreg.model.doctor.entity.Doctor;
import com.theshamuel.medreg.model.schedule.dao.ScheduleOperations;
import com.theshamuel.medreg.model.schedule.entity.Schedule;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

/**
 * The Schedule repository implementation class.
 *
 * @author Alex Gladkikh
 */
@Repository
public class ScheduleRepositoryImpl implements ScheduleOperations {

    private final MongoOperations mongo;

    public ScheduleRepositoryImpl(MongoOperations mongo) {
        this.mongo = mongo;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Schedule findByDateWorkAndDoctor(Doctor doctor, LocalDate dateWork) {
        Criteria where = Criteria.where("doctor").is(doctor).and("dateWork").is(dateWork);
        Query query = Query.query(where);
        return this.mongo.findOne(query, Schedule.class);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<Schedule> findByDoctor(Doctor doctor) {
        Criteria where = Criteria.where("doctor").is(doctor);
        Query query = Query.query(where)
                .with(new Sort(new Sort.Order(Sort.Direction.DESC, "dateWork")));
        return mongo.find(query, Schedule.class);
    }

    @Override
    public List<Schedule> findByFilter(String filter) {
        List<Schedule> result = Collections.emptyList();
        String[] params = filter.trim().split(";");
        if (params.length > 0) {
            Criteria where = Criteria.where("id").exists(true);
            for (String param : params) {
                String[] tmp = param.split("=");
                if (tmp[0].equals("dateWork")) {
                    where = where.and("dateWork").is(LocalDate.parse(tmp[1].trim()));
                } else if (!tmp[0].equals("doctor")) {
                    //Filter by doctor is implemented in ScheduleServiceImpl,
                    //due to complex relation. So that doctor parameter is excludes
                    where = where.and(tmp[0]).regex("^.*".concat(tmp[1].trim()).concat(".*$"), "i");
                }
            }
            Query query = Query.query(where)
                    .with(new Sort(new Sort.Order(Sort.Direction.ASC, "dateWork")))
                    .with(new Sort(new Sort.Order(Sort.Direction.ASC, "timeEvent")));
            result = mongo.find(query, Schedule.class);
        }
        return result;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Schedule checkAppointmentInScheduleSlots(Doctor doctor, LocalDate dateEvent,
            LocalTime timeEvent) {
        Criteria where = Criteria.where("doctor").is(doctor).and("dateWork").is(dateEvent)
                .and("timeFrom").lte(timeEvent).and("timeTo").gte(timeEvent);
        Query query = Query.query(where);
        return mongo.findOne(query, Schedule.class);
    }

}
