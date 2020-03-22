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
package com.theshamuel.medreg.model.client.entity;

import com.theshamuel.medreg.model.baseclasses.entity.BaseEntity;

/**
 * The History entity class.
 */
public class History extends BaseEntity {

    private String label;

    private String dateEvent;

    /**
     * Instantiates a new History.
     */
    public History() {
    }

    /**
     * Instantiates a new History.
     *
     * @param label     the label
     * @param dateEvent the date of event
     */
    public History(String label, String dateEvent) {
        this.label = label;
        this.dateEvent = dateEvent;
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
     * Gets date event.
     *
     * @return the date event
     */
    public String getDateEvent() {
        return dateEvent;
    }

    /**
     * Sets date event.
     *
     * @param dateEvent the date event
     */
    public void setDateEvent(String dateEvent) {
        this.dateEvent = dateEvent;
    }
}
