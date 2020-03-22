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
package com.theshamuel.medreg.model.visit.service.impl;

import com.theshamuel.medreg.model.appointment.dao.AppointmentRepository;
import com.theshamuel.medreg.model.appointment.entity.Appointment;
import com.theshamuel.medreg.model.baseclasses.service.BaseServiceImpl;
import com.theshamuel.medreg.model.client.dao.ClientRepository;
import com.theshamuel.medreg.model.client.entity.Client;
import com.theshamuel.medreg.model.doctor.dao.DoctorRepository;
import com.theshamuel.medreg.model.doctor.entity.Doctor;
import com.theshamuel.medreg.model.service.dao.ServiceRepository;
import com.theshamuel.medreg.model.service.dto.ServiceDto;
import com.theshamuel.medreg.model.service.entity.PersonalRate;
import com.theshamuel.medreg.model.service.entity.Service;
import com.theshamuel.medreg.model.service.service.ServiceService;
import com.theshamuel.medreg.model.visit.dao.VisitRepository;
import com.theshamuel.medreg.model.visit.dto.VisitDto;
import com.theshamuel.medreg.model.visit.entity.Visit;
import com.theshamuel.medreg.model.visit.service.VisitService;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

/**
 * The type Visit service.
 *
 * @author Alex Gladkikh
 */
@org.springframework.stereotype.Service
public class VisitServiceImpl extends BaseServiceImpl<VisitDto, Visit> implements VisitService {

    private VisitRepository visitRepository;

    private DoctorRepository doctorRepository;

    private ClientRepository clientRepository;

    private AppointmentRepository appointmentRepository;

    private ServiceRepository serviceRepository;

    private ServiceService serviceService;

