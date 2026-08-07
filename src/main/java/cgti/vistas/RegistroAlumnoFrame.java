package cgti.vistas;

import cgti.clase.*;
import cgti.funcion.CatalogoFuncion;
import cgti.funcion.UsuarioFuncion;
import javax.swing.*;
import javax.swing.text.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

/**
 * Ventana de registro de alumno.
 * Permite completar datos personales y académicos para nuevos alumnos.
 * 
 * Atajos de teclado:
 * - Enter: Avanzar al siguiente campo / Guardar si es el último
 * - Escape: Regresar a la ventana anterior
 */
public class RegistroAlumnoFrame extends JFrame {

    // ==================== CONSTANTES ====================
    private static final int VENTANA_ANCHO = 450;
    private static final int VENTANA_ALTO = 560;
    private static final int PADDING = 40;

    // ==================== SERVICIOS ====================
    private UsuarioFuncion funcion = new UsuarioFuncion();
    private CatalogoFuncion catalogoFuncion = new CatalogoFuncion();

    // ==================== COMPONENTES ====================
    private JLabel lblMensaje;
    private JComboBox<String> cmbArea, cmbCarrera, cmbCuatrimestre;
    private JTextField txtNombre, txtPaterno, txtMaterno;
    private JTextField txtFecha, txtTelefono, txtGrupo;
    private JButton btnGuardar, btnRegresar;
    private int matriculaAlumno; // Guardar matrícula para usar en eventos

    // ==================== CONSTRUCTOR ====================
    public RegistroAlumnoFrame(int matricula) {
        this.matriculaAlumno = matricula; // Guardar referencia
        configurarVentana();
        JPanel panel = crearPanel(matricula);
        add(panel);
        setSize(VENTANA_ANCHO, VENTANA_ALTO);
        setLocationRelativeTo(null);
        configurarAtajosTeclado(panel); // Configurar teclas
    }

