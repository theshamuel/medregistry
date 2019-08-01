package com.theshamuel.medreg.model.schedule.dao.impl;


import com.theshamuel.medreg.buiders.DoctorBuilder;
import com.theshamuel.medreg.buiders.ScheduleBuilder;
import com.theshamuel.medreg.model.base.dao.impl.BaseRepositoryImplTest;
import com.theshamuel.medreg.model.doctor.entity.Doctor;
import com.theshamuel.medreg.model.schedule.entity.Schedule;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * The integration tests for {@link ScheduleRepositoryImpl}
 *
 * @author Alex Gladkikh
 */
public class ScheduleRepositoryImplTest extends BaseRepositoryImplTest {

    ScheduleRepositoryImpl scheduleRepositoryImpl = new ScheduleRepositoryImpl();
    private Doctor doc1;
    private Doctor doc2;
    private Doctor doc3;
    private Schedule schedule1;
    private Schedule schedule2;
    private Schedule schedule3;
    private Schedule schedule4;

    @Test
    public void testFindByFilter(){
        List<Schedule> actual = scheduleRepositoryImpl.findByFilter("dateWork=2018-01-01;");
        assertThat(actual.size(),is(2));
        assertThat(actual,hasItems(schedule3,schedule2));

        actual = scheduleRepositoryImpl.findByFilter("doctor=IVANOv;");
        assertThat(actual.size(),is(4));
        assertThat(actual,hasItems(schedule1,schedule2,schedule3,schedule4));
    }

    @Test
    public void testFindByDoctor(){
        List<Schedule> actual = scheduleRepositoryImpl.findByDoctor(doc1);
        assertThat(actual.size(),is(2));
        assertThat(actual,hasItems(schedule1,schedule2));

        actual = scheduleRepositoryImpl.findByDoctor(doc2);
        assertThat(actual.size(),is(1));

        actual = scheduleRepositoryImpl.findByDoctor(new DoctorBuilder().id("000011").build());
        assertThat(actual,is(Collections.emptyList()));
    }

    @Test
    public void testFindByDateWorkAndDoctor(){
        Schedule actual = scheduleRepositoryImpl.findByDateWorkAndDoctor(doc1,LocalDate.now());
        assertThat(actual,notNullValue());
        assertThat(actual,is(schedule1));

        actual = scheduleRepositoryImpl.findByDateWorkAndDoctor(doc1,LocalDate.of(2018,01,05));
        assertThat(actual,nullValue());

    }

    @Before
    @Override
    public void createTestRecords() {
        initCollection("doctors");
        template.findAllAndRemove(Query.query(Criteria.where("id").exists(true)),Doctor.class);
        doc1 = new DoctorBuilder().name("Ivan").surname("Ivanov").middlename("Petrovich").contractor(0).isNotWork(0).excludeFromReport(0).position("therapist").build();
        doc2 = new DoctorBuilder().name("Petr").surname("Petrov").middlename("Alexeevich").contractor(0).isNotWork(0).excludeFromReport(0).position("oculist").build();
        doc3 = new DoctorBuilder().name("Alexey").surname("Sidorov").middlename("Ivanovich").contractor(0).isNotWork(0).excludeFromReport(0).position("surgeon").build();
        template.save(doc1);
        template.save(doc2);
        template.save(doc3);

        initCollection("schedule");
        template.findAllAndRemove(Query.query(Criteria.where("id").exists(true)),Schedule.class);
        schedule1 = new ScheduleBuilder().id("0001").doctor(doc1).dateWork(LocalDate.now()).build();
        template.save(schedule1);

        schedule2 = new ScheduleBuilder().id("0002").doctor(doc1).dateWork(LocalDate.of(2018,01,01)).build();
        template.save(schedule2);

        schedule3 = new ScheduleBuilder().id("0003").doctor(doc2).dateWork(LocalDate.of(2018,01,01)).build();
        template.save(schedule3);

        schedule4 = new ScheduleBuilder().id("0004").doctor(doc3).dateWork(LocalDate.of(2018,01,02)).build();
        template.save(schedule4);
    }

    @Override
    public void setMongo() {
        scheduleRepositoryImpl.setMongo(template);
    }
}
