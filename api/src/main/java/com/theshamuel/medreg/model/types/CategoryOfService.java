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
package com.theshamuel.medreg.model.types;

/**
 * The type Category of service.
 *
 * @author Alex Gladkikh
 */
public final class CategoryOfService {

    /**
     * The constant ULTRASOUND.
     */
    public static final String ULTRA = "ultra";
    /**
     * The constant CONSULTATION.
     */
    public static final String CONSULTATION = "consult";

    /**
     * The constant ANALYZES.
     */
    public static final String ANALYZES = "analyzes";

    /**
     * The constant PCR.
     */
    public static final String PCR = "pcr";

    /**
     * The constant MAZOK.
     */
    public static final String MAZOK = "mazok";
}