    /**
     * Instantiates a new Visit service.
     *
     * @param visitRepository       the visit repository
     * @param doctorRepository      the doctor repository
     * @param clientRepository      the client repository
     * @param appointmentRepository the appointment repository
     * @param serviceRepository     the service repository
     * @param serviceService        the service service
     */
    @Autowired
    public VisitServiceImpl(VisitRepository visitRepository, DoctorRepository doctorRepository,
            ClientRepository clientRepository, AppointmentRepository appointmentRepository,
            ServiceRepository serviceRepository, ServiceService serviceService) {
        super(visitRepository);
        this.visitRepository = visitRepository;
        this.doctorRepository = doctorRepository;
        this.clientRepository = clientRepository;
        this.appointmentRepository = appointmentRepository;
        this.serviceRepository = serviceRepository;
        this.serviceService = serviceService;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isUniqueVisit(String doctorId, String appointmentId) {
        Optional<Appointment> appointment = Optional
                .ofNullable(appointmentRepository.findOne(appointmentId));
        Optional<Doctor> doctor = Optional.ofNullable(doctorRepository.findOne(doctorId));
        final boolean[] result = new boolean[]{true};
        doctor.ifPresent(e -> {
            LocalDate dateEvent = null;
            LocalTime timeEvent = null;
            if (appointment.isPresent()) {
                dateEvent = appointment.get().getDateEvent();
                timeEvent = appointment.get().getTimeEvent();
            } else {
                dateEvent = LocalDate.now();
                timeEvent = LocalTime.now();
            }
            if (visitRepository.findByDateTimeEventAndDoctor(e, dateEvent, timeEvent) != null) {
                result[0] = false;
            }
        });
        return result[0];
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<ServiceDto> getServices(String visitId) {
        List<ServiceDto> result = new ArrayList<>();
        Visit visit = visitRepository.findOne(visitId);
        if (visit != null) {
            Optional<List<Service>> tmp = Optional.ofNullable(visit.getServices());
            tmp.ifPresent(item -> {
                item.forEach(e -> {
                    BigInteger price = e.getPrice();

                    if (serviceService.hasPersonalRate(e.getId(), visit.getDoctor().getId())) {
                        price = serviceService
                                .getPriceFromPersonalRate(e.getId(), visit.getDoctor().getId());
                    }

                    result.add(new ServiceDto(e.getId(), e.getLabel(), price, e.getDiscount()));
                });
            });
            return result;
        }
        return Collections.emptyList();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List getVisitsByDoctorAndDateEvent(String doctorId, LocalDate dateEvent) {
        Optional<Doctor> doctor = Optional.ofNullable(doctorRepository.findOne(doctorId));
        List<VisitDto> result = new ArrayList<>();
        doctor.ifPresent(e -> {
            List<Visit> list = visitRepository.findByDateEventAndDoctor(e, dateEvent);
            result.addAll(list.stream().map(i -> obj2dto(i)).collect(Collectors.toList()));
            //Calculation sum of visit
            result.forEach(item -> {
                if (item.getServices() != null) {
                    BigInteger totalSum = item.getServices().stream()
                            .reduce(BigInteger.valueOf(0), (res, p) -> {
                                        BigInteger percentDiscount = (p.getDiscount() != null
                                                && p.getDiscount().compareTo(BigInteger.valueOf(0)) > 0) ? p
                                                .getDiscount() : BigInteger.valueOf(1);
                                        if (percentDiscount.compareTo(BigInteger.valueOf(1)) > 0) {
                                            return res = res
                                                    .add(BigInteger.valueOf(100).subtract(percentDiscount)
                                                            .multiply(p.getPrice()).abs()
                                                            .divide(BigInteger.valueOf(100)));
                                        } else {
                                            return res = res.add(percentDiscount.multiply(p.getPrice()));
                                        }
                                    },
                                    (sum1, sum2) -> {
                                        return sum1.add(sum2);
                                    });
                    item.setTotalSum(totalSum);
                }
            });
        });

        return result;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<VisitDto> getVisitsByDateEvent(LocalDate dateEvent) {
        List<VisitDto> result = new ArrayList<>();

        List<Visit> list = visitRepository.findByDateEvent(dateEvent);
        result.addAll(list.stream().map(i -> obj2dto(i)).collect(Collectors.toList()));
        //Calculation sum of visit
        result.forEach(item -> {
            if (item.getServices() != null) {
                BigInteger totalSum = item.getServices().stream()
                        .reduce(BigInteger.valueOf(0), (res, p) -> {
                                    BigInteger percentDiscount = (p.getDiscount() != null
                                            && p.getDiscount().compareTo(BigInteger.valueOf(0)) > 0) ? p
                                            .getDiscount() : BigInteger.valueOf(1);
                                    if (percentDiscount.compareTo(BigInteger.valueOf(1)) > 0) {
                                        return res = res
                                                .add(BigInteger.valueOf(100).subtract(percentDiscount)
                                                        .multiply(p.getPrice()).abs()
                                                        .divide(BigInteger.valueOf(100)));
                                    } else {
                                        return res = res.add(percentDiscount.multiply(p.getPrice()));
                                    }
                                },
                                (sum1, sum2) -> {
                                    return sum1.add(sum2);
                                });
                item.setTotalSum(totalSum);
            }
        });

        return result;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void addService(String visitId, String serviceId, BigInteger discount) {
        Visit visit = visitRepository.findOne(visitId);
        Service service = serviceRepository.findOne(serviceId);
        Doctor doctor = visit.getDoctor();
        Random random = new Random();
        if (visit != null && service != null && doctor != null) {
            if (service.getPersonalRates() != null && service.getPersonalRates().size() > 0) {
                for (PersonalRate personalRate : service.getPersonalRates()) {
                    if (personalRate.getDoctorId().equals(doctor.getId())) {
                        service.setPrice(personalRate.getPrice());
                        service.setDoctorPay(personalRate.getDoctorPay());
                        service.setDoctorPayType(personalRate.getDoctorPayType());
                        break;
                    }
                }
            }
            service.setDiscount(discount);
            service.setId(service.getId().concat("MEDREG")
                    .concat(String.valueOf(random.nextInt(Integer.MAX_VALUE))));
            if (visit.getServices() != null) {
                List result = visit.getServices();
                result.add(service);
                visit.setServices(result);
                visitRepository.save(visit);
            } else {
                List<Service> list = new ArrayList<>();
                list.add(service);
                visit.setServices(list);
                visitRepository.save(visit);
            }
        }

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteService(String visitId, String serviceId) {

        Visit visit = visitRepository.findOne(visitId);
        if (visit != null) {
            List<Service> list = visit.getServices();
            if (list != null) {
                for (Service el : list) {
                    if (el.getId().equals(serviceId)) {
                        list.remove(el);
                        break;
                    }
                }
                visit.setServices(list);
                visitRepository.save(visit);
            }

        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public VisitDto save(VisitDto dto) {
        final Visit[] result = {null};
        if (dto.getAppointmentId() != null && dto.getAppointmentId().trim().length() > 0) {
            if (dto.getId() != null) {
                Optional<Visit> editVisit = Optional
                        .ofNullable(visitRepository.findOne(dto.getId()));
                editVisit.ifPresent(e -> {
                    Optional<Appointment> oldAppointment = Optional.ofNullable(e.getAppointment());
                    oldAppointment.ifPresent(i -> {
                        if (!i.getId().equals(dto.getAppointmentId())) {
                            oldAppointment.ifPresent(item -> {
                                item.setHasVisit(false);
                                appointmentRepository.save(item);
                            });

                        }

                        dto.setServices(editVisit.get().getServices());
                    });
                    Optional<Appointment> newAppointment = Optional
                            .ofNullable(appointmentRepository.findOne(dto.getAppointmentId()));
                    newAppointment.ifPresent(newItem -> {
                        newItem.setHasVisit(true);
                        dto.setDateEvent(newItem.getDateEvent());
                        dto.setTimeEvent(newItem.getTimeEvent());
                        appointmentRepository.save(newItem);
                    });
                    VisitDto savedDto = super.save(dto);
                    result[0] = visitRepository.findOne(savedDto.getId());
                });

            } else {
                Optional<Appointment> appointment = Optional
                        .ofNullable(appointmentRepository.findOne(dto.getAppointmentId()));
                appointment.ifPresent(item -> {
                    item.setHasVisit(true);
                    appointmentRepository.save(item);
                    dto.setDateEvent(item.getDateEvent());
                    dto.setTimeEvent(item.getTimeEvent());
                    VisitDto savedDto = super.save(dto);
                    result[0] = visitRepository.findOne(savedDto.getId());
                });
            }
        } else {
            boolean isFirstChange = false;
            Visit visit = null;
            if (dto.getId() != null) {
                visit = visitRepository.findOne(dto.getId());
            }
            if (visit != null && visit.getAppointment() != null) {
                isFirstChange = true;
            }
            if ((dto != null && dto.getId() != null && isFirstChange) || dto.getId() == null) {
                dto.setDateEvent(LocalDate.now());
                dto.setTimeEvent(LocalTime.now());
            }
            VisitDto savedDto = super.save(dto);
            result[0] = visitRepository.findOne(savedDto.getId());
        }
        return obj2dto(result[0]);
    }

    @Override
    public List<VisitDto> findAll(Sort sort) {
        return super.findAll(sort);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(String id) {
        Visit visit = visitRepository.findOne(id);
        if (visit.getAppointment() != null) {
            Optional<Appointment> appointment = Optional
                    .ofNullable(appointmentRepository.findOne(visit.getAppointment().getId()));
            appointment.ifPresent(item -> {
                item.setHasVisit(false);
                appointmentRepository.save(item);
            });
        }
        super.delete(id);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public VisitDto obj2dto(Visit visit) {
        String doctorId = visit.getDoctor() != null ? visit.getDoctor().getId() : null;
        String doctorLabel = visit.getDoctor() != null ? visit.getDoctor().getValue() : "";

        String clientId = visit.getClient() != null ? visit.getClient().getId() : null;
        String clientLabel = visit.getClient() != null ? visit.getClient().getValue() : "";
        String phoneLabel = visit.getClient() != null ? visit.getClient().getPhone() : "-";
        String passportLabel =
                visit.getClient() != null ? visit.getClient().getPassportSerial().concat("/")
                        .concat(visit.getClient().getPassportNumber()) : "-";
        String appointmentId =
                visit.getAppointment() != null ? visit.getAppointment().getId() : null;

        return new VisitDto(visit.getId(), visit.getCreatedDate(), visit.getModifyDate(),
                visit.getAuthor(), visit.getContractNum(), doctorId, doctorLabel, clientId,
                visit.getServices(), visit.getTerminalSum(), BigInteger.valueOf(0),
                visit.getDateEvent(), visit.getTimeEvent(), clientLabel, appointmentId,
                visit.getDateTimeLabel(), visit.getTimeLabel(), phoneLabel, passportLabel);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Visit dto2obj(VisitDto dto) {
        Doctor doctor = null;
        Client client = null;
        Appointment appointment = null;
        if (dto != null) {
            if (dto.getDoctorId() != null) {
                doctor = doctorRepository.findOne(dto.getDoctorId());
            }
            if (dto.getClientId() != null) {
                client = clientRepository.findOne(dto.getClientId());
            }
            if (dto.getAppointmentId() != null) {
                appointment = appointmentRepository.findOne(dto.getAppointmentId());
            }
        }
        return new Visit(dto.getId(), dto.getCreatedDate(), dto.getModifyDate(),
                dto.getAuthor(), dto.getContractNum(), doctor,
                client, dto.getDateEvent(), dto.getTimeEvent(), appointment, dto.getServices(),
                dto.getTerminalSum());
    }
}
