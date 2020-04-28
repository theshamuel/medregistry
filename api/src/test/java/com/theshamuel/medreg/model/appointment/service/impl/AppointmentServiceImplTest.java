package com.theshamuel.medreg.model.appointment.service.impl;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import com.theshamuel.medreg.buiders.AppointmentBuilder;
import com.theshamuel.medreg.buiders.DoctorBuilder;
import com.theshamuel.medreg.buiders.ScheduleBuilder;
import com.theshamuel.medreg.buiders.VisitBuilder;
import com.theshamuel.medreg.model.appointment.dao.AppointmentRepository;
import com.theshamuel.medreg.model.appointment.dto.AppointmentDto;
import com.theshamuel.medreg.model.appointment.entity.Appointment;
import com.theshamuel.medreg.model.appointment.service.AppointmentService;
import com.theshamuel.medreg.model.doctor.dao.DoctorRepository;
import com.theshamuel.medreg.model.doctor.entity.Doctor;
import com.theshamuel.medreg.model.schedule.dao.ScheduleRepository;
import com.theshamuel.medreg.model.schedule.entity.Schedule;
import com.theshamuel.medreg.model.visit.dao.VisitRepository;
import com.theshamuel.medreg.model.visit.entity.Visit;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

/**
 * The unit tests for {@link AppointmentServiceImpl}
 *
 * @author Alex Gladkikh
 */
