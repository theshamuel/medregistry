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
package com.theshamuel.medreg.exception;

import com.theshamuel.medreg.ResponseError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * The Medreg exception handler class.
 *
 * @author Alex Gladkikh
 */
@ControllerAdvice
public class MedRegExceptionHandler {

    private static Logger logger = LoggerFactory.getLogger(MedRegExceptionHandler.class);

    /**
     * Duplicate login response error.
     *
     * @param e the exception
     * @return the response error
     */
    @ExceptionHandler(DuplicateRecordException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public @ResponseBody
    ResponseError duplicateLogin(DuplicateRecordException e) {
        logger.error("MedRegistryError:", e);
        return new ResponseError(HttpStatus.CONFLICT.value(), e.getMessage());
    }

    /**
     * Not found entity response error.
     *
     * @param e the exception
     * @return the response error
     */
    @ExceptionHandler(NotFoundEntityException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public @ResponseBody
    ResponseError notFoundEntity(NotFoundEntityException e) {
        logger.error("MedRegistryError:", e);
        return new ResponseError(HttpStatus.NOT_FOUND.value(), e.getMessage());
    }

    /**
     * Not found entity response error.
     *
     * @param e the exception
     * @return the response error
     */
    @ExceptionHandler(NotCorrectData.class)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public @ResponseBody
    ResponseError notCorrectData(NotCorrectData e) {
        logger.error("MedRegistryError:", e);
        return new ResponseError(HttpStatus.NO_CONTENT.value(), e.getMessage());
    }

}