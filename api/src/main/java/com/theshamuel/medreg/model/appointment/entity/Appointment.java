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
package com.theshamuel.medreg.model.appointment.entity;

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
 * The Appointment entity class.
 *
 * @author Alex Gladkikh
 */
@Document(collection = "appointments")
public class Appointment extends BaseEntity {

    @Field("dateEvent")
    private LocalDate dateEvent;

    @Field("timeEvent")
    private LocalTime timeEvent;

    @DBRef
    private Doctor doctor;

    @Field("client")
    private String client;

    @Field("phone")
    private String phone;

    @Field("service")
    private String service;

    @Transient
    private String doctorLabel;

    @Transient
    private String dateTimeEventLabel;

    @Field("hasVisit")
    private Boolean hasVisit;

    @Field("isHere")
    private Boolean isHere = false;

    @Field("isDoubleAppointment")
    private Boolean isDoubleAppointment = false;

    /**
     * Instantiates a new Appointment.
     */
    public Appointment() {
    }

    /**
     * Instantiates a new Appointment.
     *
     * @param id          the id
     * @param createdDate the created date
     * @param modifyDate  the modify date
     * @param author      the author
     * @param dateEvent   the date event
     * @param timeEvent   the time event
     * @param doctor      the doctor
     * @param client      the client
     * @param phone       the phone
     * @param service     the service
     * @param hasVisit    the has visit
     * @param isHere      the is here
     */
    public Appointment(String id, LocalDateTime createdDate, LocalDateTime modifyDate,
            String author, LocalDate dateEvent, LocalTime timeEvent, Doctor doctor, String client,
            String phone, String service, Boolean hasVisit, Boolean isHere,
            Boolean isDoubleAppointment) {
        setId(id);
        setCreatedDate(createdDate);
        setModifyDate(modifyDate);
        setAuthor(author);

        this.dateEvent = dateEvent;
        this.timeEvent = timeEvent;
        this.doctor = doctor;
        this.client = client;
        this.phone = phone;
        this.service = service;
        this.hasVisit = hasVisit;
        this.isHere = isHere;
        this.isDoubleAppointment = isDoubleAppointment;
    }

    /**
     * Gets date event.
     *
     * @return the date event
     */
    public LocalDate getDateEvent() {
        return dateEvent;
    }

    /**
     * Sets date event.
     *
     * @param dateEvent the date event
     */
    public void setDateEvent(LocalDate dateEvent) {
        this.dateEvent = dateEvent;
    }

    /**
     * Gets time event.
     *
     * @return the time event
     */
    public LocalTime getTimeEvent() {
        return timeEvent;
    }

    /**
     * Sets time event.
     *
     * @param timeEvent the time event
     */
    public void setTimeEvent(LocalTime timeEvent) {
        this.timeEvent = timeEvent;
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
     * Gets client.
     *
     * @return the client
     */
    public String getClient() {
        return client;
    }

    /**
     * Sets client.
     *
     * @param client the client
     */
    public void setClient(String client) {
        this.client = client;
    }

    /**
     * Gets phone.
     *
     * @return the phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets phone.
     *
     * @param phone the phone
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Gets service.
     *
     * @return the service
     */
    public String getService() {
        return service;
    }

    /**
     * Sets service.
     *
     * @param service the service
     */
    public void setService(String service) {
        this.service = service;
    }

    /**
     * Gets has visit.
     *
     * @return the has visit
     */
    public Boolean getHasVisit() {
        return hasVisit;
    }

    /**
     * Sets has visit.
     *
     * @param hasVisit the has visit
     */
    public void setHasVisit(Boolean hasVisit) {
        this.hasVisit = hasVisit;
    }

    /**
     * Gets doctor label.
     *
     * @return the doctor label
     */
    public String getDoctorLabel() {
        if (getDoctor() != null) {
            return getDoctor().getValue();
        } else {
            return "-";
        }
    }

    /**
     * Gets isHere.
     *
     * @return the here
     */
    public Boolean getIsHere() {
        return isHere;
    }

    /**
     * Sets isHere.
     *
     * @param isHere the here
     */
    public void setIsHere(Boolean isHere) {
        this.isHere = isHere;
    }

    /**
     * Gets is double record.
     *
     * @return the isDoubleAppointment
     */
    public Boolean getIsDoubleAppointment() {
        return this.isDoubleAppointment;
    }

    /**
     * Sets is double record.
     *
     * @param isDoubleAppointment the is double record
     */
    public void setIsDoubleAppointment(Boolean isDoubleAppointment) {
        this.isDoubleAppointment = isDoubleAppointment;
    }

    /**
     * Gets date time event label.
     *
     * @return the date time event label
     */
    public String getDateTimeEventLabel() {
        return (getDateEvent() != null && getTimeEvent() != null) ?
                getDateEvent().format(formatterDate) + " " + timeEvent.format(formatterTime) : "-";
    }

    @Override
    public boolean equals(Object o) {

        if (o == this) {
            return true;
        }
        if (!(o instanceof Appointment)) {
            return false;
        }

        Appointment appointment = (Appointment) o;

        return new EqualsBuilder()
                .append(getId(), appointment.getId())
                .append(dateEvent, appointment.dateEvent)
                .append(timeEvent, appointment.timeEvent)
                .append(client, appointment.client)
                .append(doctor, appointment.doctor)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getId())
                .append(dateEvent)
                .append(timeEvent)
                .append(client)
                .append(doctor)
                .toHashCode();
    }

}
