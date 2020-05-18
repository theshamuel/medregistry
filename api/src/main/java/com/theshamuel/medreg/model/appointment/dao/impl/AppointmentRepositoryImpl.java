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
package com.theshamuel.medreg.model.appointment.dao.impl;

import com.theshamuel.medreg.model.appointment.dao.AppointmentOperations;
import com.theshamuel.medreg.model.appointment.entity.Appointment;
import com.theshamuel.medreg.model.doctor.entity.Doctor;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

/**
 * The Appointment repository implementation class.
 *
 * @author Alex Gladkikh
 */
public class AppointmentRepositoryImpl implements AppointmentOperations {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentRepositoryImpl.class);

    @Autowired
    private MongoOperations mongo;


    /**
     * {@inheritDoc}
     */
    @Override
    public Appointment findByDateTimeEventAndDoctor(Doctor doctor, LocalDate dateEvent,
            LocalTime timeEvent) {
        Criteria where = Criteria.where("doctor").is(doctor).and("dateEvent").is(dateEvent)
                .and("timeEvent").is(timeEvent);
        Query query = Query.query(where);
        return mongo.findOne(query, Appointment.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteOutdatedAppointments(LocalDate dateEvent) {
        Criteria criteria = new Criteria();
        criteria.orOperator(Criteria.where("hasVisit").is(false).and("dateEvent").lt(dateEvent),
                Criteria.where("hasVisit").exists(false).and("dateEvent").lt(dateEvent),
                Criteria.where("hasVisit").is(null).and("dateEvent").lt(dateEvent));
        Query query = Query.query(criteria);

        List deletedRecords = mongo.findAllAndRemove(query, Appointment.class);
        logger.info("Has been deleted {} outdated appointments",
                deletedRecords != null ? deletedRecords.size() : 0);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<Appointment> findReservedAppointmentsByDoctorAndDate(Doctor doctor,
            LocalDate dateEvent) {
        Instant start = Instant.now();
        Criteria where = Criteria.where("doctor").is(doctor).and("dateEvent").is(dateEvent);
        Query query = Query.query(where)
                .with(new Sort(new Sort.Order(Sort.Direction.ASC, "dateEvent")))
                .with(new Sort(new Sort.Order(Sort.Direction.ASC, "timeEvent")));
        List result = mongo.find(query, Appointment.class);
        logger.debug("Elapsed time findReservedAppointmentsByDoctorAndDate: {}", Duration
                .between(start, Instant.now()).toMillis());
        return result != null ? result : Collections.emptyList();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Appointment> findByFilter(String filter) {
        List<Appointment> result = Collections.emptyList();
        String[] params = filter.trim().split(";");
        if (params.length > 0) {
            Criteria where = Criteria.where("id").exists(true);
            for (int i = 0; i < params.length; i++) {
                String[] tmp = params[i].split("=");
                if (tmp[0].equals("dateEvent")) {
                    where = where.and("dateEvent").is(LocalDate.parse(tmp[1].trim()));
                } else if (!tmp[0].equals("doctor")) {
                    //Filter by doctor was implemented in
                    // AppointmentServiceImpl, due to complex relation.
                    //So that doctor parameter is excludes
                    where = where.and(tmp[0]).regex("^.*".concat(tmp[1].trim()).concat(".*$"), "i");
                }
            }
            Query query = Query.query(where)
                    .with(new Sort(new Sort.Order(Sort.Direction.ASC, "dateEvent")))
                    .with(new Sort(new Sort.Order(Sort.Direction.ASC, "timeEvent")));
            result = mongo.find(query, Appointment.class);
        }
        return result;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<Appointment> findReservedAppointmentsByDoctor(Doctor doctor) {
        Instant start = Instant.now();
        Criteria where = Criteria.where("doctor").is(doctor);
        Query query = Query.query(where)
                .with(new Sort(new Sort.Order(Sort.Direction.ASC, "dateEvent")))
                .with(new Sort(new Sort.Order(Sort.Direction.ASC, "timeEvent")));
        List result = mongo.find(query, Appointment.class);
        logger.debug("Elapsed time findReservedAppointmentsByDoctor: {}", Duration
                .between(start, Instant.now()).toMillis());
        return result != null ? result : Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Appointment> findReservedAppointmentsByDoctorAndDateAndHasVisit(Doctor doctor,
            LocalDate dateEvent, boolean hasVisit) {
        Instant start = Instant.now();
        Criteria where = new Criteria();
        if (!hasVisit) {
            where = where
                    .orOperator(Criteria.where("doctor").is(doctor).and("dateEvent").is(dateEvent)
                                    .and("hasVisit").is(false),
                            Criteria.where("doctor").is(doctor).and("dateEvent").
                                    is(dateEvent).and("hasVisit").exists(false));
        } else {
            where = Criteria.where("doctor").is(doctor).and("dateEvent").is(dateEvent)
                                    .and("hasVisit").is(true);
        }
        Query query = Query.query(where)
                .with(new Sort(new Sort.Order(Sort.Direction.ASC, "dateEvent")))
                .with(new Sort(new Sort.Order(Sort.Direction.ASC, "timeEvent")));
        List result = mongo.find(query, Appointment.class);
        logger.debug("Elapsed time findReservedAppointmentsByDoctorAndDate: {}", Duration
                .between(start, Instant.now()).toMillis());
        return result != null ? result : Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Appointment> findReservedAppointmentsByDoctorAndHasVisit(Doctor doctor,
            boolean hasVisit) {
        Instant start = Instant.now();
        Criteria where = new Criteria();
        if (!hasVisit) {
            where = where
                    .orOperator(Criteria.where("doctor").is(doctor).and("hasVisit").is(hasVisit),
                            Criteria.where("doctor").is(doctor).and("hasVisit").exists(false));
        } else {
            where = Criteria.where("doctor").is(doctor).and("hasVisit").is(true);
        }
        Query query = Query.query(where)
                .with(new Sort(new Sort.Order(Sort.Direction.ASC, "dateEvent")))
                .with(new Sort(new Sort.Order(Sort.Direction.ASC, "timeEvent")));
        List result = mongo.find(query, Appointment.class);
        logger.debug("Elapsed time findReservedAppointmentsByDoctor: {}", Duration
                .between(start, Instant.now()).toMillis());
        return result != null ? result : Collections.emptyList();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<Appointment> findReservedAppointmentsByDateEventAfterTimeEvent(
            LocalDate dateEvent) {
        Criteria where = Criteria.where("dateEvent").is(dateEvent);
        Query query = Query.query(where)
                .with(new Sort(new Sort.Order(Sort.Direction.ASC, "dateEvent")))
                .with(new Sort(new Sort.Order(Sort.Direction.ASC, "timeEvent")));
        List result = mongo.find(query, Appointment.class);
        return result != null ? result : Collections.emptyList();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setMongo(MongoOperations mongo) {
        this.mongo = mongo;
    }
}
