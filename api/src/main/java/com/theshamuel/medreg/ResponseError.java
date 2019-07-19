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
package com.theshamuel.medreg;

/**
 * The Response error class.
 *
 * @author Alex Gladkikh
 */
public class ResponseError  {

    public final int code;

    public final  String message;

    /**
     * Instantiates a new Response error.
     *
     * @param code    the code
     * @param message the message
     */

    public ResponseError( int code, String message) {
        this.code = code;
        this.message = message;
    }

}
