package main.java.com.podruzhinskii.dao;

import java.sql.SQLException;
import java.util.List;

public interface Crud<T> {

    T get(Long id) throws SQLException;

    List<T> getAll() throws SQLException;

    Boolean create(T object) throws SQLException;

    Boolean update(T object) throws SQLException;

    Boolean delete(Long id) throws SQLException;
}
