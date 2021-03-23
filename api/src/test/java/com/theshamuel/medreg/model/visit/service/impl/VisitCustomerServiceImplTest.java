package com.theshamuel.medreg.model.visit.service.impl;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import com.theshamuel.medreg.buiders.AppointmentBuilder;
import com.theshamuel.medreg.buiders.DoctorBuilder;
import com.theshamuel.medreg.buiders.ServiceBuilder;
import com.theshamuel.medreg.buiders.VisitBuilder;
import com.theshamuel.medreg.model.appointment.dao.AppointmentRepository;
import com.theshamuel.medreg.model.appointment.entity.Appointment;
import com.theshamuel.medreg.model.client.dao.ClientRepository;
import com.theshamuel.medreg.model.doctor.dao.DoctorRepository;
import com.theshamuel.medreg.model.doctor.entity.Doctor;
import com.theshamuel.medreg.model.customerservice.dao.CustomerCustomerServiceRepository;
import com.theshamuel.medreg.model.customerservice.dto.CustomerServiceDto;
import com.theshamuel.medreg.model.customerservice.entity.CustomerService;
import com.theshamuel.medreg.model.customerservice.service.CustomerServiceService;
import com.theshamuel.medreg.model.visit.dao.VisitRepository;
import com.theshamuel.medreg.model.visit.dto.VisitDto;
import com.theshamuel.medreg.model.visit.entity.Visit;
import com.theshamuel.medreg.model.visit.service.VisitService;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

/**
 * The unit tests for {@link VisitServiceImpl}
 *
 * @author Alex Gladkikh
 */
public class VisitCustomerServiceImplTest {

    @Mock
    private VisitRepository visitRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private CustomerCustomerServiceRepository customerServiceRepository;

    @Mock
    private CustomerServiceService customerServiceService;

    private VisitService visitService;

    @Before
    public void setUp() {
        initMocks(this);
        visitService = new VisitServiceImpl(visitRepository, doctorRepository, clientRepository,
                appointmentRepository, customerServiceRepository, customerServiceService);
    }

    @Test
    public void testIsUniqueVisit() {
        Appointment appointment = new AppointmentBuilder().id("app1").dateEvent(LocalDate.now())
                .timeEvent(LocalTime.parse("15:00")).build();
        Doctor doctor = new DoctorBuilder().id("doc1").build();
        when(appointmentRepository.findOne(appointment.getId())).thenReturn(appointment);
        when(doctorRepository.findOne(doctor.getId())).thenReturn(doctor);
        when(visitRepository.findByDateTimeEventAndDoctor(doctor, appointment.getDateEvent(),
                appointment.getTimeEvent())).thenReturn(new VisitBuilder().id("visit01").build());

        Boolean actual = visitService.isUniqueVisit(doctor.getId(), appointment.getId());
        assertThat(actual, is(false));

        actual = visitService.isUniqueVisit(doctor.getId(), null);
        assertThat(actual, is(true));

        actual = visitService.isUniqueVisit(null, null);
        assertThat(actual, is(true));

        verify(appointmentRepository, times(1)).findOne(appointment.getId());
        verify(doctorRepository, times(2)).findOne(doctor.getId());
        verify(visitRepository, times(1))
                .findByDateTimeEventAndDoctor(doctor, LocalDate.now(), LocalTime.parse("15:00"));

    }

