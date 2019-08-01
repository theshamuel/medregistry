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
package com.theshamuel.medreg.model.report.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.math.BigInteger;

/**
 * The Report of workday record class.
 * The one record in report table contained all info about particular service provided in work day
 *
 * @author Alex Gladkikh
 */
public class ReportOfWorkDayRecord {

    private String serviceId;

    private String label;

    private int amount = 1;

    private BigInteger price;

    private BigInteger sum;

    private BigInteger salary;

    private BigInteger doctorPayPersonal;

    private String doctorPayTypePersonal;

    /**
     * Instantiates a new Report of work day record.
     *
     * @param serviceId the service id
     * @param label     the label
     * @param price     the price
     */
    public ReportOfWorkDayRecord(String serviceId, String label, BigInteger price) {
        this(serviceId,label,price,null,null);
    }

    /**
     * Instantiates a new Report of work day record.
     *
     * @param serviceId             the service id
     * @param label                 the label
     * @param price                 the price
     * @param doctorPayPersonal     the doctor pay personal
     * @param doctorPayTypePersonal the doctor pay type personal
     */
    public ReportOfWorkDayRecord(String serviceId, String label, BigInteger price, BigInteger doctorPayPersonal, String doctorPayTypePersonal) {
        this.serviceId = serviceId;
        this.label = label;
        this.price = price;
        this.doctorPayPersonal = doctorPayPersonal;
        this.doctorPayTypePersonal = doctorPayTypePersonal;
    }

    /**
     * Gets service id.
     *
     * @return the service id
     */
    public String getServiceId() {
        return serviceId;
    }

    /**
     * Sets service id.
     *
     * @param serviceId the service id
     */
    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
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
     * Gets amount.
     *
     * @return the amount
     */
    public int getAmount() {
        return amount;
    }

    /**
     * Sets amount.
     *
     * @param amount the amount
     */
    public void setAmount(int amount) {
        this.amount = amount;
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
     * Gets sum.
     *
     * @return the sum
     */
    public BigInteger getSum() {
        if (price!=null)
            return price.multiply(BigInteger.valueOf(amount));
        return BigInteger.valueOf(-1);
    }

    /**
     * Gets doctor pay personal.
     *
     * @return the doctor pay personal
     */
    public BigInteger getDoctorPayPersonal() {
        return doctorPayPersonal;
    }

    /**
     * Sets doctor pay personal.
     *
     * @param doctorPayPersonal the doctor pay personal
     */
    public void setDoctorPayPersonal(BigInteger doctorPayPersonal) {
        this.doctorPayPersonal = doctorPayPersonal;
    }

    /**
     * Gets doctor pay type personal.
     *
     * @return the doctor pay type personal
     */
    public String getDoctorPayTypePersonal() {
        return doctorPayTypePersonal;
    }

    /**
     * Sets doctor pay type personal.
     *
     * @param doctorPayTypePersonal the doctor pay type personal
     */
    public void setDoctorPayTypePersonal(String doctorPayTypePersonal) {
        this.doctorPayTypePersonal = doctorPayTypePersonal;
    }

    /**
     * Gets salary.
     *
     * @return the salary
     */
    public BigInteger getSalary() {
        if (doctorPayPersonal!=null && doctorPayTypePersonal!=null){
            if (doctorPayTypePersonal.equals("percent"))
                return getSum().multiply(doctorPayPersonal).divide(BigInteger.valueOf(100));
            else
                return doctorPayPersonal;
        }
        return BigInteger.valueOf(0);
    }

    @Override
    public boolean equals(Object o) {

        if (o == this) return true;
        if (!(o instanceof ReportOfWorkDayRecord)) {
            return false;
        }

        ReportOfWorkDayRecord obj = (ReportOfWorkDayRecord) o;

        return new EqualsBuilder()
                .append(serviceId.split("MEDREG")[0], obj.getServiceId().split("MEDREG")[0])
                .append(label, obj.getLabel())
                .append(price, obj.getPrice())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(serviceId.split("MEDREG")[0])
                .append(label)
                .append(price)
                .toHashCode();
    }
}
