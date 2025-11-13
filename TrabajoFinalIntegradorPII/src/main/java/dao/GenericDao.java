/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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