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
package com.theshamuel.medreg.model.report.service.impl;

import com.theshamuel.medreg.model.baseclasses.entity.BaseEntity;
import com.theshamuel.medreg.model.client.dao.ClientRepository;
import com.theshamuel.medreg.model.client.entity.Client;
import com.theshamuel.medreg.model.company.entity.Company;
import com.theshamuel.medreg.model.doctor.dao.DoctorRepository;
import com.theshamuel.medreg.model.doctor.entity.Doctor;
import com.theshamuel.medreg.model.sequence.dao.SequenceRepository;
import com.theshamuel.medreg.model.user.dao.UserRepository;
import com.theshamuel.medreg.model.user.entity.User;
import com.theshamuel.medreg.model.visit.dao.VisitRepository;
import com.theshamuel.medreg.model.visit.entity.Visit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class ReportParamsService {
    private static Logger logger = LoggerFactory.getLogger(ReportParamsService.class);

    ClientRepository clientRepository;

    DoctorRepository doctorRepository;

    UserRepository userRepository;

    VisitRepository visitRepository;

    SequenceRepository sequenceRepository;

    @Autowired
    public ReportParamsService(ClientRepository clientRepository, DoctorRepository doctorRepository, UserRepository userRepository, VisitRepository visitRepository, SequenceRepository sequenceRepository) {
        this.clientRepository = clientRepository;
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
        this.visitRepository = visitRepository;
        this.sequenceRepository = sequenceRepository;
    }

    public String getValue(String parameter, String clientId, String doctorId, Optional<Visit> visit, Optional<Company> company, String author, LocalDate date) {
        Optional<Client> client = Optional.ofNullable(clientRepository.findOne(clientId));
        Optional<Doctor> doctor = Optional.ofNullable(doctorRepository.findOne(doctorId));


        switch (parameter) {
            case "[number]":
                return LocalDate.now().format(DateTimeFormatter.ofPattern("yy/ddMM"));
            case "[contractNum]":
                if (visit.isPresent() && visit.get().getContractNum()!=null)
                    return visit.get().getContractNum();
                else
                    logger.error("Параметр \"Номер договора не задан\" ошибка расчета последовательности для visit c ID="+(visit.isPresent()?visit.get().getId():"null"));
                return "";
            case "[clientCardNum]":
                if (doctor.isPresent() && doctor.get().getPosition()!=null && doctor.get().getPosition().toLowerCase().contains("ginekolog"))
                    return "";
                else  {
                    if (client.isPresent() && client.get().getCardNumber() != null){
                        return client.get().getCardNumber().toString();
                    }else {
                        if (doctor.isPresent() && doctor.get().getPosition()!=null && !doctor.get().getPosition().toLowerCase().contains("ginekolog")) {
                            client.get().setCardNumber(Long.valueOf(sequenceRepository.getNextSequence("clientCardNum")));
                            clientRepository.save(client.get());
                            return client.get().getCardNumber().toString();
                        }
                    }
                    return "";
                }
            case "[date]": {
                if (date != null)
                    return date.format(BaseEntity.formatterDate);
                else
                    return LocalDate.now().format(BaseEntity.formatterDate);
            }
            case "[orgExtraName]": {
                if (company.isPresent() && company.get().getExtraName() != null)
                    return company.get().getExtraName();
                else {
                    logger.error("Параметр \"Альтернативное альтернативное\" в справочнике Паспорт организации НЕ задан");
                }
            }
            case "[orgFullName]": {
                if (company.isPresent() && company.get().getFullName() != null)
                    return company.get().getFullName();
                else {
                    logger.error("Параметр \"Полное наименование\" в справочнике \"Паспорт организации\" НЕ задан");
                }

                return "";
            }
            case "[orgShortName]": {
                if (company.isPresent() && company.get().getShortName() != null)
                    return company.get().getShortName();
                else {
                    logger.error("Параметр \"Краткое наименование\" в справочнике \"Паспорт организации\" НЕ задан");
                }

                return "";
            }
            case "[orgJurAddress]": {
                if (company.isPresent() && company.get().getAddressJur() != null)
                    return company.get().getAddressJur();
                else {
                    logger.error("Параметр \"Юридический адрес\" в справочнике \"Паспорт организации\" НЕ задан");
                }

                return "";
            }
            case "[orgAddress]": {
                if (company.isPresent() && company.get().getAddressFact() != null)
                    return company.get().getAddressFact();
                else {
                    logger.error("Параметр \"Фактический адрес\" в справочнике \"Паспорт организации\" НЕ задан");
                }

                return "";
            }
            case "[orgLicence]": {
                if (company.isPresent() && company.get().getLicense() != null)
                    return company.get().getLicense();
                else {
                    logger.error("Параметр \"Лицензия\" в справочнике \"Паспорт организации\" НЕ задан");
                }

                return "";
            }
            case "[orgOkpo]": {
                if (company.isPresent() && company.get().getOkpo() != null)
                    return company.get().getOkpo();
                else {
                    logger.error("Параметр \"ОКПО\" в справочнике \"Паспорт организации\" НЕ задан");
                }

                return "";
            }case "[orgOgrn]": {
                if (company.isPresent() && company.get().getOgrn() != null)
                    return company.get().getOgrn();
                else {
                    logger.error("Параметр \"ОГРН\" в справочнике \"Паспорт организации\" НЕ задан");
                }

                return "";
            }case "[orgDirectorFioRp]": {
                if (company.isPresent() && company.get().getDirectorNameRp() != null)
                    return company.get().getDirectorNameRp();
                else {
                    logger.error("Параметр \"ФИО директора в р/п\" в справочнике \"Паспорт организации\" НЕ задан");
                }

                return "";
            }case "[orgDirectorFioDp]": {
                if (company.isPresent() && company.get().getDirectorNameDp() != null)
                    return company.get().getDirectorNameDp();
                else {
                    logger.error("Параметр \"ФИО директора в д/п\" в справочнике \"Паспорт организации\" НЕ задан");
                }

                return "";
            }case "[orgDirectorFio]": {
                if (company.isPresent() && company.get().getDirector() != null) {
                    String[] tokens = company.get().getDirector().replace(" ","#").split("#");
                    if (tokens.length == 3) {
                        String result = tokens[0].concat(" ").concat(tokens[1].substring(0, 1).toUpperCase()).concat(". ").concat(tokens[2].substring(0, 1).toUpperCase()).concat(".");
                        return result;
                    }
                    return company.get().getDirector();
                }else {
                    logger.error("Параметр \"ФИО директора\" в справочнике \"Паспорт организации\" НЕ задан");
                }

                return "";
            }
            case "[orgInn]": {
                if (company.isPresent() && company.get().getInn() != null)
                    return company.get().getInn();
                else {
                    logger.error("Параметр \"ИНН\" в справочнике \"Паспорт организации\" НЕ задан");
                }

                return "";
            }
            case "[orgKpp]": {
                if (company.isPresent() && company.get().getKpp() != null)
                    return company.get().getKpp();
                else {
                    logger.error("Параметр \"КПП\" в справочнике \"Паспорт организации\" НЕ задан");
                }

                return "";
            }
            case "[orgSite]": {
                if (company.isPresent() && company.get().getSite() != null)
                    return company.get().getSite();
                else {
                    logger.error("Параметр \"EMAIL\" в справочнике \"Паспорт организации\" НЕ задан");
                }

                return "";
            }
            case "[orgPhone]": {
                if (company.isPresent() && company.get().getPhone() != null)
                    return company.get().getPhone();
                else {
                    logger.error("Параметр \"Телефон\" в справочнике \"Паспорт организации\" НЕ задан");
                }

                return "";
            }
            case "[patFio]": {
                if (client.isPresent() && client.get().getValue() != null)
                    return client.get().getValue();
                else {
                    logger.error("Параметр \"Имя, Фамилия, Отчество\" у пациента в справочнике \"Пациенты\" НЕ задан");
                }

                return "";
            }
            case "[patOld]": {
                if (client.isPresent() && client.get().getBirthday() != null)
                    return String.valueOf(ChronoUnit.YEARS.between(client.get().getBirthday(),LocalDate.now()));
                else {
                    logger.error("Параметр \"Дата рождения\" у пациента в справочнике \"Пациенты\" НЕ задан");
                }

                return "";
            }
            case "[patFioFull]": {
                if (client.isPresent() && client.get().getSurname() != null)
                    return client.get().getSurname() + " " + client.get().getName() + " " + client.get().getMiddlename();
                else {
                    logger.error("Параметр \"Имя, Фамилия, Отчество\" у пациента в справочнике \"Пациенты\" НЕ задан");
                }

                return "";
            }
            case "[patSex]": {
                if (client.isPresent() && client.get().getGender() != null) {
                    if (client.get().getGender().equals("woman"))
                        return "жен.";
                    else if (client.get().getGender().equals("man"))

                        return "муж.";
                    return "";
                } else {
                    logger.error("Параметр \"Пол\" у пациента в справочнике \"Пациенты\" НЕ задан");
                    return "";
                }
            }
            case "[patBirthdate]": {
                if (client.isPresent() && client.get().getBirthday() != null)
                    return client.get().getBirthday().format(BaseEntity.formatterDate);
                else {
                    logger.error("Параметр \"Дата рождения\" упациента в справочнике \"Пациенты\" НЕ задан");
                    return "";
                }
            }
            case "[patAddress]": {
                if (client.isPresent() && client.get().getAddress() != null)
                    return client.get().getAddress();
                else {
                    logger.error("Параметр \"Адрес\" у пациента в справочнике \"Пациенты\" НЕ задан");
                    return "";
                }
            }
            case "[patWorkPlace]": {
                if (client.isPresent() && client.get().getWorkPlace() != null)
                    return client.get().getWorkPlace();
                else {
                    logger.error("Параметр \"Место работы\" у пациента в справочнике \"Пациенты\" НЕ задан");
                    return "";
                }
            }
            case "[patWorkPosition]": {
                if (client.isPresent() && client.get().getWorkPosition() != null)
                    return client.get().getWorkPosition();
                else {
                    logger.error("Параметр \"Должноть\" у пациента в справочнике \"Пациенты\" НЕ задан");
                    return "";
                }
            }
            case "[patPhone]": {
                if (client.isPresent() && client.get().getPhone() != null)
                    return client.get().getPhone();
                else {
                    logger.error("Параметр \"Телефон\" у пациента в справочнике \"Пациенты\" НЕ задан");
                    return "";
                }
            }
            case "[patPassSerial]": {
                if (client.isPresent() && client.get().getPassportSerial() != null)
                    return client.get().getPassportSerial();
                else {
                    logger.error("Параметр \"Серия пасспорта\" у пациента в справочнике \"Пациенты\" НЕ задан");
                    return "";
                }
            }
            case "[patPassNum]": {
                if (client.isPresent() && client.get().getPassportNumber() != null)
                    return client.get().getPassportNumber();
                else {
                    logger.error("Параметр \"Номер пасспорта\" у пациента в справочнике \"Пациенты\" НЕ задан");
                }

                return "";
            }
            case "[patPassPlace]": {
                if (client.isPresent() && client.get().getPassportPlace() != null)
                    return client.get().getPassportPlace();
                else {
                    logger.error("Параметр \"Место выдачи пасспорта\" у пациента в справочнике \"Пациенты\" НЕ задан");
                }

                return "";
            }
            case "[patPassDate]": {
                if (client.isPresent() && client.get().getPassportDate() != null)
                    return client.get().getPassportDate().format(BaseEntity.formatterDate);
                else {
                    logger.error("Параметр \"Дата выдачи пасспорта\" у пациента в справочнике \"Пациенты\" НЕ задан");
                }

                return "";
            }
            case "[user]": {
                User user = userRepository.findByLogin(author);
                if (author != null && user != null && user.getFullname() != null)
                    return user.getFullname();
                else {

                    logger.error("Параметр \"author\" НЕ задан");
                    return "";
                }
            }
            case "[doctorFioFull]": {
                if (doctor.isPresent() && doctor.get().getSurname() != null)
                    return doctor.get().getSurname() + " " + doctor.get().getName() + " " + doctor.get().getMiddlename();
                else {
                    logger.error("Параметр \"ФИО\" у пользователя в справочнике \"Пользователи\" НЕ задан");
                    return "";
                }
            }
            case "[doctorFio]": {
                if (doctor.isPresent() && doctor.get().getValue() != null)
                    return doctor.get().getValue();
                else {
                    logger.error("Параметр \"ФИО\" у доктора в справочнике \"Доктора\" НЕ задан");
                    return "";
                }
            }
            default:
                return "";
        }

    }

    public List<String> getParams (String str){
       List<String> result = new ArrayList<>();
       String[] tokens = str.split("]");
       for (int i=0; i < tokens.length; i++){
           if (tokens[i].contains("[")) {
               String tmp = tokens[i].substring(tokens[i].indexOf("["), tokens[i].length()).concat("]");
               result.add(tmp);
           }
       }
       return result;
    }
}
