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
package com.theshamuel.medreg.model.appointment.service.impl;

import com.theshamuel.medreg.exception.NotFoundEntityException;
import com.theshamuel.medreg.model.appointment.dao.AppointmentRepository;
import com.theshamuel.medreg.model.appointment.dto.AppointmentDto;
import com.theshamuel.medreg.model.appointment.entity.Appointment;
import com.theshamuel.medreg.model.appointment.service.AppointmentService;
import com.theshamuel.medreg.model.baseclasses.entity.BaseEntity;
import com.theshamuel.medreg.model.baseclasses.service.BaseServiceImpl;
import com.theshamuel.medreg.model.doctor.dao.DoctorRepository;
import com.theshamuel.medreg.model.doctor.entity.Doctor;
import com.theshamuel.medreg.model.schedule.dao.ScheduleRepository;
import com.theshamuel.medreg.model.schedule.entity.Schedule;
import com.theshamuel.medreg.model.visit.dao.VisitRepository;
import com.theshamuel.medreg.model.visit.entity.Visit;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

/**
 * The type Appointment service.
 */
@Service
public class AppointmentServiceImpl extends BaseServiceImpl<AppointmentDto, Appointment> implements
        AppointmentService {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentServiceImpl.class);

    private final AppointmentRepository appointmentRepository;

    private final DoctorRepository doctorRepository;

    private final ScheduleRepository scheduleRepository;

    private final VisitRepository visitRepository;

    /**
     * Instantiates a new Appointment service.
     *
     * @param appointmentRepository the appointment repository
     * @param doctorRepository      the doctor repository
     * @param scheduleRepository    the schedule repository
     * @param visitRepository       the visit repository
     */
    @Autowired
    public AppointmentServiceImpl(AppointmentRepository appointmentRepository,
            DoctorRepository doctorRepository, ScheduleRepository scheduleRepository,
            VisitRepository visitRepository) {
        super(appointmentRepository);
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.scheduleRepository = scheduleRepository;
        this.visitRepository = visitRepository;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean findByDoctorAndDateEventAndTimeEvent(String doctorId, LocalDate dateEvent,
            LocalTime timeEvent) {
        Optional<Doctor> doctor = Optional.ofNullable(doctorRepository.findOne(doctorId));

        final boolean[] result = new boolean[]{false};
        doctor.ifPresent(e -> {
            if (appointmentRepository.findByDateTimeEventAndDoctor(e, dateEvent, timeEvent)
                    != null) {
                result[0] = true;
            }
        });
        return result[0];
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean findAppointmentInScheduleSlots(String doctorId, LocalDate dateEvent,
            LocalTime timeEvent) {
        Optional<Doctor> doctor = Optional.ofNullable(doctorRepository.findOne(doctorId));
        final boolean[] result = new boolean[]{false};
        doctor.ifPresent(e -> {
            if (scheduleRepository.checkAppointmentInScheduleSlots(e, dateEvent, timeEvent)
                    != null) {
                result[0] = true;
            }

        });

        return result[0];
    }

    @Override
    public Page<AppointmentDto> findByFilter(PageRequest pageRequest, String filter) {
        if (!filter.contains("doctor")) {
            return super.findByFilter(pageRequest, filter);
        } else {
            Page<AppointmentDto> result = super.findByFilter(pageRequest, filter);
            String[] params = filter.trim().split(";");
            for (String param : params) {
                String[] tokens = param.trim().split("=");
                if (tokens[0].equals("doctor") && tokens.length == 2 && result != null
                        && result.getContent() != null) {
                    List<Doctor> doctorList = doctorRepository.findBySurnameWeak(tokens[1]);
                    List<AppointmentDto> content = result.getContent();
                    List<String> doctorsId = doctorList.stream().map(doctor -> doctor.getId())
                            .collect(Collectors.toList());
                    content = content.stream().filter(p -> doctorsId.contains(p.getDoctorId()))
                            .collect(Collectors.toList());
                    result = new PageImpl<>(content, pageRequest, content.size());
                    return result;
                }
            }
        }
        return new PageImpl<>(Collections.emptyList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AppointmentDto> getAppointmentsByDoctorDateEventWithState(String doctorId,
            LocalDate dateEvent) {
        List<AppointmentDto> result = new ArrayList<>();
        Optional<Doctor> doctor = Optional.ofNullable(doctorRepository.findOne(doctorId));
        doctor.ifPresent(e -> {
            Optional<Schedule> schedule = Optional
                    .ofNullable(scheduleRepository.findByDateWorkAndDoctor(e, dateEvent));
            if (schedule.isPresent()) {

                Optional<LocalTime> startWorkDay = Optional
                        .ofNullable(schedule.get().getTimeFrom());
                Optional<LocalTime> endWorkDay = Optional.ofNullable(schedule.get().getTimeTo());
                Optional<LocalTime> breakFrom = Optional.ofNullable(schedule.get().getBreakFrom());
                Optional<LocalTime> breakTo = Optional.ofNullable(schedule.get().getBreakTo());

                Optional<Integer> interval = Optional.ofNullable(schedule.get().getInterval());
                Optional<LocalTime> startWorkDayOnline = Optional
                        .ofNullable(schedule.get().getTimeFromOnline());
                Optional<LocalTime> endWorkDayOnline = Optional
                        .ofNullable(schedule.get().getTimeToOnline());

                /*
                    The use case when online time is does not set in schedule at workday.
                 */
                if (startWorkDay.isPresent() && endWorkDay.isPresent() && interval.isPresent()
                        && (!startWorkDayOnline.isPresent() || !endWorkDayOnline.isPresent())) {
                    LocalTime currentTime = startWorkDay.get();
                    Map<LocalTime, AppointmentDto> cacheOfAppointments = new HashMap<>();
                    while (currentTime
                            .isBefore(endWorkDay.get().minusMinutes(interval.get() / 2))) {
                        if (breakFrom.isPresent() && breakTo.isPresent() && !(
                                currentTime.isAfter(breakFrom.get()) && currentTime
                                        .isBefore(breakTo.get()))
                                && !currentTime.equals(breakFrom.get()) && !(
                                currentTime.isAfter(breakFrom.get().minusMinutes(interval.get()))
                                        && currentTime.isBefore(breakFrom.get()))
                                || !breakFrom.isPresent() || !breakTo.isPresent()) {
                            AppointmentDto appointmentDto = new AppointmentDto();
                            appointmentDto.setTimeEvent(currentTime);
                            appointmentDto.setState(0);
                            result.add(appointmentDto);
                            cacheOfAppointments.put(currentTime, appointmentDto);
                        }
                        currentTime = currentTime.plusMinutes(interval.get());
                    }
                    List<Appointment> reservedAppointments = appointmentRepository
                            .findReservedAppointmentsByDoctorAndDate(e, dateEvent);
                    reservedAppointments.stream().forEachOrdered((item) -> {
                        if (cacheOfAppointments.get(item.getTimeEvent()) != null) {
                            cacheOfAppointments.get(item.getTimeEvent()).setState(1);
                            cacheOfAppointments.get(item.getTimeEvent())
                                    .setClient(item.getClient());
                            cacheOfAppointments.get(item.getTimeEvent()).setPhone(item.getPhone());
                            cacheOfAppointments.get(item.getTimeEvent())
                                    .setService(item.getService());
                        }
                    });

                }
                /*
                    The use case when online time is go after ordinary work time in schedule at workday.
                    Graph timeline description:
                    1)
                     |-------------|       |---------|
                        NonOnline(N)        Online(O)
                    2)
                     |---------------------|---------|
                        NonOnline(N)        Online(O)
                 */
                else if (startWorkDay.isPresent() && endWorkDay.isPresent() && interval.isPresent()
                        && startWorkDayOnline.isPresent() && endWorkDayOnline.isPresent()
                        && (endWorkDay.get().isBefore(startWorkDayOnline.get())
                        || endWorkDay.get().equals(startWorkDayOnline.get()))) {
                    LocalTime currentTime = startWorkDay.get();
                    Map<LocalTime, AppointmentDto> cacheOfAppointments = new HashMap<>();
                    while (currentTime
                            .isBefore(endWorkDay.get().minusMinutes(interval.get() / 2))) {
                        if (breakFrom.isPresent() && breakTo.isPresent() && !(
                                currentTime.isAfter(breakFrom.get()) && currentTime
                                        .isBefore(breakTo.get()))
                                && !currentTime.equals(breakFrom.get()) && !(
                                currentTime.isAfter(breakFrom.get().minusMinutes(interval.get()))
                                        && currentTime.isBefore(breakFrom.get()))
                                || !breakFrom.isPresent() || !breakTo.isPresent()) {
                            AppointmentDto appointmentDto = new AppointmentDto();
                            appointmentDto.setTimeEvent(currentTime);
                            appointmentDto.setState(0);
                            appointmentDto.setIsOnline(0);
                            result.add(appointmentDto);
                            cacheOfAppointments.put(currentTime, appointmentDto);
                        }
                        currentTime = currentTime.plusMinutes(interval.get());
                    }
                    LocalTime currentTimeOnline = startWorkDayOnline.get();
                    while (currentTimeOnline
                            .isBefore(endWorkDayOnline.get().minusMinutes(interval.get() / 2))) {
                        if (breakFrom.isPresent() && breakTo.isPresent() && !(
                                currentTime.isAfter(breakFrom.get()) && currentTime
                                        .isBefore(breakTo.get()))
                                && !currentTime.equals(breakFrom.get()) && !(
                                currentTime.isAfter(breakFrom.get().minusMinutes(interval.get()))
                                        && currentTime.isBefore(breakFrom.get()))
                                || !breakFrom.isPresent() || !breakTo.isPresent()) {
                            AppointmentDto appointmentDto = new AppointmentDto();
                            appointmentDto.setTimeEvent(currentTimeOnline);
                            appointmentDto.setState(0);
                            appointmentDto.setIsOnline(1);
                            result.add(appointmentDto);
                            cacheOfAppointments.put(currentTimeOnline, appointmentDto);
                        }
                        currentTimeOnline = currentTimeOnline.plusMinutes(interval.get());
                    }
                    List<Appointment> reservedAppointments = appointmentRepository
                            .findReservedAppointmentsByDoctorAndDate(e, dateEvent);
                    reservedAppointments.stream().forEachOrdered((item) -> {
                        if (cacheOfAppointments.get(item.getTimeEvent()) != null) {
                            cacheOfAppointments.get(item.getTimeEvent()).setState(1);
                            cacheOfAppointments.get(item.getTimeEvent())
                                    .setClient(item.getClient());
                            cacheOfAppointments.get(item.getTimeEvent()).setPhone(item.getPhone());
                            cacheOfAppointments.get(item.getTimeEvent())
                                    .setService(item.getService());
                        }
                    });
                }/*
                    Graph timeline description:
                    1)
                    |--------------|
                        N        |-------|
                                    O
                 */ else if (startWorkDay.isPresent() && endWorkDay.isPresent() && interval
                        .isPresent()
                        && startWorkDayOnline.isPresent() && endWorkDayOnline.isPresent()
                        && ((endWorkDay.get().isAfter(startWorkDayOnline.get()) && endWorkDayOnline
                        .get().isAfter(endWorkDay.get())) ||
                        (endWorkDay.get().equals(endWorkDayOnline.get()) && startWorkDayOnline.get()
                                .isAfter(startWorkDay.get())))) {
                    LocalTime currentTime = startWorkDay.get();
                    Map<LocalTime, AppointmentDto> cacheOfAppointments = new HashMap<>();
                    while (currentTime
                            .isBefore(startWorkDayOnline.get().minusMinutes(interval.get() / 2))) {
                        if (breakFrom.isPresent() && breakTo.isPresent() && !(
                                currentTime.isAfter(breakFrom.get()) && currentTime
                                        .isBefore(breakTo.get()))
                                && !currentTime.equals(breakFrom.get()) && !(
                                currentTime.isAfter(breakFrom.get().minusMinutes(interval.get()))
                                        && currentTime.isBefore(breakFrom.get()))
                                || !breakFrom.isPresent() || !breakTo.isPresent()) {
                            AppointmentDto appointmentDto = new AppointmentDto();
                            appointmentDto.setTimeEvent(currentTime);
                            appointmentDto.setState(0);
                            appointmentDto.setIsOnline(0);
                            result.add(appointmentDto);
                            cacheOfAppointments.put(currentTime, appointmentDto);
                        }
                        currentTime = currentTime.plusMinutes(interval.get());
                    }
                    LocalTime currentTimeOnline = startWorkDayOnline.get();
                    while (currentTimeOnline
                            .isBefore(endWorkDayOnline.get().minusMinutes(interval.get() / 2))) {
                        if (breakFrom.isPresent() && breakTo.isPresent() && !(
                                currentTime.isAfter(breakFrom.get()) && currentTime
                                        .isBefore(breakTo.get()))
                                && !currentTime.equals(breakFrom.get()) && !(
                                currentTime.isAfter(breakFrom.get().minusMinutes(interval.get()))
                                        && currentTime.isBefore(breakFrom.get()))
                                || !breakFrom.isPresent() || !breakTo.isPresent()) {
                            AppointmentDto appointmentDto = new AppointmentDto();
                            appointmentDto.setTimeEvent(currentTimeOnline);
                            appointmentDto.setState(0);
                            appointmentDto.setIsOnline(1);
                            result.add(appointmentDto);
                            cacheOfAppointments.put(currentTimeOnline, appointmentDto);
                        }
                        currentTimeOnline = currentTimeOnline.plusMinutes(interval.get());
                    }
                    List<Appointment> reservedAppointments = appointmentRepository
                            .findReservedAppointmentsByDoctorAndDate(e, dateEvent);
                    reservedAppointments.stream().forEachOrdered((item) -> {
                        if (cacheOfAppointments.get(item.getTimeEvent()) != null) {
                            cacheOfAppointments.get(item.getTimeEvent()).setState(1);
                            cacheOfAppointments.get(item.getTimeEvent())
                                    .setClient(item.getClient());
                            cacheOfAppointments.get(item.getTimeEvent()).setPhone(item.getPhone());
                            cacheOfAppointments.get(item.getTimeEvent())
                                    .setService(item.getService());
                        }
                    });
                }/*
                    Graph timeline description:
                    1)
                    |-------|
                        N
                    |-------|
                        O

                    2)
                        |-------|
                            N
                    |---------------|
                            O
                    3)
                           |--------|
                               N
                    |---------------|
                        O
                    4)
                    |--------|
                        N
                    |---------------|
                        O
                 */ else if (startWorkDay.isPresent() && endWorkDay.isPresent() && interval
                        .isPresent()
                        && startWorkDayOnline.isPresent() && endWorkDayOnline.isPresent()
                        && ((startWorkDay.get().equals(startWorkDayOnline.get()) && endWorkDay.get()
                        .equals(endWorkDayOnline.get()))
                        || (startWorkDay.get().isAfter(startWorkDayOnline.get()) && endWorkDay.get()
                        .isBefore(endWorkDayOnline.get()))
                        || (startWorkDay.get().isAfter(startWorkDayOnline.get()) && endWorkDay.get()
                        .equals(endWorkDayOnline.get()))
                        || (startWorkDay.get().equals(startWorkDayOnline.get()) && endWorkDay.get()
                        .isBefore(endWorkDayOnline.get())))) {
                    Map<LocalTime, AppointmentDto> cacheOfAppointments = new HashMap<>();
                    LocalTime currentTime = startWorkDayOnline.get();
                    while (currentTime
                            .isBefore(endWorkDayOnline.get().minusMinutes(interval.get() / 2))) {
                        if (breakFrom.isPresent() && breakTo.isPresent() && !(
                                currentTime.isAfter(breakFrom.get()) && currentTime
                                        .isBefore(breakTo.get()))
                                && !currentTime.equals(breakFrom.get()) && !(
                                currentTime.isAfter(breakFrom.get().minusMinutes(interval.get()))
                                        && currentTime.isBefore(breakFrom.get()))
                                || !breakFrom.isPresent() || !breakTo.isPresent()) {
                            AppointmentDto appointmentDto = new AppointmentDto();
                            appointmentDto.setTimeEvent(currentTime);
                            appointmentDto.setState(0);
                            appointmentDto.setIsOnline(1);
                            result.add(appointmentDto);
                            cacheOfAppointments.put(currentTime, appointmentDto);
                        }
                        currentTime = currentTime.plusMinutes(interval.get());
                    }
                    List<Appointment> reservedAppointments = appointmentRepository
                            .findReservedAppointmentsByDoctorAndDate(e, dateEvent);
                    reservedAppointments.stream().forEachOrdered((item) -> {
                        if (cacheOfAppointments.get(item.getTimeEvent()) != null) {
                            cacheOfAppointments.get(item.getTimeEvent()).setState(1);
                            cacheOfAppointments.get(item.getTimeEvent())
                                    .setClient(item.getClient());
                            cacheOfAppointments.get(item.getTimeEvent()).setPhone(item.getPhone());
                            cacheOfAppointments.get(item.getTimeEvent())
                                    .setService(item.getService());
                        }
                    });
                }/*
                    Graph timeline description:
                    1)
                        |--------|
                            N
                    |-------|
                        O

                    2)
                    |--------------|
                            N
                    |------|
                        O
                 */ else if (startWorkDay.isPresent() && endWorkDay.isPresent() && interval
                        .isPresent()
                        && startWorkDayOnline.isPresent() && endWorkDayOnline.isPresent()
                        && (
                        (startWorkDay.get().isAfter(startWorkDayOnline.get()) && endWorkDay.get()
                                .isAfter(endWorkDayOnline.get()) && startWorkDay.get()
                                .isBefore(endWorkDayOnline.get()))
                                || (startWorkDay.get().equals(startWorkDayOnline.get())
                                && endWorkDay.get().isAfter(endWorkDayOnline.get())))) {
                    LocalTime currentTime = startWorkDayOnline.get();
                    Map<LocalTime, AppointmentDto> cacheOfAppointments = new HashMap<>();
                    while (currentTime
                            .isBefore(endWorkDayOnline.get().minusMinutes(interval.get() / 2))) {
                        if (breakFrom.isPresent() && breakTo.isPresent() && !(
                                currentTime.isAfter(breakFrom.get()) && currentTime
                                        .isBefore(breakTo.get()))
                                && !currentTime.equals(breakFrom.get()) && !(
                                currentTime.isAfter(breakFrom.get().minusMinutes(interval.get()))
                                        && currentTime.isBefore(breakFrom.get()))
                                || !breakFrom.isPresent() || !breakTo.isPresent()) {
                            AppointmentDto appointmentDto = new AppointmentDto();
                            appointmentDto.setTimeEvent(currentTime);
                            appointmentDto.setState(0);
                            appointmentDto.setIsOnline(1);
                            result.add(appointmentDto);
                            cacheOfAppointments.put(currentTime, appointmentDto);
                        }
                        currentTime = currentTime.plusMinutes(interval.get());
                    }
                    currentTime = endWorkDayOnline.get();
                    while (currentTime
                            .isBefore(endWorkDay.get().minusMinutes(interval.get() / 2))) {
                        if (breakFrom.isPresent() && breakTo.isPresent() && !(
                                currentTime.isAfter(breakFrom.get()) && currentTime
                                        .isBefore(breakTo.get()))
                                && !currentTime.equals(breakFrom.get()) && !(
                                currentTime.isAfter(breakFrom.get().minusMinutes(interval.get()))
                                        && currentTime.isBefore(breakFrom.get()))
                                || !breakFrom.isPresent() || !breakTo.isPresent()) {
                            AppointmentDto appointmentDto = new AppointmentDto();
                            appointmentDto.setTimeEvent(currentTime);
                            appointmentDto.setState(0);
                            appointmentDto.setIsOnline(0);
                            result.add(appointmentDto);
                            cacheOfAppointments.put(currentTime, appointmentDto);
                        }
                        currentTime = currentTime.plusMinutes(interval.get());
                    }
                    List<Appointment> reservedAppointments = appointmentRepository
                            .findReservedAppointmentsByDoctorAndDate(e, dateEvent);
                    reservedAppointments.stream().forEachOrdered((item) -> {
                        if (cacheOfAppointments.get(item.getTimeEvent()) != null) {
                            cacheOfAppointments.get(item.getTimeEvent()).setState(1);
                            cacheOfAppointments.get(item.getTimeEvent())
                                    .setClient(item.getClient());
                            cacheOfAppointments.get(item.getTimeEvent()).setPhone(item.getPhone());
                            cacheOfAppointments.get(item.getTimeEvent())
                                    .setService(item.getService());
                        }
                    });
                }/*
                    Graph timeline description:
                    1)
                        |--------|
                            N
                    |-------|
                        O

                    2)
                    |--------------|
                            N
                    |------|
                        O
                 */ else if (startWorkDay.isPresent() && endWorkDay.isPresent() && interval
                        .isPresent()
                        && startWorkDayOnline.isPresent() && endWorkDayOnline.isPresent()
                        && (endWorkDayOnline.get().equals(startWorkDay.get()) && endWorkDay.get()
                        .isAfter(endWorkDayOnline.get()))) {
                    LocalTime currentTime = startWorkDayOnline.get();
                    Map<LocalTime, AppointmentDto> cacheOfAppointments = new HashMap<>();
                    while (currentTime
                            .isBefore(endWorkDayOnline.get().minusMinutes(interval.get() / 2))) {
                        if (breakFrom.isPresent() && breakTo.isPresent() && !(
                                currentTime.isAfter(breakFrom.get()) && currentTime
                                        .isBefore(breakTo.get()))
                                && !currentTime.equals(breakFrom.get()) && !(
                                currentTime.isAfter(breakFrom.get().minusMinutes(interval.get()))
                                        && currentTime.isBefore(breakFrom.get()))
                                || !breakFrom.isPresent() || !breakTo.isPresent()) {
                            AppointmentDto appointmentDto = new AppointmentDto();
                            appointmentDto.setTimeEvent(currentTime);
                            appointmentDto.setState(0);
                            appointmentDto.setIsOnline(1);
                            result.add(appointmentDto);
                            cacheOfAppointments.put(currentTime, appointmentDto);
                        }
                        currentTime = currentTime.plusMinutes(interval.get());
                    }
                    currentTime = endWorkDayOnline.get();
                    while (currentTime
                            .isBefore(endWorkDay.get().minusMinutes(interval.get() / 2))) {
                        if (breakFrom.isPresent() && breakTo.isPresent() && !(
                                currentTime.isAfter(breakFrom.get()) && currentTime
                                        .isBefore(breakTo.get()))
                                && !currentTime.equals(breakFrom.get()) && !(
                                currentTime.isAfter(breakFrom.get().minusMinutes(interval.get()))
                                        && currentTime.isBefore(breakFrom.get()))
                                || !breakFrom.isPresent() || !breakTo.isPresent()) {
                            AppointmentDto appointmentDto = new AppointmentDto();
                            appointmentDto.setTimeEvent(currentTime);
                            appointmentDto.setState(0);
                            appointmentDto.setIsOnline(0);
                            result.add(appointmentDto);
                            cacheOfAppointments.put(currentTime, appointmentDto);
                        }
                        currentTime = currentTime.plusMinutes(interval.get());
                    }
                    List<Appointment> reservedAppointments = appointmentRepository
                            .findReservedAppointmentsByDoctorAndDate(e, dateEvent);
                    reservedAppointments.stream().forEachOrdered((item) -> {
                        if (cacheOfAppointments.get(item.getTimeEvent()) != null) {
                            cacheOfAppointments.get(item.getTimeEvent()).setState(1);
                            cacheOfAppointments.get(item.getTimeEvent())
                                    .setClient(item.getClient());
                            cacheOfAppointments.get(item.getTimeEvent()).setPhone(item.getPhone());
                            cacheOfAppointments.get(item.getTimeEvent())
                                    .setService(item.getService());
                        }
                    });
                }
                /*
                    Graph timeline description:
                    1)
                    |---------------|
                            N
                       |-------|
                           O
                 */
                else if (startWorkDay.isPresent() && endWorkDay.isPresent() && interval.isPresent()
                        && startWorkDayOnline.isPresent() && endWorkDayOnline.isPresent()
                        && (startWorkDay.get().isBefore(startWorkDayOnline.get()) && endWorkDay
                        .get().isAfter(endWorkDayOnline.get()))) {
                    LocalTime currentTime = startWorkDay.get();
                    Map<LocalTime, AppointmentDto> cacheOfAppointments = new HashMap<>();
                    while (currentTime
                            .isBefore(startWorkDayOnline.get().minusMinutes(interval.get() / 2))) {
                        if (breakFrom.isPresent() && breakTo.isPresent() && !(
                                currentTime.isAfter(breakFrom.get()) && currentTime
                                        .isBefore(breakTo.get()))
                                && !currentTime.equals(breakFrom.get()) && !(
                                currentTime.isAfter(breakFrom.get().minusMinutes(interval.get()))
                                        && currentTime.isBefore(breakFrom.get()))
                                || !breakFrom.isPresent() || !breakTo.isPresent()) {
                            AppointmentDto appointmentDto = new AppointmentDto();
                            appointmentDto.setTimeEvent(currentTime);
                            appointmentDto.setState(0);
                            appointmentDto.setIsOnline(0);
                            result.add(appointmentDto);
                            cacheOfAppointments.put(currentTime, appointmentDto);
                        }
                        currentTime = currentTime.plusMinutes(interval.get());
                    }
                    LocalTime currentTimeOnline = startWorkDayOnline.get();
                    while (currentTimeOnline
                            .isBefore(endWorkDayOnline.get().minusMinutes(interval.get() / 2))) {
                        if (breakFrom.isPresent() && breakTo.isPresent() && !(
                                currentTime.isAfter(breakFrom.get()) && currentTime
                                        .isBefore(breakTo.get()))
                                && !currentTime.equals(breakFrom.get()) && !(
                                currentTime.isAfter(breakFrom.get().minusMinutes(interval.get()))
                                        && currentTime.isBefore(breakFrom.get()))
                                || !breakFrom.isPresent() || !breakTo.isPresent()) {
                            AppointmentDto appointmentDto = new AppointmentDto();
                            appointmentDto.setTimeEvent(currentTimeOnline);
                            appointmentDto.setState(0);
                            appointmentDto.setIsOnline(1);
                            result.add(appointmentDto);
                            cacheOfAppointments.put(currentTimeOnline, appointmentDto);
                        }
                        currentTimeOnline = currentTimeOnline.plusMinutes(interval.get());
                    }
                    currentTime = endWorkDayOnline.get();
                    while (currentTime
                            .isBefore(endWorkDay.get().minusMinutes(interval.get() / 2))) {
                        if (breakFrom.isPresent() && breakTo.isPresent() && !(
                                currentTime.isAfter(breakFrom.get()) && currentTime
                                        .isBefore(breakTo.get()))
                                && !currentTime.equals(breakFrom.get()) && !(
                                currentTime.isAfter(breakFrom.get().minusMinutes(interval.get()))
                                        && currentTime.isBefore(breakFrom.get()))
                                || !breakFrom.isPresent() || !breakTo.isPresent()) {
                            AppointmentDto appointmentDto = new AppointmentDto();
                            appointmentDto.setTimeEvent(currentTime);
                            appointmentDto.setState(0);
                            appointmentDto.setIsOnline(0);
                            result.add(appointmentDto);
                            cacheOfAppointments.put(currentTime, appointmentDto);
                        }
                        currentTime = currentTime.plusMinutes(interval.get());
                    }
                    List<Appointment> reservedAppointments = appointmentRepository
                            .findReservedAppointmentsByDoctorAndDate(e, dateEvent);
                    reservedAppointments.stream().forEachOrdered((item) -> {
                        if (cacheOfAppointments.get(item.getTimeEvent()) != null) {
                            cacheOfAppointments.get(item.getTimeEvent()).setState(1);
                            cacheOfAppointments.get(item.getTimeEvent())
                                    .setClient(item.getClient());
                            cacheOfAppointments.get(item.getTimeEvent()).setPhone(item.getPhone());
                            cacheOfAppointments.get(item.getTimeEvent())
                                    .setService(item.getService());
                        }
                    });
                }/*
                    Graph timeline description:
                    1)
                    |---------------|   |-------|
                            O               N

                 */ else if (startWorkDay.isPresent() && endWorkDay.isPresent() && interval
                        .isPresent()
                        && startWorkDayOnline.isPresent() && endWorkDayOnline.isPresent()
                        && (startWorkDay.get().isAfter(endWorkDayOnline.get()))) {
                    LocalTime currentTime = startWorkDayOnline.get();
                    Map<LocalTime, AppointmentDto> cacheOfAppointments = new HashMap<>();
                    while (currentTime
                            .isBefore(endWorkDayOnline.get().minusMinutes(interval.get() / 2))) {
                        if (breakFrom.isPresent() && breakTo.isPresent() && !(
                                currentTime.isAfter(breakFrom.get()) && currentTime
                                        .isBefore(breakTo.get()))
                                && !currentTime.equals(breakFrom.get()) && !(
                                currentTime.isAfter(breakFrom.get().minusMinutes(interval.get()))
                                        && currentTime.isBefore(breakFrom.get()))
                                || !breakFrom.isPresent() || !breakTo.isPresent()) {
                            AppointmentDto appointmentDto = new AppointmentDto();
                            appointmentDto.setTimeEvent(currentTime);
                            appointmentDto.setState(0);
                            appointmentDto.setIsOnline(1);
                            result.add(appointmentDto);
                            cacheOfAppointments.put(currentTime, appointmentDto);
                        }
                        currentTime = currentTime.plusMinutes(interval.get());
                    }
                    LocalTime currentTimeOnline = startWorkDay.get();
                    while (currentTimeOnline
                            .isBefore(endWorkDay.get().minusMinutes(interval.get() / 2))) {
                        if (breakFrom.isPresent() && breakTo.isPresent() && !(
                                currentTime.isAfter(breakFrom.get()) && currentTime
                                        .isBefore(breakTo.get()))
                                && !currentTime.equals(breakFrom.get()) && !(
                                currentTime.isAfter(breakFrom.get().minusMinutes(interval.get()))
                                        && currentTime.isBefore(breakFrom.get()))
                                || breakFrom.isEmpty() || breakTo.isEmpty()) {
                            AppointmentDto appointmentDto = new AppointmentDto();
                            appointmentDto.setTimeEvent(currentTimeOnline);
                            appointmentDto.setState(0);
                            appointmentDto.setIsOnline(0);
                            result.add(appointmentDto);
                            cacheOfAppointments.put(currentTimeOnline, appointmentDto);
                        }
                        currentTimeOnline = currentTimeOnline.plusMinutes(interval.get());
                    }

                    List<Appointment> reservedAppointments = appointmentRepository
                            .findReservedAppointmentsByDoctorAndDate(e, dateEvent);
                    reservedAppointments.stream().forEachOrdered((item) -> {
                        if (cacheOfAppointments.get(item.getTimeEvent()) != null) {
                            cacheOfAppointments.get(item.getTimeEvent()).setState(1);
                            cacheOfAppointments.get(item.getTimeEvent())
                                    .setClient(item.getClient());
                            cacheOfAppointments.get(item.getTimeEvent()).setPhone(item.getPhone());
                            cacheOfAppointments.get(item.getTimeEvent())
                                    .setService(item.getService());
                        }
                    });
                } else {
                    throw new NotFoundEntityException(
                            "Данные графика для " + doctor.get().getValue() + " на " + dateEvent
                                    + " - некоректны");
                }
            } else {
                throw new NotFoundEntityException(
                        "На данную дату не найден график для " + doctor.get().getValue());
            }
        });
        result.forEach(item -> {
            if (item.getIsOnline() == 1 && item.getState() == 0) {
                item.setStateLabel("@Свободно");
            } else if (item.getIsOnline() == 0 && item.getState() == 0) {
                item.setStateLabel("Свободно");
            } else if (item.getIsOnline() == 1 && item.getState() == 1) {
                item.setStateLabel(
                        "@" + item.getClient() + "<br/>" + item.getPhone() + "<br/>" + item
                                .getService());
            } else if (item.getIsOnline() == 0 && item.getState() == 1) {
                item.setStateLabel(
                        item.getClient() + "<br/>" + item.getPhone() + "<br/>" + item.getService());
            }
        });
        return result;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<AppointmentDto> getReservedAppointmentsByDoctorDateEvent(String doctorId,
            LocalDate dateEvent) {
        Instant start = Instant.now();
        List<AppointmentDto> result = new ArrayList<>();
        Optional<Doctor> doctor = Optional.ofNullable(doctorRepository.findOne(doctorId));
        doctor.ifPresent(e -> {
            List<Appointment> reservedAppointments = appointmentRepository
                    .findReservedAppointmentsByDoctorAndDate(e, dateEvent);
            reservedAppointments.stream().forEachOrdered(item -> {
                result.add(obj2dto(item));
            });
        });
        logger.debug("Elapsed time: {}", Duration
                .between(start, Instant.now()).toMillis());
        return result.stream().sorted().collect(Collectors.toList());
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<AppointmentDto> getReservedAppointmentsByDoctor(String doctorId) {
        Instant start = Instant.now();
        List<AppointmentDto> result = new ArrayList<>();
        Optional<Doctor> doctor = Optional.ofNullable(doctorRepository.findOne(doctorId));
        doctor.ifPresent(e -> {
            List<Appointment> reservedAppointments = appointmentRepository
                    .findReservedAppointmentsByDoctor(e);
            reservedAppointments.stream().forEachOrdered(item -> {
                result.add(obj2dto(item));
            });
        });
        logger.debug("Elapsed time: {}", Duration
                .between(start, Instant.now()).toMillis());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AppointmentDto> getReservedAppointmentsByDoctorDateEventHasVisit(String doctorId,
            LocalDate dateEvent, boolean hasVisit) {
        Instant start = Instant.now();
        List<AppointmentDto> result = new ArrayList<>();
        Optional<Doctor> doctor = Optional.ofNullable(doctorRepository.findOne(doctorId));
        doctor.ifPresent(e -> {
            List<Appointment> reservedAppointments = appointmentRepository
                    .findReservedAppointmentsByDoctorAndDateAndHasVisit(e, dateEvent, hasVisit);
            reservedAppointments.stream().forEachOrdered(item -> {
                result.add(obj2dto(item));
            });
        });
        logger.debug("Elapsed time: {}", Duration
                .between(start, Instant.now()).toMillis());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AppointmentDto> getReservedAppointmentsByDoctorHasVisit(String doctorId,
            boolean hasVisit) {
        Instant start = Instant.now();
        List<AppointmentDto> result = new ArrayList<>();
        Optional<Doctor> doctor = Optional.ofNullable(doctorRepository.findOne(doctorId));
        doctor.ifPresent(e -> {
            List<Appointment> reservedAppointments = appointmentRepository
                    .findReservedAppointmentsByDoctorAndHasVisit(e, hasVisit);
            reservedAppointments.stream().forEachOrdered(item -> {
                result.add(obj2dto(item));
            });
        });
        logger.debug("Elapsed time getReservedAppointmentsByDoctor: {}", Duration
                .between(start, Instant.now()).toMillis());
        return result;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<AppointmentDto> getReservedAppointmentsByDoctorDateEventHasVisit(String doctorId,
            LocalDate dateEvent, Boolean hasVisit, String visitId) {
        Instant start = Instant.now();
        Optional<Visit> visit = Optional.ofNullable(visitRepository.findOne(visitId));
        final Optional<Appointment>[] apppointment = new Optional[]{Optional.empty()};
        visit.ifPresent(i -> {
            apppointment[0] = Optional.ofNullable(i.getAppointment());
        });
        Stream<AppointmentDto> stream;
        if (dateEvent != null) {
            stream = getReservedAppointmentsByDoctorDateEventHasVisit(doctorId, dateEvent, hasVisit).stream();
        } else {
            stream = getReservedAppointmentsByDoctorHasVisit(doctorId, hasVisit).stream();
        }
        List<AppointmentDto> result = stream.collect(Collectors.toList());
        apppointment[0].ifPresent(e -> {
            result.add(obj2dto(e));
        });
        result.forEach(e -> e.setValue(
                e.getDateEvent().format(BaseEntity.formatterDate) + "(" + e.getTimeEvent() + ") - ("
                        + e.getClient() + ")"));
        logger.debug("Elapsed time getReservedAppointmentsByDoctorHasVisit: {}", Duration
                .between(start, Instant.now()).toMillis());
        return result.stream().sorted().collect(Collectors.toList());
    }

    @Override
    public void setIsHereForAppointment(String appointmentId) {
        Appointment appointment = appointmentRepository.findOne(appointmentId);
        if (appointment != null) {
            if (appointment.getIsHere() != null) {
                appointment.setIsHere(!appointment.getIsHere());
            } else {
                appointment.setIsHere(true);
            }
            appointmentRepository.save(appointment);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteOutdatedAppointments() {
        appointmentRepository.deleteOutdatedAppointments(LocalDate.now().minusDays(1));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AppointmentDto> getFreeTimeAppointmentsByDoctorDateEvent(String doctorId,
            LocalDate dateEvent) {
        return getFreeTimeAppointmentsByDoctorDateEvent(doctorId, dateEvent, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AppointmentDto> getFreeTimeAppointmentsByDoctorDateEvent(String doctorId,
            LocalDate dateEvent, String id) {
        final List<AppointmentDto> result = new ArrayList<>();
        if (id != null && id.trim().length() > 0) {
            Appointment updRecord = appointmentRepository.findOne(id);
            result.add(obj2dto(updRecord));
        }
        result.addAll(getAppointmentsByDoctorDateEventWithState(doctorId, dateEvent).stream()
                .filter(item -> item.getState() == 0).collect(Collectors.toSet()));
        result.forEach(item -> item.setValue(item.getTimeEvent().toString()));
        return result.stream().sorted().collect(Collectors.toList());
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<AppointmentDto> getAllAppointmentsByDateEventAfterTimeEvent(LocalDate dateEvent,
            LocalTime timeEvent) {
        final List<AppointmentDto> result = new ArrayList<>();
        if (dateEvent != null && timeEvent != null) {
            List<Appointment> tmpList = appointmentRepository
                    .findReservedAppointmentsByDateEventAfterTimeEvent(dateEvent);
            List<Appointment> appointmentList = tmpList.stream()
                    .filter(i -> i.getTimeEvent().isAfter(timeEvent)).collect(Collectors.toList());
            appointmentList.stream().forEachOrdered(item -> {
                result.add(obj2dto(item));
            });

        }
        return result.stream().sorted().collect(Collectors.toList());
    }

    @Override
    public AppointmentDto save(AppointmentDto appointmentDto) {
        final AppointmentDto[] result = {null};
        if (appointmentDto != null && appointmentDto.getIsDoubleAppointment() != null && appointmentDto
                .getIsDoubleAppointment().equals(true)) {
            if (appointmentDto.getDoctorId() != null) {
                final Doctor doctor = doctorRepository.findOne(appointmentDto.getDoctorId());
                if (doctor != null) {
                    Optional<Schedule> currentSchedule = Optional.ofNullable(scheduleRepository
                            .findByDateWorkAndDoctor(doctor, appointmentDto.getDateEvent()));
                    currentSchedule.ifPresent(schedule -> {
                        Integer interval = schedule.getInterval();
                        if (interval != null) {
                            List<AppointmentDto> free = getFreeTimeAppointmentsByDoctorDateEvent(
                                    appointmentDto.getDoctorId(), appointmentDto.getDateEvent());
                            if (free != null && free.size() > 0) {
                                AppointmentDto nextApppointmentDto = new AppointmentDto();
                                nextApppointmentDto.setClient(appointmentDto.getClient());
                                nextApppointmentDto.setDoctorId(appointmentDto.getDoctorId());
                                nextApppointmentDto.setService(appointmentDto.getService());
                                nextApppointmentDto.setDateEvent(appointmentDto.getDateEvent());
                                nextApppointmentDto.setTimeEvent(
                                        appointmentDto.getTimeEvent().plusMinutes(interval));
                                nextApppointmentDto.setPhone(appointmentDto.getPhone());
                                if (free.stream().map(AppointmentDto::getTimeEvent)
                                        .collect(Collectors.toList())
                                        .contains(nextApppointmentDto.getTimeEvent())) {
                                    appointmentRepository.save(dto2obj(nextApppointmentDto));
                                    result[0] = obj2dto(
                                            appointmentRepository.save(dto2obj(appointmentDto)));
                                } else {
                                    throw new NotFoundEntityException(
                                            "Невозможно задать двойное время приема.\nСледующее время для записи занято!");
                                }
                            } else {
                                throw new NotFoundEntityException(
                                        "Следующее время для записи не входит в график доктора");
                            }
                        } else {
                            throw new NotFoundEntityException(
                                    "Интервал в расписании доктора отсутствует");
                        }
                    });
                } else {
                    throw new NotFoundEntityException("Указанный доктор не найден");
                }
            } else {
                throw new NotFoundEntityException("ID доктора не найден");
            }
            return result[0];
        } else {
            return super.save(appointmentDto);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AppointmentDto obj2dto(Appointment appointment) {
        String doctorId = appointment.getDoctor() != null ? appointment.getDoctor().getId() : null;

        return new AppointmentDto(appointment.getId(), appointment.getCreatedDate(),
                appointment.getModifyDate(),
                appointment.getAuthor(), appointment.getDateEvent(), appointment.getTimeEvent(),
                doctorId,
                appointment.getClient(), appointment.getPhone(), appointment.getService(),
                appointment.getDoctorLabel(), appointment.getDateTimeEventLabel(),
                appointment.getHasVisit(), appointment.getIsHere(),
                appointment.getIsDoubleAppointment());

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Appointment dto2obj(AppointmentDto appointmentDto) {
        Doctor doctor = null;
        if (appointmentDto != null && appointmentDto.getDoctorId() != null) {
            doctor = doctorRepository.findOne(appointmentDto.getDoctorId());
        }
        if (doctor != null) {
            return new Appointment(appointmentDto.getId(), appointmentDto.getCreatedDate(),
                    appointmentDto.getModifyDate(),
                    appointmentDto.getAuthor(), appointmentDto.getDateEvent(),
                    appointmentDto.getTimeEvent(), doctor,
                    appointmentDto.getClient(), appointmentDto.getPhone(),
                    appointmentDto.getService(), appointmentDto.getHasVisit(),
                    appointmentDto.getIsHere(), appointmentDto.getIsDoubleAppointment());
        }
        return null;
    }
}
