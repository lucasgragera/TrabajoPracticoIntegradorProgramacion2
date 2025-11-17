/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import config.DatabaseConnection;
import dao.SeguroVehicularDao;
import entities.SeguroVehicular;
import exception.ServiceException;
import exception.ValidationException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class SeguroVehicularService implements GenericService<SeguroVehicular> {
    
    private SeguroVehicularDao dao = new SeguroVehicularDao();

    @Override
    public SeguroVehicular insertar(SeguroVehicular seguro) {
        
        if (seguro.getNroPoliza() == null || seguro.getNroPoliza().trim().isEmpty()) {
            throw new ValidationException("El número de póliza es obligatorio");
        }
        
        Connection conn = null; 

        try {
            conn = DatabaseConnection.getConnection(); 
            conn.setAutoCommit(false);
            SeguroVehicular nuevoSeguro = dao.crear(seguro, conn);
            conn.commit(); 
            return nuevoSeguro;

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback(); // Deshace en caso de error
            } catch (SQLException ex) {
                throw new ServiceException("Error al hacer rollback", ex);
            }
            throw new ServiceException("Error al crear el seguro", e);
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true); // Restablece autoCommit
                    conn.close(); // Cierra conexión
                }
            } catch (SQLException e) {
                throw new ServiceException("Error al cerrar la conexión", e);
            }
        }
    }
       
    //Al solamente leer no lo generamos como una transaccion
    @Override
    public SeguroVehicular getById(long id) {
    
    try (Connection conn = DatabaseConnection.getConnection()) {
        return dao.leer(id, conn);
    } catch (SQLException e) {
        throw new ServiceException("Error al leer el seguro por ID", e);
    }
}
    
    //Al solamente leer no lo generamos como una transaccion
    @Override
    public List<SeguroVehicular> getAll() {
        
    try (Connection conn = DatabaseConnection.getConnection()) {
        return dao.leerTodos(conn);
    } catch (SQLException e) {
        throw new ServiceException("Error al leer todos los seguros", e);
    }
}
    
    @Override
    public SeguroVehicular actualizar(SeguroVehicular entity) {
        
        if (entity.getId() == null) {
            throw new ValidationException("No se puede actualizar un seguro con ID nulo.");
        }
        
        Connection conn = null; 

        try {
            conn = DatabaseConnection.getConnection(); 
            conn.setAutoCommit(false);
            SeguroVehicular actualizado = dao.actualizar(entity, conn);
            conn.commit(); 
            return actualizado;

        } catch (SQLException | ValidationException e) {
            try {
                if (conn != null) conn.rollback(); 
            } catch (SQLException ex) {
                throw new ServiceException("Error al hacer rollback en actualizar seguro", ex);
            }
            throw new ServiceException("Error al actualizar el seguro", e);
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                throw new ServiceException("Error al cerrar la conexión", e);
            }
        }
    }

    @Override
    public void eliminar(long id) {
        
        Connection conn = null; 

        try {
            conn = DatabaseConnection.getConnection(); 
            conn.setAutoCommit(false);

            // Verificar si existe antes de eliminar
            SeguroVehicular existente = dao.leer(id, conn);
            if (existente != null) {
                dao.eliminar(id, conn);
            }
            
            conn.commit(); 

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                throw new ServiceException("Error al hacer rollback en eliminar seguro", ex);
            }
            throw new ServiceException("Error al eliminar el seguro", e);
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                throw new ServiceException("Error al cerrar la conexión", e);
            }
        }
    }
}