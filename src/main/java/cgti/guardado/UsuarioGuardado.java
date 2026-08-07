package cgti.guardado;

import cgti.clase.Usuario;
import cgti.clase.Alumno;
import cgti.clase.Rol;
import cgti.conexion.DatabaseConfig;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioGuardado {

    public void guardar(Usuario usuario) {
        String sql = "INSERT INTO dbo.usuarios (matricula, nombre, apellido_paterno, apellido_materno, "
                + "fecha_nacimiento, telefono, rol, cuatrimestre, carrera, grupo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DatabaseConfig.conectar();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, String.valueOf(usuario.getMatricula()));
            ps.setString(2, usuario.getNombre());
            ps.setString(3, usuario.getApellidoPaterno());
            ps.setString(4, usuario.getApellidoMaterno());
            if (usuario.getFechaNacimiento() != null) {
                ps.setDate(5, Date.valueOf(usuario.getFechaNacimiento()));
            } else {
                ps.setNull(5, Types.DATE);
            }
            ps.setString(6, usuario.getTelefono());
            ps.setString(7, usuario.getRol().name());
            if (usuario instanceof Alumno alumno) {
                ps.setString(8, alumno.getCuatrimestre());
                ps.setString(9, alumno.getCarrera());
                ps.setString(10, alumno.getGrupo());
            } else {
                ps.setNull(8, Types.VARCHAR);
                ps.setNull(9, Types.VARCHAR);
                ps.setNull(10, Types.VARCHAR);
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("❌ Error al guardar usuario: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Optional<Usuario> buscarPorMatricula(int matricula) {
        String sql = "SELECT * FROM dbo.usuarios WHERE matricula = ?";
        try (Connection con = DatabaseConfig.conectar();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, String.valueOf(matricula));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapear(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al buscar usuario: " + e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public List<Usuario> listarTodos() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM dbo.usuarios";
        try (Connection con = DatabaseConfig.conectar();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al listar usuarios: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    public void eliminar(int matricula) {
        String sql = "DELETE FROM dbo.usuarios WHERE matricula = ?";
        try (Connection con = DatabaseConfig.conectar();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, String.valueOf(matricula));
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar usuario: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        Rol rol = Rol.valueOf(rs.getString("rol"));
        Usuario u = rol == Rol.ALUMNO ? new Alumno() : new Usuario();
        u.setMatricula(Integer.parseInt(rs.getString("matricula").trim()));
        u.setNombre(rs.getString("nombre"));
        u.setApellidoPaterno(rs.getString("apellido_paterno"));
        u.setApellidoMaterno(rs.getString("apellido_materno"));
        Date fecha = rs.getDate("fecha_nacimiento");
        if (fecha != null) {
            u.setFechaNacimiento(fecha.toLocalDate());
        }
        u.setTelefono(rs.getString("telefono"));
        u.setRol(rol);
        if (u instanceof Alumno alumno) {
            alumno.setCuatrimestre(rs.getString("cuatrimestre"));
            alumno.setCarrera(rs.getString("carrera"));
            alumno.setGrupo(rs.getString("grupo"));
        }
        return u;
    }
}