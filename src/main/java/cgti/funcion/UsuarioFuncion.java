package cgti.funcion;

import cgti.clase.Rol;
import cgti.clase.Usuario;
import cgti.guardado.UsuarioGuardado;
import java.util.List;
import java.util.Optional;

/**
 * Lógica de negocio para gestión de usuarios.
 * Maneja registro, búsqueda, eliminación y control de roles.
 */
public class UsuarioFuncion {

    private UsuarioGuardado guardar = new UsuarioGuardado(); // Capa de persistencia

    /**
     * Verifica si un rol puede ser asignado por el creador.
     * Solo SUPERADMINISTRADOR puede crear otros roles.
     */
    public boolean puedeAsignarRol(Rol creador, Rol rolNuevo) {
        return creador == Rol.SUPERADMINISTRADOR && rolNuevo != Rol.SUPERADMINISTRADOR;
    }

    /**
     * Busca un usuario por su matrícula.
     * Retorna Optional vacío si no existe.
     */
    public Optional<Usuario> buscarPorMatricula(int matricula) {
        return guardar.buscarPorMatricula(matricula);
    }

    /**
     * Lista todos los usuarios registrados.
     */
    public List<Usuario> listarTodos() {
        return guardar.listarTodos();
    }

    /**
     * Registra un nuevo usuario.
     * Retorna mensaje de error si la matrícula ya existe.
     */
    public String registrar(Usuario usuario) {
        if (guardar.buscarPorMatricula(usuario.getMatricula()).isPresent()) {
            return "Ya existe un usuario con esta matrícula"; // Matrícula duplicada
        }
        guardar.guardar(usuario);
        return "Usuario registrado correctamente";
    }

    /**
     * Elimina un usuario por matrícula.
     * Protege al último ADMINISTRADOR y SUPERADMINISTRADOR.
     */
    public String eliminar(int matricula) {
        Optional<Usuario> usuario = guardar.buscarPorMatricula(matricula);

        if (usuario.isEmpty()) {
            return "Usuario no encontrado"; // No existe
        }

        Rol rol = usuario.get().getRol();

        // No permitir eliminar al último administrador
        if (rol == Rol.ADMINISTRADOR && contarPorRol(Rol.ADMINISTRADOR) <= 1) {
            return "No puedes eliminar al único administrador del sistema";
        }

        // No permitir eliminar al último superadministrador
        if (rol == Rol.SUPERADMINISTRADOR && contarPorRol(Rol.SUPERADMINISTRADOR) <= 1) {
            return "No puedes eliminar al único superadministrador del sistema";
        }

        guardar.eliminar(matricula);
        return "Usuario eliminado correctamente";
    }

    /**
     * Cuenta cuántos usuarios tienen un rol específico.
     */
    private long contarPorRol(Rol rol) {
        return guardar.listarTodos().stream()
                .filter(u -> u.getRol() == rol)
                .count();
    }
}