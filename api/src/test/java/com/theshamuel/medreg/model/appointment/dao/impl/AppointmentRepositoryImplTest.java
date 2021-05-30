package com.theshamuel.medreg.model.appointment.dao.impl;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.junit.Assert.assertThat;

import com.theshamuel.medreg.buiders.AppointmentBuilder;
import com.theshamuel.medreg.buiders.DoctorBuilder;
import com.theshamuel.medreg.model.appointment.entity.Appointment;
import com.theshamuel.medreg.model.base.dao.impl.BaseRepositoryImplTest;
import com.theshamuel.medreg.model.doctor.entity.Doctor;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

/**
 * The integration tests for {@link AppointmentRepositoryImpl}
 *
 * @author Alex Gladkikh
 */
public class AppointmentRepositoryImplTest extends BaseRepositoryImplTest {

    AppointmentRepositoryImpl appointmentRepositoryImpl = new AppointmentRepositoryImpl();

    private Doctor docIvanov;
    private Doctor docPetrov;
    private Doctor docSidorov;

    private Appointment appointmentDocIvanov1;
    private Appointment appointmentDocIvanov2;
    private Appointment appointmentDocPetrov;

    @Test
    public void testFindReservedAppointmentsByDoctorAndDate() {
        List<Appointment> actual = appointmentRepositoryImpl
                .findReservedAppointmentsByDoctorAndDate(docIvanov, LocalDate.now());
        assertThat(actual.size(), is(2));
        assertThat(actual, hasItems(appointmentDocIvanov1, appointmentDocIvanov1));

        actual = appointmentRepositoryImpl
                .findReservedAppointmentsByDoctorAndDate(docSidorov, LocalDate.now());
        assertThat(actual, is(Collections.emptyList()));
    }

    @Test
    public void testFindByFilter() {
        List<Appointment> actual = appointmentRepositoryImpl.findByFilter("dateEvent=2018-01-01;");
        assertThat(actual.size(), is(1));
        assertThat(actual, hasItem(appointmentDocPetrov));

        actual = appointmentRepositoryImpl.findByFilter("dateEvent=2018-01-01;doctor=IVANOv;");
        assertThat(actual.size(), is(1));
        assertThat(actual, hasItem(appointmentDocPetrov));

        actual = appointmentRepositoryImpl.findByFilter("dateEvent=2018-01-09;");
        assertThat(actual, is(Collections.emptyList()));
    }

    @Test
    public void testFindReservedAppointmentsByDateEventAfterTimeEvent() {
        List<Appointment> actual = appointmentRepositoryImpl
                .findReservedAppointmentsByDateEventAfterTimeEvent(LocalDate.of(2018, 01, 01));
        assertThat(actual.size(), is(1));
        assertThat(actual, hasItems(appointmentDocPetrov));

        actual = appointmentRepositoryImpl
                .findReservedAppointmentsByDateEventAfterTimeEvent(LocalDate.of(2018, 01, 02));
        assertThat(actual, is(Collections.emptyList()));

    }

    @Test
    public void testFindReservedAppointmentsByDoctor() {
        List<Appointment> actual = appointmentRepositoryImpl
                .findReservedAppointmentsByDoctor(docIvanov);
        assertThat(actual.size(), is(2));
        assertThat(actual, hasItems(appointmentDocIvanov1, appointmentDocIvanov1));

        actual = appointmentRepositoryImpl.findReservedAppointmentsByDoctor(docPetrov);
        assertThat(actual.size(), is(1));
        assertThat(actual, hasItems(appointmentDocPetrov));
    }

    @Test
    public void testFindByDateTimeEventAndDoctor() {
        Appointment actual = appointmentRepositoryImpl
                .findByDateTimeEventAndDoctor(docIvanov, LocalDate.now(), LocalTime.of(12, 00));
        assertThat(actual, is(appointmentDocIvanov1));

        actual = appointmentRepositoryImpl
                .findByDateTimeEventAndDoctor(docIvanov, LocalDate.now(), LocalTime.of(13, 00));
        assertThat(actual, is(nullValue()));
    }

    @Test
    public void testDeleteOutdatedAppointments() {
        appointmentRepositoryImpl.deleteOutdatedAppointments(LocalDate.now());
        List<Appointment> actual = template.findAll(Appointment.class);
        assertThat(actual.size(), is(2));
        assertThat(actual, hasItems(appointmentDocIvanov1, appointmentDocIvanov2));
    }

    @Before
    @Override
    public void createTestRecords() {
        initCollection("doctors");
        template.findAllAndRemove(Query.query(Criteria.where("id").exists(true)), Doctor.class);
        docIvanov = new DoctorBuilder().name("Ivan").surname("Ivanov").middlename("Petrovich")
                .id("d0001").contractor(0).isNotWork(0).excludeFromReport(0).position("therapist")
                .build();
        docPetrov = new DoctorBuilder().name("Petr").surname("Petrov").middlename("Alexeevich")
                .id("d0002").contractor(0).isNotWork(0).excludeFromReport(0).position("oculist")
                .build();
        docSidorov = new DoctorBuilder().name("Oleg").surname("Sidorov").middlename("Olegovich")
                .id("d0003").contractor(0).isNotWork(0).excludeFromReport(0).position("oculist")
                .build();
        template.save(docIvanov);
        template.save(docPetrov);
        template.save(docPetrov);

        initCollection("appointments");
        template.findAllAndRemove(Query.query(Criteria.where("id").exists(true)),
                Appointment.class);
        appointmentDocIvanov1 = new AppointmentBuilder().id("0001").doctor(docIvanov)
                .dateEvent(LocalDate.now()).timeEvent(LocalTime.of(12, 00)).build();
        appointmentDocIvanov2 = new AppointmentBuilder().id("0002").doctor(docIvanov)
                .dateEvent(LocalDate.now()).timeEvent(LocalTime.of(12, 15)).build();
        appointmentDocPetrov = new AppointmentBuilder().id("0003").doctor(docPetrov)
                .dateEvent(LocalDate.of(2018, 01, 01)).timeEvent(LocalTime.of(8, 15)).build();
        template.save(appointmentDocIvanov1);
        template.save(appointmentDocIvanov2);
        template.save(appointmentDocPetrov);

    }
}
