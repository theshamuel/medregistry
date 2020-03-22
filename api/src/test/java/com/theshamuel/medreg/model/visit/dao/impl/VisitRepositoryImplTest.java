package com.theshamuel.medreg.model.visit.dao.impl;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import com.theshamuel.medreg.buiders.AppointmentBuilder;
import com.theshamuel.medreg.buiders.ClientBuilder;
import com.theshamuel.medreg.buiders.DoctorBuilder;
import com.theshamuel.medreg.buiders.ServiceBuilder;
import com.theshamuel.medreg.buiders.VisitBuilder;
import com.theshamuel.medreg.model.appointment.entity.Appointment;
import com.theshamuel.medreg.model.base.dao.impl.BaseRepositoryImplTest;
import com.theshamuel.medreg.model.client.entity.Client;
import com.theshamuel.medreg.model.doctor.entity.Doctor;
import com.theshamuel.medreg.model.service.entity.Service;
import com.theshamuel.medreg.model.types.CategoryOfService;
import com.theshamuel.medreg.model.visit.entity.Visit;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

/**
 * The integration tests for {@link VisitRepositoryImpl}
 *
 * @author Alex Gladkikh
 */
public class VisitRepositoryImplTest extends BaseRepositoryImplTest {

    VisitRepositoryImpl visitRepositoryImpl = new VisitRepositoryImpl();
    private Doctor doc1;
    private Doctor doc2;
    private Doctor doc3;
    private Client client1;
    private Client client2;
    private Client client3;
    private Visit visit3;

    @Test
    public void testFindByDateTimeEventAndDoctor() {
        Visit actual = visitRepositoryImpl
                .findByDateTimeEventAndDoctor(doc1, LocalDate.now(), LocalTime.parse("00:00"));
        assertThat(actual, nullValue());
        actual = visitRepositoryImpl
                .findByDateTimeEventAndDoctor(doc3, LocalDate.now(), LocalTime.parse("16:00"));
        assertThat(actual, is(visit3));
    }

    @Test
    public void testFindByDateEventAndDoctor() {
        List<Visit> actual = visitRepositoryImpl.findByDateEventAndDoctor(doc1, LocalDate.now());
        assertThat(actual.size(), is(1));
        actual = visitRepositoryImpl
                .findByDateEventAndDoctor(new Doctor(), LocalDate.now().plusDays(1));
        assertThat(actual.size(), is(0));
    }

    @Test
    public void testFindByDateEvent() {
        List<Visit> actual = visitRepositoryImpl.findByDateEvent(LocalDate.now());
        assertThat(actual.size(), is(3));
        actual = visitRepositoryImpl.findByDateEvent(LocalDate.now().plusDays(1));
        assertThat(actual.size(), is(0));

    }

    @Test
    public void testFindAllClientVisits() {
        List<Visit> actual = visitRepositoryImpl
                .findAllClientVisits(client1.getId(), CategoryOfService.CONSUTLATION);
        assertThat(actual.size(), is(1));
        actual = visitRepositoryImpl.findAllClientVisits(client2.getId(), CategoryOfService.ULTRA);
        assertThat(actual.size(), is(0));
    }

    @Before
    @Override
    public void createTestRecords() {
        initCollection("doctors");
        template.findAllAndRemove(Query.query(Criteria.where("id").exists(true)), Doctor.class);
        doc1 = new DoctorBuilder().name("Doctor").surname("Doctorov").middlename("Petrovich")
                .contractor(0).isNotWork(0).excludeFromReport(0).position("therapist").build();
        doc2 = new DoctorBuilder().name("Doctor2").surname("Doctorov2").middlename("Alexeevich")
                .contractor(0).isNotWork(0).excludeFromReport(0).position("oculist").build();
        doc3 = new DoctorBuilder().name("Doctor3").surname("Doctorov3").middlename("Ivanovich")
                .contractor(0).isNotWork(0).excludeFromReport(0).position("surgeon").build();
        template.save(doc1);
        template.save(doc2);
        template.save(doc3);

        initCollection("appointments");
        template.findAllAndRemove(Query.query(Criteria.where("id").exists(true)),
                Appointment.class);

        Appointment appointment1 = new AppointmentBuilder().client("Ivanov Ivan").service("uzi")
                .doctor(doc1).dateEvent(LocalDate.now()).timeEvent(LocalTime.parse("12:00"))
                .build();
        Appointment appointment2 = new AppointmentBuilder().client("Petrov Petr")
                .service("checking").doctor(doc2).dateEvent(LocalDate.now())
                .timeEvent(LocalTime.parse("08:00")).build();
        Appointment appointment3 = new AppointmentBuilder().client("Sidorova Anna")
                .service("surgeon operation").doctor(doc3).dateEvent(LocalDate.now())
                .timeEvent(LocalTime.parse("16:00")).build();
        template.save(appointment1);
        template.save(appointment2);
        template.save(appointment3);

        initCollection("services");
        template.findAllAndRemove(Query.query(Criteria.where("id").exists(true)), Service.class);

        Service service1 = new ServiceBuilder().label("uzi").price(BigInteger.valueOf(500))
                .category(CategoryOfService.ULTRA).build();
        Service service2 = new ServiceBuilder().label("surgeon operation")
                .price(BigInteger.valueOf(1000)).category(CategoryOfService.CONSUTLATION).build();
        Service service3 = new ServiceBuilder().label("checking").price(BigInteger.valueOf(200))
                .category(CategoryOfService.CONSUTLATION).build();

        initCollection("clients");
        template.findAllAndRemove(Query.query(Criteria.where("id").exists(true)), Client.class);

        client1 = new ClientBuilder().name("Ivan").surname("Ivanov").passportSerial("1111")
                .passportNumber("222222").address("Lenina 1").gender("men").build();
        client2 = new ClientBuilder().name("Petr").surname("Petrov").passportSerial("3333")
                .passportNumber("444444").address("Mira 11").gender("men").build();
        client3 = new ClientBuilder().name("Anna").surname("Sidorova").passportSerial("5555")
                .passportNumber("555555").address("Req Squarer 118").gender("women").build();
        template.save(client1);
        template.save(client2);
        template.save(client3);

        initCollection("visits");
        template.findAllAndRemove(Query.query(Criteria.where("id").exists(true)), Visit.class);

        List<Service> services = new ArrayList<>();
        services.add(service1);
        Visit visit1 = new VisitBuilder().appointment(appointment1).doctor(doc1).client(client1)
                .dateEvent(appointment1.getDateEvent()).timeEvent(appointment1.getTimeEvent())
                .services(services).build();
        services.clear();
        services.add(service2);
        Visit visit2 = new VisitBuilder().appointment(appointment2).doctor(doc2).client(client2)
                .dateEvent(appointment2.getDateEvent()).timeEvent(appointment2.getTimeEvent())
                .services(services).build();
        services.clear();
        services.add(service3);
        visit3 = new VisitBuilder().appointment(appointment3).doctor(doc3).client(client3)
                .dateEvent(appointment3.getDateEvent()).timeEvent(appointment3.getTimeEvent())
                .services(services).build();
        template.save(visit1);
        template.save(visit2);
        template.save(visit3);

    }

    @Override
    public void setMongo() {
        visitRepositoryImpl.setMongo(template);
    }
}
