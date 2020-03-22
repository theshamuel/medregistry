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
package com.theshamuel.medreg.model.baseclasses.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * The Base entity class.
 *
 * @author Alex Gladkikh
 */
public class BaseEntity implements Serializable {

    /**
     * The constant formatterDate (dd.mm.yyyy).
     */
    @Transient
    @JsonIgnore
    public static final DateTimeFormatter formatterDate = DateTimeFormatter
            .ofPattern("dd.MM.yyyy", Locale.getDefault());

    /**
     * The constant formatterTime (hh:mm).
     */
    @Transient
    @JsonIgnore
    public static final DateTimeFormatter formatterTime = DateTimeFormatter
            .ofPattern("HH:mm", Locale.getDefault());


    @Id
    private String id;

    @JsonIgnore
    @Field("createdDate")
    private LocalDateTime createdDate;

    @Transient
    private String createdDateLabel;

    @JsonIgnore
    @Field("modifyDate")
    private LocalDateTime modifyDate;

    @Transient
    private String modifyDateLabel;

    @Field("author")
    private String author;

    @Transient
    private String value;


    /**
     * Gets id.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets created date.
     *
     * @return the created date
     */
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    /**
     * Sets created date.
     *
     * @param createdDate the created date
     */
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    /**
     * Gets created date string.
     *
     * @return the created date string
     */
    public String getCreatedDateLabel() {
        return createdDate != null ? createdDate.format(formatterDate) : "";
    }

    /**
     * Sets created date label.
     *
     * @param createdDateLabel the created date label
     */
    public void setCreatedDateLabel(String createdDateLabel) {
        this.createdDateLabel = createdDateLabel;
    }

    /**
     * Gets modify date.
     *
     * @return the modify date
     */
    public LocalDateTime getModifyDate() {
        return modifyDate;
    }

    /**
     * Sets modify date.
     *
     * @param modifyDate the modify date
     */
    public void setModifyDate(LocalDateTime modifyDate) {
        this.modifyDate = modifyDate;
    }

    /**
     * Gets modify date label.
     *
     * @return the modify date label
     */
    public String getModifyDateLabel() {
        return modifyDate != null ? modifyDate.format(formatterDate) : "";
    }

    /**
     * Sets modify date label.
     *
     * @param modifyDateLabel the modify date label
     */
    public void setModifyDateLabel(String modifyDateLabel) {
        this.modifyDateLabel = modifyDateLabel;
    }

    /**
     * Gets author.
     *
     * @return the author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Sets author.
     *
     * @param author the author
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Get value string.
     *
     * @return the string
     */
    public String getValue() {
        return value;
    }

    /**
     * Set value.
     *
     * @param value the value
     */
    public void setValue(String value) {
        this.value = value;
    }
}
