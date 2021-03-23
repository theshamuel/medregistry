package com.theshamuel.medreg.buiders;

import com.theshamuel.medreg.model.report.entity.Report;
import com.theshamuel.medreg.model.customerservice.entity.CustomerService;

/**
 * The Builder class for {@link Report}
 *
 * @author Alex Gladkikh
 */
public class ReportBuilder {

    private Report report;

    public ReportBuilder() {
        report = new Report();
    }

    public ReportBuilder id(String id) {
        report.setId(id);
        return this;
    }

    public ReportBuilder service(CustomerService customerService) {
        report.setService(customerService);
        return this;
    }

    public ReportBuilder template(String template) {
        report.setTemplate(template);
        return this;
    }

    public ReportBuilder label(String label) {
        report.setLabel(label);
        return this;
    }

    public ReportBuilder author(String author) {
        report.setAuthor(author);
        return this;
    }


    public Report build() {
        return report;
    }
}
