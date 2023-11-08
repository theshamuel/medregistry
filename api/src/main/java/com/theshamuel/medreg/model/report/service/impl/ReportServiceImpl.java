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
package com.theshamuel.medreg.model.report.service.impl;

import com.theshamuel.medreg.exception.NotFoundEntityException;
import com.theshamuel.medreg.model.appointment.dto.AppointmentDto;
import com.theshamuel.medreg.model.appointment.service.AppointmentService;
import com.theshamuel.medreg.model.baseclasses.service.BaseServiceImpl;
import com.theshamuel.medreg.model.company.dao.CompanyRepository;
import com.theshamuel.medreg.model.company.entity.Company;
import com.theshamuel.medreg.model.doctor.dao.DoctorRepository;
import com.theshamuel.medreg.model.doctor.entity.Doctor;
import com.theshamuel.medreg.model.report.dao.ReportRepository;
import com.theshamuel.medreg.model.report.dao.impl.ReportRepositoryImpl;
import com.theshamuel.medreg.model.report.dto.ReportDto;
import com.theshamuel.medreg.model.report.entity.Report;
import com.theshamuel.medreg.model.report.entity.ReportOfWorkDay;
import com.theshamuel.medreg.model.report.entity.ReportOfWorkDayByDoctor;
import com.theshamuel.medreg.model.report.entity.ReportOfWorkDayRecord;
import com.theshamuel.medreg.model.report.entity.RootReportByDoctor;
import com.theshamuel.medreg.model.report.service.ReportService;
import com.theshamuel.medreg.model.service.dao.ServiceRepository;
import com.theshamuel.medreg.model.service.entity.PersonalRate;
import com.theshamuel.medreg.model.service.entity.Service;
import com.theshamuel.medreg.model.service.service.ServiceService;
import com.theshamuel.medreg.model.types.CategoryOfService;
import com.theshamuel.medreg.model.visit.dao.VisitRepository;
import com.theshamuel.medreg.model.visit.entity.Visit;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.CharacterRun;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.hwpf.usermodel.Section;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xwpf.usermodel.IBody;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.LineSpacingRule;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFStyles;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFonts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

/**
 * The Report service class.
 *
 * @author Alex Gladkikh
 */
@org.springframework.stereotype.Service
public class ReportServiceImpl extends BaseServiceImpl<ReportDto, Report> implements ReportService {

    private static Logger logger = LoggerFactory.getLogger(ReportRepositoryImpl.class);
    private final ReportParamsService reportParamsService;
    private final Environment environment;
    private ReportRepository reportRepository;
    private VisitRepository visitRepository;
    private ServiceRepository serviceRepository;
    private AppointmentService appointmentService;
    private ServiceService serviceService;
    private DoctorRepository doctorRepository;
    private CompanyRepository companyRepository;

