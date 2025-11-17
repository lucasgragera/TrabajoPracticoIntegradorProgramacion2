package dao;

import entities.SeguroVehicular;
import entities.Vehiculo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class VehiculoDao implements GenericDao<Vehiculo> {

    private static final String INSERT_SQL = "INSERT INTO Vehiculo (dominio, marca, modelo, anio, nroChasis, seguro_id, eliminado) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE Vehiculo SET dominio = ?, marca = ?, modelo = ?, anio = ?, nroChasis = ?, seguro_id = ? WHERE id = ?";

    // SQL con JOIN para traer el Vehiculo y su Seguro
    private static final String SELECT_BY_ID_SQL
            = "SELECT v.*, s.id AS s_id, s.eliminado AS s_eliminado, s.aseguradora, s.nroPoliza, s.cobertura, s.vencimiento "
            + "FROM Vehiculo v "
            + "LEFT JOIN SeguroVehicular s ON v.seguro_id = s.id "
            + "WHERE v.id = ? AND v.eliminado = false";

    private static final String SELECT_ALL_SQL
            = "SELECT v.*, s.id AS s_id, s.eliminado AS s_eliminado, s.aseguradora, s.nroPoliza, s.cobertura, s.vencimiento "
            + "FROM Vehiculo v "
            + "LEFT JOIN SeguroVehicular s ON v.seguro_id = s.id "
            + "WHERE v.eliminado = false";

    private static final String SOFT_DELETE_SQL = "UPDATE Vehiculo SET eliminado = true WHERE id = ?";

    // Query de validación para la regla 1-a-1
    private static final String CHECK_SEGURO_USADO_SQL = "SELECT COUNT(*) FROM Vehiculo WHERE seguro_id = ? AND eliminado = false";
    private static final String CHECK_DOMINIO_SQL = "SELECT COUNT(*) FROM Vehiculo WHERE dominio = ? AND eliminado = false";

    @Override
    public Vehiculo crear(Vehiculo vehiculo, Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, vehiculo.getDominio());
            ps.setString(2, vehiculo.getMarca());
            ps.setString(3, vehiculo.getModelo());
            ps.setInt(4, vehiculo.getAnio());
            ps.setString(5, vehiculo.getNroChasis());

            if (vehiculo.getSeguro() != null && vehiculo.getSeguro().getId() != null) {
                ps.setLong(6, vehiculo.getSeguro().getId());
            } else {
                ps.setNull(6, Types.BIGINT);
            }

            ps.setBoolean(7, false);

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    vehiculo.setId(rs.getLong(1));
                }
            }
            return vehiculo;
        }
    }

    @Override
    public Vehiculo leer(long id, Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID_SQL)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToVehiculo(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Vehiculo> leerTodos(Connection conn) throws SQLException {
        List<Vehiculo> vehiculos = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(SELECT_ALL_SQL); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                vehiculos.add(mapResultSetToVehiculo(rs));
            }
        }
        return vehiculos;
    }

    @Override
    public Vehiculo actualizar(Vehiculo vehiculo, Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(UPDATE_SQL)) {
            ps.setString(1, vehiculo.getDominio());
            ps.setString(2, vehiculo.getMarca());
            ps.setString(3, vehiculo.getModelo());
            ps.setInt(4, vehiculo.getAnio());
            ps.setString(5, vehiculo.getNroChasis());

            if (vehiculo.getSeguro() != null && vehiculo.getSeguro().getId() != null) {
                ps.setLong(6, vehiculo.getSeguro().getId());
            } else {
                ps.setNull(6, Types.BIGINT);
            }

            ps.setLong(7, vehiculo.getId());
            ps.executeUpdate();
            return vehiculo;
        }
    }

    @Override
    public void eliminar(long id, Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SOFT_DELETE_SQL)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    // Métodos de validación extra pedidos por el Service
    public boolean existeSeguroAsignado(long seguroId, Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(CHECK_SEGURO_USADO_SQL)) {
            ps.setLong(1, seguroId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public boolean existeDominio(String dominio, Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(CHECK_DOMINIO_SQL)) {
            ps.setString(1, dominio);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    // Metodo de Mapeo
    private Vehiculo mapResultSetToVehiculo(ResultSet rs) throws SQLException {
        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setId(rs.getLong("id"));
        vehiculo.setEliminado(rs.getBoolean("eliminado"));
        vehiculo.setDominio(rs.getString("dominio"));
        vehiculo.setMarca(rs.getString("marca"));
        vehiculo.setModelo(rs.getString("modelo"));
        vehiculo.setAnio(rs.getInt("anio"));
        vehiculo.setNroChasis(rs.getString("nroChasis"));

        // Verificar el objeto SeguroVehicular asociado
        long seguroId = rs.getLong("s_id");
        if (!rs.wasNull()) { // Verifica si el LEFT JOIN trajo un seguro
            SeguroVehicular seguro = new SeguroVehicular(
                    seguroId,
                    rs.getBoolean("s_eliminado"),
                    rs.getString("aseguradora"),
                    rs.getString("nroPoliza"),
                    entities.Cobertura.valueOf(rs.getString("cobertura")),
                    rs.getDate("vencimiento").toLocalDate()
            );
            vehiculo.setSeguro(seguro);
        }
        return vehiculo;
    }
}
