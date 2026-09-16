package repository;

import java.sql.SQLException;
import java.util.List;

public interface Repository<T> {

    int save(T item) throws SQLException;

    List<T> findAll() throws SQLException;

    void update(T item) throws SQLException;

    void delete(int id) throws SQLException;
}