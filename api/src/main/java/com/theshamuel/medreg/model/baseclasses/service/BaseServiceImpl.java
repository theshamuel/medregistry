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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * The Base service implementation.
 *
 * @param <T> the type parameter define data transaction object (dto) class
 * @param <E> the type parameter define entity object class
 * @author Alex Gladkikh
 */
public abstract class BaseServiceImpl<T extends BaseEntity, E extends BaseEntity> implements
        BaseService<T, E> {


    /**
     * The Mongo repository of entity object class.
     */
    MongoRepository<E, String> mongoRepository;

    /**
     * Instantiates a new Base service.
     *
     * @param mongoRepository the mongo repository
     */
    public BaseServiceImpl(MongoRepository mongoRepository) {
        this.mongoRepository = mongoRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<T> findAll(PageRequest pageRequest) {
        Page<E> page = mongoRepository.findAll(pageRequest);

        return page.map(i -> obj2dto(i));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<T> findByFilter(PageRequest pageRequest, String filter) {
        Pattern pattern = Pattern.compile("^(.+=.+;+)+$");
        Matcher matcher = pattern.matcher(filter);
        if (matcher.matches()) {
            try {
                Method method = mongoRepository.getClass().getMethod("findByFilter", String.class);
                List<E> contentObj = (List<E>) method.invoke(mongoRepository, filter);
                List<T> contentDto = contentObj.stream().map(i -> obj2dto(i))
                        .collect(Collectors.toList());
                Page<T> result = new PageImpl<T>(contentDto, pageRequest, contentDto.size());
                return result;
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            throw new IllegalArgumentException("Фильтр не соответсвует шаблону");
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> findAll(Sort sort) {
        final List<T> result = new ArrayList<>();
        Optional<List<E>> listOfEntities = Optional.ofNullable(mongoRepository.findAll(sort));
        listOfEntities.ifPresent(item -> {
            item.forEach(e -> {
                result.add(obj2dto(e));
            });
        });
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long count() {
        return mongoRepository.count();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T save(T dto) {
        return obj2dto(mongoRepository.save(dto2obj(dto)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T findOne(String id) {
        E result = mongoRepository.findOne(id);
        if (result != null) {
            return obj2dto(result);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(String id) {
        mongoRepository.delete(id);
    }


}
