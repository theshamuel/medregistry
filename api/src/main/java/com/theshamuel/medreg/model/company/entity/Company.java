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
package com.theshamuel.medreg.model.company.entity;

import com.theshamuel.medreg.model.baseclasses.entity.BaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * The Company entity class.
 *
 * @author Alex Gladkikh
 */
@Document(collection = "company")
public class Company extends BaseEntity {

    @Field("okpo")
    private String okpo;

    @Field("extraName")
    private String extraName;

    @Field("shortName")
    private String shortName;

    @Field("fullName")
    private String fullName;

    @Field("addressFact")
    private String addressFact;

    @Field("addressJur")
    private String addressJur;

    @Field("ogrn")
    private String ogrn;

    @Field("okato")
    private String okato;

    @Field("inn")
    private String inn;

    @Field("kpp")
    private String kpp;

    @Field("license")
    private String license;

    @Field("bank")
    private String bank;

    @Field("checkingAccount")
    private String checkingAccount;

    @Field("corrAccount")
    private String corrAccount;

    @Field("director")
    private String director;

    @Field("directorNameRp")
    private String directorNameRp;

    @Field("directorNameDp")
    private String directorNameDp;

    @Field("phone")
    private String phone;

    @Field("site")
    private String site;

    @Field("extraInfo")
    private String extraInfo;

    /**
     * Gets okpo.
     *
     * @return the okpo
     */
    public String getOkpo() {
        return okpo;
    }

    /**
     * Sets okpo.
     *
     * @param okpo the okpo
     */
    public void setOkpo(String okpo) {
        this.okpo = okpo;
    }

    /**
     * Gets extra name.
     *
     * @return the extra name
     */
    public String getExtraName() {
        return extraName;
    }

    /**
     * Sets extra name.
     *
     * @param extraName the extra name
     */
    public void setExtraName(String extraName) {
        this.extraName = extraName;
    }

    /**
     * Gets short name.
     *
     * @return the short name
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * Sets short name.
     *
     * @param shortName the short name
     */
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    /**
     * Gets full name.
     *
     * @return the full name
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Sets full name.
     *
     * @param fullName the full name
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * Gets address fact.
     *
     * @return the address fact
     */
    public String getAddressFact() {
        return addressFact;
    }

    /**
     * Sets address fact.
     *
     * @param addressFact the address fact
     */
    public void setAddressFact(String addressFact) {
        this.addressFact = addressFact;
    }

    /**
     * Gets address jur.
     *
     * @return the address jur
     */
    public String getAddressJur() {
        return addressJur;
    }

    /**
     * Sets address jur.
     *
     * @param addressJur the address jur
     */
    public void setAddressJur(String addressJur) {
        this.addressJur = addressJur;
    }

    /**
     * Gets ogrn.
     *
     * @return the ogrn
     */
    public String getOgrn() {
        return ogrn;
    }

    /**
     * Sets ogrn.
     *
     * @param ogrn the ogrn
     */
    public void setOgrn(String ogrn) {
        this.ogrn = ogrn;
    }

    /**
     * Gets okato.
     *
     * @return the okato
     */
    public String getOkato() {
        return okato;
    }

    /**
     * Sets okato.
     *
     * @param okato the okato
     */
    public void setOkato(String okato) {
        this.okato = okato;
    }

    /**
     * Gets inn.
     *
     * @return the inn
     */
    public String getInn() {
        return inn;
    }

    /**
     * Sets inn.
     *
     * @param inn the inn
     */
    public void setInn(String inn) {
        this.inn = inn;
    }

    /**
     * Gets kpp.
     *
     * @return the kpp
     */
    public String getKpp() {
        return kpp;
    }

    /**
     * Sets kpp.
     *
     * @param kpp the kpp
     */
    public void setKpp(String kpp) {
        this.kpp = kpp;
    }

    /**
     * Gets license.
     *
     * @return the license
     */
    public String getLicense() {
        return license;
    }

    /**
     * Sets license.
     *
     * @param license the license
     */
    public void setLicense(String license) {
        this.license = license;
    }

    /**
     * Gets bank.
     *
     * @return the bank
     */
    public String getBank() {
        return bank;
    }

    /**
     * Sets bank.
     *
     * @param bank the bank
     */
    public void setBank(String bank) {
        this.bank = bank;
    }

    /**
     * Gets checking account.
     *
     * @return the checking account
     */
    public String getCheckingAccount() {
        return checkingAccount;
    }

    /**
     * Sets checking account.
     *
     * @param checkingAccount the checking account
     */
    public void setCheckingAccount(String checkingAccount) {
        this.checkingAccount = checkingAccount;
    }

    /**
     * Gets corr account.
     *
     * @return the corr account
     */
    public String getCorrAccount() {
        return corrAccount;
    }

    /**
     * Sets corr account.
     *
     * @param corrAccount the corr account
     */
    public void setCorrAccount(String corrAccount) {
        this.corrAccount = corrAccount;
    }

    /**
     * Gets director.
     *
     * @return the director
     */
    public String getDirector() {
        return director;
    }

    /**
     * Sets director.
     *
     * @param director the director
     */
    public void setDirector(String director) {
        this.director = director;
    }

    /**
     * Gets director name rp.
     *
     * @return the director name rp
     */
    public String getDirectorNameRp() {
        return directorNameRp;
    }

    /**
     * Sets director name rp.
     *
     * @param directorNameRp the director name rp
     */
    public void setDirectorNameRp(String directorNameRp) {
        this.directorNameRp = directorNameRp;
    }

    /**
     * Gets director name dp.
     *
     * @return the director name dp
     */
    public String getDirectorNameDp() {
        return directorNameDp;
    }

    /**
     * Sets director name dp.
     *
     * @param directorNameDp the director name dp
     */
    public void setDirectorNameDp(String directorNameDp) {
        this.directorNameDp = directorNameDp;
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
     * Gets site.
     *
     * @return the site
     */
    public String getSite() {
        return site;
    }

    /**
     * Sets site.
     *
     * @param site the site
     */
    public void setSite(String site) {
        this.site = site;
    }

    /**
     * Gets extra info.
     *
     * @return the extra info
     */
    public String getExtraInfo() {
        return extraInfo;
    }

    /**
     * Sets extra info.
     *
     * @param extraInfo the extra info
     */
    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }
}
