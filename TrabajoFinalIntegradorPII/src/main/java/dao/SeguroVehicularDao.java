package dao;

import entities.Cobertura;
import entities.SeguroVehicular;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SeguroVehicularDao implements GenericDao<SeguroVehicular> {

    private static final String INSERT_SQL = "INSERT INTO SeguroVehicular (aseguradora, nroPoliza, cobertura, vencimiento, eliminado) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE SeguroVehicular SET aseguradora = ?, nroPoliza = ?, cobertura = ?, vencimiento = ? WHERE id = ?";
    private static final String SELECT_BY_ID_SQL = "SELECT * FROM SeguroVehicular WHERE id = ? AND eliminado = false";
    private static final String SELECT_ALL_SQL = "SELECT * FROM SeguroVehicular WHERE eliminado = false";
    private static final String SOFT_DELETE_SQL = "UPDATE SeguroVehicular SET eliminado = true WHERE id = ?";

    @Override
    public SeguroVehicular crear(SeguroVehicular seguro, Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, seguro.getAseguradora());
            ps.setString(2, seguro.getNroPoliza());
            ps.setString(3, seguro.getCobertura().name());
            ps.setDate(4, Date.valueOf(seguro.getVencimiento()));
            ps.setBoolean(5, false);

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        seguro.setId(rs.getLong(1));
                    }
                }
            }
            return seguro;
        }
    }

    @Override
    public SeguroVehicular leer(long id, Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID_SQL)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSeguro(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<SeguroVehicular> leerTodos(Connection conn) throws SQLException {
        List<SeguroVehicular> seguros = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(SELECT_ALL_SQL); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                seguros.add(mapResultSetToSeguro(rs));
            }
        }
        return seguros;
    }

    @Override
    public SeguroVehicular actualizar(SeguroVehicular seguro, Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(UPDATE_SQL)) {
            ps.setString(1, seguro.getAseguradora());
            ps.setString(2, seguro.getNroPoliza());
            ps.setString(3, seguro.getCobertura().name());
            ps.setDate(4, Date.valueOf(seguro.getVencimiento()));
            ps.setLong(5, seguro.getId());

            ps.executeUpdate();
            return seguro;
        }
    }

    @Override
    public void eliminar(long id, Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SOFT_DELETE_SQL)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    // Método para no repetir código
    private SeguroVehicular mapResultSetToSeguro(ResultSet rs) throws SQLException {
        return new SeguroVehicular(
                rs.getLong("id"),
                rs.getBoolean("eliminado"),
                rs.getString("aseguradora"),
                rs.getString("nroPoliza"),
                Cobertura.valueOf(rs.getString("cobertura")), // Convirtiendo String a Enum
                rs.getDate("vencimiento").toLocalDate()
        );
    }
}
