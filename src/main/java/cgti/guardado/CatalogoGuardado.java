package cgti.guardado;

import cgti.clase.Laboratorio;
import cgti.conexion.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase de acceso a datos para catalogos del sistema.
 * Consulta las tablas: area, carreras, cuatrimestres
 * 
 * Esta clase maneja todas las consultas relacionadas con
 * los catalogos del sistema (areas academicas, carreras,
 * cuatrimestres y grupos).
 */
public class CatalogoGuardado {

    public List<String> listarSoftware() {
        List<String> software = new ArrayList<>();
        String sql = "SELECT nombreSft FROM dbo.software ORDER BY nombreSft";

        try (Connection con = DatabaseConfig.conectar();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                software.add(rs.getString("nombreSft"));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar software: " + e.getMessage());
        }
        return software;
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
            e.printStackTrace();
        }
        return laboratorios;
    }
    // ==================== AREAS ====================

    /**
     * Obtiene todas las areas academicas desde la base de datos.
     * Tabla: dbo.area
     * 
     * @return Lista de nombres de areas (ej: "Tecnologias de la Informacion")
     */
    public List<String> listarAreas() {
        List<String> areas = new ArrayList<>();
        String sql = "SELECT nombre FROM dbo.area ORDER BY nombre";

        try (Connection con = DatabaseConfig.conectar();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                areas.add(rs.getString("nombre"));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar areas: " + e.getMessage());
        }

        // Si no hay datos en la BD, devolver lista por defecto
        if (areas.isEmpty()) {
            areas.add("Economico Administrativo");
            areas.add("Tecnologias de la Informacion");
            areas.add("Industrial, Electrica y Electronica");
        }

        return areas;
    }

    // ==================== CARRERAS ====================

    /**
     * Obtiene todas las carreras desde la base de datos.
     * Tabla: dbo.carreras
     * 
     * @return Lista de nombres de carreras
     */
    public List<String> listarCarreras() {
        List<String> carreras = new ArrayList<>();
        String sql = "SELECT nombre FROM dbo.carreras ORDER BY nombre";

        try (Connection con = DatabaseConfig.conectar();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                carreras.add(rs.getString("nombre"));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar carreras: " + e.getMessage());
        }

        // Si no hay datos en la BD, devolver lista por defecto
        if (carreras.isEmpty()) {
            carreras.add("TICS TSU en Desarrollo de software multiplataforma");
            carreras.add("TICS Ingenieria en Desarrollo y Gestion de Software");
            carreras.add("INDUSTRIAL Ingenieria en Mecatronica");
            carreras.add("ADMON Licenciatura en Contaduria");
            carreras.add("ADMON Licenciatura en Gestion del Capital Humano");
        }

        return carreras;
    }

    /**
     * Obtiene las carreras filtradas por area academica.
     * Hace un JOIN entre carreras y area para filtrar.
     * 
     * @param area Nombre del area (ej: "Tecnologias de la Informacion")
     *             Si es null o vacio, devuelve todas las carreras
     * @return Lista de carreras del area especificada
     */
    public List<String> listarCarrerasPorArea(String area) {
        List<String> carreras = new ArrayList<>();
        String sql;

        if (area == null || area.isEmpty() || area.equals("Todas las areas")) {
            // Sin filtro: todas las carreras
            sql = "SELECT c.nombre FROM dbo.carreras c ORDER BY c.nombre";
        } else {
            // Con filtro: solo carreras del area seleccionada
            sql = "SELECT c.nombre FROM dbo.carreras c " +
                    "INNER JOIN dbo.area a ON c.area_id = a.id " +
                    "WHERE a.nombre = ? " +
                    "ORDER BY c.nombre";
        }

        try (Connection con = DatabaseConfig.conectar();
                PreparedStatement ps = con.prepareStatement(sql)) {

            // Si hay filtro de area, establecer el parametro
            if (area != null && !area.isEmpty() && !area.equals("Todas las areas")) {
                ps.setString(1, area);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    carreras.add(rs.getString("nombre"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al listar carreras por area: " + e.getMessage());
        }

        return carreras;
    }

    // ==================== CUATRIMESTRES ====================

    /**
     * Obtiene todos los cuatrimestres desde la base de datos.
     * Tabla: dbo.cuatrimestres (CORREGIDO: antes decia dbo.cuatrimestre)
     * 
     * @return Lista de cuatrimestres (ej: "1 cuatrimestre", "2 cuatrimestre")
     */
    public List<String> listarCuatrimestres() {
        List<String> cuatrimestres = new ArrayList<>();
        // CORREGIDO: nombre de tabla correcto es 'cuatrimestres' (con 's' al final)
        String sql = "SELECT nombre FROM dbo.cuatrimestres ORDER BY id";

        try (Connection con = DatabaseConfig.conectar();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                cuatrimestres.add(rs.getString("nombre"));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar cuatrimestres: " + e.getMessage());
        }

        // Si no hay datos, devolver lista por defecto
        if (cuatrimestres.isEmpty()) {
            for (int i = 1; i <= 12; i++) {
                cuatrimestres.add(i + " cuatrimestre");
            }
        }

        return cuatrimestres;
    }

    // ==================== GRUPOS ====================

    /**
     * Obtiene todos los grupos registrados en el sistema.
     * Los grupos se obtienen de los usuarios existentes.
     * Tabla: dbo.usuarios
     * 
     * @return Lista de grupos (ej: "A", "B", "GTID343")
     */
    public List<String> listarGrupos() {
        List<String> grupos = new ArrayList<>();
        String sql = "SELECT DISTINCT grupo FROM dbo.usuarios WHERE grupo IS NOT NULL AND grupo != '' ORDER BY grupo";

        try (Connection con = DatabaseConfig.conectar();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String grupo = rs.getString("grupo");
                if (grupo != null && !grupo.isEmpty()) {
                    grupos.add(grupo);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al listar grupos: " + e.getMessage());
        }

        // Si no hay datos, devolver lista por defecto
        if (grupos.isEmpty()) {
            grupos.add("A");
            grupos.add("B");
            grupos.add("C");
        }

        return grupos;
    }
}