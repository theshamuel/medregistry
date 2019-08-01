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
package com.theshamuel.medreg.model.doctor.entity;

import com.theshamuel.medreg.model.baseclasses.entity.BaseEntity;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

/**
 * The Doctor entity class.
 *
 * @author Alex Gladkikh
 */
@Document(collection = "doctors")
public class Doctor extends BaseEntity{

    @Field("name")
    private String name;

    @Field("surname")
    private String surname;

    @Field("middlename")
    private String middlename;

    @Field("position")
    private String position;

    @Field("phone")
    private String phone;

    @Field("isNotWork")
    private Integer isNotWork;

    @Field("excludeFromReport")
    private Integer excludeFromReport;

    @Field("contractor")
    private Integer contractor;

    /**
     * Instantiates a new Doctor.
     */
    public Doctor() {
    }

    /**
     * Instantiates a new Doctor.
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
     */
    public Doctor(String id, LocalDateTime createdDate, LocalDateTime modifyDate, String author, String name, String surname, String middlename, String position, String phone, Integer isNotWork, Integer excludeFromReport, Integer contractor) {
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
     * Gets is not work.
     *
     * @return the is not work
     */
    public Integer getIsNotWork() {
        return isNotWork;
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

    @Override
    public String getValue() {
        StringBuilder value = new StringBuilder();
        if (getSurname()!=null)
            value.append(getSurname());
        if (getName()!=null && getName().length()>0){
            value.append(" ");
            value.append(getName().substring(0,1).toUpperCase());
            value.append(".");
        }
        if (getMiddlename()!=null && getMiddlename().length()>0){
            value.append(" ");
            value.append(getMiddlename().substring(0,1).toUpperCase());
            value.append(".");
        }
        return value.toString();
    }

    @Override
    public boolean equals(Object o) {

        if (o == this) return true;
        if (!(o instanceof Doctor)) {
            return false;
        }

        Doctor doctor = (Doctor) o;

        return new EqualsBuilder()
                .append(getId(), doctor.getId())
                .append(name, doctor.name)
                .append(surname, doctor.surname)
                .append(middlename, doctor.middlename)
                .append(position, doctor.position)
                .append(phone, doctor.phone)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getId())
                .append(name)
                .append(surname)
                .append(middlename)
                .append(position)
                .append(phone)
                .toHashCode();
    }

}
