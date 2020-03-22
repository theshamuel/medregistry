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
package com.theshamuel.medreg.model.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.theshamuel.medreg.model.baseclasses.entity.BaseEntity;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * The User entity class.
 *
 * @author Alex Gladkikh
 */
@Document(collection = "users")
public class User extends BaseEntity {

    @Transient
    private DateTimeFormatter formatter = DateTimeFormatter
            .ofPattern("dd.MM.yyyy", Locale.getDefault());

    @Field("login")
    private String login;

    @Field("password")
    private String password;

    @Transient
    private String confirmPassword;

    @JsonIgnore
    @Field("salt")
    private String salt;

    @Field("fullname")
    private String fullname;

    @Field("isBlock")
    private Integer isBlock;

    @Transient
    private String isBlockLabel;

    @Field("roles")
    private Map<String, Integer> roles;

    @JsonIgnore
    @Field("blockDate")
    private LocalDateTime blockDate;


    /**
     * Instantiates a new User.
     */
    public User() {
    }


    /**
     * Gets login.
     *
     * @return the login
     */
    public String getLogin() {
        return login;
    }

    /**
     * Sets login.
     *
     * @param login the login
     */
    public void setLogin(String login) {
        this.login = login.trim();
    }

    /**
     * Gets salt.
     *
     * @return the salt
     */
    public String getSalt() {
        return salt;
    }

    /**
     * Sets salt.
     *
     * @param salt the salt
     */
    public void setSalt(String salt) {
        this.salt = salt;
    }

    /**
     * Gets password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets password.
     *
     * @param password the password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets fullname.
     *
     * @return the fullname
     */
    public String getFullname() {
        return fullname;
    }

    /**
     * Sets fullname.
     *
     * @param fullname the fullname
     */
    public void setFullname(String fullname) {
        this.fullname = fullname.trim();
    }

    /**
     * Gets IsBlock.
     *
     * @return the state
     */
    public Integer getIsBlock() {
        return isBlock;
    }

    /**
     * Sets IsBlock.
     *
     * @param isBlock the state
     */
    public void setIsBlock(Integer isBlock) {
        this.isBlock = isBlock;
    }

    /**
     * Gets is block label.
     *
     * @return the is block label
     */
    public String getIsBlockLabel() {
        if (getIsBlock() != null && getIsBlock().equals(1)) {
            return "Заблокирован";
        } else {
            return "Активный";
        }
    }

    /**
     * Gets roles.
     *
     * @return the roles
     */
    public Map getRoles() {
        return roles;
    }

    /**
     * Sets roles.
     *
     * @param roles the roles
     */
    public void setRoles(Map roles) {
        this.roles = roles;
    }

    /**
     * Gets block date.
     *
     * @return the block date
     */
    public LocalDateTime getBlockDate() {
        return blockDate;
    }

    /**
     * Sets block date.
     *
     * @param blockDate the block date
     */
    public void setBlockDate(LocalDateTime blockDate) {
        this.blockDate = blockDate;
    }


    /**
     * Gets confirm password.
     *
     * @return the confirm password
     */
    public String getConfirmPassword() {
        return getPassword();
    }


    @Override
    public boolean equals(Object o) {

        if (o == this) {
            return true;
        }
        if (!(o instanceof User)) {
            return false;
        }

        User user = (User) o;

        return new EqualsBuilder()
                .append(getId(), user.getId())
                .append(login, user.login)
                .append(fullname, user.fullname)
                .append(password, user.password)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getId())
                .append(login)
                .append(fullname)
                .append(password)
                .toHashCode();
    }
}