package com.pht.service.impl;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;

import com.pht.repository.BaseRepository;
import com.pht.service.BaseService;


public class BaseServiceImpl<T, ID> implements BaseService<T, ID> {

    @Autowired
    @Nullable
    protected BaseRepository<T, ID> repository;

    @Override
    @Nullable
    public BaseRepository<T, ID> getRepository() {
        return this.repository;
    }

    @Override
    public <S extends BaseRepository<T, ID>> S getRepository(Class<S> aClass) {
        return aClass.cast(this.repository);
    }

    @Override
    public <S extends T> S save(S entity) {
        return repository != null ? repository.save(entity) : null;
    }

    @Override
    public <S extends T> List<S> saveAll(Iterable<S> entities) {
        return repository != null ? repository.saveAll(entities) : null;
    }

    @Override
    public T findById(ID id) {
        return repository != null ? repository.findById(id).orElse(null) : null;
    }

    @Override
    public List<T> findAll() {
        return repository != null ? repository.findAll() : null;
    }

    @Override
    public List<T> findAllById(Iterable<ID> ids) {
        return repository != null ? repository.findAllById(ids) : null;
    }

    @Override
    public boolean existsById(ID id) {
        return Objects.requireNonNull(repository).existsById(id);
    }

    @Override
    public void deleteById(ID id) {
        if (repository != null) {
            repository.deleteById(id);
        }
    }
}