    // ==================== CONFIGURACIÓN ====================
    private void configurarVentana() {
        setTitle("Registro Alumno - CGTI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
    }

    /**
     * Configura los atajos de teclado para toda la ventana.
     * Enter: navegar entre campos o guardar
     * Escape: regresar
     */
    private void configurarAtajosTeclado(JPanel panel) {
        // Escape: regresar a la ventana anterior
        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "regresar");
        panel.getActionMap().put("regresar", new AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                dispose();
                new AlumnoAccesoFrame().setVisible(true);
            }
        });

        // Enter en campos de texto: navegar al siguiente campo
        configurarNavegacionEnter();
    }

    // ==================== INTERFAZ GRÁFICA ====================
    private JPanel crearPanel(int matricula) {
        JPanel panel = new JPanel(null);
        panel.setBackground(Estilos.COLOR_BLANCO);

        // Títulos
        agregarTitulo(panel, "Registro de Alumno", Estilos.TITULO, Estilos.COLOR_NEGRO, 100, 15, 250, 30);
        agregarTitulo(panel, "Matrícula: " + matricula, Estilos.NOMBRE, Estilos.COLOR_GRIS, 100, 50, 250, 20);

        // Campos personales
        txtNombre = agregarCampo(panel, "Nombres:", PADDING, 90, 370);
        txtPaterno = agregarCampo(panel, "Apellido Paterno:", PADDING, 145, 175);
        txtMaterno = agregarCampo(panel, "Apellido Materno:", 235, 145, 175);
        txtFecha = agregarCampo(panel, "F. Nacimiento (AAAA-MM-DD):", PADDING, 200, 175);
        aplicarMascaraFecha(txtFecha);
        txtTelefono = agregarCampo(panel, "Teléfono (opcional):", 235, 200, 175);

        // Selectores
        cmbArea = agregarCombo(panel, "Área:", PADDING, 255, 370, catalogoFuncion.listarAreas());
        cmbCarrera = agregarCombo(panel, "Carrera:", PADDING, 310, 370, catalogoFuncion.listarCarreras());
        cmbCuatrimestre = agregarCombo(panel, "Cuatrimestre:", PADDING, 365, 175,
                catalogoFuncion.listarCuatrimestres());
        txtGrupo = agregarCampo(panel, "Grupo:", 235, 365, 175);

        // Evento filtrar carreras
        cmbArea.addActionListener(e -> actualizarCarreras((String) cmbArea.getSelectedItem()));

        // Mensaje
        lblMensaje = new JLabel(" ", SwingConstants.CENTER);
        lblMensaje.setFont(new Font("Arial", Font.PLAIN, 12));
        lblMensaje.setBounds(PADDING, 420, 370, 25);
        panel.add(lblMensaje);

        // Botones
        btnGuardar = crearBoton("Registrarme", Estilos.COLOR_VERDE, 250, 460);
        btnGuardar.addActionListener(e -> guardarAlumno(matricula));

        btnRegresar = crearBoton("Regresar", Estilos.COLOR_AZUL, 60, 460);
        btnRegresar.addActionListener(e -> {
            dispose();
            new AlumnoAccesoFrame().setVisible(true);
        });

        panel.add(btnGuardar);
        panel.add(btnRegresar);

        return panel;
    }

    /**
     * Configura la navegación con Enter entre campos.
     * Al llegar al último campo, ejecuta el botón Guardar.
     */
    private void configurarNavegacionEnter() {
        // Orden de navegación: Nombre -> Paterno -> Materno -> Fecha -> Teléfono ->
        // Grupo -> Guardar
        JTextField[] campos = { txtNombre, txtPaterno, txtMaterno, txtFecha, txtTelefono, txtGrupo };

        for (int i = 0; i < campos.length; i++) {
            final int index = i;
            campos[i].addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        e.consume(); // Evitar sonido de beep
                        if (index < campos.length - 1) {
                            campos[index + 1].requestFocus(); // Siguiente campo
                        } else {
                            btnGuardar.doClick(); // Último campo: guardar
                        }
                    }
                }
            });
        }

        // Enter en Combos: avanzar al siguiente
        cmbArea.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    e.consume();
                    cmbCarrera.requestFocus();
                }
            }
        });

        cmbCarrera.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    e.consume();
                    cmbCuatrimestre.requestFocus();
                }
            }
        });

        cmbCuatrimestre.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    e.consume();
                    txtGrupo.requestFocus();
                }
            }
        });
    }

    // ==================== MÉTODOS DE CREACIÓN DE COMPONENTES ====================
    private void agregarTitulo(JPanel panel, String texto, Font fuente, Color color, int x, int y, int ancho,
            int alto) {
        JLabel label = new JLabel(texto, SwingConstants.CENTER);
        label.setFont(fuente);
        label.setForeground(color);
        label.setBounds(x, y, ancho, alto);
        panel.add(label);
    }

    private JTextField agregarCampo(JPanel panel, String etiqueta, int x, int y, int ancho) {
        JLabel lbl = new JLabel(etiqueta);
        lbl.setForeground(Estilos.COLOR_NEGRO);
        lbl.setFont(new Font("Arial", Font.PLAIN, 13));
        lbl.setBounds(x, y, 200, 20);
        panel.add(lbl);

        JTextField campo = new JTextField();
        campo.setBounds(x, y + 25, ancho, 28);
        campo.setFont(new Font("Arial", Font.PLAIN, 13));
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilos.COLOR_BORDE, 1, true),
                BorderFactory.createEmptyBorder(2, 8, 2, 8)));
        panel.add(campo);
        return campo;
    }

    private JComboBox<String> agregarCombo(JPanel panel, String etiqueta, int x, int y, int ancho, List<String> items) {
        JLabel lbl = new JLabel(etiqueta);
        lbl.setForeground(Estilos.COLOR_NEGRO);
        lbl.setFont(new Font("Arial", Font.PLAIN, 13));
        lbl.setBounds(x, y, 200, 20);
        panel.add(lbl);

        JComboBox<String> combo = new JComboBox<>();
        combo.addItem("-- Seleccionar --");
        for (String item : items)
            combo.addItem(item);
        combo.setBounds(x, y + 25, ancho, 28);
        combo.setFont(new Font("Arial", Font.PLAIN, 13));
        panel.add(combo);
        return combo;
    }

    private JButton crearBoton(String texto, Color color, int x, int y) {
        JButton btn = new JButton(texto);
        btn.setBounds(x, y, 140, 38);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        Estilos.estilizarBotonRedondo(btn, color, Color.WHITE, 20);
        return btn;
    }

    // ==================== LÓGICA DE NEGOCIO ====================
    private void actualizarCarreras(String area) {
        cmbCarrera.removeAllItems();
        cmbCarrera.addItem("-- Seleccionar --");
        List<String> carreras = area.equals("-- Seleccionar --") ? catalogoFuncion.listarCarreras()
                : catalogoFuncion.listarCarrerasPorArea(area);
        for (String c : carreras)
            cmbCarrera.addItem(c);
    }

    private void guardarAlumno(int matricula) {
        if (!validarCampos())
            return;

        try {
            Alumno alumno = new Alumno();
            alumno.setMatricula(matricula);
            alumno.setNombre(txtNombre.getText().trim());
            alumno.setApellidoPaterno(txtPaterno.getText().trim());
            alumno.setApellidoMaterno(txtMaterno.getText().trim());
            alumno.setFechaNacimiento(LocalDate.parse(txtFecha.getText().trim()));
            alumno.setTelefono(txtTelefono.getText().trim());
            alumno.setCarrera((String) cmbCarrera.getSelectedItem());
            alumno.setCuatrimestre((String) cmbCuatrimestre.getSelectedItem());
            alumno.setGrupo(txtGrupo.getText().trim());
            alumno.setRol(Rol.ALUMNO);

            String resultado = funcion.registrar(alumno);
            boolean exito = resultado.contains("correctamente");
            mostrarMensaje(resultado, exito);

            if (exito) {
                Timer timer = new Timer(800, e -> {
                    dispose();
                    funcion.buscarPorMatricula(matricula)
                            .ifPresent(u -> new DashboardFrame(u).setVisible(true));
                });
                timer.setRepeats(false);
                timer.start();
            }
        } catch (Exception ex) {
            mostrarMensaje("Error: " + ex.getMessage(), false);
        }
    }

    private boolean validarCampos() {
        if (txtNombre.getText().trim().isEmpty() ||
                txtPaterno.getText().trim().isEmpty() ||
                txtFecha.getText().trim().isEmpty() ||
                txtGrupo.getText().trim().isEmpty()) {
            mostrarMensaje("Completa todos los campos obligatorios", false);
            return false;
        }

        if (((String) cmbArea.getSelectedItem()).equals("-- Seleccionar --") ||
                ((String) cmbCarrera.getSelectedItem()).equals("-- Seleccionar --")) {
            mostrarMensaje("Selecciona área y carrera", false);
            return false;
        }

        if (!esFechaValida(txtFecha.getText().trim()))
            return false;

        String tel = txtTelefono.getText().trim();
        if (!tel.isEmpty() && !tel.matches("\\d{10}")) {
            mostrarMensaje("Teléfono: 10 dígitos numéricos", false);
            return false;
        }

        return true;
    }

    private boolean esFechaValida(String fecha) {
        try {
            LocalDate f = LocalDate.parse(fecha);
            LocalDate hoy = LocalDate.now();
            if (f.isAfter(hoy)) {
                mostrarMensaje("Fecha no puede ser futura", false);
                return false;
            }
            if (f.isBefore(hoy.minusYears(70))) {
                mostrarMensaje("Edad máxima: 70 años", false);
                return false;
            }
            if (f.isAfter(hoy.minusYears(18))) {
                mostrarMensaje("Debes ser mayor de 18 años", false);
                return false;
            }
            return true;
        } catch (DateTimeParseException e) {
            mostrarMensaje("Formato inválido: AAAA-MM-DD", false);
            return false;
        }
    }

    private void aplicarMascaraFecha(JTextField campo) {
        ((AbstractDocument) campo.getDocument()).setDocumentFilter(new DocumentFilter() {
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                String digits = (fb.getDocument().getText(0, fb.getDocument().getLength()) + text)
                        .replaceAll("[^0-9]", "");
                if (digits.length() > 8)
                    return;
                StringBuilder fmt = new StringBuilder();
                for (int i = 0; i < digits.length(); i++) {
                    if (i == 4 || i == 6)
                        fmt.append("-");
                    fmt.append(digits.charAt(i));
                }
                super.replace(fb, 0, fb.getDocument().getLength(), fmt.toString(), attrs);
            }
        });
    }

    private void mostrarMensaje(String texto, boolean exito) {
        lblMensaje.setForeground(exito ? Estilos.COLOR_EXITO : Estilos.COLOR_ERROR);
        lblMensaje.setText(texto);
    }
}