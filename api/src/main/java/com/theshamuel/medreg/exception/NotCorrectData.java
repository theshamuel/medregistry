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

/**
 * The type of runtime exception using when request not correct data.
 *
 * @author Alex Gladkikh
 */
public class NotCorrectData extends RuntimeException {

    /**
     * Instantiates a new Entity DuplicateRecordException.
     *
     * @param message the message of error.
     */
    public NotCorrectData(String message) {
        super(message);
    }
}