    /**
     * Instantiates a new Report service.
     *
     * @param reportRepository   the report repository
     * @param visitRepository    the visit repository
     * @param serviceRepository  the service repository
     * @param appointmentService the appointment service
     * @param serviceService     the service service
     * @param doctorRepository   the doctor repository
     * @param companyRepository  the company repository
     */
    @Autowired
    public ReportServiceImpl(ReportRepository reportRepository, VisitRepository visitRepository,
                             ServiceRepository serviceRepository, AppointmentService appointmentService,
                             ServiceService serviceService, DoctorRepository doctorRepository,
                             CompanyRepository companyRepository, ReportParamsService reportParamsService, Environment environment) {
        super(reportRepository);
        this.reportRepository = reportRepository;
        this.visitRepository = visitRepository;
        this.serviceRepository = serviceRepository;
        this.serviceService = serviceService;
        this.appointmentService = appointmentService;
        this.doctorRepository = doctorRepository;
        this.companyRepository = companyRepository;
        this.reportParamsService = reportParamsService;
        this.environment = environment;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isUniqueReport(String serviceId, String template) {
        Service service = serviceRepository.findOne(serviceId);
        return reportRepository.isUniqueReport(service, template);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<ReportDto> getReportsToVisit(String visitId) {
        List result = new ArrayList();
        Optional<Visit> visit = Optional.ofNullable(visitRepository.findOne(visitId));
        visit.ifPresent(e -> {
            Optional<List> services = Optional.ofNullable(e.getServices());
            services.ifPresent(i -> {
                i.stream().distinct().forEachOrdered(action -> {
                    String serviceId = ((Service) action).getId().split("MEDREG")[0];
                    result.addAll(getReportsByService(serviceId));
                });
            });
        });
        return result;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<ReportDto> getReportsByService(String serviceId) {
        List result = new ArrayList();
        result.addAll(getCommonReports());
        Optional<Service> service = Optional.ofNullable(serviceRepository.findOne(serviceId));
        service.ifPresent(e -> {
            result.addAll(reportRepository.findByService(e).stream().map(i -> obj2dto(i))
                    .collect(Collectors.toList()));
        });
        return result;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<ReportDto> getCommonReports() {
        return reportRepository.findCommonReports().stream().map(i -> obj2dto(i))
                .collect(Collectors.toList());
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getReportOfWorkDay(LocalDate dateWork, String author) {
        logger.info("CALL getReportOfWorkDay START - {}", LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss", Locale.getDefault())));
        byte[] resultFile = null;
        Long start = System.currentTimeMillis();
        Path commonPath = Paths.get("//Users/theal-f/IdeaProjects/reportstest/");
        if (environment.getProperty("REPORT_PATH") != null) {
            commonPath = Paths.get(environment.getProperty("REPORT_PATH"));
        }
        logger.info("ENV: REPORT_PATH={}", commonPath);
        final ReportOfWorkDay dataForReport = getDataForReportOfWorkDay(dateWork);

        try {
            XWPFDocument fileReport = new XWPFDocument(getFileReportOfWorkDay(commonPath));

            List<IBodyElement> bodyElements = fileReport.getBodyElements();
            String pattern = "^.*\\[\\w+\\].*$";
            for (int j = 0; j < bodyElements.size(); j++) {
                IBodyElement element = bodyElements.get(j);
                if (element instanceof XWPFTable) {
                    int amountServices = 100;
                    int header = 1;
                    if (dataForReport.getRecords() != null && dataForReport.getRecords() != null) {
                        for (int i = 0; i < dataForReport.getRecords().size(); i++) {
                            ReportOfWorkDayRecord record = dataForReport.getRecords().get(i);
                            if (record != null) {
                                ((XWPFTable) element).getRow(header + i).getCell(0)
                                        .setText(String.valueOf(i + 1));
                                ((XWPFTable) element).getRow(header + i).getCell(1)
                                        .setText(String.valueOf(record.getLabel()));
                                ((XWPFTable) element).getRow(header + i).getCell(2)
                                        .setText(String.valueOf(record.getAmount()));
                                ((XWPFTable) element).getRow(header + i).getCell(3)
                                        .setText(String.valueOf(record.getPrice()));
                                ((XWPFTable) element).getRow(header + i).getCell(4)
                                        .setText(String.valueOf(record.getSum()));
                            }
                        }
                        ((XWPFTable) element).getRows().get(header + amountServices).getCell(1)
                                .setText(String.valueOf(dataForReport.getTotalSum()));
                        ((XWPFTable) element).getRows().get(header + amountServices + 1).getCell(1)
                                .setText(String.valueOf(dataForReport.getTerminalSum()));
                        ((XWPFTable) element).getRows().get(header + amountServices + 2).getCell(1)
                                .setText(String.valueOf(dataForReport.getMazkiSum()));
                        ((XWPFTable) element).getRows().get(header + amountServices + 3).getCell(1)
                                .setText(String.valueOf(dataForReport.getRemainder()));

                    }
                    int lastFillRow = dataForReport.getRecords().size() + header;
                    int count = amountServices - dataForReport.getRecords().size() - 1;
                    for (int k = 0; k <= count; k++) {
                        ((XWPFTable) element).removeRow(lastFillRow);
                    }

                } else {
                    Optional<IBody> iBody = Optional.ofNullable(element.getBody());
                    iBody.ifPresent(iBodyVal -> {
                        List paragraphs = iBodyVal.getParagraphs();
                        if (paragraphs != null) {
                            paragraphs.forEach(paragraph -> {
                                if (paragraph instanceof XWPFParagraph) {
                                    List<XWPFRun> runs = ((XWPFParagraph) paragraph).getRuns();
                                    if (runs != null) {
                                        runs.forEach(run -> {
                                            if (run.text() != null && run.text().matches(pattern)) {
                                                String param = run.text()
                                                        .substring(run.text().indexOf("["),
                                                                run.text().indexOf("]") + 1);
                                                String value = reportParamsService
                                                        .getValue(param, "-1", "-1",
                                                                Optional.empty(), Optional.empty(),
                                                                author, dateWork);
                                                if (value != null) {
                                                    int index = run.text().indexOf("[");
                                                    if (index > 0) {
                                                        value = run.text().substring(0, index)
                                                                .concat(value);
                                                    }

                                                    run.setText(value, 0);
                                                }

                                            }

                                        });
                                    }
                                }
                            });
                        }
                    });

                }
            }
            XWPFStyles styles = fileReport.createStyles();

            CTFonts fonts = CTFonts.Factory.newInstance();
            fonts.setEastAsia("Times New Roman");
            fonts.setHAnsi("Times New Roman");

            styles.setDefaultFonts(fonts);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            fileReport.write(baos);
            resultFile = baos.toByteArray();
            fileReport.close();
        } catch (IOException e) {
            logger.error("ERROR: Not found report file template in path: {}\n{}", commonPath, e);
            throw new NotFoundEntityException("Не найден файл для отчета за день");
        }

        Long end = System.currentTimeMillis();
        logger.info("PROCESSING TIME: {}", ((end - start) / 1000) / 60);
        logger.info("CALL getReportOfWorkDay END - {}", LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss", Locale.getDefault())));
        return resultFile;
    }


    private InputStream getFileReportOfWorkDay(Path path) {
        Path filePath = Paths
                .get(path.toString().concat("/templateReportOfWorkDay").concat(".docx"));
        InputStream inputStream = null;
        try {
            inputStream = Files.newInputStream(filePath);
        } catch (IOException e) {
            logger.error("ERROR: Not found report file template in path: {}\n{}", filePath, e);
            filePath = Paths.get(path.toString().concat("/templateReportOfWorkDay").concat(".doc"));
            try {
                inputStream = Files.newInputStream(filePath);
            } catch (IOException e1) {
                logger.error("ERROR: Not found report file template in path: {}\n{}", filePath, e);
                throw new NotFoundEntityException("Не найден файл для отчета за день");
            }

        }

        return inputStream;
    }


    private InputStream getFileReportOfWorkDayByDoctor(Path path) {
        Path filePath = Paths
                .get(path.toString().concat("/templateReportOfWorkDayByDoctor").concat(".docx"));
        InputStream inputStream = null;
        try {
            inputStream = Files.newInputStream(filePath);
        } catch (IOException e) {
            logger.error("ERROR: Not found report file template in path: {}\n{}", filePath, e);
            filePath = Paths
                    .get(path.toString().concat("/templateReportOfWorkDayByDoctor").concat(".doc"));
            try {
                inputStream = Files.newInputStream(filePath);
            } catch (IOException e1) {
                logger.error("ERROR: Not found report file template in path: {}\n{}", filePath, e);
                throw new NotFoundEntityException("Не найден файл для отчета за день по доктору");
            }

        }

        return inputStream;
    }

    private InputStream getFileReportClientCard(Path path) {
        Path filePath = Paths.get(path.toString().concat("/CardClient").concat(".xls"));
        InputStream inputStream = null;
        try {
            inputStream = Files.newInputStream(filePath);
        } catch (IOException e) {
            logger.error("ERROR: Not found report file template in path: {}\n{}", filePath, e);
            filePath = Paths.get(path.toString().concat("/CardClient").concat(".xlsx"));
            try {
                inputStream = Files.newInputStream(filePath);
                return inputStream;
            } catch (IOException e1) {
                logger.error("ERROR: Not found report file template in path: {}\n{}", filePath, e);
                throw new NotFoundEntityException("Не найден файл для отчета за день по доктору");
            }

        }

        return inputStream;
    }


    private InputStream getFileReportTemplate(Path path, String reportId) {
        String templateName = reportRepository.findOne(reportId).getTemplate();
        InputStream inputStream = null;
        if (templateName != null) {
            Path filePath = Paths.get(path.toString().concat("/" + templateName).concat(".docx"));

            try {
                inputStream = Files.newInputStream(filePath);
            } catch (IOException e) {
                logger.error("ERROR: Not found report file template in path: {}\n{}", filePath, e);
                filePath = Paths.get(path.toString().concat("/" + templateName).concat(".doc"));
                try {
                    inputStream = Files.newInputStream(filePath);
                } catch (IOException e1) {
                    logger.error("ERROR: Not found report file template in path: {}\n{}", filePath,
                            e);
                    throw new NotFoundEntityException(
                            "Не найден файл для отчета за день по доктору");
                }

            }
        } else {
            throw new NotFoundEntityException(
                    "Не найден шаблон отчета в справочнике \"Бланки услуг\"");
        }

        return inputStream;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getReportOfWorkDayByDoctor(LocalDate dateWork, String author) {
        logger.info("CALL getReportOfWorkDayByDoctor START - {}", LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss", Locale.getDefault())));
        byte[] resultFile;
        Long start = System.currentTimeMillis();
        Path commonPath = Paths.get("//Users/theal-f/IdeaProjects/reportstest/");
        if (environment.getProperty("REPORT_PATH") != null) {
            commonPath = Paths.get(environment.getProperty("REPORT_PATH"));
        }
        logger.info("ENV: REPORT_PATH={}", commonPath);
        final RootReportByDoctor dataForReport = getDataForReportOfWorkDayByDoctor(dateWork);
        try {
            XWPFDocument fileReport = new XWPFDocument(getFileReportOfWorkDayByDoctor(commonPath));

            List<IBodyElement> bodyElements = fileReport.getBodyElements();
            String pattern = "^.*\\[\\w+\\].*$";
            for (int ix = 0; ix < bodyElements.size(); ix++) {
                IBodyElement element = bodyElements.get(ix);
                if (element instanceof XWPFTable) {
                    int amountDoctors = 30;
                    int amountServices = 30;
                    int header = 1;
                    int delta = 2;
                    Optional<List<ReportOfWorkDayByDoctor>> reportOfWorkDayByDoctorList = Optional
                            .ofNullable(dataForReport.getRecords());
                    ((XWPFTable) element).getRows()
                            .get(amountDoctors * amountServices + header + amountDoctors * 2)
                            .getCell(1).setText(String.valueOf(dataForReport.getTotalSum()));
                    if (reportOfWorkDayByDoctorList.isPresent()
                            && reportOfWorkDayByDoctorList.get().size() > 0) {
                        for (int i = 0; i < reportOfWorkDayByDoctorList.get().size(); i++) {
                            ReportOfWorkDay reportOfWorkDay = reportOfWorkDayByDoctorList.get()
                                    .get(i).getReportOfWorkDay();
                            int doctorFioRow = i * (amountServices + delta) + header;
                            int totalRowByDoctor =
                                    i * (amountServices + delta) + header + amountServices + 1;
                            ((XWPFTable) element).getRows().get(doctorFioRow).getCell(0).setText(
                                    reportOfWorkDayByDoctorList.get().get(i).getDoctorFio());
                            List<ReportOfWorkDayRecord> records = reportOfWorkDay.getRecords();

                            for (int j = 0; j < records.size(); j++) {
                                int recordRow = i * (amountServices + delta) + header + j + 1;
                                logger.info(
                                        "j=" + j + ";" + records.get(j).getLabel() + ";" + records
                                                .get(j).getSum() + ";" + records.get(j).getAmount()
                                                + ";" + records.get(j).getDoctorPayTypePersonal()
                                                + ";" + records.get(j).getSalary().multiply(
                                                BigInteger.valueOf(records.get(j).getAmount())));
                                ((XWPFTable) element).getRows().get(recordRow).getCell(0)
                                        .setText(String.valueOf(j + 1));
                                ((XWPFTable) element).getRows().get(recordRow).getCell(1)
                                        .setText(records.get(j).getLabel());
                                ((XWPFTable) element).getRows().get(recordRow).getCell(2)
                                        .setText(String.valueOf(records.get(j).getAmount()));
                                ((XWPFTable) element).getRows().get(recordRow).getCell(3).setText(
                                        records.get(j).getDoctorPayTypePersonal() != null && records
                                                .get(j).getDoctorPayTypePersonal().equals("percent")
                                                ? records.get(j).getDoctorPayPersonal() + "%"
                                                : records.get(j).getDoctorPayPersonal()
                                                        + " руб.");
                                if (records.get(j).getDoctorPayTypePersonal() != null && records
                                        .get(j).getDoctorPayTypePersonal().equals("percent")) {
                                    ((XWPFTable) element).getRows().get(recordRow).getCell(4)
                                            .setText(String.valueOf(records.get(j).getPrice()
                                                    .multiply(records.get(j).getDoctorPayPersonal())
                                                    .multiply(BigInteger
                                                            .valueOf(records.get(j).getAmount()))
                                                    .divide(BigInteger.valueOf(100))));
                                } else {
                                    ((XWPFTable) element).getRows().get(recordRow).getCell(4)
                                            .setText(String.valueOf(records.get(j).getSalary()
                                                    .multiply(BigInteger
                                                            .valueOf(records.get(j).getAmount()))));
                                }

                            }
                            ((XWPFTable) element).getRows().get(totalRowByDoctor).getCell(1)
                                    .setText(String.valueOf(reportOfWorkDay.getTotalSum()));
                        }

                    }
                    int lastFillRow =
                            reportOfWorkDayByDoctorList.get().size() * (amountServices + delta)
                                    + header;
                    int count = (amountDoctors * (amountServices + delta) + header) - lastFillRow;
                    for (int k = 0; k < count; k++) {
                        ((XWPFTable) element).removeRow(lastFillRow);
                    }

                    for (int n = reportOfWorkDayByDoctorList.get().size() - 1; n > -1; n--) {
                        if (n > 0) {
                            lastFillRow =
                                    reportOfWorkDayByDoctorList.get().get(n).getReportOfWorkDay()
                                            .getRecords().size() + (amountServices + delta) * n
                                            + delta;
                        } else {
                            lastFillRow =
                                    reportOfWorkDayByDoctorList.get().get(n).getReportOfWorkDay()
                                            .getRecords().size() + header + 1;
                        }

                        count = amountServices - (reportOfWorkDayByDoctorList.get().get(n)
                                .getReportOfWorkDay().getRecords().size());

                        for (int k = 0; k < count; k++) {
                            ((XWPFTable) element).removeRow(lastFillRow);
                        }
                    }
                } else {
                    Optional<IBody> iBody = Optional.ofNullable(element.getBody());
                    iBody.ifPresent(iBodyVal -> {
                        List paragraphs = iBodyVal.getParagraphs();
                        if (paragraphs != null) {
                            paragraphs.forEach(paragraph -> {
                                if (paragraph instanceof XWPFParagraph) {
                                    List<XWPFRun> runs = ((XWPFParagraph) paragraph).getRuns();
                                    if (runs != null) {
                                        runs.forEach(run -> {
                                            if (run.text() != null && run.text().matches(pattern)) {
                                                String param = run.text()
                                                        .substring(run.text().indexOf("["),
                                                                run.text().indexOf("]") + 1);
                                                String value = reportParamsService
                                                        .getValue(param, "-1", "-1",
                                                                Optional.empty(), Optional.empty(),
                                                                author, dateWork);
                                                if (value != null) {
                                                    int index = run.text().indexOf("[");
                                                    if (index > 0) {
                                                        value = run.text().substring(0, index - 1)
                                                                .concat(value);
                                                    }

                                                    run.setText(value, 0);
                                                }

                                            }

                                        });
                                    }
                                }
                            });
                        }
                    });

                }
            }
            XWPFStyles styles = fileReport.createStyles();

            CTFonts fonts = CTFonts.Factory.newInstance();
            fonts.setEastAsia("Times New Roman");
            fonts.setHAnsi("Times New Roman");
            styles.setDefaultFonts(fonts);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            fileReport.write(baos);
            resultFile = baos.toByteArray();
            fileReport.close();
        } catch (IOException e) {
            logger.error("ERROR: Not found report file template in path: {}\n{}", commonPath, e);
            throw new NotFoundEntityException("Не найден файл для отчета за день");
        }

        Long end = System.currentTimeMillis();
        logger.info("PROCESSING TIME: {}", ((end - start) / 1000) / 60);
        logger.info("CALL getReportOfWorkDay END - {}", LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss", Locale.getDefault())));
        return resultFile;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getReportTemplate(String clientId, String doctorId, String reportId,
            String visitId, LocalDate dateEvent) {
        logger.info("CALL getReportClientCard START - {}", LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss", Locale.getDefault())));
        Report report = reportRepository.findOne(reportId);
        byte[] resultFile = null;
        if (report != null && report.getTemplate() != null) {

            Long start = System.currentTimeMillis();
            Path commonPath = Paths.get("//Users/theal-f/IdeaProjects/reportstest/");
            if (environment.getProperty("REPORT_PATH") != null) {
                commonPath = Paths.get(environment.getProperty("REPORT_PATH"));
            }
            logger.info("ENV: REPORT_PATH={}", commonPath);

            try {
                XWPFDocument fileReport = new XWPFDocument(
                        getFileReportTemplate(commonPath, reportId));

                List<IBodyElement> bodyElements = fileReport.getBodyElements();
                String pattern = "^.*\\[\\w+\\].*$";
                int countTable = 0;
                List<Company> companyList = companyRepository.findAll();
                Company company = null;
                int rowSecondTable = 46;
                if (companyList != null && companyList.size() > 0) {
                    company = companyList.get(0);
                }
                for (int ix = 0; ix < bodyElements.size(); ix++) {
                    IBodyElement element = bodyElements.get(ix);
                    if (element instanceof XWPFTable && countTable == 0) {
                        //Fill up header info
                        XWPFRun paragraphOrgExtraName = ((XWPFTable) element).getRows().get(0)
                                .getCell(0).getParagraphs().get(0).createRun();
                        paragraphOrgExtraName.setFontSize(12);
                        paragraphOrgExtraName.setBold(true);
                        paragraphOrgExtraName.setSmallCaps(true);
                        paragraphOrgExtraName.setText(reportParamsService
                                .getValue("[orgExtraName]", clientId, doctorId, Optional.empty(),
                                        Optional.ofNullable(company), "-1", dateEvent));

                        XWPFRun paragraphOrgAddress = ((XWPFTable) element).getRows().get(1)
                                .getCell(1).getParagraphs().get(0).createRun();
                        paragraphOrgAddress.setFontSize(8);
                        paragraphOrgAddress.setSmallCaps(true);
                        paragraphOrgAddress.setText(reportParamsService
                                .getValue("[orgAddress]", clientId, doctorId, Optional.empty(),
                                        Optional.ofNullable(company), "-1", dateEvent));

                        XWPFRun paragraphOrgPhone = ((XWPFTable) element).getRows().get(1)
                                .getCell(2).getParagraphs().get(0).createRun();
                        paragraphOrgPhone.setFontSize(10);
                        paragraphOrgPhone.setBold(true);
                        paragraphOrgPhone.setSmallCaps(true);
                        paragraphOrgPhone.setText(reportParamsService
                                .getValue("[orgPhone]", clientId, doctorId, Optional.empty(),
                                        Optional.ofNullable(company), "-1", dateEvent));

                        XWPFRun paragraphOrgLicence = ((XWPFTable) element).getRows().get(2)
                                .getCell(2).getParagraphs().get(0).createRun();
                        paragraphOrgLicence.setFontSize(8);
                        paragraphOrgLicence.setSmallCaps(true);
                        paragraphOrgLicence.setText(reportParamsService
                                .getValue("[orgLicence]", clientId, doctorId, Optional.empty(),
                                        Optional.ofNullable(company), "-1", dateEvent));

                        countTable++;
                    } else if (element instanceof XWPFTable && countTable == 1) {
                        //Fill up client info
                        ((XWPFTable) element).getRows().get(0).getCell(1).setText(
                                reportParamsService
                                        .getValue("[patFio]", clientId, doctorId, Optional.empty(),
                                                Optional.ofNullable(company), "-1", dateEvent));
                        ((XWPFTable) element).getRows().get(1).getCell(1).setText(
                                reportParamsService
                                        .getValue("[patOld]", clientId, doctorId, Optional.empty(),
                                                Optional.ofNullable(company), "-1", dateEvent));
                        ((XWPFTable) element).getRows().get(2).getCell(1).setText(
                                reportParamsService
                                        .getValue("[date]", clientId, doctorId, Optional.empty(),
                                                Optional.ofNullable(company), "-1", dateEvent));
                        countTable++;

                    } else if (element instanceof XWPFTable) {
                        countTable++;
                    }
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                fileReport.write(baos);
                resultFile = baos.toByteArray();
                fileReport.close();
            } catch (IOException e) {
                logger.error("ERROR: Not found report file template in path: {}\n{}", commonPath,
                        e);
                throw new NotFoundEntityException("Не найден файл для отчета за день");
            }

            Long end = System.currentTimeMillis();
            logger.info("PROCESSING TIME: {}", ((end - start) / 1000) / 60);
            logger.info("CALL getReportOfWorkDay END - {}", LocalDateTime.now()
                    .format(DateTimeFormatter
                            .ofPattern("dd.MM.yyyy HH:mm:ss", Locale.getDefault())));
        } else {
            throw new NotFoundEntityException("Не найден шаблон отчета");
        }
        return resultFile;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getReportContract(String clientId, String doctorId, String visitId,
            LocalDate dateEvent) {
        logger.info("CALL getReportContract START - {}", LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss", Locale.getDefault())));
        byte[] resultFile = null;
        Optional<Visit> visit = Optional.ofNullable(visitRepository.findOne(visitId));
        Long start = System.currentTimeMillis();
        Path commonPath = Paths.get("//Users/theal-f/IdeaProjects/reportstest/");
        if (environment.getProperty("REPORT_PATH") != null) {
            commonPath = Paths.get(environment.getProperty("REPORT_PATH"));
        }
        logger.info("ENV: REPORT_PATH={}", commonPath);

        try {
            ReportOfWorkDay dataReport = getDataForReportContract(visitId);
            POIFSFileSystem fileReport = null;
            if (dataReport.getRecords() != null && dataReport.getRecords().size() > 6) {
                fileReport = new POIFSFileSystem(Files.newInputStream(
                        Paths.get(commonPath.toString().concat("/Contract30.xls"))));
            } else {
                fileReport = new POIFSFileSystem(Files.newInputStream(
                        Paths.get(commonPath.toString().concat("/Contract.xls"))));
            }

            HSSFWorkbook wbook = new HSSFWorkbook(fileReport);
            HSSFSheet sheet = wbook.getSheetAt(0);
            HSSFRow row = null;
            List<Company> companyList = companyRepository.findAll();
            Company company = null;
            int rowSecondTable = 46;
            if (companyList != null && companyList.size() > 0) {
                company = companyList.get(0);
            }
            int rowsNums = sheet.getPhysicalNumberOfRows();
            BigInteger totalSum = new BigInteger("0");
            for (int i = 0; i < rowsNums; i++) {
                row = sheet.getRow(i);

                if ((i == 20 || i == rowSecondTable) && visit.isPresent()
                        && dataReport.getRecords().size() <= 6) {
                    if (visit.get().getServices() != null) {

                        totalSum = dataReport.getTotalSum();
                        List<ReportOfWorkDayRecord> records = dataReport.getRecords();
                        for (int y = 0; y < records.size(); y++) {
                            HSSFCell cellNum = row.getCell(1);
                            HSSFCell cellServiceLabel = row.getCell(2);
                            HSSFCell cellServiceAmount = row.getCell(15);
                            HSSFCell cellServicePrice = row.getCell(17);
                            HSSFCell cellServiceSum = row.getCell(19);
                            cellNum.setCellValue(y + 1);
                            cellServiceLabel.setCellValue(records.get(y).getLabel());
                            cellServiceAmount.setCellValue(records.get(y).getAmount());
                            cellServicePrice
                                    .setCellValue(String.valueOf(records.get(y).getPrice()));
                            cellServiceSum.setCellValue(String.valueOf(records.get(y).getSum()));

                            i++;
                            row = sheet.getRow(i);
                        }
                    }
                } else if (visit.isPresent() && dataReport.getRecords().size() > 6 && i == 23) {
                    if (visit.get().getServices() != null) {

                        totalSum = dataReport.getTotalSum();
                        List<ReportOfWorkDayRecord> records = dataReport.getRecords();
                        for (int y = 0; y < records.size(); y++) {
                            HSSFCell cellNum = row.getCell(1);
                            HSSFCell cellServiceLabel = row.getCell(2);
                            HSSFCell cellServiceAmount = row.getCell(15);
                            HSSFCell cellServicePrice = row.getCell(17);
                            HSSFCell cellServiceSum = row.getCell(19);
                            cellNum.setCellValue(y + 1);
                            cellServiceLabel.setCellValue(records.get(y).getLabel());
                            cellServiceAmount.setCellValue(records.get(y).getAmount());
                            cellServicePrice
                                    .setCellValue(String.valueOf(records.get(y).getPrice()));
                            cellServiceSum.setCellValue(String.valueOf(records.get(y).getSum()));

                            i++;
                            row = sheet.getRow(i);
                        }
                        i = 55;
                    }
                } else {
                    if (row != null && row.getLastCellNum() > 0) {
                        for (int j = 0; j < row.getLastCellNum(); j++) {
                            HSSFCell tmpCeil = row.getCell(j);
                            CellStyle ownCellStyle = tmpCeil.getCellStyle();
                            if (tmpCeil != null) {
                                boolean existParam = true;
                                while (existParam) {
                                    if (tmpCeil.getStringCellValue() != null && tmpCeil
                                            .getStringCellValue().contains("[") && tmpCeil
                                            .getStringCellValue().contains("]")) {
                                        List<String> params = reportParamsService
                                                .getParams(tmpCeil.getStringCellValue());
                                        for (String el : params) {
                                            if (el.equals("[totalSum]")) {
                                                tmpCeil.setCellValue(String.valueOf(totalSum));
                                            } else {
                                                String tmp = tmpCeil.getStringCellValue()
                                                        .replace(el, reportParamsService
                                                                .getValue(el, clientId, doctorId,
                                                                        visit, Optional.ofNullable(
                                                                                company), "",
                                                                        dateEvent));
                                                tmpCeil.setCellValue(tmp);
                                            }
                                        }
                                        tmpCeil.setCellStyle(ownCellStyle);
                                        existParam = false;
                                    } else {
                                        existParam = false;
                                    }
                                }

                            }
                        }
                    }
                }

            }
            // wbook.write(Files.newOutputStream(Paths.get("//Users/theal-f/IdeaProjects/reportstest/res.xls")));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            wbook.write(baos);
            resultFile = baos.toByteArray();
            fileReport.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Long end = System.currentTimeMillis();
        logger.info("PROCESSING TIME: {}", ((end - start) / 1000) / 60);
        logger.info("CALL getReportContract END - {}", LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss", Locale.getDefault())));
        return resultFile;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getReportListOfAppointments(String doctorId, LocalDate dateEvent) {
        byte[] resultFile = null;
        XWPFDocument fileReport = new XWPFDocument();
        XWPFParagraph tmpParagraph = fileReport.createParagraph();
        tmpParagraph.setSpacingBetween(1.5);
        tmpParagraph.setSpacingLineRule(LineSpacingRule.AUTO);
        XWPFRun tmpRun = tmpParagraph.createRun();
        List<AppointmentDto> listAppointments = appointmentService
                .getReservedAppointmentsByDoctorDateEvent(doctorId, dateEvent);
        for (AppointmentDto el : listAppointments) {
            tmpRun.setText(
                    el.getTimeEvent().toString().concat(" - ").concat(el.getClient()).concat(" (")
                            .concat(el.getService()).concat(")"));
            tmpRun.addBreak();
            tmpRun.setFontSize(16);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            fileReport.write(baos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        XWPFStyles styles = fileReport.createStyles();

        CTFonts fonts = CTFonts.Factory.newInstance();
        fonts.setEastAsia("Times New Roman");
        fonts.setHAnsi("Times New Roman");

        styles.setDefaultFonts(fonts);
        resultFile = baos.toByteArray();

        return resultFile;
    }

    private HWPFDocument replaceText(HWPFDocument doc, String findText, String replaceText) {
        Range r = doc.getRange();
        for (int i = 0; i < r.numSections(); ++i) {
            Section s = r.getSection(i);
            for (int j = 0; j < s.numParagraphs(); j++) {
                Paragraph p = s.getParagraph(j);
                StringBuilder sb = new StringBuilder();
                for (int k = 0; k < p.numCharacterRuns(); k++) {
                    CharacterRun run = p.getCharacterRun(k);
                    sb.append(run.text());

                    System.out.println("---" + run.text());
                }
                for (int k = 1; k < p.numCharacterRuns(); k++) {
                    p.delete();
                }
                for (int l = 0; l < p.numCharacterRuns(); l++) {
                    CharacterRun run = p.getCharacterRun(l);
                    run.replaceText(sb.toString(), false);
                }


            }
        }
        return doc;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getReportClientCard(String clientId, String doctorId, LocalDate dateEvent) {
        logger.info("CALL getReportClientCard START - {}", LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss", Locale.getDefault())));
        byte[] resultFile = null;
        Long start = System.currentTimeMillis();
        Path commonPath = Paths.get("/Users/theal-f/IdeaProjects/reportstest/");
        if (environment.getProperty("REPORT_PATH") != null) {
            commonPath = Paths.get(environment.getProperty("REPORT_PATH"));
        }
        logger.info("ENV: REPORT_PATH={}", commonPath);
        try {
            POIFSFileSystem fileReport = new POIFSFileSystem(getFileReportClientCard(commonPath));
            HSSFWorkbook wbook = new HSSFWorkbook(fileReport);
            HSSFSheet sheet = wbook.getSheetAt(0);
            HSSFRow row = null;
            List<Company> companyList = companyRepository.findAll();
            Company company = null;
            if (companyList != null && companyList.size() > 0) {
                company = companyList.get(0);
            }
            int rowsNums = sheet.getPhysicalNumberOfRows();
            for (int i = 0; i < rowsNums; i++) {
                row = sheet.getRow(i);
                row.getFirstCellNum();
                row.getLastCellNum();
                for (int j = 0; j < row.getLastCellNum(); j++) {
                    HSSFCell tmpCeil = row.getCell(j);
                    if (tmpCeil != null) {
                        boolean existParam = true;
                        while (existParam) {
                            if (tmpCeil.getStringCellValue() != null && tmpCeil.getStringCellValue()
                                    .contains("[") && tmpCeil.getStringCellValue().contains("]")) {
                                List<String> params = reportParamsService
                                        .getParams(tmpCeil.getStringCellValue());
                                for (String el : params) {
                                    String tmp = tmpCeil.getStringCellValue().replace(el,
                                            reportParamsService.getValue(el, clientId, doctorId,
                                                    Optional.empty(), Optional.ofNullable(company),
                                                    "", dateEvent));
                                    tmpCeil.setCellValue(tmp);
                                }
                                existParam = false;
                            } else {
                                existParam = false;
                            }
                        }

                    }

                }

            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            wbook.write(baos);
            resultFile = baos.toByteArray();
        } catch (Exception e) {
            logger.error("{}", e);
        }

        Long end = System.currentTimeMillis();
        logger.info("PROCESSING TIME: {}", ((end - start) / 1000) / 60);
        logger.info("CALL getReportClientCard END - {}", LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss", Locale.getDefault())));
        return resultFile;

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ReportDto obj2dto(Report report) {
        return new ReportDto(report.getId(), report.getCreatedDate(), report.getModifyDate(),
                report.getAuthor(), report.getLabel(),
                report.getService() != null ? report.getService().getId() : null,
                report.getTemplate(), report.getServiceLabel());
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Report dto2obj(ReportDto reportDto) {
        Service service = null;
        if (reportDto != null && reportDto.getServiceId() != null) {
            service = serviceRepository.findOne(reportDto.getServiceId());
        }

        return new Report(reportDto.getId(), reportDto.getCreatedDate(), reportDto.getModifyDate(),
                reportDto.getAuthor(), reportDto.getLabel(), service, reportDto.getTemplate());

    }


    /**
     * Gets data for report contract.
     *
     * @param visitId the visit id
     * @return the data for report contract
     */
    public ReportOfWorkDay getDataForReportContract(String visitId) {
        ReportOfWorkDay result = new ReportOfWorkDay();
        Visit visit = visitRepository.findOne(visitId);

        List<ReportOfWorkDayRecord> records = new ArrayList<>();
        BigInteger sum = BigInteger.valueOf(0);
        List<Service> services = visit.getServices();
        if (services != null) {
            services.forEach(service -> {
                ReportOfWorkDayRecord tmpRecord = new ReportOfWorkDayRecord(service.getId(),
                        service.getLabel(), service.getPrice());
                //Calculation new price with discount
                if (service.getDiscount() != null
                        && service.getDiscount().compareTo(BigInteger.valueOf(0)) > 0) {
                    BigInteger priceWithDiscount = tmpRecord.getPrice();
                    priceWithDiscount = BigInteger.valueOf(100).subtract(service.getDiscount())
                            .multiply(priceWithDiscount).abs().divide(BigInteger.valueOf(100));
                    tmpRecord.setPrice(priceWithDiscount);
                    tmpRecord.setLabel(
                            tmpRecord.getLabel() + " (со скидкой " + service.getDiscount() + "%)");
                }
                if (records.contains(tmpRecord) && !serviceService
                        .hasPersonalRate(service.getId(), visit.getDoctor().getId())) {
                    ReportOfWorkDayRecord duplicate = records.stream()
                            .filter(i -> i.equals(tmpRecord)).collect(Collectors.toList()).get(0);
                    duplicate.setAmount(duplicate.getAmount() + 1);
                } else {
                    if (serviceService
                            .hasPersonalRate(service.getId(), visit.getDoctor().getId())) {
                        tmpRecord.setPrice(serviceService.getPriceFromPersonalRate(service.getId(),
                                visit.getDoctor().getId()));
                    }
                    if (records.contains(tmpRecord)) {
                        ReportOfWorkDayRecord duplicate = records.stream()
                                .filter(i -> i.equals(tmpRecord)).collect(Collectors.toList())
                                .get(0);
                        duplicate.setAmount(duplicate.getAmount() + 1);
                    } else {
                        records.add(tmpRecord);
                    }
                }
            });
        }
        sum = records.stream().reduce(BigInteger.valueOf(0),
                (res, p) -> {
                    return res = res.add(BigInteger.valueOf(p.getAmount()).multiply(p.getPrice()));
                },
                (sum1, sum2) -> {
                    return sum1.add(sum2);
                });

        result.setRecords(records);
        result.setTotalSum(sum);
        return result;
    }

    /**
     * Gets data for report of work day.
     *
     * @param dateWork the date work
     * @return the data for report of work day
     */
    public ReportOfWorkDay getDataForReportOfWorkDay(LocalDate dateWork) {
        ReportOfWorkDay result = new ReportOfWorkDay();
        List<Visit> visits = visitRepository.findByDateEvent(dateWork);
        final BigInteger[] contractorSum = {BigInteger.valueOf(0)};
        List<ReportOfWorkDayRecord> records = new ArrayList<>();
        BigInteger totalSum = BigInteger.valueOf(0);
        final BigInteger[] terminalSum = {BigInteger.valueOf(0)};
        visits.forEach(visit -> {
            List<Service> services = visit.getServices();
            if (visit.getTerminalSum() != null) {
                terminalSum[0] = terminalSum[0].add(visit.getTerminalSum());
            }
            if (services != null) {
                services.forEach(service -> {
                    String serviceId = service.getId().split("MEDREG")[0];

                    ReportOfWorkDayRecord tmpRecord = new ReportOfWorkDayRecord(service.getId(),
                            service.getLabel(), service.getPrice());
                    //Processing contractors personal rates
                    Optional<List<Doctor>> contractors = Optional
                            .ofNullable(doctorRepository.findAllContractors());
                    contractors.ifPresent(listDoctor -> {
                        if (listDoctor != null) {
                            for (Doctor doctor : listDoctor) {
                                String doctorId = doctor.getId();
                                BigInteger contractorRate = serviceService
                                        .getPersonalRateByServiceIdAndDoctorId(serviceId, doctorId)
                                        != null ? serviceService
                                        .getPersonalRateByServiceIdAndDoctorId(serviceId, doctorId)
                                        .getDoctorPay() : BigInteger.valueOf(0);
                                if (contractorRate != null) {
                                    contractorSum[0] = contractorSum[0].add(contractorRate);
                                }
                            }
                        }

                    });
                    //Calculation new price with discount
                    if (service.getDiscount() != null
                            && service.getDiscount().compareTo(BigInteger.valueOf(0)) > 0) {
                        BigInteger priceWithDiscount = tmpRecord.getPrice();
                        priceWithDiscount = BigInteger.valueOf(100).subtract(service.getDiscount())
                                .multiply(priceWithDiscount).abs().divide(BigInteger.valueOf(100));
                        tmpRecord.setPrice(priceWithDiscount);
                        tmpRecord.setLabel(
                                tmpRecord.getLabel() + " (со скидкой " + service.getDiscount()
                                        + "%)");
                    }
                    if (records.contains(tmpRecord) && !serviceService
                            .hasPersonalRate(service.getId(), visit.getDoctor().getId())) {
                        ReportOfWorkDayRecord duplicate = records.stream()
                                .filter(i -> i.equals(tmpRecord)).collect(Collectors.toList())
                                .get(0);
                        duplicate.setAmount(duplicate.getAmount() + 1);
                    } else {
                        if (serviceService.hasPersonalRate(serviceId, visit.getDoctor().getId())) {
                            tmpRecord.setPrice(serviceService.getPriceFromPersonalRate(serviceId,
                                    visit.getDoctor().getId()));
                        }
                        if (records.contains(tmpRecord)) {
                            ReportOfWorkDayRecord duplicate = records.stream()
                                    .filter(i -> i.equals(tmpRecord)).collect(Collectors.toList())
                                    .get(0);
                            duplicate.setAmount(duplicate.getAmount() + 1);
                        } else {
                            records.add(tmpRecord);
                        }
                    }
                });
            }
        });

        totalSum = records.stream().reduce(BigInteger.valueOf(0),
                (res, p) -> {
                    return res = res.add(BigInteger.valueOf(p.getAmount()).multiply(p.getPrice()));
                },
                (sum1, sum2) -> {
                    return sum1.add(sum2);
                });

        result.setRecords(records);

        result.setTerminalSum(terminalSum[0]);
        result.setMazkiSum(contractorSum[0]);
        result.setSalarySum(getDataForReportOfWorkDayByDoctor(dateWork).getTotalSum());
        result.setTotalSum(totalSum);
        result.setRemainder(result.getTotalSum().subtract(result.getTerminalSum())
                .subtract(result.getSalarySum()).subtract(result.getMazkiSum()));
        return result;
    }


    /**
     * Gets data for report of work day by doctor.
     *
     * @param dateWork the date work
     * @return the data for report of work day by doctor
     */
    public RootReportByDoctor getDataForReportOfWorkDayByDoctor(LocalDate dateWork) {
        RootReportByDoctor rootReportByDoctor = new RootReportByDoctor();
        List<ReportOfWorkDayByDoctor> result = new ArrayList<>();
        List<Visit> visits = visitRepository.findByDateEvent(dateWork);
        Set<String> hasPcrByDoctorSet = new HashSet<>();
        visits.forEach(visit -> {
            hasPcrByDoctorSet.clear();
            List<Service> services = visit.getServices();
            if (services != null) {
                services.forEach(service -> {
                    ReportOfWorkDayByDoctor reportOfWorkDayByDoctor = new ReportOfWorkDayByDoctor();
                    reportOfWorkDayByDoctor.setDoctorId(visit.getDoctor().getId());
                    reportOfWorkDayByDoctor.setDoctorFio(visit.getDoctor().getValue());
                    String serviceId = service.getId().split("MEDREG")[0];
                    ReportOfWorkDay reportOfWorkDay = new ReportOfWorkDay();
                    if (visit.getDoctor().getExcludeFromReport() == null || (
                            visit.getDoctor().getExcludeFromReport() != null
                                    && visit.getDoctor().getExcludeFromReport() < 1)) {
                        if (visit.getDoctor() != null && serviceId != null &&
                                serviceService.hasPersonalRate(serviceId, visit.getDoctor().getId())
                                && !service.getCategory().equals(CategoryOfService.MAZOK)) {
                            PersonalRate personalRate = serviceService
                                    .getPersonalRateByServiceIdAndDoctorId(serviceId,
                                            visit.getDoctor().getId());
                            if (personalRate.getDoctorPay() != null
                                    && personalRate.getDoctorPayType() != null) {
                                if (result.contains(reportOfWorkDayByDoctor)) {
                                    ReportOfWorkDayByDoctor finalReportOfWorkDayByDoctor = reportOfWorkDayByDoctor;
                                    reportOfWorkDayByDoctor = result.stream()
                                            .filter(i -> i.equals(finalReportOfWorkDayByDoctor))
                                            .collect(Collectors.toList()).get(0);
                                    ReportOfWorkDay tmpReportOfWorkDay = reportOfWorkDayByDoctor
                                            .getReportOfWorkDay();
                                    if (tmpReportOfWorkDay != null
                                            && tmpReportOfWorkDay.getRecords() != null) {
                                        List<ReportOfWorkDayRecord> list = tmpReportOfWorkDay
                                                .getRecords();
                                        BigInteger priceFromPersonalRate = serviceService
                                                .getPriceFromPersonalRate(serviceId,
                                                        visit.getDoctor().getId());
                                        if (service.getCategory().equals(CategoryOfService.PCR)
                                                && !hasPcrByDoctorSet
                                                .add(visit.getDoctor().getId())) {
                                            priceFromPersonalRate = service.getPrice();
                                            personalRate = serviceService
                                                    .getPersonalRateByServiceIdAndDoctorId(
                                                            serviceId, visit.getDoctor().getId());
                                        } else if (service.getCategory()
                                                .equals(CategoryOfService.PCR)) {
                                            personalRate = new PersonalRate();
                                            personalRate.setDoctorPay(BigInteger.valueOf(0));
                                        }
                                        ReportOfWorkDayRecord tmpRecord = new ReportOfWorkDayRecord(
                                                serviceId,
                                                service.getLabel(), priceFromPersonalRate,
                                                personalRate != null ? personalRate.getDoctorPay()
                                                        : null, personalRate != null ? personalRate
                                                .getDoctorPayType() : null);

                                        if (list.contains(tmpRecord)) {
                                            ReportOfWorkDayRecord duplicate = list.stream()
                                                    .filter(i -> i.equals(tmpRecord))
                                                    .collect(Collectors.toList()).get(0);
                                            duplicate.setAmount(duplicate.getAmount() + 1);
                                        } else {
                                            list.add(tmpRecord);
                                        }
                                    }
                                } else {
                                    List<ReportOfWorkDayRecord> list = new ArrayList<>();
                                    BigInteger priceFromPersonalRate = serviceService
                                            .getPriceFromPersonalRate(serviceId,
                                                    visit.getDoctor().getId());
                                    if (service.getCategory().equals(CategoryOfService.PCR)
                                            && hasPcrByDoctorSet.add(visit.getDoctor().getId())) {
                                        priceFromPersonalRate = serviceService
                                                .getPriceFromPersonalRate(serviceId,
                                                        doctorRepository
                                                                .findBySurnameStrong("лаборатория")
                                                                != null && doctorRepository
                                                                .findBySurnameStrong("лаборатория")
                                                                .size() > 0 ? doctorRepository
                                                                .findBySurnameStrong("лаборатория")
                                                                .get(0).getId() : "-1");
                                        personalRate = serviceService
                                                .getPersonalRateByServiceIdAndDoctorId(serviceId,
                                                        doctorRepository
                                                                .findBySurnameStrong("лаборатория")
                                                                != null && doctorRepository
                                                                .findBySurnameStrong("лаборатория")
                                                                .size() > 0 ? doctorRepository
                                                                .findBySurnameStrong("лаборатория")
                                                                .get(0).getId() : "-1");
                                    } else if (service.getCategory()
                                            .equals(CategoryOfService.PCR)) {
                                        personalRate = new PersonalRate();
                                        personalRate.setDoctorPay(BigInteger.valueOf(0));
                                    }
                                    ReportOfWorkDayRecord reportOfWorkDayRecord = new ReportOfWorkDayRecord(
                                            serviceId, service.getLabel(), priceFromPersonalRate,
                                            personalRate != null ? personalRate.getDoctorPay()
                                                    : null,
                                            personalRate != null ? personalRate.getDoctorPayType()
                                                    : null);
                                    list.add(reportOfWorkDayRecord);
                                    reportOfWorkDay.setRecords(list);
                                    reportOfWorkDayByDoctor.setReportOfWorkDay(reportOfWorkDay);
                                    result.add(reportOfWorkDayByDoctor);
                                }
                            }
                        } else if (!service.getCategory().equals(CategoryOfService.MAZOK)) {
                            BigInteger doctorPay = service.getDoctorPay();
                            String doctorPayType = service.getDoctorPayType();
                            if (service.getCategory().equals(CategoryOfService.PCR)
                                    && !hasPcrByDoctorSet.add(visit.getDoctor().getId())) {
                                doctorPay = BigInteger.valueOf(0);
                                service.setPrice(BigInteger.valueOf(0));
                            }

                            if (doctorPay != null && doctorPayType != null) {
                                if (result.contains(reportOfWorkDayByDoctor)) {
                                    ReportOfWorkDayByDoctor finalReportOfWorkDayByDoctor = reportOfWorkDayByDoctor;
                                    reportOfWorkDayByDoctor = result.stream()
                                            .filter(i -> i.equals(finalReportOfWorkDayByDoctor))
                                            .collect(Collectors.toList()).get(0);

                                    ReportOfWorkDay tmpReportOfWorkDay = reportOfWorkDayByDoctor
                                            .getReportOfWorkDay();
                                    if (tmpReportOfWorkDay != null
                                            && tmpReportOfWorkDay.getRecords() != null) {
                                        List<ReportOfWorkDayRecord> list = tmpReportOfWorkDay
                                                .getRecords();
                                        ReportOfWorkDayRecord tmpRecord = new ReportOfWorkDayRecord(
                                                serviceId,
                                                service.getLabel(), service.getPrice(), doctorPay,
                                                doctorPayType);
                                        if (list.contains(tmpRecord)) {
                                            ReportOfWorkDayRecord duplicate = list.stream()
                                                    .filter(i -> i.equals(tmpRecord))
                                                    .collect(Collectors.toList()).get(0);
                                            duplicate.setAmount(duplicate.getAmount() + 1);
                                        } else {
                                            list.add(tmpRecord);
                                        }

                                    }
                                } else {
                                    List<ReportOfWorkDayRecord> list = new ArrayList<>();
                                    ReportOfWorkDayRecord reportOfWorkDayRecord = new ReportOfWorkDayRecord(
                                            serviceId, service.getLabel(), service.getPrice(),
                                            doctorPay, doctorPayType);

                                    list.add(reportOfWorkDayRecord);
                                    reportOfWorkDay.setRecords(list);
                                    reportOfWorkDayByDoctor.setReportOfWorkDay(reportOfWorkDay);
                                    result.add(reportOfWorkDayByDoctor);
                                }
                            }
                        }
                    }
                });
            }
        });

        result.forEach(item -> {
            ReportOfWorkDay reportOfWorkDay = item.getReportOfWorkDay();
            BigInteger sumOfDayByDoctor = reportOfWorkDay.getRecords().stream()
                    .reduce(BigInteger.valueOf(0),
                            (res, p) -> {
                                return res = res.add(p.getSalary()
                                        .multiply(BigInteger.valueOf(p.getAmount())));
                            },
                            (sum1, sum2) -> {
                                return sum1.add(sum2);
                            });
            reportOfWorkDay.setTotalSum(sumOfDayByDoctor);
            item.setTotalSum(sumOfDayByDoctor);
        });
        BigInteger totalSum = result.stream().reduce(BigInteger.valueOf(0),
                (res, p) -> {
                    return res = res.add(p.getReportOfWorkDay().getTotalSum());
                },
                (sum1, sum2) -> {
                    return sum1.add(sum2);
                });

        rootReportByDoctor.setRecords(result);
        rootReportByDoctor.setTotalSum(totalSum);

        return rootReportByDoctor;
    }
}