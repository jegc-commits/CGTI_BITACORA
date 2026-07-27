package cgti.funcion;

import cgti.clase.Apartado;
import cgti.clase.DiaSemana;
import cgti.clase.Equipo;
import cgti.clase.EstadoApartado;
import cgti.clase.Laboratorio;
import cgti.clase.Usuario;
import cgti.guardado.ApartadoGuardado;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ApartadoFuncion {

    private ApartadoGuardado guardar = new ApartadoGuardado();

    public String apartarEquipo(Usuario usuario, Laboratorio laboratorio, Equipo equipo,
            LocalDateTime inicio, LocalDateTime fin, String proposito) {

        if (guardar.laboratorioBloqueado(laboratorio, inicio, fin)) {
            return "El laboratorio esta bloqueado en ese horario";
        }

        if (guardar.equipoOcupado(equipo.getId(), inicio, fin)) {
            return "Esa maquina ya esta reservada en ese horario";
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
        return "Maquina apartada";
    }

    public String bloquearLaboratorio(Laboratorio laboratorio, LocalDateTime inicio,
            LocalDateTime fin, String motivo) {
        if (guardar.laboratorioOcupado(laboratorio, inicio, fin)) {
            return "Ya existe un apartado u horario insertado en ese horario";
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

    public boolean estaReservadoAhoraPorOtro(Long equipoId, int matrícula) {
        return guardar.estaReservadoAhoraPorOtro(equipoId, matrícula);
    }

    // Usado por el registro de entrada/salida para saber si el laboratorio esta
    // bloqueado en este momento (horario de clase u otro bloqueo insertado por el
    // admin).
    public boolean laboratorioBloqueadoAhora(Laboratorio laboratorio) {
        return guardar.laboratorioBloqueadoAhora(laboratorio);
    }

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
                .filter(a -> a.getUsuario() != null && a.getUsuario().getMatricula() == matricula)
                .sorted(Comparator.comparing(Apartado::getHoraInicio))
                .collect(Collectors.toList());
    }

    public List<Apartado> listarBloqueos() {
        return guardar.listarBloqueos().stream()
                .sorted(Comparator.comparing(Apartado::getHoraInicio))
                .collect(Collectors.toList());
    }

    public void importarHorarioClase(Laboratorio lab, DiaSemana dia, String horaInicio, String horaFin) {
        Apartado bloqueoAdmin = new Apartado();
        bloqueoAdmin.setLaboratorio(lab);
        bloqueoAdmin.setDiaSemana(dia);
        bloqueoAdmin.setHoraInicio(crearFechaHora(dia, horaInicio));
        bloqueoAdmin.setHoraFin(crearFechaHora(dia, horaFin));
        bloqueoAdmin.setEstado(EstadoApartado.BLOQUEADO);
        guardar.guardar(bloqueoAdmin);
    }

    public String importarHorarios(String texto) {
        int insertados = 0;
        int errores = 0;

        for (String linea : texto.split("\\R")) {
            if (linea.trim().isEmpty()) {
                continue;
            }

            String[] partes = linea.split(",", 5);
            if (partes.length < 4) {
                errores++;
                continue;
            }

            try {
                Laboratorio laboratorio = Laboratorio.valueOf(partes[0].trim().toUpperCase());
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
                }
            } catch (Exception ex) {
                errores++;
            }
        }

        return "Importacion completa: " + insertados + " horarios insertados, "
                + errores + " con error";
    }

    public boolean esHorarioDisponible(Laboratorio lab, DiaSemana dia,
            String horaDeseadaInicio, String horaDeseadaFin) {
        List<Apartado> todosLosApartados = guardar.listarTodos();

        for (Apartado ap : todosLosApartados) {
            if (ap.getLaboratorio() == lab && ap.getDiaSemana() == dia
                    && (ap.getEstado() == EstadoApartado.BLOQUEADO
                            || ap.getEstado() == EstadoApartado.ACTIVO)
                    && cruzanHorarios(horaDeseadaInicio, horaDeseadaFin,
                            ap.getHoraInicio(), ap.getHoraFin())) {
                return false;
            }
        }
        return true;
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
