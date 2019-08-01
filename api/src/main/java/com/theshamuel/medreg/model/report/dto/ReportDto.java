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
package com.theshamuel.medreg.model.report.dto;

import com.theshamuel.medreg.model.baseclasses.entity.BaseEntity;

import java.time.LocalDateTime;

public class ReportDto extends BaseEntity {

    public ReportDto() {
    }

    public ReportDto(String id, LocalDateTime createdDate, LocalDateTime modifyDate, String author, String label, String serviceId, String template, String serviceLabel) {
        setId(id);
        setCreatedDate(createdDate);
        setModifyDate(modifyDate);
        setAuthor(author);
        this.label = label;
        this.serviceId = serviceId;
        this.template = template;
        this.serviceLabel = serviceLabel;
    }

    private String label;

    private String serviceId;

    private String template;

    private String serviceLabel;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getServiceLabel() {
        return serviceLabel;
    }

    public void setServiceLabel(String serviceLabel) {
        this.serviceLabel = serviceLabel;
    }
}
