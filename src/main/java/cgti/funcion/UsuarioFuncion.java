package cgti.funcion;

import cgti.clase.Rol;
import cgti.clase.Usuario;
import cgti.guardado.UsuarioGuardado;
import java.util.List;
import java.util.Optional;

public class UsuarioFuncion {

    private UsuarioGuardado guardar = new UsuarioGuardado();

    public boolean puedeAsignarRol(Rol creador, Rol rolNuevo) {
        if (creador == Rol.SUPERADMINISTRADOR) {
            return rolNuevo == Rol.ADMINISTRADOR || rolNuevo == Rol.OBSERVADOR || rolNuevo == Rol.ALUMNO;
        }
        return false;
    }

    public Optional<Usuario> buscarPorMatricula(int matricula) {
        return guardar.buscarPorMatricula(matricula);
    }

    public List<Usuario> listarTodos() {
        return guardar.listarTodos();
    }

    public String registrar(Usuario usuario) {
        // Verificar si ya existe la matrícula
        if (guardar.buscarPorMatricula(usuario.getMatricula()).isPresent()) {
            return "Ya existe un usuario con esta matrícula";
        }

        guardar.guardar(usuario);
        return "Usuario registrado correctamente";
    }

    public String eliminar(int matricula) {
        Optional<Usuario> u = guardar.buscarPorMatricula(matricula);
        if (u.isEmpty()) {
            return "Usuario no encontrado";
        }
        if (u.get().getRol() == Rol.ADMINISTRADOR) {
            long totalAdmins = guardar.listarTodos().stream()
                    .filter(x -> x.getRol() == Rol.ADMINISTRADOR)
                    .count();
            if (totalAdmins <= 1) {
                return "No puedes eliminar al único administrador del sistema";
            }
        }
        if (u.get().getRol() == Rol.SUPERADMINISTRADOR) {
            long totalSuperAdmins = guardar.listarTodos().stream()
                    .filter(x -> x.getRol() == Rol.SUPERADMINISTRADOR)
                    .count();
            if (totalSuperAdmins <= 1) {
                return "No puedes eliminar al único superadministrador del sistema";
            }
        }
        guardar.eliminar(matricula);
        return "Usuario eliminado correctamente";
    }
}