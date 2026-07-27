package cgti.funcion;

import cgti.clase.Equipo;
import cgti.guardado.EquipoGuardado;
import java.util.List;

public class EquipoFuncion {

    private EquipoGuardado guardar = new EquipoGuardado();
    private RegistroFuncion registroFuncion;

    public EquipoFuncion() {
    }

    public EquipoFuncion(RegistroFuncion registroFuncion) {
        this.registroFuncion = registroFuncion;
    }

    public void guardar(Equipo equipo) {
        guardar.guardar(equipo);
    }

    public List<Equipo> listarPorLaboratorio(String laboratorio) {
        return guardar.listarPorLaboratorio(laboratorio);
    }

    public List<Equipo> listarTodos() {
        return guardar.listarTodos();
    }

    // Disponibilidad del equipo: solo mira si alguien ya tiene una sesion fisica
    // (entrada sin salida) abierta con este equipo.
    public boolean estaDisponible(Long equipoId) {
        if (registroFuncion == null) {
            return true; // Si no hay registroFuncion, asumimos disponible
        }

        return registroFuncion.listarTodos()
                .stream()
                .filter(r -> r.getHoraSalida() == null) // Solo registros activos
                .noneMatch(r -> r.getEquipoId() != null && r.getEquipoId().equals(equipoId));
    }

    public void actualizar(Equipo equipo) {
        guardar.actualizar(equipo);
    }
}
