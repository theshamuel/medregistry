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
package com.theshamuel.medreg.model.report.entity;

import java.math.BigInteger;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * The Report of workday by doctor class.
 *
 * @author Alex Gladkikh
 */
public class ReportOfWorkDayByDoctor {

    private String doctorId;

    private String doctorFio;

    private BigInteger totalSum;

    private ReportOfWorkDay reportOfWorkDay;

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
     * Gets doctor fio.
     *
     * @return the doctor fio
     */
    public String getDoctorFio() {
        return doctorFio;
    }

    /**
     * Sets doctor fio.
     *
     * @param doctorFio the doctor fio
     */
    public void setDoctorFio(String doctorFio) {
        this.doctorFio = doctorFio;
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
     * Gets report of work day.
     *
     * @return the report of work day
     */
    public ReportOfWorkDay getReportOfWorkDay() {
        return reportOfWorkDay;
    }

    /**
     * Sets report of work day.
     *
     * @param reportOfWorkDay the report of work day
     */
    public void setReportOfWorkDay(ReportOfWorkDay reportOfWorkDay) {
        this.reportOfWorkDay = reportOfWorkDay;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ReportOfWorkDayByDoctor)) {
            return false;
        }

        ReportOfWorkDayByDoctor reportOfWorkDayByDoctor = (ReportOfWorkDayByDoctor) o;

        return new EqualsBuilder()
                .append(doctorId, reportOfWorkDayByDoctor.getDoctorId())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(doctorId)
                .toHashCode();
    }
}
