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
package com.theshamuel.medreg.model.schedule.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.theshamuel.medreg.model.baseclasses.entity.BaseEntity;
import com.theshamuel.medreg.model.doctor.entity.Doctor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * The Schedule entity class.
 *
 * @author Alex Gladkikh
 */
@Document(collection = "schedule")
public class Schedule extends BaseEntity implements Comparable {

    @DBRef
    private Doctor doctor;

    @Transient
    private String doctorLabel;

    @Field("dateWork")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateWork;

    @Field("timeFrom")
    private LocalTime timeFrom;

    @Field("timeTo")
    private LocalTime timeTo;

    @Field("breakFrom")
    private LocalTime breakFrom;

    @Field("breakTo")
    private LocalTime breakTo;

    @Field("interval")
    private Integer interval;

    @Field("timeFromOnline")
    private LocalTime timeFromOnline;

    @Field("timeToOnline")
    private LocalTime timeToOnline;

    @Field("intervalOnline")
    private Integer intervalOnline;
    @Transient
    private String dateWorkLabel;

    /**
     * Instantiates a new Schedule.
     */
    public Schedule() {
    }

    /**
     * Instantiates a new Schedule.
     *
     * @param id             the id
     * @param createdDate    the created date
     * @param modifyDate     the modify date
     * @param author         the author
     * @param doctor         the doctor
     * @param dateWork       the date work
     * @param timeFrom       the time from
     * @param timeTo         the time to
     * @param interval       the interval
     * @param timeFromOnline the time from online
     * @param timeToOnline   the time to online
     * @param intervalOnline the interval online
     */
    public Schedule(String id, LocalDateTime createdDate, LocalDateTime modifyDate, String author,
            Doctor doctor, LocalDate dateWork, LocalTime timeFrom, LocalTime timeTo,
            LocalTime breakFrom, LocalTime breakTo, Integer interval, LocalTime timeFromOnline,
            LocalTime timeToOnline, Integer intervalOnline) {
        setId(id);
        setCreatedDate(createdDate);
        setModifyDate(modifyDate);
        setAuthor(author);
        this.doctor = doctor;
        this.dateWork = dateWork;
        this.timeFrom = timeFrom;
        this.timeTo = timeTo;
        this.breakFrom = breakFrom;
        this.breakTo = breakTo;
        this.interval = interval;
        this.timeFromOnline = timeFromOnline;
        this.timeToOnline = timeToOnline;
        this.intervalOnline = intervalOnline;
    }

    /**
     * Gets doctor.
     *
     * @return the doctor
     */
    public Doctor getDoctor() {
        return doctor;
    }

    /**
     * Sets doctor.
     *
     * @param doctor the doctor
     */
    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    /**
     * Gets doctor label.
     *
     * @return the doctor label
     */
    public String getDoctorLabel() {
        if (getDoctor() != null) {
            return getDoctor().getValue();
        }
        return "";
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
     * Gets date work label.
     *
     * @return the date work label
     */
    public String getDateWorkLabel() {
        return dateWork != null ? dateWork.format(formatterDate) : "";
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

    @Override
    public int compareTo(Object o) {

        return 0;
    }

    @Override
    public boolean equals(Object o) {

        if (o == this) {
            return true;
        }
        if (!(o instanceof Schedule)) {
            return false;
        }

        Schedule schedule = (Schedule) o;

        return new EqualsBuilder()
                .append(getId(), schedule.getId())
                .append(dateWork, schedule.dateWork)
                .append(doctor, schedule.doctor)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getId())
                .append(doctor)
                .toHashCode();
    }
}
