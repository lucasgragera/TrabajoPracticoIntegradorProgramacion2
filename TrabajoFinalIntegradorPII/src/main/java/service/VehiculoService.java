package service;

import config.DatabaseConnection;
import dao.SeguroVehicularDao;
import dao.VehiculoDao;
import entities.Vehiculo;
import exception.ServiceException;
import exception.ValidationException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class VehiculoService implements GenericService<Vehiculo> {

    private final VehiculoDao vehiculoDao = new VehiculoDao();
    private final SeguroVehicularDao seguroDao = new SeguroVehicularDao();

    @Override
    public Vehiculo insertar(Vehiculo vehiculo) {

        Connection conn = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            validarVehiculo(vehiculo, conn);

            if (vehiculo.getSeguro() != null && vehiculo.getSeguro().getId() == null) {
                seguroDao.crear(vehiculo.getSeguro(), conn);
            }

            Vehiculo nuevoVehiculo = vehiculoDao.crear(vehiculo, conn);
            conn.commit();
            return nuevoVehiculo;

        } catch (SQLException | ValidationException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                throw new ServiceException("Error fatal: Falla en rollback", ex);
            }
            throw new ServiceException("Error al insertar el vehículo: " + e.getMessage(), e);

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

    private void validarVehiculo(Vehiculo vehiculo, Connection conn) throws SQLException, ValidationException {
        if (vehiculo.getDominio() == null || vehiculo.getDominio().trim().isEmpty()) {
            throw new ValidationException("El dominio (patente) es obligatorio.");
        }

        // Validación de dominio 
        if (vehiculo.getDominio().length() < 6 || vehiculo.getDominio().length() > 7) {
            throw new ValidationException("Formato de dominio incorrecto.");
        }

        // Validación de duplicados
        if (vehiculoDao.existeDominio(vehiculo.getDominio(), conn)) {
            throw new ValidationException("Ya existe un vehículo con el dominio " + vehiculo.getDominio());
        }

        // Validación Regla 1-a-1: "impedir más de un B por A" (en este caso, 
        // "impedir que un B sea usado por más de un A")
        if (vehiculo.getSeguro() != null && vehiculo.getSeguro().getId() != null) {
            if (vehiculoDao.existeSeguroAsignado(vehiculo.getSeguro().getId(), conn)) {
                throw new ValidationException("El seguro con ID " + vehiculo.getSeguro().getId() + " ya está asignado a otro vehículo.");
            }
        }
    }

    //Al solamente leer no lo generamos como una transaccion
    @Override
    public Vehiculo getById(long id) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return vehiculoDao.leer(id, conn);
        } catch (SQLException e) {
            throw new ServiceException("Error al leer vehículo por ID", e);
        }
    }

    //Al solamente leer no lo generamos como una transaccion
    @Override
    public List<Vehiculo> getAll() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return vehiculoDao.leerTodos(conn);
        } catch (SQLException e) {
            throw new ServiceException("Error al leer todos los vehículos", e);
        }
    }

    @Override
    public Vehiculo actualizar(Vehiculo vehiculo) {
        if (vehiculo.getId() == null) {
            throw new ValidationException("No se puede actualizar un vehículo con ID nulo.");
        }
        Connection conn = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            Vehiculo vehiculoActual = vehiculoDao.leer(vehiculo.getId(), conn);

            if (vehiculoActual == null) {
                throw new ValidationException("No se encontró el vehículo con ID " + vehiculo.getId() + " para actualizar.");
            }

            // Validar Dominio (solo si el dominio cambió)
            if (!vehiculoActual.getDominio().equals(vehiculo.getDominio())) {
                if (vehiculoDao.existeDominio(vehiculo.getDominio(), conn)) {
                    throw new ValidationException("El nuevo dominio '" + vehiculo.getDominio() + "' ya está en uso por otro vehículo.");
                }
            }

            // Validar Seguro 1-a-1 (solo si el seguro cambió)
            Long idSeguroActual = (vehiculoActual.getSeguro() != null) ? vehiculoActual.getSeguro().getId() : null;
            Long idSeguroNuevo = (vehiculo.getSeguro() != null) ? vehiculo.getSeguro().getId() : null;

            // Si el ID del seguro nuevo es diferente al actual...
            if (idSeguroNuevo != null && !idSeguroNuevo.equals(idSeguroActual)) {
                if (vehiculoDao.existeSeguroAsignado(idSeguroNuevo, conn)) {
                    throw new ValidationException("El seguro con ID " + idSeguroNuevo + " ya está asignado a otro vehículo.");
                }
            }

            // Ejecutar la actualización en el DAO
            Vehiculo vehiculoActualizado = vehiculoDao.actualizar(vehiculo, conn);
            conn.commit();
            return vehiculoActualizado;

        } catch (SQLException | ValidationException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                throw new ServiceException("Error grave al hacer rollback en 'actualizar'", ex);
            }
            throw new ServiceException("Error al actualizar el vehículo: " + e.getMessage(), e);

        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                throw new ServiceException("Error al cerrar la conexión en 'actualizar'", e);
            }
        }
    }

    @Override
    public void eliminar(long id) {

        Connection conn = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            Vehiculo vehiculoExistente = vehiculoDao.leer(id, conn);

            if (vehiculoExistente == null) {
            } else {
                vehiculoDao.eliminar(id, conn);
            }
            conn.commit();

        } catch (SQLException | ValidationException e) {
            try {
                if (conn != null) {
                    conn.rollback(); // 4. Rollback
                }
            } catch (SQLException ex) {
                throw new ServiceException("Error grave al hacer rollback en 'eliminar'", ex);
            }
            throw new ServiceException("Error al eliminar el vehículo: " + e.getMessage(), e);

        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                throw new ServiceException("Error al cerrar la conexión en 'eliminar'", e);
            }
        }
    }
}
