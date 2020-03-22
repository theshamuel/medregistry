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
import java.util.List;

/**
 * The Root class in report by doctor.
 * <p>
 * The class contains common calculated information in doctor report
 *
 * @author Alex Gladkikh
 */
public class RootReportByDoctor {

    private BigInteger totalSum;

    private List<ReportOfWorkDayByDoctor> records;

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
     * Gets records.
     *
     * @return the records
     */
    public List<ReportOfWorkDayByDoctor> getRecords() {
        return records;
    }

    /**
     * Sets records.
     *
     * @param records the records
     */
    public void setRecords(List<ReportOfWorkDayByDoctor> records) {
        this.records = records;
    }
}
