package cgti.funcion;

import cgti.clase.Apartado;
import cgti.clase.DiaSemana;
import cgti.clase.Equipo;
import cgti.clase.EstadoApartado;
import cgti.clase.Usuario;
import cgti.guardado.HorarioGuardado;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class HorarioFuncion {

    private final HorarioGuardado guardar = new HorarioGuardado();

    public List<String> listarLaboratorios() {
        return guardar.listarLaboratorios();
    }

    // ==================== APARTADOS DE EQUIPOS ====================

    public String importarHorarioClase(String lab, DiaSemana dia,
            String horaInicio, String horaFin, String motivo) {

        if (!esHorarioDisponible(lab, dia, horaInicio, horaFin)) {
            return "Ya existe un horario en ese día y hora";
        }

        Apartado bloqueoAdmin = new Apartado();
        bloqueoAdmin.setLaboratorio(lab);
        bloqueoAdmin.setDiaSemana(dia);
        bloqueoAdmin.setHoraInicio(crearFechaHora(dia, horaInicio));
        bloqueoAdmin.setHoraFin(crearFechaHora(dia, horaFin));
        bloqueoAdmin.setProposito(motivo);
        bloqueoAdmin.setEstado(EstadoApartado.BLOQUEADO);

        guardar.guardar(bloqueoAdmin);
        return "Horario insertado correctamente";
    }

    public String apartarEquipo(Usuario usuario, String laboratorio, Equipo equipo,
            LocalDateTime inicio, LocalDateTime fin, String proposito) {

        if (equipo.getLaboratorio() != null &&
                !equipo.getLaboratorio().equals(laboratorio)) {
            return "El equipo no pertenece al laboratorio " + laboratorio;
        }

        if (guardar.laboratorioBloqueado(laboratorio, inicio, fin)) {
            return "El laboratorio está bloqueado en ese horario";
        }

        if (guardar.equipoOcupado(equipo.getId(), inicio, fin)) {
            return "Esa máquina ya está reservada en ese horario";
        }

        if (tieneReservaEnHorario(usuario.getMatricula(), inicio, fin)) {
            return "Ya tienes una reserva en ese horario";
        }

        Apartado apartado = new Apartado();
        apartado.setUsuario(usuario);
        apartado.setLaboratorio(laboratorio);
        apartado.setEquipo(equipo);
        apartado.setHoraInicio(inicio);
        apartado.setHoraFin(fin);
        apartado.setProposito(proposito);
        apartado.setEstado(EstadoApartado.ACTIVO);

        guardar.guardar(apartado);
        return "Máquina apartada exitosamente";
    }

    // ==================== BLOQUEOS DE LABORATORIOS ====================

    public String bloquearLaboratorio(String laboratorio, LocalDateTime inicio,
            LocalDateTime fin, String motivo) {

        if (guardar.laboratorioOcupado(laboratorio, inicio, fin)) {
            return "Ya existe un apartado u horario en ese horario";
        }

        Apartado bloqueo = new Apartado();
        bloqueo.setLaboratorio(laboratorio);
        bloqueo.setHoraInicio(inicio);
        bloqueo.setHoraFin(fin);
        bloqueo.setProposito(motivo);
        bloqueo.setEstado(EstadoApartado.BLOQUEADO);

        guardar.guardar(bloqueo);
        return "Horario insertado correctamente";
    }

    // ==================== VALIDACIONES EN TIEMPO REAL ====================

    public boolean estaReservadoAhoraPorOtro(Long equipoId, int matricula) {
        return guardar.estaReservadoAhoraPorOtro(equipoId, matricula);
    }

    public boolean laboratorioBloqueadoAhora(String laboratorio) {
        return guardar.laboratorioBloqueadoAhora(laboratorio);
    }

    public Optional<Apartado> tieneReservaActiva(int matricula, Long equipoId) {
        return guardar.buscarReservaActivaActual(matricula, equipoId);
    }

    public Optional<Apartado> tieneReservaPendiente(int matricula, Long equipoId) {
        return guardar.buscarReservaPendiente(matricula, equipoId);
    }

    // ==================== CONSULTAS DE RESERVAS ====================

    public List<Apartado> misReservasActivas(int matricula) {
        return guardar.reservasActivasDe(matricula);
    }

    public List<Apartado> misReservasPendientes(int matricula) {
        return guardar.reservasPendientesDe(matricula).stream()
                .sorted(Comparator.comparing(Apartado::getHoraInicio))
                .collect(Collectors.toList());
    }

    public List<Apartado> misApartados(int matricula) {
        return guardar.listarTodos().stream()
                .filter(a -> a != null
                        && a.getUsuario() != null
                        && a.getUsuario().getMatricula() == matricula)
                .sorted(Comparator.comparing(Apartado::getHoraInicio))
                .collect(Collectors.toList());
    }

    public List<Apartado> listarBloqueos() {
        return guardar.listarBloqueos().stream()
                .sorted(Comparator.comparing(Apartado::getHoraInicio))
                .collect(Collectors.toList());
    }

    // ==================== GESTIÓN DE REGISTROS ====================

    public String finalizarReserva(Long apartadoId) {
        try {
            guardar.finalizar(apartadoId);
            return "Reserva finalizada correctamente";
        } catch (Exception e) {
            return "Error al finalizar reserva: " + e.getMessage();
        }
    }

    public String actualizarReserva(Apartado apartado) {
        try {
            guardar.actualizar(apartado);
            return "Reserva actualizada correctamente";
        } catch (Exception e) {
            return "Error al actualizar reserva: " + e.getMessage();
        }
    }

    // ==================== IMPORTACIÓN DE HORARIOS ====================

    public void importarHorarioClase(String lab, DiaSemana dia,
            String horaInicio, String horaFin) {
        importarHorarioClase(lab, dia, horaInicio, horaFin, "Horario de clase");
    }

    public String importarHorarios(String texto) {
        int insertados = 0;
        int errores = 0;
        List<String> erroresDetalle = new ArrayList<>();

        for (String linea : texto.split("\\R")) {
            if (linea.trim().isEmpty()) {
                continue;
            }

            String[] partes = linea.split(",", 5);

            if (partes.length < 4) {
                errores++;
                erroresDetalle.add("Formato inválido: " + linea);
                continue;
            }

            try {
                String laboratorio = partes[0].trim();
                LocalDate fecha = LocalDate.parse(partes[1].trim());
                LocalTime horaInicio = LocalTime.parse(partes[2].trim());
                LocalTime horaFin = LocalTime.parse(partes[3].trim());
                String motivo = partes.length == 5 ? partes[4].trim() : "Horario importado";

                String resultado = bloquearLaboratorio(
                        laboratorio,
                        LocalDateTime.of(fecha, horaInicio),
                        LocalDateTime.of(fecha, horaFin),
                        motivo);

                if (resultado.contains("correctamente")) {
                    insertados++;
                } else {
                    errores++;
                    erroresDetalle.add(resultado + " - " + linea);
                }
            } catch (Exception ex) {
                errores++;
                erroresDetalle.add("Error: " + ex.getMessage() + " - " + linea);
            }
        }

        StringBuilder resumen = new StringBuilder();
        resumen.append("Importación completa: ").append(insertados)
                .append(" horarios insertados, ").append(errores)
                .append(" con error.");

        if (!erroresDetalle.isEmpty()) {
            resumen.append("\nDetalles de errores:");
            for (String error : erroresDetalle) {
                resumen.append("\n- ").append(error);
            }
        }

        return resumen.toString();
    }

    // ==================== VALIDACIONES DE DISPONIBILIDAD ====================

    public boolean esHorarioDisponible(String lab, DiaSemana dia,
            String horaDeseadaInicio, String horaDeseadaFin) {

        List<Apartado> todosLosApartados = guardar.listarTodos();

        for (Apartado ap : todosLosApartados) {
            if (ap.getLaboratorio() != null
                    && ap.getLaboratorio().equals(lab)
                    && ap.getDiaSemana() != null
                    && ap.getDiaSemana().equals(dia)
                    && (ap.getEstado() == EstadoApartado.BLOQUEADO
                            || ap.getEstado() == EstadoApartado.ACTIVO)
                    && cruzanHorarios(horaDeseadaInicio, horaDeseadaFin,
                            ap.getHoraInicio(), ap.getHoraFin())) {
                return false;
            }
        }
        return true;
    }

    public boolean esEquipoDisponible(Long equipoId, LocalDateTime inicio, LocalDateTime fin) {
        return !guardar.equipoOcupado(equipoId, inicio, fin);
    }

    // ==================== MÉTODOS AUXILIARES PRIVADOS ====================

    private boolean tieneReservaEnHorario(int matricula, LocalDateTime inicio, LocalDateTime fin) {
        return misApartados(matricula).stream()
                .filter(a -> a.getEstado() == EstadoApartado.ACTIVO)
                .filter(a -> a.getHoraInicio() != null && a.getHoraFin() != null)
                .anyMatch(a -> a.getHoraInicio().isBefore(fin)
                        && a.getHoraFin().isAfter(inicio));
    }

    private LocalDateTime crearFechaHora(DiaSemana dia, String hora) {
        LocalDate fecha = LocalDate.now();
        int diaObjetivo = numeroDiaSemana(dia);

        while (fecha.getDayOfWeek().getValue() != diaObjetivo) {
            fecha = fecha.plusDays(1);
        }

        return LocalDateTime.of(fecha, LocalTime.parse(hora));
    }

    private int numeroDiaSemana(DiaSemana dia) {
        return switch (dia) {
            case LUNES -> 1;
            case MARTES -> 2;
            case MIERCOLES -> 3;
            case JUEVES -> 4;
            case VIERNES -> 5;
            case SABADO -> 6;
        };
    }

    private boolean cruzanHorarios(String horaDeseadaInicio, String horaDeseadaFin,
            LocalDateTime horaExistenteInicio, LocalDateTime horaExistenteFin) {

        if (horaExistenteInicio == null || horaExistenteFin == null) {
            return false;
        }

        LocalTime deseadaInicio = LocalTime.parse(horaDeseadaInicio);
        LocalTime deseadaFin = LocalTime.parse(horaDeseadaFin);
        LocalTime existenteInicio = horaExistenteInicio.toLocalTime();
        LocalTime existenteFin = horaExistenteFin.toLocalTime();

        return deseadaInicio.isBefore(existenteFin) && deseadaFin.isAfter(existenteInicio);
    }
}