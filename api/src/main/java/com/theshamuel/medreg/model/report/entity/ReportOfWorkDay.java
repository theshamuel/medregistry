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

import java.math.BigInteger;
import java.util.List;

/**
 * The Report of workday class.
 *
 * @author Alex Gladkikh
 */
public class ReportOfWorkDay {

    private BigInteger totalSum;

    private BigInteger terminalSum;

    private BigInteger salarySum;

    private BigInteger mazkiSum;

    private BigInteger remainder;

    private List<ReportOfWorkDayRecord> records;

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
    public List<ReportOfWorkDayRecord> getRecords() {
        return records;
    }

    /**
     * Sets records.
     *
     * @param records the records
     */
    public void setRecords(List<ReportOfWorkDayRecord> records) {
        this.records = records;
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
     * Gets salary sum.
     *
     * @return the salary sum
     */
    public BigInteger getSalarySum() {
        return salarySum;
    }

    /**
     * Sets salary sum.
     *
     * @param salarySum the salary sum
     */
    public void setSalarySum(BigInteger salarySum) {
        this.salarySum = salarySum;
    }

    /**
     * Gets mazki sum.
     *
     * @return the mazki sum
     */
    public BigInteger getMazkiSum() {
        return mazkiSum;
    }

    /**
     * Sets mazki sum.
     *
     * @param mazkiSum the mazki sum
     */
    public void setMazkiSum(BigInteger mazkiSum) {
        this.mazkiSum = mazkiSum;
    }

    /**
     * Gets remainder.
     *
     * @return the remainder
     */
    public BigInteger getRemainder() {
        return remainder;
    }

    /**
     * Sets remainder.
     *
     * @param remainder the remainder
     */
    public void setRemainder(BigInteger remainder) {
        this.remainder = remainder;
    }
}
