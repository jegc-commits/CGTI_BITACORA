package cgti.vistas;

import cgti.clase.*;
import cgti.funcion.UsuarioFuncion;
import javax.swing.*;
import javax.swing.text.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.awt.*;

public class RegistroAlumnoFrame extends JFrame {

    private UsuarioFuncion Funcion = new UsuarioFuncion();
    private JLabel lblMensaje;
    private JComboBox<String> cmbCuatrimestre;
    private JComboBox<String> cmbCarrera;

    public RegistroAlumnoFrame(int matricula) {
        setTitle("Registro Alumno - CGTI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setBackground(Estilos.COLOR_BLANCO);
        panel.setLayout(null); // Diseño absoluto para usar setBounds

        // Títulos principales
        JLabel titulo = new JLabel("Registro de Alumno", SwingConstants.CENTER);
        titulo.setFont(Estilos.TITULO);
        titulo.setForeground(Estilos.COLOR_NEGRO);
        titulo.setBounds(100, 15, 250, 30);

        JLabel lblInfo = new JLabel("Matrícula: " + matricula, SwingConstants.CENTER);
        lblInfo.setFont(Estilos.Nombre);
        lblInfo.setForeground(Estilos.COLOR_GRIS);
        lblInfo.setBounds(100, 50, 250, 20);

        // Campos de Texto y sus Etiquetas respectivas
        JLabel lblNombre = new JLabel("Nombres:");
        lblNombre.setForeground(Estilos.COLOR_NEGRO);
        lblNombre.setBounds(40, 90, 150, 25);
        JTextField txtNombre = new JTextField();
        txtNombre.setBounds(40, 115, 370, 25);

        JLabel lblPaterno = new JLabel("Apellido Paterno:");
        lblPaterno.setForeground(Estilos.COLOR_NEGRO);
        lblPaterno.setBounds(40, 145, 150, 25);
        JTextField txtPaterno = new JTextField();
        txtPaterno.setBounds(40, 170, 175, 25);

        JLabel lblMaterno = new JLabel("Apellido Materno:");
        lblMaterno.setForeground(Estilos.COLOR_NEGRO);
        lblMaterno.setBounds(235, 145, 150, 25);
        JTextField txtMaterno = new JTextField();
        txtMaterno.setBounds(235, 170, 175, 25);

        JLabel lblFecha = new JLabel("F.Nacimiento (AAAA-MM-DD):");
        lblFecha.setForeground(Estilos.COLOR_NEGRO);
        lblFecha.setBounds(40, 200, 250, 25); // Crear el JTextField
        JTextField txtFecha = new JTextField();
        txtFecha.setToolTipText("AAAA-MM-DD");
        txtFecha.setBounds(40, 225, 175, 25);

        // Aplicar DocumentFilter
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

        JLabel lblTelefono = new JLabel("Teléfono (opcional):");
        lblTelefono.setForeground(Estilos.COLOR_NEGRO);
        lblTelefono.setBounds(235, 200, 150, 25);
        JTextField txtTelefono = new JTextField();
        txtTelefono.setBounds(235, 225, 175, 25);

        JLabel lblCarrera = new JLabel("Carrera:");
        lblCarrera.setForeground(Estilos.COLOR_NEGRO);
        lblCarrera.setBounds(40, 245, 150, 25);
        cmbCarrera = new JComboBox<>();
        cmbCarrera.setBounds(40, 280, 370, 25);
        cmbCarrera.addItem(" TICS\n" + //
                "TSU en Desarrollo de Software Multiplataforma");
        cmbCarrera.addItem(" TICS\n" + //
                "TSU en Entornos Virtuales y Negocios Digitales ");
        cmbCarrera.addItem("TICS\n" + //
                "TSU en Infraestructura de Redes Digitales ");
        cmbCarrera.addItem(" MECA\n" + //
                "TSU en Instalaciones Eléctricas ");

        // Bloque de Cuatrimestre y Grupo
        JLabel lblCuatrimestre = new JLabel("Cuatrimestre:");
        lblCuatrimestre.setForeground(Estilos.COLOR_NEGRO);
        lblCuatrimestre.setBounds(40, 305, 150, 25);
        cmbCuatrimestre = new JComboBox<>();
        cmbCuatrimestre.setBounds(40, 340, 175, 25);
        cmbCuatrimestre.addItem("1 cuatrimestre");
        cmbCuatrimestre.addItem("2 cuatrimestre");
        cmbCuatrimestre.addItem("3 cuatrimestre");
        cmbCuatrimestre.addItem("4 cuatrimestre");

        JLabel lblGrupo = new JLabel("Grupo :");
        lblGrupo.setForeground(Estilos.COLOR_NEGRO);
        lblGrupo.setBounds(235, 305, 150, 25);
        JTextField txtGrupo = new JTextField();
        txtGrupo.setBounds(235, 340, 175, 25);

        // Área de avisos y alertas
        lblMensaje = new JLabel(" ", SwingConstants.CENTER);
        lblMensaje.setForeground(Color.RED);
        lblMensaje.setBounds(40, 420, 370, 25);

        // Configuración de botones alineados abajo
        JButton btnGuardar = new JButton("Registrarme");
        btnGuardar.setBounds(60, 465, 140, 35);
        Estilos.estilizarBotonRedondo(btnGuardar, Estilos.COLOR_VERDE, Estilos.COLOR_BLANCO, 20);

        JButton btnRegresar = new JButton("Regresar");
        btnRegresar.setBounds(250, 465, 140, 35);
        Estilos.estilizarBotonRedondo(btnRegresar, Estilos.COLOR_VERDE, Estilos.COLOR_BLANCO, 20);

        // --- EVENTOS DE LOS BOTONES ---
        btnGuardar.addActionListener(e -> {
            if (txtNombre.getText().isEmpty() || txtPaterno.getText().isEmpty() ||
                    txtGrupo.getText().isEmpty() ||
                    txtFecha.getText().isEmpty()) {
                lblMensaje.setForeground(Color.RED);
                lblMensaje.setText("Completa todos los campos obligatorios");
                return;
            }
            if (!esFechaValida(txtFecha.getText().trim())) {
                return;
            }
            String telefono = txtTelefono.getText().trim();
            if (!telefono.isEmpty()) {
                if (!telefono.matches("\\d+")) {
                    lblMensaje.setForeground(Color.RED);
                    lblMensaje.setText("El teléfono solo debe contener números");
                    return;
                }
                if (telefono.length() != 10) {
                    lblMensaje.setForeground(Color.RED);
                    lblMensaje.setText("El teléfono debe tener 10 dígitos");
                    return;
                }
            }
            try {
                Alumno alumno = new Alumno();
                alumno.setNombre(txtNombre.getText());
                alumno.setApellidoPaterno(txtPaterno.getText());
                alumno.setApellidoMaterno(txtMaterno.getText());
                alumno.setFechaNacimiento(LocalDate.parse(txtFecha.getText()));
                alumno.setTelefono(txtTelefono.getText());
                alumno.setMatricula(matricula);
                alumno.setCarrera((String) cmbCarrera.getSelectedItem());
                alumno.setCuatrimestre((String) cmbCuatrimestre.getSelectedItem());
                alumno.setGrupo(txtGrupo.getText());
                alumno.setRol(Rol.ALUMNO);

                String resultado = Funcion.registrar(alumno);
                lblMensaje.setForeground(Color.BLACK);
                lblMensaje.setText(resultado);

                if (resultado.equals("Usuario registrado correctamente")) {
                    dispose();
                    Funcion.buscarPorMatricula(matricula).ifPresent(u -> new DashboardFrame(u).setVisible(true));
                }
            } catch (Exception ex) {
                lblMensaje.setForeground(Color.RED);
                lblMensaje.setText("Error: " + ex.getMessage());
            }
        });

        btnRegresar.addActionListener(e -> {
            dispose();
            new AlumnoAccesoFrame().setVisible(true);
        });

        // Agregando todos los componentes al panel
        panel.add(titulo);
        panel.add(lblInfo);
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
        panel.add(lblCarrera);
        panel.add(cmbCarrera);
        panel.add(lblCuatrimestre);
        panel.add(cmbCuatrimestre);
        panel.add(lblGrupo);
        panel.add(txtGrupo);
        panel.add(lblMensaje);
        panel.add(btnGuardar);
        panel.add(btnRegresar);

        // Agregamos el panel principal al JFrame
        add(panel);

        // Definimos el tamaño preferido neto para el panel (liberando espacio para los
        // botones en Y=465)
        panel.setPreferredSize(new Dimension(450, 530));
        // Ajusta el marco del JFrame al tamaño calculado del panel
        pack();
        // Centramos la ventana final en la pantalla
        setLocationRelativeTo(null);
    }

    private boolean esFechaValida(String fechaStr) {
        try {
            LocalDate fechaIngresada = LocalDate.parse(fechaStr);
            LocalDate hoy = LocalDate.now();
            LocalDate fechaMinima = hoy.minusYears(70); // Límite inferior (70 años atrás)
            LocalDate fechaMaxima = hoy.minusYears(18); // Límite superior (18 años atrás)

            // Validar que no sea fecha futura
            if (hoy.isBefore(fechaIngresada)) {
                lblMensaje.setForeground(Color.RED);
                lblMensaje.setText("La fecha no puede ser futura");
                return false;
            }
            // Validar que no sea mayor de 70 años
            if (fechaIngresada.isBefore(fechaMinima)) {
                lblMensaje.setForeground(Color.RED);
                lblMensaje.setText("La fecha no puede ser mayor a 70 años");
                return false;
            }
            // Validar que sea mayor de 18 años
            if (fechaMaxima.isBefore(fechaIngresada)) {
                lblMensaje.setForeground(Color.RED);
                lblMensaje.setText("Debes ser mayor de 18 años");
                return false;
            }
            return true;
        } catch (DateTimeParseException e) {
            lblMensaje.setForeground(Color.RED);
            lblMensaje.setText("Formato de fecha inválido");
            return false;
        }
    }
}
