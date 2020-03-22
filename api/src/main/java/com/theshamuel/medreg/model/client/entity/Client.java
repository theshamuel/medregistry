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
import java.time.LocalDate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * The Client entity class.
 *
 * @author Alex Gladkikh
 */
@Document(collection = "clients")
public class Client extends BaseEntity {


    //General fields
    @Field("name")
    private String name;

    @Field("surname")
    private String surname;

    @Field("middlename")
    private String middlename;

    @Field("gender")
    private String gender;

    @Field("birthday")
    private LocalDate birthday;

    @Field("phone")
    private String phone;

    @Field("workPlace")
    private String workPlace;

    @Field("workPosition")
    private String workPosition;

    //Passport's fields
    @Field("passportSerial")
    private String passportSerial;

    @Field("passportNumber")
    private String passportNumber;

    @Field("passportPlace")
    private String passportPlace;

    @Field("passportCodePlace")
    private String passportCodePlace;

    @Field("passportDate")
    private LocalDate passportDate;

    @Field("cardNumber")
    private Long cardNumber;

    @Transient
    private String passportLabel;

    //Live place fields
    @Field("address")
    private String address;

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets surname.
     *
     * @return the surname
     */
    public String getSurname() {
        return surname;
    }

    /**
     * Sets surname.
     *
     * @param surname the surname
     */
    public void setSurname(String surname) {
        this.surname = surname;
    }

    /**
     * Gets middle name.
     *
     * @return the middle name
     */
    public String getMiddlename() {
        return middlename;
    }

    /**
     * Sets middle name.
     *
     * @param middlename the middle name
     */
    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    /**
     * Gets gender.
     *
     * @return the gender
     */
    public String getGender() {
        return gender;
    }

    /**
     * Sets gender.
     *
     * @param gender the gender
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * Gets birthday.
     *
     * @return the birthday
     */
    public LocalDate getBirthday() {
        return birthday;
    }

    /**
     * Sets birthday.
     *
     * @param birthday the birthday
     */
    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    /**
     * Gets work place.
     *
     * @return the work place
     */
    public String getWorkPlace() {
        return workPlace;
    }

    /**
     * Sets work place.
     *
     * @param workPlace the work place
     */
    public void setWorkPlace(String workPlace) {
        this.workPlace = workPlace;
    }

    /**
     * Gets work position.
     *
     * @return the work position
     */
    public String getWorkPosition() {
        return workPosition;
    }

    /**
     * Sets work position.
     *
     * @param workPosition the work position
     */
    public void setWorkPosition(String workPosition) {
        this.workPosition = workPosition;
    }

    /**
     * Gets phone.
     *
     * @return the phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets phone.
     *
     * @param phone the phone
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Gets passport serial.
     *
     * @return the passport serial
     */
    public String getPassportSerial() {
        return passportSerial;
    }

    /**
     * Sets passport serial.
     *
     * @param passportSerial the passport serial
     */
    public void setPassportSerial(String passportSerial) {
        this.passportSerial = passportSerial;
    }

    /**
     * Gets passport number.
     *
     * @return the passport number
     */
    public String getPassportNumber() {
        return passportNumber;
    }

    /**
     * Sets passport number.
     *
     * @param passportNumber the passport number
     */
    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    /**
     * Gets passport place.
     *
     * @return the passport place
     */
    public String getPassportPlace() {
        return passportPlace;
    }

    /**
     * Sets passport place.
     *
     * @param passportPlace the passport place
     */
    public void setPassportPlace(String passportPlace) {
        this.passportPlace = passportPlace;
    }

    /**
     * Gets passport code place.
     *
     * @return the passport code place
     */
    public String getPassportCodePlace() {
        return passportCodePlace;
    }

    /**
     * Sets passport code place.
     *
     * @param passportCodePlace the passport code place
     */
    public void setPassportCodePlace(String passportCodePlace) {
        this.passportCodePlace = passportCodePlace;
    }

    /**
     * Gets passport date.
     *
     * @return the passport date
     */
    public LocalDate getPassportDate() {
        return passportDate;
    }

    /**
     * Sets passport date.
     *
     * @param passportDate the passport date
     */
    public void setPassportDate(LocalDate passportDate) {
        this.passportDate = passportDate;
    }

    /**
     * Gets passport label.
     *
     * @return the passport label
     */
    public String getPassportLabel() {
        if (getPassportSerial() != null && getPassportNumber() != null) {
            return getPassportSerial().concat("/").concat(getPassportNumber());
        } else if (getPassportSerial() != null) {
            return getPassportSerial().concat("/");
        } else if (getPassportNumber() != null) {
            return "/".concat(getPassportNumber());
        } else {
            return "-";
        }
    }

    /**
     * Sets passport label.
     *
     * @param passportLabel the passport label
     */
    public void setPassportLabel(String passportLabel) {
        this.passportLabel = passportLabel;
    }

    /**
     * Gets address.
     *
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets address.
     *
     * @param address the address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String getValue() {
        StringBuilder value = new StringBuilder();
        if (getSurname() != null) {
            value.append(getSurname());
        }
        if (getName() != null && getName().length() > 0) {
            value.append(" ");
            value.append(getName().substring(0, 1).toUpperCase());
            value.append(".");
        }
        if (getMiddlename() != null && getMiddlename().length() > 0) {
            value.append(" ");
            value.append(getMiddlename().substring(0, 1).toUpperCase());
            value.append(".");
        }
        return value.toString();
    }

    /**
     * Gets card number.
     *
     * @return the card number
     */
    public Long getCardNumber() {
        return cardNumber;
    }

    /**
     * Sets card number.
     *
     * @param cardNumber the card number
     */
    public void setCardNumber(Long cardNumber) {
        this.cardNumber = cardNumber;
    }

    @Override
    public boolean equals(Object o) {

        if (o == this) {
            return true;
        }
        if (!(o instanceof Client)) {
            return false;
        }

        Client client = (Client) o;

        return new EqualsBuilder()
                .append(getId(), client.getId())
                .append(passportSerial, client.passportSerial)
                .append(passportNumber, client.passportNumber)
                .append(address, client.address)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getId())
                .append(passportSerial)
                .append(passportNumber)
                .append(address)
                .toHashCode();
    }
}
