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
package com.theshamuel.medreg.model.appointment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.theshamuel.medreg.model.baseclasses.entity.BaseEntity;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


/**
 * The Appointment data transaction object class.
 *
 * @author Alex Gladkikh
 */
public class AppointmentDto extends BaseEntity implements Comparable{

    private DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault());

    private LocalDate dateEvent;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime timeEvent;

    private String doctorId;

    private String client;

    private String phone;

    private String service;

    private String doctorLabel;

    private String dateTimeEventLabel;

    private String title;

    private int state = 0;

    private Boolean hasVisit;

    private Boolean isHere;

    private Boolean isDoubleAppointment;

    private String stateLabel;

    private int isOnline = 0;

    /**
     * Instantiates a new Appointment dto.
     */
    public AppointmentDto() {
    }

    /**
     * Instantiates a new Appointment dto.
     *
     * @param id                  the id
     * @param createdDate         the created date
     * @param modifyDate          the modify date
     * @param author              the author
     * @param dateEvent           the date event
     * @param timeEvent           the time event
     * @param doctorId            the doctor id
     * @param client              the client
     * @param phone               the phone
     * @param service             the service
     * @param doctorLabel         the doctor label
     * @param dateTimeEventLabel  the date time event label
     * @param hasVisit            the has visit
     * @param isHere              the is here
     * @param isDoubleAppointment the is double appointment
     */
    public AppointmentDto(String id, LocalDateTime createdDate, LocalDateTime modifyDate, String author, LocalDate dateEvent, LocalTime timeEvent, String doctorId, String client, String phone, String service, String doctorLabel, String dateTimeEventLabel, Boolean hasVisit, Boolean isHere, Boolean isDoubleAppointment) {

        setId(id);
        setCreatedDate(createdDate);
        setModifyDate(modifyDate);
        setAuthor(author);

        this.dateEvent = dateEvent;
        this.timeEvent = timeEvent;
        this.doctorId = doctorId;
        this.client = client;
        this.phone = phone;
        this.service = service;
        this.doctorLabel = doctorLabel;
        this.dateTimeEventLabel = dateTimeEventLabel;
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
     * Gets doctor id.
     *
     * @return the doctor id
     */
    public String getDoctorId() {
        return doctorId;
    }

    /**
     * Sets doctor id.
     *
     * @param doctorId the doctor id
     */
    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
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

    /**
     * Gets date time event label.
     *
     * @return the date time event label
     */
    public String getDateTimeEventLabel() {
        return dateTimeEventLabel;
    }

    /**
     * Sets date time event label.
     *
     * @param dateTimeEventLabel the date time event label
     */
    public void setDateTimeEventLabel(String dateTimeEventLabel) {
        this.dateTimeEventLabel = dateTimeEventLabel;
    }

    /**
     * Gets title.
     *
     * @return the title
     */
    public String getTitle() {
        StringBuilder sb = new StringBuilder();
        if (getTimeEvent()!=null)
            sb.append(getTimeEvent());
        if (getClient()!=null)
            sb.append(" ".concat(client));
        if (getService()!=null)
            sb.append(" ".concat(service));
        return sb.toString();
    }

    /**
     * Gets state.
     *
     * @return the state
     */
    public int getState() {
        return state;
    }

    /**
     * Sets state.
     *
     * @param state the state
     */
    public void setState(int state) {
        this.state = state;
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
     * Gets is here.
     *
     * @return the is here
     */
    public Boolean getIsHere() {
        return this.isHere;
    }

    /**
     * Sets is here.
     *
     * @param isHere the is here
     */
    public void setIsHere(Boolean isHere) {
        this.isHere = isHere;
    }

    /**
     * Gets is double appointment.
     *
     * @return the is double appointment
     */
    public Boolean getIsDoubleAppointment() {
        return this.isDoubleAppointment;
    }

    /**
     * Sets is double appointment.
     *
     * @param isDoubleAppointment the is double appointment
     */
    public void setIsDoubleAppointment(Boolean isDoubleAppointment) {
        this.isDoubleAppointment = isDoubleAppointment;
    }

    /**
     * Gets state label.
     *
     * @return the state label
     */
    public String getStateLabel() {
        return this.stateLabel;
    }

    /**
     * Sets state label.
     *
     * @param stateLabel the state label
     */
    public void setStateLabel(String stateLabel) {
        this.stateLabel = stateLabel;
    }

    /**
     * Gets is online.
     *
     * @return the is online
     */
    public int getIsOnline() {
        return isOnline;
    }

    /**
     * Sets is online.
     *
     * @param isOnline the is online
     */
    public void setIsOnline(int isOnline) {
        this.isOnline = isOnline;
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof AppointmentDto){
            AppointmentDto appointment = (AppointmentDto) o;
            if (getDateEvent()!=null && appointment.getDateEvent() !=null && getDateEvent().isAfter(appointment.getDateEvent()))
                return 1;
            else if (getDateEvent()!=null && appointment.getDateEvent()!=null && getDateEvent().isBefore(appointment.getDateEvent()))
                return -1;
            else if (getTimeEvent()!=null && appointment.getTimeEvent()!=null && getTimeEvent().isAfter(appointment.getTimeEvent()))
                return 1;
            else if (getTimeEvent()!=null && appointment.getTimeEvent()!=null && getTimeEvent().isBefore(appointment.getTimeEvent()))
                return -1;
            else
                return 0;

        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {

        if (o == this) return true;
        if (!(o instanceof AppointmentDto)) {
            return false;
        }

        AppointmentDto appointmentDto = (AppointmentDto) o;

        return new EqualsBuilder()
                .append(dateEvent, appointmentDto.dateEvent)
                .append(timeEvent, appointmentDto.timeEvent)
                .append(client, appointmentDto.client)
                .append(doctorId, appointmentDto.doctorId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(dateEvent)
                .append(timeEvent)
                .append(client)
                .append(doctorId)
                .toHashCode();
    }
}