    @Test
    public void testGetServices() {
        Appointment appointment = new AppointmentBuilder().id("app1").dateEvent(LocalDate.now())
                .timeEvent(LocalTime.parse("15:00")).build();
        Doctor doctor = new DoctorBuilder().id("doc1").build();

        List<CustomerService> customerServices = new ArrayList<>();
        customerServices.add(
                new ServiceBuilder().id("s01").label("Ultrasound 1").price(BigInteger.valueOf(1000))
                        .build());
        customerServices.add(new ServiceBuilder().id("s02").label("Consultation 1")
                .price(BigInteger.valueOf(500)).build());

        Visit visit = new VisitBuilder().id("visit01").appointment(appointment).doctor(doctor)
                .services(customerServices).build();

        when(visitRepository.findOne(visit.getId())).thenReturn(visit);
        when(customerServiceService.obj2dto(customerServices.get(0))).thenReturn(
                new CustomerServiceDto(customerServices.get(0).getId(), customerServices.get(0).getLabel(),
                        customerServices.get(0).getPrice(), customerServices.get(0).getDiscount()));
        when(customerServiceService.obj2dto(customerServices.get(1))).thenReturn(
                new CustomerServiceDto(customerServices.get(1).getId(), customerServices.get(1).getLabel(),
                        customerServices.get(1).getPrice(), customerServices.get(1).getDiscount()));

        List<CustomerServiceDto> actualDto = visitService.getServices(visit.getId());
        List<CustomerServiceDto> expectedDto = customerServices.stream().map(customerServiceService::obj2dto)
                .collect(Collectors.toList());
        assertThat(actualDto.size(), is(2));

        assertThat(actualDto, hasItems(expectedDto.get(0), expectedDto.get(1)));

        verify(visitRepository, times(1)).findOne(visit.getId());
        verify(customerServiceService, times(1)).obj2dto(customerServices.get(0));
        verify(customerServiceService, times(1)).obj2dto(customerServices.get(1));

    }

    @Test
    public void testGetVisitsByDoctorAndDateEvent() {
        Appointment appointment = new AppointmentBuilder().id("app1").dateEvent(LocalDate.now())
                .timeEvent(LocalTime.parse("15:00")).build();
        Appointment appointment2 = new AppointmentBuilder().id("app2").dateEvent(LocalDate.now())
                .timeEvent(LocalTime.parse("16:00")).build();
        Appointment appointment3 = new AppointmentBuilder().id("app3").dateEvent(LocalDate.now())
                .timeEvent(LocalTime.parse("17:00")).build();
        List<CustomerService> customerServices = new ArrayList<>();
        customerServices.add(
                new ServiceBuilder().id("s01").label("Ultrasound 1").price(BigInteger.valueOf(1000))
                        .build());
        customerServices.add(new ServiceBuilder().id("s02").label("Consultation 1")
                .price(BigInteger.valueOf(500)).build());

        List<CustomerService> services2 = new ArrayList<>();
        services2.add(new ServiceBuilder().id("s201").label("Ultrasound 2")
                .price(BigInteger.valueOf(500)).build());
        services2.add(new ServiceBuilder().id("s202").label("Consultation 2")
                .price(BigInteger.valueOf(100)).build());

        Doctor doctor = new DoctorBuilder().id("doc1").build();
        Doctor doctor2 = new DoctorBuilder().id("doc2").build();

        Visit visit = new VisitBuilder().id("visit01").appointment(appointment).doctor(doctor)
                .services(customerServices).build();
        Visit visit2 = new VisitBuilder().id("visit02").appointment(appointment2).doctor(doctor)
                .services(customerServices).build();
        Visit visit3 = new VisitBuilder().id("visit03").appointment(appointment3).doctor(doctor2)
                .services(services2).build();
        List<Visit> visits = new ArrayList<>();
        visits.add(visit);
        visits.add(visit2);

        List<Visit> visits2 = new ArrayList<>();
        visits2.add(visit3);

        when(doctorRepository.findOne(doctor.getId())).thenReturn(doctor);
        when(doctorRepository.findOne(doctor2.getId())).thenReturn(doctor2);

        when(visitRepository.findByDoctorAndDateEvent(doctor, LocalDate.now())).thenReturn(visits);
        when(visitRepository.findByDoctorAndDateEvent(doctor2, LocalDate.now()))
                .thenReturn(visits2);

        List<VisitDto> expected = new ArrayList<>();
        expected.add(visitService.obj2dto(visit));
        expected.add(visitService.obj2dto(visit2));

        List<VisitDto> expected2 = new ArrayList<>();
        expected2.add(visitService.obj2dto(visit3));

        List<VisitDto> actual = visitService
                .getVisitsByDoctorAndDateEvent(doctor.getId(), LocalDate.now());
        assertThat(actual, hasItems(expected.get(0), expected.get(1)));
        assertThat(actual.get(0).getTotalSum(), is(BigInteger.valueOf(1500)));
        assertThat(actual.get(1).getTotalSum(), is(BigInteger.valueOf(1500)));

        actual = visitService.getVisitsByDoctorAndDateEvent(doctor2.getId(), LocalDate.now());
        assertThat(actual, hasItem(expected2.get(0)));
        assertThat(actual.get(0).getTotalSum(), is(BigInteger.valueOf(600)));

        verify(doctorRepository, times(1)).findOne(doctor.getId());
        verify(doctorRepository, times(1)).findOne(doctor2.getId());
        verify(visitRepository, times(1)).findByDoctorAndDateEvent(doctor, LocalDate.now());
        verify(visitRepository, times(1)).findByDoctorAndDateEvent(doctor2, LocalDate.now());

    }

