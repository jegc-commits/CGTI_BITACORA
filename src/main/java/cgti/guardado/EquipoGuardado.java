
package cgti.guardado;

import cgti.clase.Equipo;
import cgti.clase.Laboratorio;
import cgti.conexion.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class EquipoGuardado {

    public void guardar(Equipo equipo) {
        String sql = "INSERT INTO dbo.equipos (nombre_eqp, laboratorio, software) VALUES (?, ?, ?)";
        try (PreparedStatement ps = DatabaseConfig.conectar()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, equipo.getNombreEqp());
            ps.setString(2, equipo.getLaboratorio().name());
            ps.setString(3, equipo.getSoftware());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    equipo.setId(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al guardar equipo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Optional<Equipo> buscarPorId(Long id) {
        String sql = "SELECT * FROM dbo.equipos WHERE id = ?";
        try (PreparedStatement ps = DatabaseConfig.conectar().prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapear(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al buscar equipo: " + e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public List<Equipo> listarPorLaboratorio(String laboratorio) {
        return listarTodos().stream()
                .filter(e -> e.getLaboratorio() == Laboratorio.valueOf(laboratorio))
                .collect(Collectors.toList());
    }

    public List<Equipo> listarTodos() {
        List<Equipo> lista = new ArrayList<>();
        String sql = "SELECT * FROM dbo.equipos";
        try (PreparedStatement ps = DatabaseConfig.conectar().prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al listar equipos: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    public void actualizar(Equipo equipo) {
        String sql = "UPDATE dbo.equipos SET nombre_eqp = ?, laboratorio = ?, software = ? WHERE id = ?";
        try (PreparedStatement ps = DatabaseConfig.conectar().prepareStatement(sql)) {
            ps.setString(1, equipo.getNombreEqp());
            ps.setString(2, equipo.getLaboratorio().name());
            ps.setString(3, equipo.getSoftware());
            ps.setLong(4, equipo.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar equipo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Equipo mapear(ResultSet rs) throws SQLException {
        Equipo e = new Equipo();
        e.setId(rs.getLong("id"));
        e.setNombreEqp(rs.getString("nombre_eqp"));
        e.setLaboratorio(Laboratorio.valueOf(rs.getString("laboratorio")));
        e.setSoftware(rs.getString("software"));
        return e;
    }
}
