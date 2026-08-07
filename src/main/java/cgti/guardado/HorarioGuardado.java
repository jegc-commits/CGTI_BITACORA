package cgti.guardado;

import cgti.clase.*;
import cgti.conexion.DatabaseConfig;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HorarioGuardado {

    private final UsuarioGuardado usuarioGuardado = new UsuarioGuardado();
    private final EquipoGuardado equipoGuardado = new EquipoGuardado();

    // ==================== OPERACIONES CRUD ====================

    public void guardar(Apartado apartado) {
        if (apartado.getId() == null) {
            insertarNuevo(apartado);
        } else {
            actualizar(apartado);
        }
    }

    private void insertarNuevo(Apartado apartado) {
        String sql = "INSERT INTO dbo.Horario (nombreLab, dia_semana, hora_inicio, hora_fin, proposito, estado) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = DatabaseConfig.conectar();
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            establecerParametrosHorario(ps, apartado);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    apartado.setId(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al guardar horario: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void actualizar(Apartado apartado) {
        String sql = "UPDATE dbo.Horario SET nombreLab = ?, dia_semana = ?, hora_inicio = ?, " +
                "hora_fin = ?, proposito = ?, estado = ? WHERE id = ?";

        try (Connection con = DatabaseConfig.conectar();
                PreparedStatement ps = con.prepareStatement(sql)) {

            establecerParametrosHorario(ps, apartado);
            ps.setLong(7, apartado.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al actualizar horario: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Apartado> listarTodos() {
        List<Apartado> lista = new ArrayList<>();
        String sql = "SELECT * FROM dbo.Horario ORDER BY hora_inicio";

        try (Connection con = DatabaseConfig.conectar();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(convertirHorarioAApartado(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar horarios: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    // ==================== VALIDACIONES DE DISPONIBILIDAD ====================

    public boolean equipoOcupado(Long equipoId, LocalDateTime inicio, LocalDateTime fin) {
        String sql = "SELECT COUNT(*) FROM dbo.registros " +
                "WHERE equipo_id = ? AND hora_salida IS NULL " +
                "AND ((hora_entrada BETWEEN ? AND ?) OR (hora_salida IS NULL AND hora_entrada <= ?))";

        try (Connection con = DatabaseConfig.conectar();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, equipoId);
            ps.setTimestamp(2, Timestamp.valueOf(inicio));
            ps.setTimestamp(3, Timestamp.valueOf(fin));
            ps.setTimestamp(4, Timestamp.valueOf(fin));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar equipo ocupado: " + e.getMessage());
        }
        return false;
    }

    public boolean laboratorioBloqueado(String laboratorio, LocalDateTime inicio, LocalDateTime fin) {
        String sql = "SELECT COUNT(*) FROM dbo.Horario " +
                "WHERE nombreLab = ? AND estado = 'BLOQUEADO' " +
                "AND ((hora_inicio <= ? AND hora_fin >= ?) " +
                "OR (hora_inicio >= ? AND hora_inicio <= ?))";

        try (Connection con = DatabaseConfig.conectar();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, laboratorio);
            ps.setTimestamp(2, Timestamp.valueOf(fin));
            ps.setTimestamp(3, Timestamp.valueOf(inicio));
            ps.setTimestamp(4, Timestamp.valueOf(inicio));
            ps.setTimestamp(5, Timestamp.valueOf(fin));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar laboratorio bloqueado: " + e.getMessage());
        }
        return false;
    }

    public boolean laboratorioBloqueadoAhora(String laboratorio) {
        LocalDateTime ahora = LocalDateTime.now();
        String sql = "SELECT COUNT(*) FROM dbo.Horario " +
                "WHERE nombreLab = ? AND estado = 'BLOQUEADO' " +
                "AND hora_inicio <= ? AND hora_fin >= ?";

        try (Connection con = DatabaseConfig.conectar();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, laboratorio);
            ps.setTimestamp(2, Timestamp.valueOf(ahora));
            ps.setTimestamp(3, Timestamp.valueOf(ahora));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar bloqueo actual: " + e.getMessage());
        }
        return false;
    }

    public boolean laboratorioOcupado(String laboratorio, LocalDateTime inicio, LocalDateTime fin) {
        return laboratorioBloqueado(laboratorio, inicio, fin) ||
                laboratorioConRegistrosActivos(laboratorio, inicio, fin);
    }

    private boolean laboratorioConRegistrosActivos(String laboratorio, LocalDateTime inicio, LocalDateTime fin) {
        String sql = "SELECT COUNT(*) FROM dbo.registros " +
                "WHERE laboratorio = ? AND hora_salida IS NULL " +
                "AND hora_entrada <= ?";

        try (Connection con = DatabaseConfig.conectar();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, laboratorio);
            ps.setTimestamp(2, Timestamp.valueOf(fin));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar registros activos: " + e.getMessage());
        }
        return false;
    }

    public boolean estaReservadoAhoraPorOtro(Long equipoId, int matricula) {
        LocalDateTime ahora = LocalDateTime.now();
        String sql = "SELECT COUNT(*) FROM dbo.registros " +
                "WHERE equipo_id = ? AND hora_salida IS NULL " +
                "AND usuario_matricula != ? AND hora_entrada <= ?";

        try (Connection con = DatabaseConfig.conectar();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, equipoId);
            ps.setInt(2, matricula);
            ps.setTimestamp(3, Timestamp.valueOf(ahora));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar reserva actual: " + e.getMessage());
        }
        return false;
    }

    // ==================== CONSULTAS ESPECÍFICAS ====================

    public List<Apartado> reservasActivasDe(int matricula) {
        List<Apartado> lista = new ArrayList<>();
        String sql = "SELECT * FROM dbo.registros WHERE usuario_matricula = ? AND hora_salida IS NULL";

        try (Connection con = DatabaseConfig.conectar();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, matricula);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(convertirRegistroAApartado(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener reservas activas: " + e.getMessage());
        }
        return lista;
    }

    public List<Apartado> reservasPendientesDe(int matricula) {
        LocalDateTime ahora = LocalDateTime.now();
        List<Apartado> lista = new ArrayList<>();
        String sql = "SELECT * FROM dbo.registros WHERE usuario_matricula = ? " +
                "AND hora_entrada > ? AND hora_salida IS NULL";

        try (Connection con = DatabaseConfig.conectar();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, matricula);
            ps.setTimestamp(2, Timestamp.valueOf(ahora));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(convertirRegistroAApartado(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener reservas pendientes: " + e.getMessage());
        }
        return lista;
    }

    public Optional<Apartado> buscarReservaActivaActual(int matricula, Long equipoId) {
        LocalDateTime ahora = LocalDateTime.now();
        String sql = "SELECT * FROM dbo.registros WHERE usuario_matricula = ? AND equipo_id = ? " +
                "AND hora_salida IS NULL AND hora_entrada <= ?";

        try (Connection con = DatabaseConfig.conectar();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, matricula);
            ps.setLong(2, equipoId);
            ps.setTimestamp(3, Timestamp.valueOf(ahora));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(convertirRegistroAApartado(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar reserva activa: " + e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<Apartado> buscarReservaPendiente(int matricula, Long equipoId) {
        LocalDateTime ahora = LocalDateTime.now();
        String sql = "SELECT * FROM dbo.registros WHERE usuario_matricula = ? AND equipo_id = ? " +
                "AND hora_entrada > ? AND hora_salida IS NULL";

        try (Connection con = DatabaseConfig.conectar();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, matricula);
            ps.setLong(2, equipoId);
            ps.setTimestamp(3, Timestamp.valueOf(ahora));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(convertirRegistroAApartado(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar reserva pendiente: " + e.getMessage());
        }
        return Optional.empty();
    }

    public void finalizar(Long registroId) {
        String sql = "UPDATE dbo.registros SET hora_salida = ? WHERE id = ?";

        try (Connection con = DatabaseConfig.conectar();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setLong(2, registroId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al finalizar reserva: " + e.getMessage());
        }
    }

    public List<Apartado> listarBloqueos() {
        List<Apartado> lista = new ArrayList<>();
        String sql = "SELECT * FROM dbo.Horario WHERE estado = 'BLOQUEADO' ORDER BY hora_inicio";

        try (Connection con = DatabaseConfig.conectar();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(convertirHorarioAApartado(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar bloqueos: " + e.getMessage());
        }
        return lista;
    }

    public List<String> listarLaboratorios() {
        List<String> laboratorios = new ArrayList<>();
        String sql = "SELECT nombreLab FROM dbo.laboratorio ORDER BY nombreLab";

        try (Connection con = DatabaseConfig.conectar();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                laboratorios.add(rs.getString("nombreLab"));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar laboratorios: " + e.getMessage());
        }
        return laboratorios;
    }

    // ==================== MÉTODOS AUXILIARES PRIVADOS ====================

    private void establecerParametrosHorario(PreparedStatement ps, Apartado a) throws SQLException {
        ps.setString(1, a.getLaboratorio());

        if (a.getDiaSemana() != null) {
            ps.setString(2, a.getDiaSemana().name());
        } else {
            ps.setNull(2, Types.VARCHAR);
        }

        if (a.getHoraInicio() != null) {
            ps.setTimestamp(3, Timestamp.valueOf(a.getHoraInicio()));
        } else {
            ps.setNull(3, Types.TIMESTAMP);
        }

        if (a.getHoraFin() != null) {
            ps.setTimestamp(4, Timestamp.valueOf(a.getHoraFin()));
        } else {
            ps.setNull(4, Types.TIMESTAMP);
        }

        ps.setString(5, a.getProposito() != null ? a.getProposito() : "");
        ps.setString(6, a.getEstado().name());
    }

    private Apartado convertirHorarioAApartado(ResultSet rs) throws SQLException {
        Apartado a = new Apartado();

        a.setId(rs.getLong("id"));
        a.setLaboratorio(rs.getString("nombreLab"));

        String dia = rs.getString("dia_semana");
        if (dia != null) {
            a.setDiaSemana(DiaSemana.valueOf(dia));
        }

        Timestamp inicio = rs.getTimestamp("hora_inicio");
        if (inicio != null) {
            a.setHoraInicio(inicio.toLocalDateTime());
        }

        Timestamp fin = rs.getTimestamp("hora_fin");
        if (fin != null) {
            a.setHoraFin(fin.toLocalDateTime());
        }

        a.setProposito(rs.getString("proposito"));

        String estado = rs.getString("estado");
        if (estado != null) {
            a.setEstado(EstadoApartado.valueOf(estado));
        }

        return a;
    }

    private Apartado convertirRegistroAApartado(ResultSet rs) throws SQLException {
        Apartado a = new Apartado();

        a.setId(rs.getLong("id"));
        a.setLaboratorio(rs.getString("laboratorio"));

        Timestamp entrada = rs.getTimestamp("hora_entrada");
        if (entrada != null) {
            a.setHoraInicio(entrada.toLocalDateTime());
        }

        Timestamp salida = rs.getTimestamp("hora_salida");
        if (salida != null) {
            a.setHoraFin(salida.toLocalDateTime());
        }

        a.setProposito(rs.getString("proposito"));
        a.setEstado(EstadoApartado.ACTIVO);

        int matricula = rs.getInt("usuario_matricula");
        if (!rs.wasNull()) {
            usuarioGuardado.buscarPorMatricula(matricula).ifPresent(a::setUsuario);
        }

        long equipoId = rs.getLong("equipo_id");
        if (!rs.wasNull()) {
            equipoGuardado.buscarPorId(equipoId);
        }

        return a;
    }
}