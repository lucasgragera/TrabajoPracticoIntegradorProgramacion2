package dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface GenericDao<T> {

    T crear(T entity, Connection conn) throws SQLException;

    T leer(long id, Connection conn) throws SQLException;

    List<T> leerTodos(Connection conn) throws SQLException;

    T actualizar(T entity, Connection conn) throws SQLException;

    void eliminar(long id, Connection conn) throws SQLException;
}
