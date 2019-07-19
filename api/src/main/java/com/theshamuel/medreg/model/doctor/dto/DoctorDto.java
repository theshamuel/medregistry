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
package com.theshamuel.medreg.model.doctor.dto;

import com.theshamuel.medreg.model.baseclasses.entity.BaseEntity;
import org.springframework.data.annotation.Transient;

import java.time.LocalDateTime;

/**
 * The Doctor data transaction object class.
 *
 * @author Alex Gladkikh
 */
public class DoctorDto extends BaseEntity {
    private String name;

    private String surname;

    private String middlename;

    private String position;

    private String phone;

    @Transient
    private String personalRateLabel;

    private Integer isNotWork;

    private Integer excludeFromReport;

    private Integer contractor;

    @Transient
    private String isNotWorkLabel;

    @Transient
    private String positionLabel;

    /**
     * Gets position label.
     *
     * @return the position label
     */
    public String getPositionLabel() {
        return positionLabel;
    }

    /**
     * Sets position label.
     *
     * @param positionLabel the position label
     */
    public void setPositionLabel(String positionLabel) {
        this.positionLabel = positionLabel;
    }

    /**
     * Instantiates a new Doctor dto.
     */
    public DoctorDto() {
    }

    /**
     * Instantiates a new Doctor dto.
     *
     * @param id                the id
     * @param createdDate       the created date
     * @param modifyDate        the modify date
     * @param author            the author
     * @param name              the name
     * @param surname           the surname
     * @param middlename        the middlename
     * @param position          the position
     * @param phone             the phone
     * @param isNotWork         the is not work
     * @param excludeFromReport the exclude from report
     * @param contractor        the contractor
     * @param personalRateLabel the personal rate label
     * @param positionLabel     the position label
     * @param value             the value
     */
    public DoctorDto(String id, LocalDateTime createdDate, LocalDateTime modifyDate, String author, String name, String surname, String middlename, String position, String phone,Integer isNotWork, Integer excludeFromReport, Integer contractor, String personalRateLabel,  String positionLabel, String value) {
        setId(id);
        setCreatedDate(createdDate);
        setModifyDate(modifyDate);
        setAuthor(author);

        this.name = name;
        this.surname = surname;
        this.middlename = middlename;
        this.position = position;
        this.phone = phone;
        this.isNotWork = isNotWork;
        this.excludeFromReport = excludeFromReport;
        this.contractor = contractor;
        this.personalRateLabel = personalRateLabel;
        this.positionLabel = positionLabel;
        setValue(value);
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets surname.
     *
     * @return the surname
     */
    public String getSurname() {
        return surname;
    }

    /**
     * Sets surname.
     *
     * @param surname the surname
     */
    public void setSurname(String surname) {
        this.surname = surname;
    }

    /**
     * Gets middlename.
     *
     * @return the middlename
     */
    public String getMiddlename() {
        return middlename;
    }

    /**
     * Sets middlename.
     *
     * @param middlename the middlename
     */
    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    /**
     * Gets position.
     *
     * @return the position
     */
    public String getPosition() {
        return position;
    }

    /**
     * Sets position.
     *
     * @param position the position
     */
    public void setPosition(String position) {
        this.position = position;
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
     * Gets personal rate label.
     *
     * @return the personal rate label
     */
    public String getPersonalRateLabel() {
        return personalRateLabel;
    }

    /**
     * Sets is not work.
     *
     * @param isNotWork the is not work
     */
    public void setIsNotWork(Integer isNotWork) {
        this.isNotWork = isNotWork;
    }

    /**
     * Gets is not work.
     *
     * @return the is not work
     */
    public Integer getIsNotWork() {
        return isNotWork;
    }

    /**
     * Gets exclude from report.
     *
     * @return the exclude from report
     */
    public Integer getExcludeFromReport() {
        return excludeFromReport;
    }

    /**
     * Sets exclude from report.
     *
     * @param excludeFromReport the exclude from report
     */
    public void setExcludeFromReport(Integer excludeFromReport) {
        this.excludeFromReport = excludeFromReport;
    }

    /**
     * Gets contractor.
     *
     * @return the contractor
     */
    public Integer getContractor() {
        return contractor;
    }

    /**
     * Sets contractor.
     *
     * @param contractor the contractor
     */
    public void setContractor(Integer contractor) {
        this.contractor = contractor;
    }

    /**
     * Gets is not work label.
     *
     * @return the is not work label
     */
    public String getIsNotWorkLabel() {
        if (getIsNotWork()!=null && getIsNotWork().equals(1))
            return "Уволен";
        else
            return "В штате";
    }

}
