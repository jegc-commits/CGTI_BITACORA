package cgti.guardado;

import cgti.clase.Equipo;
import cgti.clase.Registro;
import cgti.clase.Usuario;
import cgti.conexion.DatabaseConfig;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Clase de acceso a datos para la tabla dbo.registros
 * Maneja entradas y salidas de usuarios en laboratorios
 */
public class RegistroGuardado {

    private final UsuarioGuardado usuarioGuardado = new UsuarioGuardado();
    private final EquipoGuardado equipoGuardado = new EquipoGuardado();

    // ==================== OPERACIONES CRUD ====================

    /**
     * Guarda un nuevo registro o actualiza uno existente
     */
    public void guardar(Registro registro) {
        if (registro.getId() == null) {
            insertar(registro);
        } else {
            actualizar(registro);
        }
    }

    /**
     * Inserta un nuevo registro de entrada
     */
    private void insertar(Registro registro) {
        String sql = "INSERT INTO dbo.registros (hora_entrada, hora_salida, proposito, laboratorio, " +
                "usuario_matricula, equipo_id, incidencia) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DatabaseConfig.conectar();
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            establecerParametros(ps, registro);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    registro.setId(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar registro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Actualiza un registro existente
     */
    public void actualizar(Registro registro) {
        String sql = "UPDATE dbo.registros SET hora_entrada = ?, hora_salida = ?, proposito = ?, " +
                "laboratorio = ?, usuario_matricula = ?, equipo_id = ?, incidencia = ? " +
                "WHERE id = ?";

        try (Connection con = DatabaseConfig.conectar();
                PreparedStatement ps = con.prepareStatement(sql)) {

            establecerParametros(ps, registro);
            ps.setLong(8, registro.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al actualizar registro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Lista todos los registros ordenados por fecha de entrada descendente
     */
    public List<Registro> listarTodos() {
        List<Registro> lista = new ArrayList<>();
        String sql = "SELECT * FROM dbo.registros ORDER BY hora_entrada DESC";

        try (Connection con = DatabaseConfig.conectar();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(convertirARegistro(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar registros: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * Busca un registro por su ID
     */
    public Optional<Registro> buscarPorId(Long id) {
        String sql = "SELECT * FROM dbo.registros WHERE id = ?";

        try (Connection con = DatabaseConfig.conectar();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(convertirARegistro(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar registro por ID: " + e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    // ==================== CONSULTAS ESPECÍFICAS ====================

    /**
     * Verifica si existe una sesión activa para un equipo
     * (entrada registrada pero sin salida)
     */
    public boolean existeSesionActivaParaEquipo(Long equipoId) {
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
            System.err.println("Error al verificar sesión activa de equipo: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Busca el registro activo de un usuario (entrada sin salida)
     */
    public Optional<Registro> buscarRegistroActivo(int matricula) {
        String sql = "SELECT * FROM dbo.registros WHERE usuario_matricula = ? AND hora_salida IS NULL";

        try (Connection con = DatabaseConfig.conectar();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, matricula);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(convertirARegistro(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar registro activo: " + e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     * Verifica si un usuario tiene una sesión activa
     */
    public boolean tieneSesionActiva(int matricula) {
        String sql = "SELECT COUNT(*) FROM dbo.registros WHERE usuario_matricula = ? AND hora_salida IS NULL";

        try (Connection con = DatabaseConfig.conectar();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, matricula);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar sesión activa de usuario: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Obtiene los registros de un usuario específico
     */
    public List<Registro> listarPorUsuario(int matricula) {
        List<Registro> lista = new ArrayList<>();
        String sql = "SELECT * FROM dbo.registros WHERE usuario_matricula = ? ORDER BY hora_entrada DESC";

        try (Connection con = DatabaseConfig.conectar();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, matricula);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(convertirARegistro(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al listar registros por usuario: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * Obtiene los registros de un laboratorio específico
     */
    public List<Registro> listarPorLaboratorio(String laboratorio) {
        List<Registro> lista = new ArrayList<>();
        String sql = "SELECT * FROM dbo.registros WHERE laboratorio = ? ORDER BY hora_entrada DESC";

        try (Connection con = DatabaseConfig.conectar();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, laboratorio);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(convertirARegistro(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al listar registros por laboratorio: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * Registra la salida de un usuario (cierra la sesión activa)
     * 
     * @return true si se actualizó correctamente, false si no había sesión activa
     */
    public boolean registrarSalida(int matricula) {
        String sql = "UPDATE dbo.registros SET hora_salida = ? " +
                "WHERE usuario_matricula = ? AND hora_salida IS NULL";

        try (Connection con = DatabaseConfig.conectar();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(2, matricula);

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println("Error al registrar salida: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Reporta una incidencia en un registro activo
     * 
     * @return true si se actualizó correctamente
     */
    public boolean reportarIncidencia(Long registroId, String descripcion) {
        String sql = "UPDATE dbo.registros SET incidencia = 1, proposito = ? WHERE id = ?";

        try (Connection con = DatabaseConfig.conectar();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "INCIDENCIA: " + descripcion);
            ps.setLong(2, registroId);

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println("Error al reportar incidencia: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Obtiene los registros con incidencias
     */
    public List<Registro> listarIncidencias() {
        List<Registro> lista = new ArrayList<>();
        String sql = "SELECT * FROM dbo.registros WHERE incidencia = 1 ORDER BY hora_entrada DESC";

        try (Connection con = DatabaseConfig.conectar();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(convertirARegistro(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar incidencias: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * Obtiene los registros activos (sin salida) de un laboratorio
     */
    public List<Registro> listarActivosPorLaboratorio(String laboratorio) {
        List<Registro> lista = new ArrayList<>();
        String sql = "SELECT * FROM dbo.registros WHERE laboratorio = ? AND hora_salida IS NULL " +
                "ORDER BY hora_entrada";

        try (Connection con = DatabaseConfig.conectar();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, laboratorio);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(convertirARegistro(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al listar registros activos: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * Cuenta cuántos equipos están ocupados en un laboratorio
     */
    public int contarEquiposOcupados(String laboratorio) {
        String sql = "SELECT COUNT(DISTINCT equipo_id) FROM dbo.registros " +
                "WHERE laboratorio = ? AND hora_salida IS NULL";

        try (Connection con = DatabaseConfig.conectar();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, laboratorio);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al contar equipos ocupados: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    // ==================== MÉTODOS AUXILIARES PRIVADOS ====================

    /**
     * Establece los parámetros del PreparedStatement
     */
    private void establecerParametros(PreparedStatement ps, Registro r) throws SQLException {
        // Parámetro 1: hora_entrada (DATETIME2)
        if (r.getHoraEntrada() != null) {
            ps.setTimestamp(1, Timestamp.valueOf(r.getHoraEntrada()));
        } else {
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
        }

        // Parámetro 2: hora_salida (DATETIME2, puede ser NULL)
        if (r.getHoraSalida() != null) {
            ps.setTimestamp(2, Timestamp.valueOf(r.getHoraSalida()));
        } else {
            ps.setNull(2, Types.TIMESTAMP);
        }

        // Parámetro 3: proposito (VARCHAR)
        ps.setString(3, r.getProposito() != null ? r.getProposito() : "");

        // Parámetro 4: laboratorio (VARCHAR)
        ps.setString(4, r.getLaboratorio());

        // Parámetro 5: usuario_matricula (INT)
        if (r.getUsuario() != null) {
            ps.setInt(5, r.getUsuario().getMatricula());
        } else {
            ps.setNull(5, Types.INTEGER);
        }

        // Parámetro 6: equipo_id (BIGINT, puede ser NULL)
        if (r.getEquipoId() != null) {
            ps.setLong(6, r.getEquipoId());
        } else {
            ps.setNull(6, Types.BIGINT);
        }

        // Parámetro 7: incidencia (BIT)
        ps.setBoolean(7, r.isIncidencia());
    }

    /**
     * Convierte un ResultSet en un objeto Registro
     */
    private Registro convertirARegistro(ResultSet rs) throws SQLException {
        Registro r = new Registro();

        // ID
        r.setId(rs.getLong("id"));

        // Fechas
        Timestamp entrada = rs.getTimestamp("hora_entrada");
        if (entrada != null) {
            r.setHoraEntrada(entrada.toLocalDateTime());
        }

        Timestamp salida = rs.getTimestamp("hora_salida");
        if (salida != null) {
            r.setHoraSalida(salida.toLocalDateTime());
        }

        // Datos básicos
        r.setProposito(rs.getString("proposito"));
        r.setLaboratorio(rs.getString("laboratorio"));
        r.setIncidencia(rs.getBoolean("incidencia"));

        // Usuario (matricula es INT)
        int matricula = rs.getInt("usuario_matricula");
        if (!rs.wasNull()) {
            Optional<Usuario> usuarioOpt = usuarioGuardado.buscarPorMatricula(matricula);
            usuarioOpt.ifPresent(r::setUsuario);
        }

        // Equipo
        long equipoId = rs.getLong("equipo_id");
        if (!rs.wasNull()) {
            r.setEquipoId(equipoId);
            r.setEquipoNombre("Equipo #" + equipoId);
        }

        return r;
    }
}