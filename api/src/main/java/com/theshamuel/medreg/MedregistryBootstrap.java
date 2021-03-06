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
package com.theshamuel.medreg;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.theshamuel.medreg.filter.JwtFilter;
import com.theshamuel.medreg.utils.BusinessGarbageCollectorDaemon;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * The Medregistry bootstrap class.
 *
 * @author Alex Gladkikh
 */
@SpringBootApplication
public class MedregistryBootstrap {

    private static Logger logger = LoggerFactory.getLogger(MedregistryBootstrap.class);

    private final Environment env;

    public MedregistryBootstrap(Environment env) {
        this.env = env;
    }

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     * @throws IOException the io exception
     */
    public static void main(String[] args) throws IOException {
        logger.info("Medregistry is starting...{}", LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss", Locale.getDefault())));
        SpringApplication app = new SpringApplication(MedregistryBootstrap.class, args);

        SpringApplication.run(MedregistryBootstrap.class, args);
        BusinessGarbageCollectorDaemon.start();
    }

    /**
     * Register JWT filter bean.
     *
     * @return the filter registration bean
     */
    @Bean
    public FilterRegistrationBean jwtFilter() {
        final FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(new JwtFilter());
        registrationBean.addUrlPatterns("/api/*");

        return registrationBean;
    }

    /**
     * Jackson builder jackson object mapper builder.
     *
     * @return the jackson object mapper builder
     */
    @Bean
    public Jackson2ObjectMapperBuilder jacksonBuilder() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.indentOutput(true).dateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        builder.serializationInclusion(JsonInclude.Include.NON_NULL);
        return builder;
    }

    /**
     * Add Java time module module to Jackson.
     *
     * @return the module
     */
    @Bean
    public Module javaTimeModule() {
        return new JavaTimeModule();
    }

    /**
     * Add java 8 module module to Jackson.
     *
     * @return the module
     */
    @Bean
    public Module java8Module() {
        return new Jdk8Module();
    }

    /**
     * Add parameter names module to Jackson.
     *
     * @return the module
     */
    @Bean
    public Module parameterNamesModule() {
        return new ParameterNamesModule(JsonCreator.Mode.PROPERTIES);
    }

}
