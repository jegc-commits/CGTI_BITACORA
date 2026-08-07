package cgti.guardado;

import cgti.clase.Equipo;
import cgti.conexion.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipoGuardado {

    // ==================== OPERACIONES CRUD ====================

    /**
     * Guarda un nuevo equipo en la base de datos
     */
    public void guardar(Equipo equipo) {
        String sql = "INSERT INTO dbo.equipos (nombre_eqp, nombreLab, nombreSft) VALUES (?, ?, ?)";

        try (Connection con = DatabaseConfig.conectar();
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, equipo.getNombreEqp());
            ps.setString(2, equipo.getLaboratorio());
            ps.setString(3, equipo.getSoftware() != null ? equipo.getSoftware() : "");

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    equipo.setId(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al guardar equipo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Actualiza un equipo existente
     */
    public void actualizar(Equipo equipo) {
        String sql = "UPDATE dbo.equipos SET nombre_eqp = ?, nombreLab = ?, nombreSft = ? WHERE id = ?";

        try (Connection con = DatabaseConfig.conectar();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, equipo.getNombreEqp());
            ps.setString(2, equipo.getLaboratorio());
            ps.setString(3, equipo.getSoftware() != null ? equipo.getSoftware() : "");
            ps.setLong(4, equipo.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al actualizar equipo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Elimina un equipo por su ID
     */
    public void eliminar(Long id) {
        String sql = "DELETE FROM dbo.equipos WHERE id = ?";

        try (Connection con = DatabaseConfig.conectar();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al eliminar equipo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ==================== CONSULTAS ====================

    /**
     * Lista todos los equipos
     */
    public List<Equipo> listarTodos() {
        List<Equipo> equipos = new ArrayList<>();
        String sql = "SELECT id, nombre_eqp, nombreLab, nombreSft FROM dbo.equipos ORDER BY nombreLab, nombre_eqp";

        try (Connection con = DatabaseConfig.conectar();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Equipo eq = new Equipo();
                eq.setId(rs.getLong("id"));
                eq.setNombreEqp(rs.getString("nombre_eqp"));
                eq.setLaboratorio(rs.getString("nombreLab"));
                eq.setSoftware(rs.getString("nombreSft"));
                equipos.add(eq);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar equipos: " + e.getMessage());
            e.printStackTrace();
        }
        return equipos;
    }

    /**
     * Lista equipos por laboratorio
     */
    public List<Equipo> listarPorLaboratorio(String laboratorio) {
        List<Equipo> equipos = new ArrayList<>();
        String sql = "SELECT id, nombre_eqp, nombreLab, nombreSft FROM dbo.equipos WHERE nombreLab = ? ORDER BY nombre_eqp";

        try (Connection con = DatabaseConfig.conectar();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, laboratorio);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Equipo eq = new Equipo();
                    eq.setId(rs.getLong("id"));
                    eq.setNombreEqp(rs.getString("nombre_eqp"));
                    eq.setLaboratorio(rs.getString("nombreLab"));
                    eq.setSoftware(rs.getString("nombreSft"));
                    equipos.add(eq);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al listar equipos por laboratorio: " + e.getMessage());
            e.printStackTrace();
        }
        return equipos;
    }

    /**
     * Busca un equipo por su ID
     */
    public Equipo buscarPorId(Long id) {
        String sql = "SELECT id, nombre_eqp, nombreLab, nombreSft FROM dbo.equipos WHERE id = ?";

        try (Connection con = DatabaseConfig.conectar();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Equipo eq = new Equipo();
                    eq.setId(rs.getLong("id"));
                    eq.setNombreEqp(rs.getString("nombre_eqp"));
                    eq.setLaboratorio(rs.getString("nombreLab"));
                    eq.setSoftware(rs.getString("nombreSft"));
                    return eq;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar equipo: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // ==================== VALIDACIONES ====================

    /**
     * Verifica si existe un equipo con el mismo nombre en el mismo laboratorio
     * 
     * @param nombre      Nombre del equipo a verificar
     * @param laboratorio Laboratorio donde se busca
     * @param idExcluir   ID a excluir de la búsqueda (null para nuevos)
     * @return true si ya existe, false si no
     */
    public boolean existeNombreEnLaboratorio(String nombre, String laboratorio, Long idExcluir) {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(*) FROM dbo.equipos WHERE nombre_eqp = ? AND nombreLab = ?");

        if (idExcluir != null) {
            sql.append(" AND id != ?");
        }

        try (Connection con = DatabaseConfig.conectar();
                PreparedStatement ps = con.prepareStatement(sql.toString())) {

            ps.setString(1, nombre);
            ps.setString(2, laboratorio);

            if (idExcluir != null) {
                ps.setLong(3, idExcluir);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar nombre de equipo: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Verifica si un equipo existe por su ID
     */
    public boolean existePorId(Long id) {
        String sql = "SELECT COUNT(*) FROM dbo.equipos WHERE id = ?";

        try (Connection con = DatabaseConfig.conectar();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar existencia de equipo: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Verifica si un equipo tiene reservas activas o pendientes
     */
    public boolean tieneReservasActivas(Long equipoId) {
        String sql = "SELECT COUNT(*) FROM dbo.registros WHERE equipo_id = ? AND hora_salida IS NULL";

        try (Connection con = DatabaseConfig.conectar();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, equipoId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar reservas activas: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Verifica si un equipo está disponible (no tiene sesión activa)
     */
    public boolean estaDisponible(Long equipoId) {
        String sql = "SELECT COUNT(*) FROM dbo.registros WHERE equipo_id = ? AND hora_salida IS NULL";

        try (Connection con = DatabaseConfig.conectar();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, equipoId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == 0; // Disponible si no hay registros activos
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar disponibilidad: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}