    @Test
    public void testGetVisitsByDateEvent() {
        Appointment appointment = new AppointmentBuilder().id("app1").dateEvent(LocalDate.now())
                .timeEvent(LocalTime.parse("15:00")).build();
        Appointment appointment2 = new AppointmentBuilder().id("app2").dateEvent(LocalDate.now())
                .timeEvent(LocalTime.parse("16:00")).build();
        Appointment appointment3 = new AppointmentBuilder().id("app3").dateEvent(LocalDate.now())
                .timeEvent(LocalTime.parse("17:00")).build();
        List<CustomerService> customerServices = new ArrayList<>();
        customerServices.add(
                new ServiceBuilder().id("s01").label("Ultrasound 1").price(BigInteger.valueOf(1000))
                        .build());
        customerServices.add(new ServiceBuilder().id("s02").label("Consultation 1")
                .price(BigInteger.valueOf(500)).build());

        List<CustomerService> services2 = new ArrayList<>();
        services2.add(new ServiceBuilder().id("s201").label("Ultrasound 2")
                .price(BigInteger.valueOf(500)).build());
        services2.add(new ServiceBuilder().id("s202").label("Consultation 2")
                .price(BigInteger.valueOf(100)).build());

        Doctor doctor = new DoctorBuilder().id("doc1").build();

        Visit visit = new VisitBuilder().id("visit01").appointment(appointment).doctor(doctor)
                .services(customerServices).build();
        Visit visit2 = new VisitBuilder().id("visit02").appointment(appointment2).doctor(doctor)
                .services(customerServices).build();
        Visit visit3 = new VisitBuilder().id("visit03").appointment(appointment3).doctor(doctor)
                .services(services2).build();
        List<Visit> visits = new ArrayList<>();
        visits.add(visit);
        visits.add(visit2);
        visits.add(visit3);

        when(visitRepository.findByDateEvent(LocalDate.now())).thenReturn(visits);

        List<VisitDto> expected = new ArrayList<>();
        expected.add(visitService.obj2dto(visit));
        expected.add(visitService.obj2dto(visit2));
        expected.add(visitService.obj2dto(visit3));

        List<VisitDto> actual = visitService.getVisitsByDateEvent(LocalDate.now());

        assertThat(actual, hasItems(expected.get(0), expected.get(1), expected.get(2)));
        assertThat(actual.get(0).getTotalSum(), is(BigInteger.valueOf(1500)));
        assertThat(actual.get(1).getTotalSum(), is(BigInteger.valueOf(1500)));
        assertThat(actual.get(2).getTotalSum(), is(BigInteger.valueOf(600)));

        verify(visitRepository, times(1)).findByDateEvent(LocalDate.now());

    }
}
