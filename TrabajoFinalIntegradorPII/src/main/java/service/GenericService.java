/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import java.util.List;

public interface GenericService<T> {
    
    T insertar(T entity);

    T actualizar(T entity);

    void eliminar(long id);

    T getById(long id);

    List<T> getAll();
}