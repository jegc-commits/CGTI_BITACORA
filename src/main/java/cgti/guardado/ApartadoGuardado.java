
package cgti.guardado;

import cgti.clase.*;
import cgti.conexion.DatabaseConfig;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ApartadoGuardado {

    private final UsuarioGuardado usuarioGuardado = new UsuarioGuardado();
    private final EquipoGuardado equipoGuardado = new EquipoGuardado();

    public void guardar(Apartado apartado) {
        if (apartado.getId() == null) {
            String sql = "INSERT INTO dbo.apartados (usuario_matricula, laboratorio, equipo_id, dia_semana, "
                    + "hora_inicio, hora_fin, proposito, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = DatabaseConfig.conectar()
                    .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                setCampos(ps, apartado);
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        apartado.setId(rs.getLong(1));
                    }
                }
            } catch (SQLException e) {
                System.err.println("❌ Error al guardar apartado: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            actualizar(apartado);
        }
    }

    public List<Apartado> listarTodos() {
        List<Apartado> lista = new ArrayList<>();
        String sql = "SELECT * FROM dbo.apartados";
        try (PreparedStatement ps = DatabaseConfig.conectar().prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al listar apartados: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    public boolean equipoOcupado(Long equipoId, LocalDateTime inicio, LocalDateTime fin) {
        return listarTodos().stream()
                .filter(a -> a.getEquipo() != null && a.getEquipo().getId().equals(equipoId))
                .filter(a -> a.getEstado() == EstadoApartado.ACTIVO)
                .anyMatch(a -> cruzaHorario(a, inicio, fin));
    }

    public boolean laboratorioBloqueado(Laboratorio laboratorio, LocalDateTime inicio, LocalDateTime fin) {
        return listarTodos().stream()
                .filter(a -> a.getLaboratorio() == laboratorio)
                .filter(a -> a.getEstado() == EstadoApartado.BLOQUEADO)
                .anyMatch(a -> cruzaHorario(a, inicio, fin));
    }

    public boolean laboratorioBloqueadoAhora(Laboratorio laboratorio) {
        LocalDateTime ahora = LocalDateTime.now();
        return listarTodos().stream()
                .filter(a -> a.getLaboratorio() == laboratorio)
                .filter(a -> a.getEstado() == EstadoApartado.BLOQUEADO)
                .anyMatch(a -> estaVigente(a, ahora));
    }

    public boolean laboratorioOcupado(Laboratorio laboratorio, LocalDateTime inicio, LocalDateTime fin) {
        return listarTodos().stream()
                .filter(a -> a.getLaboratorio() == laboratorio)
                .filter(a -> a.getEstado() == EstadoApartado.ACTIVO || a.getEstado() == EstadoApartado.BLOQUEADO)
                .anyMatch(a -> cruzaHorario(a, inicio, fin));
    }

    public boolean estaReservadoAhoraPorOtro(Long equipoId, int matricula) {
        LocalDateTime ahora = LocalDateTime.now();
        return listarTodos().stream()
                .filter(a -> a.getEquipo() != null && a.getEquipo().getId().equals(equipoId))
                .filter(a -> a.getEstado() == EstadoApartado.ACTIVO)
                .filter(a -> estaVigente(a, ahora))
                .anyMatch(a -> a.getUsuario().getMatricula() != matricula);
    }

    public List<Apartado> reservasActivasDe(int matricula) {
        LocalDateTime ahora = LocalDateTime.now();
        return listarTodos().stream()
                .filter(a -> a.getUsuario() != null && a.getUsuario().getMatricula() == matricula)
                .filter(a -> a.getEstado() == EstadoApartado.ACTIVO)
                .filter(a -> estaVigente(a, ahora))
                .toList();
    }

    public List<Apartado> reservasPendientesDe(int matricula) {
        LocalDateTime ahora = LocalDateTime.now();
        return listarTodos().stream()
                .filter(a -> a.getUsuario() != null && a.getUsuario().getMatricula() == matricula)
                .filter(a -> a.getEquipo() != null)
                .filter(a -> a.getEstado() == EstadoApartado.ACTIVO)
                .filter(a -> a.getHoraInicio() != null && a.getHoraInicio().toLocalDate().equals(ahora.toLocalDate()))
                .filter(a -> a.getHoraFin() != null && a.getHoraFin().isAfter(ahora))
                .toList();
    }

    public Optional<Apartado> buscarReservaActivaActual(int matricula, Long equipoId) {
        LocalDateTime ahora = LocalDateTime.now();
        return listarTodos().stream()
                .filter(a -> a.getUsuario() != null && a.getUsuario().getMatricula() == matricula)
                .filter(a -> a.getEquipo() != null && a.getEquipo().getId().equals(equipoId))
                .filter(a -> a.getEstado() == EstadoApartado.ACTIVO)
                .filter(a -> estaVigente(a, ahora))
                .findFirst();
    }

    public Optional<Apartado> buscarReservaPendiente(int matricula, Long equipoId) {
        LocalDateTime ahora = LocalDateTime.now();
        return listarTodos().stream()
                .filter(a -> a.getUsuario() != null && a.getUsuario().getMatricula() == matricula)
                .filter(a -> a.getEquipo() != null && a.getEquipo().getId().equals(equipoId))
                .filter(a -> a.getEstado() == EstadoApartado.ACTIVO)
                .filter(a -> a.getHoraFin() != null && a.getHoraFin().isAfter(ahora))
                .findFirst();
    }

    public void finalizar(Long apartadoId) {
        listarTodos().stream()
                .filter(a -> a.getId().equals(apartadoId))
                .findFirst()
                .ifPresent(a -> {
                    a.setEstado(EstadoApartado.FINALIZADO);
                    actualizar(a);
                });
    }

    public List<Apartado> listarBloqueos() {
        return listarTodos().stream()
                .filter(a -> a.getEstado() == EstadoApartado.BLOQUEADO)
                .toList();
    }

    public void actualizar(Apartado apartado) {
        String sql = "UPDATE dbo.apartados SET usuario_matricula = ?, laboratorio = ?, equipo_id = ?, "
                + "dia_semana = ?, hora_inicio = ?, hora_fin = ?, proposito = ?, estado = ? WHERE id = ?";
        try (PreparedStatement ps = DatabaseConfig.conectar().prepareStatement(sql)) {
            setCampos(ps, apartado);
            ps.setLong(9, apartado.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar apartado: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setCampos(PreparedStatement ps, Apartado a) throws SQLException {
        ps.setString(1, a.getUsuario() != null ? String.valueOf(a.getUsuario().getMatricula()) : null);
        ps.setString(2, a.getLaboratorio().name());
        if (a.getEquipo() != null)
            ps.setLong(3, a.getEquipo().getId());
        else
            ps.setNull(3, Types.BIGINT);
        ps.setString(4, a.getDiaSemana() != null ? a.getDiaSemana().name() : null);
        ps.setTimestamp(5, a.getHoraInicio() != null ? Timestamp.valueOf(a.getHoraInicio()) : null);
        ps.setTimestamp(6, a.getHoraFin() != null ? Timestamp.valueOf(a.getHoraFin()) : null);
        ps.setString(7, a.getProposito());
        ps.setString(8, a.getEstado().name());
    }

    private Apartado mapear(ResultSet rs) throws SQLException {
        Apartado a = new Apartado();
        a.setId(rs.getLong("id"));
        String matriculaStr = rs.getString("usuario_matricula");
        if (matriculaStr != null) {
            usuarioGuardado.buscarPorMatricula(Integer.parseInt(matriculaStr.trim())).ifPresent(a::setUsuario);
        }
        a.setLaboratorio(Laboratorio.valueOf(rs.getString("laboratorio")));
        long equipoId = rs.getLong("equipo_id");
        if (!rs.wasNull()) {
            equipoGuardado.buscarPorId(equipoId).ifPresent(a::setEquipo);
        }
        String dia = rs.getString("dia_semana");
        if (dia != null)
            a.setDiaSemana(DiaSemana.valueOf(dia));
        Timestamp inicio = rs.getTimestamp("hora_inicio");
        if (inicio != null)
            a.setHoraInicio(inicio.toLocalDateTime());
        Timestamp fin = rs.getTimestamp("hora_fin");
        if (fin != null)
            a.setHoraFin(fin.toLocalDateTime());
        a.setProposito(rs.getString("proposito"));
        a.setEstado(EstadoApartado.valueOf(rs.getString("estado")));
        return a;
    }

    private boolean cruzaHorario(Apartado apartado, LocalDateTime inicio, LocalDateTime fin) {
        return apartado.getHoraInicio() != null
                && apartado.getHoraFin() != null
                && apartado.getHoraInicio().isBefore(fin)
                && apartado.getHoraFin().isAfter(inicio);
    }

    private boolean estaVigente(Apartado apartado, LocalDateTime momento) {
        return apartado.getHoraInicio() != null
                && apartado.getHoraFin() != null
                && !apartado.getHoraInicio().isAfter(momento)
                && apartado.getHoraFin().isAfter(momento);
    }
}