public class AppointmentServiceImplTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private VisitRepository visitRepository;

    @Mock
    private AppointmentService appointmentService;

    @Before
    public void setUp() {
        initMocks(this);
        appointmentService = new AppointmentServiceImpl(appointmentRepository, doctorRepository,
                scheduleRepository, visitRepository);
    }

    @Test
    public void testGetReservedAppointmentsByDoctorDateEventHasVisit() {
        Doctor docIvanov = new DoctorBuilder().id("d0001").name("Ivan").surname("Ivanov")
                .middlename("Petrovich").contractor(0).isNotWork(0).excludeFromReport(0)
                .position("therapist").build();
        Schedule scheduleForIvanov = new ScheduleBuilder().id("sch0001").dateWork(LocalDate.now())
                .doctor(docIvanov).timeFrom(LocalTime.of(8, 0)).timeTo(LocalTime.of(9, 15))
                .interval(15).build();
        Visit visitToIvanov = new VisitBuilder().id("v0001").appointment(
                new AppointmentBuilder().id("a0001").doctor(docIvanov).dateEvent(LocalDate.now())
                        .timeEvent(LocalTime.of(8, 00)).hasVisit(true).build()).doctor(docIvanov).
                dateEvent(LocalDate.now()).build();
        List<Appointment> appointmentList = Arrays
                .asList(new AppointmentBuilder().id("a0001").doctor(docIvanov)
                                .dateEvent(LocalDate.now()).timeEvent(LocalTime.of(8, 00)).hasVisit(true)
                                .build(),
                        new AppointmentBuilder().id("a0002").doctor(docIvanov)
                                .dateEvent(LocalDate.now()).timeEvent(LocalTime.of(8, 15))
                                .hasVisit(true).build(),
                        new AppointmentBuilder().id("a0003").doctor(docIvanov)
                                .dateEvent(LocalDate.now()).timeEvent(LocalTime.of(8, 30))
                                .hasVisit(false).build());
        when(appointmentRepository
                .findReservedAppointmentsByDoctorAndDateAndHasVisit(docIvanov, LocalDate.now(), true))
                .thenReturn(appointmentList);
        when(doctorRepository.findOne("d0001")).thenReturn(docIvanov);
        when(scheduleRepository.findByDateWorkAndDoctor(docIvanov, LocalDate.now()))
                .thenReturn(scheduleForIvanov);
        when(appointmentRepository
                .findReservedAppointmentsByDoctorAndDate(docIvanov, LocalDate.now()))
                .thenReturn(appointmentList);

        List<AppointmentDto> actualDto = appointmentService
                .getReservedAppointmentsByDoctorDateEventHasVisit(docIvanov.getId(),
                        LocalDate.now(), true, visitToIvanov.getId());
        assertThat(actualDto.size(), is(3));

        when(appointmentRepository
                .findReservedAppointmentsByDoctorAndDateAndHasVisit(docIvanov, LocalDate.now(), false))
                .thenReturn(Collections.emptyList());
        actualDto = appointmentService
                .getReservedAppointmentsByDoctorDateEventHasVisit(docIvanov.getId(),
                        LocalDate.now(), false, visitToIvanov.getId());
        assertThat(actualDto.size(), is(0));

        verify(appointmentRepository, times(1))
                .findReservedAppointmentsByDoctorAndDateAndHasVisit(docIvanov, LocalDate.now(), true);
        verify(appointmentRepository, times(1))
                .findReservedAppointmentsByDoctorAndDateAndHasVisit(docIvanov, LocalDate.now(), false);

    }

    @Test
    public void testGetFreeTimeAppointmentsByDoctorDateEvent() {

        Appointment updatedAppointment = new AppointmentBuilder().id("a0003")
                .dateEvent(LocalDate.now()).timeEvent(LocalTime.of(8, 0)).build();
        Doctor docIvanov = new DoctorBuilder().id("d0001").name("Ivan").surname("Ivanov")
                .middlename("Petrovich").contractor(0).isNotWork(0).excludeFromReport(0)
                .position("therapist").build();

        Schedule scheduleForIvanov = new ScheduleBuilder().id("sch0001").dateWork(LocalDate.now())
                .doctor(docIvanov).timeFrom(LocalTime.of(8, 0)).timeTo(LocalTime.of(9, 15))
                .interval(15).build();
        List<Appointment> appointmentList = Arrays
                .asList(new AppointmentBuilder().id("a0001").doctor(docIvanov)
                                .dateEvent(LocalDate.now()).timeEvent(LocalTime.of(8, 00)).build(),
                        new AppointmentBuilder().id("a0002").doctor(docIvanov)
                                .dateEvent(LocalDate.now()).timeEvent(LocalTime.of(8, 15)).build(),
                        new AppointmentBuilder().id("a0003").doctor(docIvanov)
                                .dateEvent(LocalDate.now()).timeEvent(LocalTime.of(8, 30)).build());

        when(doctorRepository.findOne("d0001")).thenReturn(docIvanov);
        when(scheduleRepository.findByDateWorkAndDoctor(docIvanov, LocalDate.now()))
                .thenReturn(scheduleForIvanov);
        when(appointmentRepository.findOne(updatedAppointment.getId()))
                .thenReturn(updatedAppointment);
        when(appointmentRepository
                .findReservedAppointmentsByDoctorAndDate(docIvanov, LocalDate.now()))
                .thenReturn(appointmentList);

        List<AppointmentDto> actualDto = appointmentService
                .getFreeTimeAppointmentsByDoctorDateEvent(docIvanov.getId(), LocalDate.now(),
                        updatedAppointment.getId());
        assertThat(actualDto.size(), is(3));

        //Check only time in free appointments + updated time (3rd element)
        assertThat(actualDto.stream().map(i -> i.getTimeEvent()).collect(Collectors.toList()),
                hasItems(LocalTime.of(8, 45), LocalTime.of(9, 00), LocalTime.of(8, 00)));

        actualDto = appointmentService
                .getFreeTimeAppointmentsByDoctorDateEvent(docIvanov.getId(), LocalDate.now());
        assertThat(actualDto.size(), is(2));

        //Check only time in free appointments without updated time (3rd element)
        assertThat(actualDto.stream().map(i -> i.getTimeEvent()).collect(Collectors.toList()),
                hasItems(LocalTime.of(8, 45), LocalTime.of(9, 00)));

        verify(appointmentRepository, times(1)).findOne(updatedAppointment.getId());
        verify(appointmentRepository, times(2))
                .findReservedAppointmentsByDoctorAndDate(docIvanov, LocalDate.now());
        verify(doctorRepository, times(2)).findOne("d0001");
        verify(scheduleRepository, times(2)).findByDateWorkAndDoctor(docIvanov, LocalDate.now());

    }

    @Test
    public void testGetAllAppointmentsByDateEventAfterTimeEvent() {

        List<Appointment> appointmentList = Arrays
                .asList(new AppointmentBuilder().id("a0001").dateEvent(LocalDate.now())
                                .timeEvent(LocalTime.of(8, 00)).build(),
                        new AppointmentBuilder().id("a0002").dateEvent(LocalDate.now())
                                .timeEvent(LocalTime.of(8, 15)).build(),
                        new AppointmentBuilder().id("a0003").dateEvent(LocalDate.now())
                                .timeEvent(LocalTime.of(8, 30)).build());
        when(appointmentRepository
                .findReservedAppointmentsByDateEventAfterTimeEvent(LocalDate.now()))
                .thenReturn(appointmentList);

        List<AppointmentDto> actualDto = appointmentService
                .getAllAppointmentsByDateEventAfterTimeEvent(LocalDate.now(), LocalTime.of(8, 1));
        List<AppointmentDto> expectedDto = appointmentList.stream().map(appointmentService::obj2dto)
                .filter(i -> !i.getId().equals("a0001")).collect(Collectors.toList());
        assertThat(actualDto, hasItems(expectedDto.get(0), expectedDto.get(1)));

        actualDto = appointmentService
                .getAllAppointmentsByDateEventAfterTimeEvent(LocalDate.now(), LocalTime.of(12, 1));
        assertThat(actualDto, is(Collections.emptyList()));

        verify(appointmentRepository, times(2))
                .findReservedAppointmentsByDateEventAfterTimeEvent(LocalDate.now());

    }

}
