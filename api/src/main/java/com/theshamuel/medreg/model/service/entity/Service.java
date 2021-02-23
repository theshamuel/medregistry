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

import com.theshamuel.medreg.model.baseclasses.entity.BaseEntity;
import com.theshamuel.medreg.model.types.CategoryOfService;
import java.math.BigInteger;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * The Service entity.
 * <p>
 * The class describe info about service which provide medical center.
 *
 * @author Alex Gladkikh
 */
@Document(collection = "services")
public class Service extends BaseEntity {

    @Field("label")
    private String label;

    @Field("price")
    private BigInteger price;

    @Field("discount")
    private BigInteger discount;

    @Field("doctorPay")
    private BigInteger doctorPay;

    @Field("doctorPayType")
    private String doctorPayType;

    @Field("category")
    private String category;

    @Field("personalRate")
    private List<PersonalRate> personalRates;

    @Transient
    private String categoryLabel;

    @Transient
    private String doctorPercentLabel;

    @Transient
    private String doctorSumLabel;

    @Transient
    private String existTemplateLabel;

    /**
     * Instantiates a new Service.
     */
    public Service() {
    }

    /**
     * Instantiates a new Service.
     *
     * @param id       the id
     * @param label    the label
     * @param price    the price
     * @param discount the discount
     */
    public Service(String id, String label, BigInteger price, BigInteger discount) {
        setId(id);
        this.label = label;
        this.price = price;
        this.discount = discount;
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
        this.label = label.trim();
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
     * Gets discount.
     *
     * @return the discount
     */
    public BigInteger getDiscount() {
        return discount;
    }

    /**
     * Sets discount.
     *
     * @param discount the discount
     */
    public void setDiscount(BigInteger discount) {
        this.discount = discount;
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
     * Gets category.
     *
     * @return the category
     */
    public String getCategory() {
        return category;
    }

    /**
     * Sets category.
     *
     * @param category the category
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Gets personal rates.
     *
     * @return the personal rates
     */
    public List<PersonalRate> getPersonalRates() {
        return personalRates;
    }

    /**
     * Sets personal rates.
     *
     * @param personalRates the personal rates
     */
    public void setPersonalRates(List<PersonalRate> personalRates) {
        this.personalRates = personalRates;
    }

    /**
     * Gets category label.
     *
     * @return the category label
     */
    public String getCategoryLabel() {
        if (getCategory() != null && !getCategory().isEmpty()) {
            if (getCategory().equals(CategoryOfService.ULTRA)) {
                return "УЗИ";
            } else if (getCategory().equals(CategoryOfService.CONSULTATION)) {
                return "Консультации";
            } else if (getCategory().equals(CategoryOfService.ANALYZES)) {
                return "Анализы";
            } else if (getCategory().equals(CategoryOfService.MAZOK)) {
                return "Мазок";
            } else if (getCategory().equals(CategoryOfService.PCR)) {
                return "ПЦР";
            }
        }
        return StringUtils.EMPTY;
    }


    /**
     * Gets doctor percent label.
     *
     * @return the doctor percent label
     */
    public String getDoctorPercentLabel() {
        if (getDoctorPayType() != null && !getDoctorPayType().isEmpty()) {
            if (getDoctorPayType().equals("percent") && getDoctorPay() != null) {
                return getDoctorPay().toString();
            }
        }
        return "-";
    }

    /**
     * Gets doctor sum label.
     *
     * @return the doctor sum label
     */
    public String getDoctorSumLabel() {
        if (getDoctorPayType() != null && !getDoctorPayType().isEmpty()) {
            if (getDoctorPayType().equals("sum") && getDoctorPay() != null) {
                return getDoctorPay().toString();
            }
        }
        return "-";
    }

    /**
     * Gets exist template label.
     *
     * @return the exist template label
     */
    public String getExistTemplateLabel() {
        return existTemplateLabel;
    }

    /**
     * Sets exist template label.
     *
     * @param existTemplateLabel the exist template label
     */
    public void setExistTemplateLabel(String existTemplateLabel) {
        this.existTemplateLabel = existTemplateLabel;
    }

    @Override
    public String getValue() {
        return getLabel();
    }

    @Override
    public boolean equals(Object o) {

        if (o == this) {
            return true;
        }
        if (!(o instanceof Service)) {
            return false;
        }

        Service service = (Service) o;

        return new EqualsBuilder()
                .append(getId(), service.getId())
                .append(label, service.label)
                .append(price, service.price)
                .append(discount, service.discount)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getId())
                .append(label)
                .append(price)
                .append(discount)
                .toHashCode();
    }
}
