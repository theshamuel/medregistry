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
package com.theshamuel.medreg.model.service.entity;

import java.math.BigInteger;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.annotation.Transient;

/**
 * The Personal rate entity class.
 * <p>
 * The class define personal rate for particular service. It can be set to any doctor.
 *
 * @author Alex Gladkikh
 */
public class PersonalRate {

    private BigInteger price;

    private BigInteger doctorPay;

    private String doctorPayType;

    private String doctorId;

    private String doctorPayTypeLabel;

    @Transient
    private String label;

    /**
     * Instantiates a new Personal rate.
     */
    public PersonalRate() {
    }

    /**
     * Instantiates a new Personal rate.
     *
     * @param price         the price
     * @param doctorPay     the doctor pay
     * @param doctorPayType the doctor pay type
     */
    public PersonalRate(BigInteger price, BigInteger doctorPay, String doctorPayType) {
        this.price = price;
        this.doctorPay = doctorPay;
        this.doctorPayType = doctorPayType;
    }

    /**
     * Gets price.
     *
     * @return the price
     */
    public BigInteger getPrice() {
        return price;
    }

    /**
     * Sets price.
     *
     * @param price the price
     */
    public void setPrice(BigInteger price) {
        this.price = price;
    }

    /**
     * Gets doctor pay.
     *
     * @return the doctor pay
     */
    public BigInteger getDoctorPay() {
        return doctorPay;
    }

    /**
     * Sets doctor pay.
     *
     * @param doctorPay the doctor pay
     */
    public void setDoctorPay(BigInteger doctorPay) {
        this.doctorPay = doctorPay;
    }

    /**
     * Gets doctor pay type.
     *
     * @return the doctor pay type
     */
    public String getDoctorPayType() {
        return doctorPayType;
    }

    /**
     * Sets doctor pay type.
     *
     * @param doctorPayType the doctor pay type
     */
    public void setDoctorPayType(String doctorPayType) {
        this.doctorPayType = doctorPayType;
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
     * Gets label.
     *
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets label.
     *
     * @param label the label
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Gets doctor pay type label.
     *
     * @return the doctor pay type label
     */
    public String getDoctorPayTypeLabel() {
        if (getDoctorPayType() != null && !getDoctorPayType().isEmpty()) {
            if (getDoctorPayType().equals("percent")) {
                return "%";
            } else {
                return "руб";
            }
        }
        return "-";
    }

    @Override
    public boolean equals(Object o) {

        if (o == this) {
            return true;
        }
        if (!(o instanceof PersonalRate)) {
            return false;
        }

        PersonalRate personalRate = (PersonalRate) o;

        return new EqualsBuilder()
                .append(getDoctorId(), personalRate.getDoctorId())
                .append(getPrice(), personalRate.getPrice())
                .append(getDoctorPay(), personalRate.getDoctorPay())
                .append(getDoctorPayType(), personalRate.getDoctorPayType())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(doctorId)
                .append(price)
                .append(doctorPay)
                .append(doctorPayType)
                .toHashCode();
    }
}
