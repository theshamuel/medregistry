/**
 * This private project is a project which automatizate workflow in medical center AVESTA (http://avesta-center.com) called "MedRegistry".
 * The "MedRegistry" demonstrates my programming skills to * potential employers.
 *
 * Here is short description: ( for more detailed description please read README.md or
 * go to https://github.com/theshamuel/medregistry )
 *
 * Front-end: JS, HTML, CSS (basic simple functionality)
 * Back-end: Spring (Spring Boot, Spring IoC, Spring Data, Spring Test), JWT library, Java8
 * DB: MongoDB
 * Tools: git,maven,docker.
 *
 * My LinkedIn profile: https://www.linkedin.com/in/alex-gladkikh-767a15115/
 */
package com.theshamuel.medreg.model.schedule.dto;

import com.theshamuel.medreg.model.baseclasses.entity.BaseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * The Schedule data transaction object entity class.
 *
 * @author Alex Gladkikh
 */
public class ScheduleDto extends BaseEntity{

    private String doctor;

    private String doctorLabel;

    private LocalDate dateWork;

    private LocalTime timeFrom;

    private LocalTime timeTo;

    private LocalTime breakFrom;

    private LocalTime breakTo;

    private Integer interval;

    private LocalTime timeFromOnline;

    private LocalTime timeToOnline;

    private Integer intervalOnline;

    private String dateWorkLabel;

    /**
     * Instantiates a new Schedule dto.
     */
    public ScheduleDto() {
    }

    /**
     * Instantiates a new Schedule dto.
     *
     * @param id             the id
     * @param createdDate    the created date
     * @param modifyDate     the modify date
     * @param author         the author
     * @param doctor         the doctor
     * @param doctorLabel    the doctor label
     * @param dateWork       the date work
     * @param timeFrom       the time from
     * @param timeTo         the time to
     * @param breakFrom      the break from
     * @param breakTo        the break to
     * @param interval       the interval
     * @param timeFromOnline the time from online
     * @param timeToOnline   the time to online
     * @param intervalOnline the interval online
     * @param dateWorkLabel  the date work label
     */
    public ScheduleDto(String id, LocalDateTime createdDate, LocalDateTime modifyDate, String author, String doctor,String doctorLabel, LocalDate dateWork, java.time.LocalTime timeFrom, LocalTime timeTo, LocalTime breakFrom, LocalTime breakTo, Integer interval, LocalTime timeFromOnline, LocalTime timeToOnline, Integer intervalOnline, String dateWorkLabel) {
        setId(id);
        setCreatedDate(createdDate);
        setModifyDate(modifyDate);
        setAuthor(author);
        this.doctor = doctor;
        this.doctorLabel = doctorLabel;
        this.dateWork = dateWork;
        this.timeFrom = timeFrom;
        this.timeTo = timeTo;
        this.breakFrom = breakFrom;
        this.breakTo = breakTo;
        this.interval = interval;
        this.timeFromOnline = timeFromOnline;
        this.timeToOnline = timeToOnline;
        this.intervalOnline = intervalOnline;
        this.dateWorkLabel = dateWorkLabel;
    }

    /**
     * Gets doctor.
     *
     * @return the doctor
     */
    public String getDoctor() {
        return doctor;
    }

    /**
     * Sets doctor.
     *
     * @param doctor the doctor
     */
    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }

    /**
     * Gets date work.
     *
     * @return the date work
     */
    public LocalDate getDateWork() {
        return dateWork;
    }

    /**
     * Sets date work.
     *
     * @param dateWork the date work
     */
    public void setDateWork(LocalDate dateWork) {
        this.dateWork = dateWork;
    }

    /**
     * Gets time from.
     *
     * @return the time from
     */
    public LocalTime getTimeFrom() {
        return timeFrom;
    }

    /**
     * Sets time from.
     *
     * @param timeFrom the time from
     */
    public void setTimeFrom(LocalTime timeFrom) {
        this.timeFrom = timeFrom;
    }

    /**
     * Gets time to.
     *
     * @return the time to
     */
    public LocalTime getTimeTo() {
        return timeTo;
    }

    /**
     * Sets time to.
     *
     * @param timeTo the time to
     */
    public void setTimeTo(LocalTime timeTo) {
        this.timeTo = timeTo;
    }

    /**
     * Gets break from.
     *
     * @return the break from
     */
    public LocalTime getBreakFrom() {
        return breakFrom;
    }

    /**
     * Sets break from.
     *
     * @param breakFrom the break from
     */
    public void setBreakFrom(LocalTime breakFrom) {
        this.breakFrom = breakFrom;
    }

    /**
     * Gets break to.
     *
     * @return the break to
     */
    public LocalTime getBreakTo() {
        return breakTo;
    }

    /**
     * Sets break to.
     *
     * @param breakTo the break to
     */
    public void setBreakTo(LocalTime breakTo) {
        this.breakTo = breakTo;
    }

    /**
     * Gets interval.
     *
     * @return the interval
     */
    public Integer getInterval() {
        return interval;
    }

    /**
     * Sets interval.
     *
     * @param interval the interval
     */
    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    /**
     * Gets time from online.
     *
     * @return the time from online
     */
    public LocalTime getTimeFromOnline() {
        return timeFromOnline;
    }

    /**
     * Sets time from online.
     *
     * @param timeFromOnline the time from online
     */
    public void setTimeFromOnline(LocalTime timeFromOnline) {
        this.timeFromOnline = timeFromOnline;
    }

    /**
     * Gets time to online.
     *
     * @return the time to online
     */
    public LocalTime getTimeToOnline() {
        return timeToOnline;
    }

    /**
     * Sets time to online.
     *
     * @param timeToOnline the time to online
     */
    public void setTimeToOnline(LocalTime timeToOnline) {
        this.timeToOnline = timeToOnline;
    }

    /**
     * Gets interval online.
     *
     * @return the interval online
     */
    public Integer getIntervalOnline() {
        return intervalOnline;
    }

    /**
     * Sets interval online.
     *
     * @param intervalOnline the interval online
     */
    public void setIntervalOnline(Integer intervalOnline) {
        this.intervalOnline = intervalOnline;
    }

    /**
     * Gets date work label.
     *
     * @return the date work label
     */
    public String getDateWorkLabel() {
        return dateWorkLabel;
    }

    /**
     * Sets date work label.
     *
     * @param dateWorkLabel the date work label
     */
    public void setDateWorkLabel(String dateWorkLabel) {
        this.dateWorkLabel = dateWorkLabel;
    }

    /**
     * Gets doctor label.
     *
     * @return the doctor label
     */
    public String getDoctorLabel() {
        return doctorLabel;
    }

    /**
     * Sets doctor label.
     *
     * @param doctorLabel the doctor label
     */
    public void setDoctorLabel(String doctorLabel) {
        this.doctorLabel = doctorLabel;
    }
}
