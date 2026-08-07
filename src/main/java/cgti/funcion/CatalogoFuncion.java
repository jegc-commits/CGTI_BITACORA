package cgti.funcion;

import cgti.guardado.CatalogoGuardado;
import java.util.List;

public class CatalogoFuncion {

    // Capa de acceso a datos para catalogos
    private CatalogoGuardado guardado = new CatalogoGuardado();

    // ==================== AREAS ====================

    /**
     * Obtiene la lista de todas las areas academicas disponibles.
     * Los datos provienen de la base de datos (tabla: dbo.area).
     * 
     * @return Lista de nombres de areas (ej: "Tecnologias de la Informacion")
     */
    public List<String> listarAreas() {
        return guardado.listarAreas();
    }

    // ==================== CARRERAS ====================

    /**
     * Obtiene la lista de todas las carreras disponibles (sin filtro).
     * Los datos provienen de la base de datos (tabla: dbo.carreras).
     * 
     * @return Lista de nombres de carreras
     */
    public List<String> listarCarreras() {
        return guardado.listarCarreras();
    }

    /**
     * Obtiene la lista de carreras filtradas por area academica.
     * Util cuando el usuario selecciona un area y necesita ver
     * solo las carreras de esa area.
     * 
     * @param area Nombre del area (ej: "Tecnologias de la Informacion")
     * @return Lista de carreras que pertenecen a esa area
     */
    public List<String> listarCarrerasPorArea(String area) {
        return guardado.listarCarrerasPorArea(area);
    }

    // ==================== CUATRIMESTRES ====================

    /**
     * Obtiene la lista de todos los cuatrimestres disponibles.
     * Los datos provienen de la base de datos (tabla: dbo.cuatrimestres).
     * 
     * @return Lista de cuatrimestres (ej: "1 cuatrimestre", "2 cuatrimestre")
     */
    public List<String> listarCuatrimestres() {
        return guardado.listarCuatrimestres();
    }

    // ==================== GRUPOS ====================

    /**
     * Obtiene la lista de todos los grupos disponibles.
     * Los datos provienen de la base de datos (tabla: dbo.usuarios).
     * 
     * @return Lista de grupos (ej: "A", "B", "C", "GTID343")
     */
    public List<String> listarlaboratorio() {
        return guardado.listarLaboratorios();
    }

    public List<String> listarSoftware() {
        return guardado.listarSoftware();
    }

}