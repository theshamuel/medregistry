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

import com.theshamuel.medreg.model.baseclasses.entity.BaseEntity;
import com.theshamuel.medreg.model.service.entity.Service;
import java.time.LocalDateTime;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * The Report entity class.
 *
 * @author Alex Gladkikh
 */
@Document(collection = "reports")
public class Report extends BaseEntity {

    @Field("label")
    private String label;
    @DBRef
    private Service service;
    @Field("template")
    private String template;
    @Transient
    private String serviceLabel;

    /**
     * Instantiates a new Report.
     */
    public Report() {
    }

    /**
     * Instantiates a new Report.
     *
     * @param id          the id
     * @param createdDate the created date
     * @param modifyDate  the modify date
     * @param author      the author
     * @param label       the label
     * @param service     the service
     * @param template    the template
     */
    public Report(String id, LocalDateTime createdDate, LocalDateTime modifyDate, String author,
            String label, Service service, String template) {
        setId(id);
        setCreatedDate(createdDate);
        setModifyDate(modifyDate);
        setAuthor(author);
        this.label = label;
        this.service = service;
        this.template = template;
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
     * Gets service.
     *
     * @return the service
     */
    public Service getService() {
        return service;
    }

    /**
     * Sets service.
     *
     * @param service the service
     */
    public void setService(Service service) {
        this.service = service;
    }

    /**
     * Gets template.
     *
     * @return the template
     */
    public String getTemplate() {
        return template;
    }

    /**
     * Sets template.
     *
     * @param template the template
     */
    public void setTemplate(String template) {
        this.template = template;
    }

    /**
     * Gets service label.
     *
     * @return the service label
     */
    public String getServiceLabel() {
        return getService() != null ? getService().getLabel() : "";
    }

    @Override
    public boolean equals(Object o) {

        if (o == this) {
            return true;
        }
        if (!(o instanceof Report)) {
            return false;
        }

        Report report = (Report) o;

        return new EqualsBuilder()
                .append(getId(), report.getId())
                .append(label, report.label)
                .append(template, report.template)
                .append(service, report.service)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getId())
                .append(label)
                .append(template)
                .append(service)
                .toHashCode();
    }
}
