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
package com.theshamuel.medreg.model.baseclasses.service;

import com.theshamuel.medreg.model.baseclasses.entity.BaseEntity;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

/**
 * The interface Base service class.
 *
 * @param <T> the type parameter define data transaction object (dto) class
 * @param <E> the type parameter define entity object class
 * @author Alex Gladkikh
 */
public interface BaseService<T extends BaseEntity, E extends BaseEntity> {


    /**
     * Find all page.
     *
     * @param pageRequest the page request
     * @return the page
     */
    Page<T> findAll(PageRequest pageRequest);

    /**
     * Find page with filter.
     *
     * @param pageRequest the page request
     * @return the page
     */
    Page<T> findByFilter(PageRequest pageRequest, String filter);

    /**
     * Find all list.
     *
     * @param sort the kind of sort
     * @return the list of entities
     */
    List<T> findAll(Sort sort);


    /**
     * Get count of collection.
     *
     * @return the size of collection
     */
    long count();

    /**
     * Save dto.
     *
     * @param dto the dto
     * @return the saved dto
     */
    T save(T dto);

    /**
     * Find dto by id.
     *
     * @param id the dto id
     * @return the dto
     */
    T findOne(String id);

    /**
     * Delete dto.
     *
     * @param id the dto id
     */
    void delete(String id);

    /**
     * Convert entity to dto.
     *
     * @param obj the entity object
     * @return the dto
     */
    T obj2dto(E obj);

    /**
     * Convert dto to entity.
     *
     * @param dto the dto
     * @return the entity
     */
    E dto2obj(T dto);
}
