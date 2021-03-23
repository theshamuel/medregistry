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
package com.theshamuel.medreg.model.visit.entity;

import com.theshamuel.medreg.model.appointment.entity.Appointment;
import com.theshamuel.medreg.model.baseclasses.entity.BaseEntity;
import com.theshamuel.medreg.model.client.entity.Client;
import com.theshamuel.medreg.model.doctor.entity.Doctor;
import com.theshamuel.medreg.model.customerservice.entity.CustomerService;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * The Visit entity class. The class define completed appointment.
 *
 * @author Alex Gladkikh
 */
@Document(collection = "visits")
public class Visit extends BaseEntity {

    @Field("contractNum")
    private String contractNum;

    @Field("doctor")
    private Doctor doctor;

    @Field("client")
    private Client client;

    @Field("dateEvent")
    private LocalDate dateEvent;

    @Field("timeEvent")
    private LocalTime timeEvent;

    @Field("appointment")
    private Appointment appointment;

    @Field("customerServices")
    private List<CustomerService> customerServices;

    @Field("terminalSum")
    private BigInteger terminalSum;

    @Field("diagnosis")
    private String diagnosis;

    @Field("additionalExamination")
    private String additionalExamination;

    @Field("therapy")
    private String therapy;

    @Transient
    private String dateTimeLabel;

    @Transient
    private String timeLabel;

    /**
     * Instantiates a new Visit.
     */
    public Visit() {
    }

    /**
     * Instantiates a new Visit.
     *
     * @param id                    the id
     * @param createdDate           the created date
     * @param modifyDate            the modify date
     * @param author                the author
     * @param contractNum           the contract num
     * @param doctor                the doctor
     * @param client                the client
     * @param dateEvent             the date event
     * @param timeEvent             the time event
     * @param appointment           the appointment
     * @param customerServices              the customerServices
     * @param terminalSum           the terminal sum
     * @param diagnosis             the diagnosis
     * @param additionalExamination the additionalExamination
     * @param therapy               the therapy
     */
    public Visit(String id, LocalDateTime createdDate, LocalDateTime modifyDate, String author,
            String contractNum, Doctor doctor, Client client, LocalDate dateEvent,
            LocalTime timeEvent, Appointment appointment, List<CustomerService> customerServices,
            BigInteger terminalSum, String diagnosis, String additionalExamination,
            String therapy) {
        setId(id);
        setCreatedDate(createdDate);
        setModifyDate(modifyDate);
        setAuthor(author);

        this.contractNum = contractNum;
        this.doctor = doctor;
        this.client = client;
        this.dateEvent = dateEvent;
        this.timeEvent = timeEvent;
        this.appointment = appointment;
        this.customerServices = customerServices;
        this.terminalSum = terminalSum;
        this.diagnosis = diagnosis;
        this.additionalExamination = additionalExamination;
        this.therapy = therapy;
    }

    /**
     * Gets contract num.
     *
     * @return the contract num
     */
    public String getContractNum() {
        return contractNum;
    }

    /**
     * Sets contract num.
     *
     * @param contractNum the contract num
     */
    public void setContractNum(String contractNum) {
        this.contractNum = contractNum;
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
    public Client getClient() {
        return client;
    }

    /**
     * Sets client.
     *
     * @param client the client
     */
    public void setClient(Client client) {
        this.client = client;
    }

    /**
     * Gets appointment.
     *
     * @return the appointment
     */
    public Appointment getAppointment() {
        return appointment;
    }

    /**
     * Sets appointment.
     *
     * @param appointment the appointment
     */
    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }


    /**
     * Gets customerServices.
     *
     * @return the customerServices
     */
    public List<CustomerService> getServices() {
        return customerServices;
    }

    /**
     * Sets customerServices.
     *
     * @param customerServices the customerServices
     */
    public void setServices(List<CustomerService> customerServices) {
        this.customerServices = customerServices;
    }

    /**
     * Gets terminal sum.
     *
     * @return the terminal sum
     */
    public BigInteger getTerminalSum() {
        return terminalSum;
    }

    /**
     * Sets terminal sum.
     *
     * @param terminalSum the terminal sum
     */
    public void setTerminalSum(BigInteger terminalSum) {
        this.terminalSum = terminalSum;
    }

    /**
     * Gets diagnosis.
     *
     * @return the diagnosis
     */
    public String getDiagnosis() {
        return diagnosis;
    }

    /**
     * Sets diagnosis.
     *
     * @param diagnosis the diagnosis
     * @return the diagnosis
     */
    public Visit setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
        return this;
    }

    /**
     * Gets additional examination.
     *
     * @return the additional examination
     */
    public String getAdditionalExamination() {
        return additionalExamination;
    }

    /**
     * Sets additional examination.
     *
     * @param additionalExamination the additional examination
     * @return the additional examination
     */
    public Visit setAdditionalExamination(String additionalExamination) {
        this.additionalExamination = additionalExamination;
        return this;
    }

    /**
     * Gets therapy.
     *
     * @return the therapy
     */
    public String getTherapy() {
        return therapy;
    }

    /**
     * Sets therapy.
     *
     * @param therapy the therapy
     * @return the therapy
     */
    public Visit setTherapy(String therapy) {
        this.therapy = therapy;
        return this;
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
     * Gets time label.
     *
     * @return the time label
     */
    public String getTimeLabel() {
        return (getTimeEvent() != null) ? getTimeEvent().format(formatterTime) : "-";

    }

    /**
     * Gets date time label.
     *
     * @return the date time label
     */
    public String getDateTimeLabel() {
        return (getDateEvent() != null && getTimeEvent() != null) ?
                getDateEvent().format(formatterDate) + " " + getTimeEvent().format(formatterTime)
                : "-";

    }

    @Override
    public boolean equals(Object o) {

        if (o == this) {
            return true;
        }
        if (!(o instanceof Visit)) {
            return false;
        }

        Visit visit = (Visit) o;

        return new EqualsBuilder()
                .append(getId(), visit.getId())
                .append(contractNum, visit.contractNum)
                .append(customerServices, visit.customerServices)
                .append(appointment, visit.appointment)
                .append(client, visit.client)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getId())
                .append(contractNum)
                .append(customerServices)
                .append(appointment)
                .append(client)
                .toHashCode();
    }
}
