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
package com.theshamuel.medreg.model.customerservice.dto;

import com.theshamuel.medreg.model.baseclasses.entity.BaseEntity;
import java.math.BigInteger;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * The CustomerService data transaction object class.
 */
public class CustomerServiceDto extends BaseEntity {


    private String label;

    private BigInteger price;

    private BigInteger discount;

    /**
     * Instantiates a new CustomerService dto.
     */
    public CustomerServiceDto() {
    }

    /**
     * Instantiates a new CustomerService dto.
     *
     * @param id       the id
     * @param label    the label
     * @param price    the price
     * @param discount the discount
     */
    public CustomerServiceDto(String id, String label, BigInteger price, BigInteger discount) {
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
        this.label = label;
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

    @Override
    public boolean equals(Object o) {

        if (o == this) {
            return true;
        }
        if (!(o instanceof CustomerServiceDto)) {
            return false;
        }

        CustomerServiceDto dto = (CustomerServiceDto) o;

        return new EqualsBuilder()
                .append(getId(), dto.getId())
                .append(label, dto.label)
                .append(discount, dto.discount)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getId())
                .append(label)
                .append(discount)
                .toHashCode();
    }
}
