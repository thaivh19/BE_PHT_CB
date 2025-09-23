package com.pht.service;

import java.util.List;

import com.pht.repository.BaseRepository;

public interface BaseService<T, ID> {
    BaseRepository<T, ID> getRepository();

    <S extends BaseRepository<T, ID>> S getRepository(Class<S> aClass);

    <S extends T> S save(S entity);

    <S extends T> List<S> saveAll(Iterable<S> entities);

    T findById(ID id);

    List<T> findAll();

    List<T> findAllById(Iterable<ID> ids);

    boolean existsById(ID id);

    void deleteById(ID id);
}
