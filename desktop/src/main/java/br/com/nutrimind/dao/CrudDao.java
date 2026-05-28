package br.com.nutrimind.dao;

import java.util.List;
import java.util.Optional;

public interface CrudDao<T> {
    T save(T entity);

    Optional<T> findById(long id);

    List<T> findAll();

    void delete(long id);
}

