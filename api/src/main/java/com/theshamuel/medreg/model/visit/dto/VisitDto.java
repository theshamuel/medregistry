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
package com.theshamuel.medreg.model.visit.dto;

import com.theshamuel.medreg.model.baseclasses.entity.BaseEntity;
import com.theshamuel.medreg.model.service.entity.Service;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * The Visit data transaction object class.
 *
 * @author Alex Gladkikh
 */
public class VisitDto extends BaseEntity {

    private String doctorId;

    private String contractNum;

    private String doctorLabel;

    private String clientId;

    private LocalDate dateEvent;

    private LocalTime timeEvent;

    private String phoneLabel;

    private String passportLabel;

    private List<Service> services;

    private String clientLabel;

    private String appointmentId;

    private String dateTimeLabel;

    private String timeLabel;

    private BigInteger totalSum;

    private BigInteger terminalSum;

    /**
     * Instantiates a new Visit dto.
     */
    public VisitDto() {
    }

    /**
     * Instantiates a new Visit dto.
     *
     * @param id            the id
     * @param createdDate   the created date
     * @param modifyDate    the modify date
     * @param author        the author
     * @param contractNum   the contract num
     * @param doctorId      the doctor id
     * @param doctorLabel   the doctor label
     * @param clientId      the client id
     * @param services      the services
     * @param terminalSum   the terminal sum
     * @param totalSum      the total sum
     * @param dateEvent     the date event
     * @param timeEvent     the time event
     * @param clientLabel   the client label
     * @param appointmentId the appointment id
     * @param dateTimeLabel the date time label
     * @param timeLabel     the time label
     * @param phoneLabel    the phone label
     * @param passportLabel the passport label
     */
    public VisitDto(String id, LocalDateTime createdDate, LocalDateTime modifyDate, String author,
            String contractNum, String doctorId, String doctorLabel, String clientId,
            List<Service> services, BigInteger terminalSum, BigInteger totalSum,
            LocalDate dateEvent, LocalTime timeEvent, String clientLabel, String appointmentId,
            String dateTimeLabel, String timeLabel, String phoneLabel, String passportLabel) {
        setId(id);
        setCreatedDate(createdDate);
        setModifyDate(modifyDate);
        setAuthor(author);
        this.contractNum = contractNum;
        this.doctorId = doctorId;
        this.doctorLabel = doctorLabel;
        this.clientId = clientId;
        this.services = services;
        this.terminalSum = terminalSum;
        this.totalSum = totalSum;
        this.dateEvent = dateEvent;
        this.timeEvent = timeEvent;
        this.clientLabel = clientLabel;
        this.appointmentId = appointmentId;
        this.dateTimeLabel = dateTimeLabel;
        this.timeLabel = timeLabel;
        this.phoneLabel = phoneLabel;
        this.passportLabel = passportLabel;
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
     * Gets client id.
     *
     * @return the client id
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * Sets client id.
     *
     * @param clientId the client id
     */
    public void setClientId(String clientId) {
        this.clientId = clientId;
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
     * Gets services.
     *
     * @return the services
     */
    public List<Service> getServices() {
        return services;
    }

    /**
     * Sets services.
     *
     * @param services the services
     */
    public void setServices(List<Service> services) {
        this.services = services;
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
     * Gets total sum.
     *
     * @return the total sum
     */
    public BigInteger getTotalSum() {
        return totalSum;
    }

    /**
     * Sets total sum.
     *
     * @param totalSum the total sum
     */
    public void setTotalSum(BigInteger totalSum) {
        this.totalSum = totalSum;
    }

    /**
     * Gets client label.
     *
     * @return the client label
     */
    public String getClientLabel() {
        return clientLabel;
    }

    /**
     * Sets client label.
     *
     * @param clientLabel the client label
     */
    public void setClientLabel(String clientLabel) {
        this.clientLabel = clientLabel;
    }

    /**
     * Gets appointment id.
     *
     * @return the appointment id
     */
    public String getAppointmentId() {
        return appointmentId;
    }

    /**
     * Sets appointment id.
     *
     * @param appointmentId the appointment id
     */
    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }


    /**
     * Gets date time label.
     *
     * @return the date time label
     */
    public String getDateTimeLabel() {
        return dateTimeLabel;
    }

    /**
     * Sets date time label.
     *
     * @param dateTimeLabel the date time label
     */
    public void setDateTimeLabel(String dateTimeLabel) {
        this.dateTimeLabel = dateTimeLabel;
    }

    /**
     * Gets time label.
     *
     * @return the time label
     */
    public String getTimeLabel() {
        return timeLabel;
    }

    /**
     * Sets time label.
     *
     * @param timeLabel the time label
     */
    public void setTimeLabel(String timeLabel) {
        this.timeLabel = timeLabel;
    }

    /**
     * Gets phone label.
     *
     * @return the phone label
     */
    public String getPhoneLabel() {
        return phoneLabel;
    }

    /**
     * Sets phone label.
     *
     * @param phoneLabel the phone label
     */
    public void setPhoneLabel(String phoneLabel) {
        this.phoneLabel = phoneLabel;
    }

    /**
     * Gets passport label.
     *
     * @return the passport label
     */
    public String getPassportLabel() {
        return passportLabel;
    }

    @Override
    public boolean equals(Object o) {

        if (o == this) {
            return true;
        }
        if (!(o instanceof VisitDto)) {
            return false;
        }

        VisitDto visitDto = (VisitDto) o;

        return new EqualsBuilder()
                .append(getId(), visitDto.getId())
                .append(contractNum, visitDto.contractNum)
                .append(services, visitDto.services)
                .append(appointmentId, visitDto.appointmentId)
                .append(clientId, visitDto.clientId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getId())
                .append(contractNum)
                .append(services)
                .append(appointmentId)
                .append(clientId)
                .toHashCode();
    }
}
