package cgti.vistas;

import cgti.clase.Administrador;
import cgti.clase.Alumno;
import cgti.clase.Rol;
import cgti.clase.Usuario;
import cgti.funcion.CatalogoFuncion;
import cgti.funcion.UsuarioFuncion;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Ventana de Registro de Nuevos Usuarios - CGTI
 * 
 * Permite crear cualquier tipo de usuario:
 * - ALUMNO: con area, carrera, cuatrimestre y grupo
 * - ADMINISTRADOR: con matricula
 * - SUPERADMINISTRADOR: con matricula
 * - OBSERVADOR: con matricula
 * 
 * El area filtra las carreras disponibles automaticamente.
 */
public class RegistroUsuarioFrame extends JFrame {

    // ==================== SERVICIOS ====================
    private UsuarioFuncion funcion = new UsuarioFuncion();
    private CatalogoFuncion catalogoFuncion = new CatalogoFuncion();

    // ==================== COMPONENTES ====================
    private JLabel lblMensaje;
    private JComboBox<String> cmbArea;
    private JComboBox<String> cmbCarrera;

    // ==================== CONSTRUCTOR ====================

    /**
     * Constructor de la ventana de registro de usuarios.
     * 
     * @param admin Usuario administrador que crea el nuevo usuario.
     *              Su rol determina que roles puede asignar.
     */
    public RegistroUsuarioFrame(Usuario admin) {
        setTitle("Nuevo Usuario - CGTI");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Estilos.bloquearCierreConX(this, "Termina tu registro");

        JPanel panel = new JPanel();
        panel.setBackground(Estilos.COLOR_BLANCO);
        panel.setLayout(null);

        // ===== TITULO =====
        JLabel titulo = new JLabel("Nuevo Usuario", SwingConstants.CENTER);
        titulo.setFont(Estilos.TITULO);
        titulo.setForeground(Estilos.COLOR_NEGRO);
        titulo.setBounds(100, 15, 250, 30);

        // ===== NOMBRES =====
        JLabel lblNombre = new JLabel("Nombres:");
        lblNombre.setForeground(Estilos.COLOR_NEGRO);
        lblNombre.setBounds(40, 60, 150, 25);
        JTextField txtNombre = new JTextField();
        txtNombre.setBounds(40, 85, 370, 25);

        JLabel lblPaterno = new JLabel("Apellido Paterno:");
        lblPaterno.setForeground(Estilos.COLOR_NEGRO);
        lblPaterno.setBounds(40, 115, 150, 25);
        JTextField txtPaterno = new JTextField();
        txtPaterno.setBounds(40, 140, 175, 25);

        JLabel lblMaterno = new JLabel("Apellido Materno:");
        lblMaterno.setForeground(Estilos.COLOR_NEGRO);
        lblMaterno.setBounds(235, 115, 150, 25);
        JTextField txtMaterno = new JTextField();
        txtMaterno.setBounds(235, 140, 175, 25);

        // ===== FECHA DE NACIMIENTO =====
        JLabel lblFecha = new JLabel("F. Nacimiento (AAAA-MM-DD):");
        lblFecha.setForeground(Estilos.COLOR_NEGRO);
        lblFecha.setBounds(40, 170, 250, 25);
        JTextField txtFecha = new JTextField();
        txtFecha.setToolTipText("AAAA-MM-DD");
        txtFecha.setBounds(40, 195, 175, 25);

        // Mascara de fecha
        ((AbstractDocument) txtFecha.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                String newText = currentText.substring(0, offset) + text + currentText.substring(offset + length);
                String digits = newText.replaceAll("-", "");

                if (digits.length() > 8 || !digits.matches("\\d*")) {
                    return;
                }

                StringBuilder formatted = new StringBuilder();
                for (int i = 0; i < digits.length(); i++) {
                    if (i == 4 || i == 6)
                        formatted.append("-");
                    formatted.append(digits.charAt(i));
                }
                super.replace(fb, 0, fb.getDocument().getLength(), formatted.toString(), attrs);
            }
        });

        // ===== TELEFONO =====
        JLabel lblTelefono = new JLabel("Telefono (opcional):");
        lblTelefono.setForeground(Estilos.COLOR_NEGRO);
        lblTelefono.setBounds(235, 170, 150, 25);
        JTextField txtTelefono = new JTextField();
        txtTelefono.setBounds(235, 195, 175, 25);

        // ===== MATRICULA (para todos los roles) =====
        JLabel lblMatricula = new JLabel("Matricula:");
        lblMatricula.setForeground(Estilos.COLOR_NEGRO);
        lblMatricula.setBounds(40, 225, 150, 25);
        JTextField txtMatricula = new JTextField();
        txtMatricula.setBounds(40, 250, 175, 25);

        // Solo digitos en matricula
        ((AbstractDocument) txtMatricula.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                String newText = currentText.substring(0, offset) + text + currentText.substring(offset + length);
                if (!newText.matches("\\d*")) {
                    return;
                }
                super.replace(fb, offset, length, text, attrs);
            }
        });

        // ===== ROL =====
        JLabel lblRol = new JLabel("Rol:");
        lblRol.setForeground(Estilos.COLOR_NEGRO);
        lblRol.setBounds(235, 225, 150, 25);

        // Obtener roles que el admin actual PUEDE asignar
        List<Rol> rolesPermitidos = new java.util.ArrayList<>();
        for (Rol r : Rol.values()) {
            if (funcion.puedeAsignarRol(admin.getRol(), r)) {
                rolesPermitidos.add(r);
            }
        }
        JComboBox<Rol> cmbRol = new JComboBox<>(rolesPermitidos.toArray(new Rol[0]));
        cmbRol.setBounds(235, 250, 175, 25);

        // ===== AREA (filtra carreras) =====
        JLabel lblArea = new JLabel("Area:");
        lblArea.setForeground(Estilos.COLOR_NEGRO);
        lblArea.setBounds(40, 285, 150, 25);
        cmbArea = new JComboBox<>();
        cmbArea.setBounds(40, 310, 370, 25);
        cmbArea.addItem("-- Selecciona un area --");
        for (String area : catalogoFuncion.listarAreas()) {
            cmbArea.addItem(area);
        }

        // ===== CARRERA (se filtra por area) =====
        JLabel lblCarrera = new JLabel("Carrera:");
        lblCarrera.setForeground(Estilos.COLOR_NEGRO);
        lblCarrera.setBounds(40, 340, 150, 25);
        cmbCarrera = new JComboBox<>();
        cmbCarrera.setBounds(40, 365, 370, 25);
        cmbCarrera.addItem("-- Selecciona una carrera --");
        // Cargar todas las carreras inicialmente
        for (String carrera : catalogoFuncion.listarCarreras()) {
            cmbCarrera.addItem(carrera);
        }

        // Evento: al cambiar area, filtrar carreras
        cmbArea.addActionListener(e -> {
            String areaSeleccionada = (String) cmbArea.getSelectedItem();
            if (areaSeleccionada != null && !areaSeleccionada.equals("-- Selecciona un area --")) {
                cmbCarrera.removeAllItems();
                cmbCarrera.addItem("-- Selecciona una carrera --");
                for (String carrera : catalogoFuncion.listarCarrerasPorArea(areaSeleccionada)) {
                    cmbCarrera.addItem(carrera);
                }
            } else {
                cmbCarrera.removeAllItems();
                cmbCarrera.addItem("-- Selecciona una carrera --");
                for (String carrera : catalogoFuncion.listarCarreras()) {
                    cmbCarrera.addItem(carrera);
                }
            }
        });

        // ===== CUATRIMESTRE Y GRUPO (solo visible para ALUMNO) =====
        JLabel lblCuatrimestre = new JLabel("Cuatrimestre:");
        lblCuatrimestre.setForeground(Estilos.COLOR_NEGRO);
        lblCuatrimestre.setBounds(40, 395, 150, 25);
        JComboBox<String> cmbCuatrimestre = new JComboBox<>();
        cmbCuatrimestre.setBounds(40, 420, 175, 25);
        for (String cuatrimestre : catalogoFuncion.listarCuatrimestres()) {
            cmbCuatrimestre.addItem(cuatrimestre);
        }

        JLabel lblGrupo = new JLabel("Grupo:");
        lblGrupo.setForeground(Estilos.COLOR_NEGRO);
        lblGrupo.setBounds(235, 395, 150, 25);
        JTextField txtGrupoAlumno = new JTextField();
        txtGrupoAlumno.setBounds(235, 420, 175, 25);

        // Componentes que solo se muestran para ALUMNO
        JComponent[] camposAlumno = {
                lblArea, cmbArea,
                lblCarrera, cmbCarrera,
                lblCuatrimestre, cmbCuatrimestre,
                lblGrupo, txtGrupoAlumno
        };

        // Mostrar/ocultar campos segun el rol seleccionado
        Runnable actualizarVisibilidadAlumno = () -> {
            boolean esAlumno = cmbRol.getSelectedItem() == Rol.ALUMNO;
            for (JComponent c : camposAlumno) {
                c.setVisible(esAlumno);
            }
            // Ajustar posicion del mensaje y botones segun visibilidad
            if (esAlumno) {
                lblMensaje.setBounds(40, 460, 370, 25);
            } else {
                lblMensaje.setBounds(40, 310, 370, 25);
            }
            panel.revalidate();
            panel.repaint();
        };
        cmbRol.addActionListener(e -> actualizarVisibilidadAlumno.run());

        // ===== MENSAJE =====
        lblMensaje = new JLabel(" ", SwingConstants.CENTER);
        lblMensaje.setForeground(Color.RED);
        lblMensaje.setBounds(40, 460, 370, 25);

        // ===== BOTONES =====
        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.setBounds(60, 500, 140, 35);
        Estilos.estilizarBotonRedondo(btnGuardar, Estilos.COLOR_VERDE, Estilos.COLOR_BLANCO, 20);

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBounds(250, 500, 140, 35);
        Estilos.estilizarBotonRedondo(btnCancelar, Estilos.COLOR_AZUL, Estilos.COLOR_BLANCO, 20);

        // ===== EVENTOS DE BOTONES =====
        btnGuardar.addActionListener(e -> {
            // Validar campos obligatorios
            if (txtNombre.getText().isEmpty() || txtPaterno.getText().isEmpty() ||
                    txtFecha.getText().isEmpty() || txtMatricula.getText().trim().isEmpty()) {
                mostrarError("Completa todos los campos obligatorios");
                return;
            }

            // Validar matricula
            int matricula;
            try {
                matricula = Integer.parseInt(txtMatricula.getText().trim());
            } catch (NumberFormatException ex) {
                mostrarError("La matricula debe ser un numero");
                return;
            }

            // Validar fecha
            if (!esFechaValida(txtFecha.getText().trim())) {
                return;
            }

            // Validar telefono
            String telefono = txtTelefono.getText().trim();
            if (!telefono.isEmpty()) {
                if (!telefono.matches("\\d+")) {
                    mostrarError("El telefono solo debe contener numeros");
                    return;
                }
                if (telefono.length() != 10) {
                    mostrarError("El telefono debe tener 10 digitos");
                    return;
                }
            }

            // Validar area y carrera si es ALUMNO
            Rol rolSel = (Rol) cmbRol.getSelectedItem();
            if (rolSel == Rol.ALUMNO) {
                String areaSeleccionada = (String) cmbArea.getSelectedItem();
                if (areaSeleccionada == null || areaSeleccionada.equals("-- Selecciona un area --")) {
                    mostrarError("Selecciona un area academica");
                    return;
                }
                String carreraSeleccionada = (String) cmbCarrera.getSelectedItem();
                if (carreraSeleccionada == null || carreraSeleccionada.equals("-- Selecciona una carrera --")) {
                    mostrarError("Selecciona una carrera");
                    return;
                }
                if (txtGrupoAlumno.getText().trim().isEmpty()) {
                    mostrarError("El grupo es obligatorio para alumnos");
                    return;
                }
            }

            try {
                Usuario nuevo;

                if (rolSel == Rol.ALUMNO) {
                    // Crear Alumno con todos sus campos
                    Alumno a = new Alumno();
                    a.setCarrera((String) cmbCarrera.getSelectedItem());
                    a.setCuatrimestre((String) cmbCuatrimestre.getSelectedItem());
                    a.setGrupo(txtGrupoAlumno.getText().trim());
                    nuevo = a;
                } else {
                    // Crear Administrador (puede ser ADMIN, SUPERADMIN u OBSERVADOR)
                    nuevo = new Administrador();
                }

                // Campos comunes para todos los roles
                nuevo.setMatricula(matricula);
                nuevo.setNombre(txtNombre.getText().trim());
                nuevo.setApellidoPaterno(txtPaterno.getText().trim());
                nuevo.setApellidoMaterno(txtMaterno.getText().trim());
                nuevo.setFechaNacimiento(LocalDate.parse(txtFecha.getText().trim()));
                nuevo.setTelefono(telefono);
                nuevo.setRol(rolSel);

                // Registrar usuario
                String resultado = funcion.registrar(nuevo);
                lblMensaje.setForeground(new Color(0, 128, 0));
                lblMensaje.setText(resultado);

                if (resultado.equals("Usuario registrado correctamente")) {
                    dispose();
                    new UsuarioFrame(admin).setVisible(true);
                }
            } catch (Exception ex) {
                mostrarError("Error: " + ex.getMessage());
            }
        });

        btnCancelar.addActionListener(e -> {
            dispose();
            new UsuarioFrame(admin).setVisible(true);
        });

        // ===== AGREGAR COMPONENTES AL PANEL =====
        panel.add(titulo);
        panel.add(lblNombre);
        panel.add(txtNombre);
        panel.add(lblPaterno);
        panel.add(txtPaterno);
        panel.add(lblMaterno);
        panel.add(txtMaterno);
        panel.add(lblFecha);
        panel.add(txtFecha);
        panel.add(lblTelefono);
        panel.add(txtTelefono);
        panel.add(lblMatricula);
        panel.add(txtMatricula);
        panel.add(lblRol);
        panel.add(cmbRol);

        // Agregar campos de alumno (area, carrera, cuatrimestre, grupo)
        for (JComponent c : camposAlumno) {
            panel.add(c);
        }

        panel.add(lblMensaje);
        panel.add(btnGuardar);
        panel.add(btnCancelar);

        // Estado inicial: ocultar campos de alumno si el primer rol no es ALUMNO
        actualizarVisibilidadAlumno.run();

        add(panel);

        // Tamano dinamico: mas grande si es alumno
        if (cmbRol.getSelectedItem() == Rol.ALUMNO) {
            panel.setPreferredSize(new Dimension(450, 570));
        } else {
            panel.setPreferredSize(new Dimension(450, 420));
        }

        pack();
        setLocationRelativeTo(null);
    }

    // ==================== METODOS AUXILIARES ====================

    /**
     * Valida que la fecha de nacimiento sea correcta.
     */
    private boolean esFechaValida(String fechaStr) {
        try {
            LocalDate fechaIngresada = LocalDate.parse(fechaStr);
            LocalDate hoy = LocalDate.now();
            LocalDate fechaMinima = hoy.minusYears(70);
            LocalDate fechaMaxima = hoy.minusYears(18);

            if (hoy.isBefore(fechaIngresada)) {
                mostrarError("La fecha no puede ser futura");
                return false;
            }
            if (fechaIngresada.isBefore(fechaMinima)) {
                mostrarError("La fecha no puede ser mayor a 70 años");
                return false;
            }
            if (fechaMaxima.isBefore(fechaIngresada)) {
                mostrarError("Debes ser mayor de 18 años");
                return false;
            }
            return true;
        } catch (DateTimeParseException e) {
            mostrarError("Formato de fecha invalido (AAAA-MM-DD)");
            return false;
        }
    }

    /**
     * Muestra un mensaje de error en rojo.
     */
    private void mostrarError(String texto) {
        lblMensaje.setForeground(Color.RED);
        lblMensaje.setText(texto);
    }
}