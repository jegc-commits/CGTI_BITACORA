package cgti.funcion;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import cgti.clase.Equipo;
import cgti.clase.Laboratorio;
import cgti.clase.Registro;
import cgti.clase.Usuario;
import cgti.guardado.RegistroGuardado;

public class RegistroFuncion {

    // Si un alumno lleva mas de este numero de horas sin registrar salida,
    // se marca automaticamente y se genera una incidencia.
    private static final long HORAS_LIMITE_SALIDA = 4;

    private RegistroGuardado guardar = new RegistroGuardado();

    public String registrarEntrada(Usuario usuario, Equipo equipo, Laboratorio laboratorio, String proposito) {
        if (obtenerRegistroActivo(usuario.getMatricula()).isPresent()) {
            return "Ya tienes una entrada activa, registra tu salida primero";
        }
        Registro registro = new Registro();
        registro.setUsuario(usuario);
        registro.setLaboratorio(laboratorio);
        registro.setEquipoId(equipo.getId());
        registro.setHoraEntrada(LocalDateTime.now());
        registro.setProposito(proposito);
        guardar.guardar(registro);
        return "Entrada registrada correctamente";
    }

    public String registrarSalida(int matricula) {
        Optional<Registro> registroActivo = obtenerRegistroActivo(matricula);
        if (registroActivo.isEmpty()) {
            return "No tienes una entrada activa";
        }

        Registro registro = registroActivo.get();
        registro.setHoraSalida(LocalDateTime.now());
        guardar.actualizar(registro);
        return "Salida registrada correctamente";
    }

    public boolean tieneSesionActiva(int matricula) {
        return obtenerRegistroActivo(matricula).isPresent();
    }

    public List<Registro> listarTodos() {
        return guardar.listarTodos();
    }

    private Optional<Registro> obtenerRegistroActivo(int matricula) {
        Optional<Registro> activo = guardar.buscarRegistroActivo(matricula);
        if (activo.isEmpty()) {
            return activo;
        }

        Registro registro = activo.get();
        long horasTranscurridas = Duration.between(registro.getHoraEntrada(), LocalDateTime.now()).toHours();

        if (horasTranscurridas >= HORAS_LIMITE_SALIDA) {
            registro.setHoraSalida(registro.getHoraEntrada().plusHours(HORAS_LIMITE_SALIDA));
            registro.setIncidencia(true);
            registro.setNotaIncidencia("Salida marcada automáticamente: excedió " + HORAS_LIMITE_SALIDA
                    + " horas sin registrar salida");
            guardar.actualizar(registro);
            return Optional.empty();
        }

        return Optional.of(registro);
    }
}
