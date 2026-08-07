package cgti.funcion;

import cgti.clase.Equipo;
import cgti.guardado.EquipoGuardado;
import java.util.List;

/**
 * Clase que contiene la lógica de negocio para la gestión de equipos
 * 
 * Responsabilidades:
 * - Guardar nuevos equipos (con validación de nombres duplicados)
 * - Actualizar equipos existentes
 * - Eliminar equipos (con validación de dependencias)
 * - Consultar equipos por laboratorio
 * - Verificar disponibilidad de equipos
 */
public class EquipoFuncion {

    // ==================== DEPENDENCIAS ====================

    // Capa de acceso a datos para equipos (base de datos)
    private EquipoGuardado guardar = new EquipoGuardado();

    // Para verificar si un equipo tiene sesiones activas (entrada sin salida)
    private RegistroFuncion registroFuncion;

    // ==================== CONSTRUCTORES ====================

    /**
     * Constructor básico
     * Se usa cuando no se necesita verificar disponibilidad
     */
    public EquipoFuncion() {
    }

    /**
     * Constructor con inyección de dependencia
     * Se usa cuando se necesita verificar disponibilidad de equipos
     * 
     * @param registroFuncion Servicio para consultar sesiones activas
     */
    public EquipoFuncion(RegistroFuncion registroFuncion) {
        this.registroFuncion = registroFuncion;
    }

    // ==================== OPERACIONES CRUD ====================
    // CRUD = Create, Read, Update, Delete

    /**
     * Guarda un equipo NUEVO en la base de datos
     * 
     * Validaciones:
     * - No puede existir otro equipo con el mismo nombre en el mismo laboratorio
     * 
     * @param equipo Equipo a guardar (sin ID, se genera automáticamente)
     * @return null si se guardó correctamente, mensaje de error si falló
     */
    public String guardar(Equipo equipo) {
        // Validar nombre duplicado en el mismo laboratorio
        if (guardar.existeNombreEnLaboratorio(
                equipo.getNombreEqp(), // Nombre a verificar
                equipo.getLaboratorio(), // Laboratorio donde se busca
                null)) { // null = no excluir ningún ID (es nuevo)

            return "Ya existe un equipo llamado \"" + equipo.getNombreEqp()
                    + "\" en " + equipo.getLaboratorio()
                    + ". Usa otro nombre.";
        }

        // Si pasa la validación, guardar
        guardar.guardar(equipo);
        return null; // Éxito
    }

    /**
     * Actualiza un equipo EXISTENTE en la base de datos
     * 
     * Validaciones:
     * - No puede existir OTRO equipo con el mismo nombre en el mismo laboratorio
     * (se excluye a sí mismo de la búsqueda)
     * 
     * @param equipo Equipo con los datos actualizados (debe tener ID)
     * @return null si se actualizó correctamente, mensaje de error si falló
     */
    public String actualizar(Equipo equipo) {
        // Validar nombre duplicado, excluyendo el equipo actual
        if (guardar.existeNombreEnLaboratorio(
                equipo.getNombreEqp(), // Nombre a verificar
                equipo.getLaboratorio(), // Laboratorio donde se busca
                equipo.getId())) { // Excluir este ID de la búsqueda

            return "Ya existe un equipo llamado \"" + equipo.getNombreEqp()
                    + "\" en " + equipo.getLaboratorio()
                    + ". Usa otro nombre.";
        }

        // Si pasa la validación, actualizar
        guardar.actualizar(equipo);
        return null; // Éxito
    }

    /**
     * Elimina un equipo de la base de datos
     * 
     * Validaciones:
     * - El equipo no debe tener sesiones activas (entrada sin salida)
     * - El equipo no debe tener reservas activas o pendientes
     * 
     * @param id ID del equipo a eliminar
     * @return null si se eliminó correctamente, mensaje de error si falló
     */
    public String eliminar(Long id) {
        // Validación 1: Verificar que el equipo existe
        if (!guardar.existePorId(id)) {
            return "El equipo con ID " + id + " no existe.";
        }

        // Validación 2: Verificar que no tenga sesiones activas
        // (alguien está usando el equipo físicamente ahora)
        if (registroFuncion != null && registroFuncion.equipoTieneSesionActiva(id)) {
            return "No se puede eliminar el equipo porque tiene una sesión activa.\n"
                    + "Espera a que el usuario registre su salida.";
        }

        // Validación 3: Verificar que no tenga reservas activas o pendientes
        // (alguien reservó el equipo para más tarde)
        if (guardar.tieneReservasActivas(id)) {
            return "No se puede eliminar el equipo porque tiene reservas activas o pendientes.\n"
                    + "Cancela las reservas primero.";
        }

        // Si pasa todas las validaciones, eliminar
        guardar.eliminar(id);
        return null; // Éxito
    }

    // ==================== CONSULTAS ====================

    /**
     * Lista todos los equipos de un laboratorio específico
     * 
     * @param laboratorio Nombre del laboratorio (ej: "LABC", "LABD")
     * @return Lista de equipos en ese laboratorio
     */
    public List<Equipo> listarPorLaboratorio(String laboratorio) {
        return guardar.listarPorLaboratorio(laboratorio);
    }

    /**
     * Lista TODOS los equipos registrados en el sistema
     * 
     * @return Lista completa de equipos
     */
    public List<Equipo> listarTodos() {
        return guardar.listarTodos();
    }

    // ==================== VERIFICACIÓN DE DISPONIBILIDAD ====================

    /**
     * Verifica si un equipo está disponible para uso
     * 
     * Un equipo NO está disponible si:
     * - Alguien tiene una sesión física activa (entrada sin salida)
     * 
     * Ventaja de este método:
     * - Usa una consulta puntual e indexada (rápida)
     * - No carga todo el historial de registros (eficiente)
     * 
     * @param equipoId ID del equipo a verificar
     * @return true si está disponible, false si está ocupado
     */
    public boolean estaDisponible(Long equipoId) {
        // Si no hay registroFuncion, no podemos verificar sesiones
        // Asumimos que está disponible (modo sin conexión)
        if (registroFuncion == null) {
            return true;
        }

        // Si el equipo NO tiene sesión activa, está disponible
        return !registroFuncion.equipoTieneSesionActiva(equipoId);
    }

    /**
     * Verifica si un equipo está disponible (sobrecarga para objeto Equipo)
     * 
     * @param equipo Equipo a verificar
     * @return true si está disponible, false si está ocupado
     */
    public boolean estaDisponible(Equipo equipo) {
        if (equipo == null || equipo.getId() == null) {
            return false; // Equipo inválido, asumimos no disponible
        }
        return estaDisponible(equipo.getId());
    }
}