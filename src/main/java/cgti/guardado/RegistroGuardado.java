package cgti.guardado;

import cgti.clase.Equipo;
import cgti.clase.Laboratorio;
import cgti.clase.Registro;
import cgti.clase.Usuario;
import cgti.conexion.DatabaseConfig;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RegistroGuardado {

    private final UsuarioGuardado usuarioGuardado = new UsuarioGuardado();
    private final EquipoGuardado equipoGuardado = new EquipoGuardado();

    public void guardar(Registro registro) {
        if (registro.getId() == null) {
            String sql = "INSERT INTO dbo.registros (hora_entrada, hora_salida, proposito, laboratorio, "
                    + "usuario_matricula, equipo_id, apartado_id, incidencia, nota_incidencia) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = DatabaseConfig.conectar()
                    .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                setCampos(ps, registro);
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        registro.setId(rs.getLong(1));
                    }
                }
            } catch (SQLException e) {
                System.err.println("❌ Error al guardar registro: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            actualizar(registro);
        }
    }

    public List<Registro> listarTodos() {
        List<Registro> lista = new ArrayList<>();
        String sql = "SELECT * FROM dbo.registros ORDER BY hora_entrada DESC";
        try (PreparedStatement ps = DatabaseConfig.conectar().prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al listar registros: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    public Optional<Registro> buscarRegistroActivo(int matricula) {
        String sql = "SELECT * FROM dbo.registros WHERE usuario_matricula = ? AND hora_salida IS NULL";
        try (PreparedStatement ps = DatabaseConfig.conectar().prepareStatement(sql)) {
            ps.setString(1, String.valueOf(matricula));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapear(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al buscar registro activo: " + e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public void actualizar(Registro registro) {
        String sql = "UPDATE dbo.registros SET hora_entrada = ?, hora_salida = ?, proposito = ?, laboratorio = ?, "
                + "usuario_matricula = ?, equipo_id = ?, apartado_id = ?, incidencia = ?, nota_incidencia = ? WHERE id = ?";
        try (PreparedStatement ps = DatabaseConfig.conectar().prepareStatement(sql)) {
            setCampos(ps, registro);
            ps.setLong(10, registro.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar registro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setCampos(PreparedStatement ps, Registro r) throws SQLException {
        ps.setTimestamp(1, r.getHoraEntrada() != null ? Timestamp.valueOf(r.getHoraEntrada()) : null);
        ps.setTimestamp(2, r.getHoraSalida() != null ? Timestamp.valueOf(r.getHoraSalida()) : null);
        ps.setString(3, r.getProposito());
        ps.setString(4, r.getLaboratorio().name());
        ps.setString(5, String.valueOf(r.getUsuario().getMatricula()));
        if (r.getEquipoId() != null)
            ps.setLong(6, r.getEquipoId());
        else
            ps.setNull(6, Types.BIGINT);
        if (r.getApartadoId() != null)
            ps.setLong(7, r.getApartadoId());
        else
            ps.setNull(7, Types.BIGINT);
        ps.setBoolean(8, r.isIncidencia());
        ps.setString(9, r.getNotaIncidencia());
    }

    private Registro mapear(ResultSet rs) throws SQLException {
        Registro r = new Registro();
        r.setId(rs.getLong("id"));

        Timestamp entrada = rs.getTimestamp("hora_entrada");
        if (entrada != null)
            r.setHoraEntrada(entrada.toLocalDateTime());

        Timestamp salida = rs.getTimestamp("hora_salida");
        if (salida != null)
            r.setHoraSalida(salida.toLocalDateTime());
        r.setProposito(rs.getString("proposito"));
        r.setLaboratorio(Laboratorio.valueOf(rs.getString("laboratorio")));

        int matricula = Integer.parseInt(rs.getString("usuario_matricula").trim());
        Usuario usuario = usuarioGuardado.buscarPorMatricula(matricula).orElse(null);
        r.setUsuario(usuario);

        long equipoId = rs.getLong("equipo_id");
        if (!rs.wasNull()) {
            r.setEquipoId(equipoId);
            try {
                Optional<Equipo> equipoOpt = equipoGuardado.buscarPorId(equipoId);
                if (equipoOpt.isPresent()) {
                    r.setEquipoNombre(equipoOpt.get().getNombreEqp());
                } else {
                    r.setEquipoNombre("Equipo #" + equipoId);
                }
            } catch (Exception e) {
                r.setEquipoNombre("equipo" + equipoId);
            }
        } else {
            r.setEquipoId(null);
            r.setEquipoNombre(null);
        }

        long apartadoId = rs.getLong("apartado_id");
        r.setApartadoId(rs.wasNull() ? null : apartadoId);
        r.setIncidencia(rs.getBoolean("incidencia"));
        r.setNotaIncidencia(rs.getString("nota_incidencia"));
        return r;
    }
}
