package cgti.funcion;

import cgti.clase.Equipo;
import cgti.clase.Registro;
import cgti.clase.Usuario;
import cgti.guardado.RegistroGuardado;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Clase que contiene la lógica de negocio para la gestión de registros
 * (entradas y salidas de laboratorio)
 */
public class RegistroFuncion {

    private final RegistroGuardado registroGuardado = new RegistroGuardado();

    // ==================== OPERACIONES DE REGISTRO ====================

    /**
     * Registra la entrada de un usuario a un laboratorio
     */
    public String registrarEntrada(Usuario usuario, Equipo equipo, String laboratorio, String proposito) {
        // Validar que el usuario no tenga una sesión activa
        if (tieneSesionActiva(usuario.getMatricula())) {
            return "El usuario ya tiene una sesión activa. Debe registrar su salida primero.";
        }

        // Validar que el equipo esté disponible
        if (equipoTieneSesionActiva(equipo.getId())) {
            return "El equipo " + equipo.getNombreEqp() + " está ocupado en este momento.";
        }

        // Crear el registro
        Registro registro = new Registro();
        registro.setHoraEntrada(LocalDateTime.now());
        registro.setUsuario(usuario);
        registro.setEquipoId(equipo.getId());
        registro.setEquipoNombre(equipo.getNombreEqp());
        registro.setLaboratorio(laboratorio);
        registro.setProposito(proposito);
        registro.setIncidencia(false);

        registroGuardado.guardar(registro);
        return "Entrada registrada correctamente";
    }

    /**
     * Registra la salida de un usuario del laboratorio
     */
    public String registrarSalida(int matricula) {
        Optional<Registro> registroActivo = registroGuardado.buscarRegistroActivo(matricula);

        if (registroActivo.isEmpty()) {
            return "No se encontró una sesión activa para este usuario.";
        }

        boolean exito = registroGuardado.registrarSalida(matricula);
        if (exito) {
            return "Salida registrada correctamente";
        } else {
            return "Error al registrar la salida.";
        }
    }

    /**
     * Reporta una incidencia en el equipo actual
     */
    public String reportarIncidencia(int matricula, String descripcion) {
        Optional<Registro> registroActivo = registroGuardado.buscarRegistroActivo(matricula);

        if (registroActivo.isEmpty()) {
            return "No tienes una sesión activa para reportar incidencia.";
        }

        boolean exito = registroGuardado.reportarIncidencia(registroActivo.get().getId(), descripcion);
        if (exito) {
            return "Incidencia reportada correctamente";
        } else {
            return "Error al reportar la incidencia.";
        }
    }

    // ==================== CONSULTAS ====================

    /**
     * Verifica si un usuario tiene una sesión activa
     */
    public boolean tieneSesionActiva(int matricula) {
        return registroGuardado.tieneSesionActiva(matricula);
    }

    /**
     * Verifica si un equipo tiene una sesión activa
     */
    public boolean equipoTieneSesionActiva(Long equipoId) {
        return registroGuardado.existeSesionActivaParaEquipo(equipoId);
    }

    /**
     * Obtiene el registro activo de un usuario
     */
    public Optional<Registro> getRegistroActivo(int matricula) {
        return registroGuardado.buscarRegistroActivo(matricula);
    }

    /**
     * Lista todos los registros
     */
    public List<Registro> listarTodos() {
        return registroGuardado.listarTodos();
    }

    /**
     * Lista los registros de un usuario específico
     */
    public List<Registro> listarPorUsuario(int matricula) {
        return registroGuardado.listarPorUsuario(matricula);
    }

    /**
     * Busca un registro por su ID
     */
    public Optional<Registro> buscarPorId(Long id) {
        return registroGuardado.buscarPorId(id);
    }

    /**
     * Obtiene el historial de registros de un usuario
     */
    public List<Registro> getHistorial(int matricula) {
        return registroGuardado.listarPorUsuario(matricula);
    }
}