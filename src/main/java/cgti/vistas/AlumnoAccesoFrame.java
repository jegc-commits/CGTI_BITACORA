package cgti.vistas;

import cgti.clase.Alumno;
import cgti.clase.Rol;
import cgti.clase.Usuario;
import cgti.funcion.UsuarioFuncion;
import java.awt.*;
import javax.swing.*;

/**
 * Ventana de acceso para alumnos.
 * Permite ingresar con matrícula existente o registrarse si es nuevo.
 */
public class AlumnoAccesoFrame extends JFrame {

    // ==================== CONSTANTES ====================
    private static final int VENTANA_ANCHO = 400;
    private static final int VENTANA_ALTO = 480;
    private static final int LONGITUD_MATRICULA = 10;

    // ==================== SERVICIOS ====================
    private UsuarioFuncion funcion = new UsuarioFuncion();

    // ==================== CONSTRUCTOR ====================
    public AlumnoAccesoFrame() {
        configurarVentana();
        add(crearPanelPrincipal());
        finalizarVentana();
        setVisible(true);
    }

    private Alumno crearAlumnoObservador() {
        Alumno observador = new Alumno();
        observador.setMatricula(0);
        observador.setNombre("Modo");
        observador.setApellidoPaterno("Invitado");
        observador.setApellidoMaterno("");
        observador.setRol(Rol.OBSERVADOR);
        return observador;
    }

    // ==================== CONFIGURACIÓN DE VENTANA ====================
    private void configurarVentana() {
        setTitle("Acceso Alumno - CGTI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
    }

    private void finalizarVentana() {
        setSize(VENTANA_ANCHO, VENTANA_ALTO);
        setLocationRelativeTo(null);
    }

    // ==================== INTERFAZ GRÁFICA ====================
    private JPanel crearPanelPrincipal() {
        JPanel panel = new JPanel(null);
        panel.setBackground(Estilos.COLOR_BLANCO);

        // Icono de usuario decorativo
        JPanel panelIcono = crearIconoUsuario();
        panelIcono.setBounds(150, 20, 100, 100);
        panel.add(panelIcono);

        // Título principal
        JLabel titulo = new JLabel("Acceso Alumno", SwingConstants.CENTER);
        titulo.setFont(Estilos.TITULO);
        titulo.setForeground(Estilos.COLOR_AZUL);
        titulo.setBounds(50, 130, 300, 35);
        panel.add(titulo);

        // Subtítulo informativo
        JLabel subtitulo = new JLabel("Ingresa tu matrícula para continuar", SwingConstants.CENTER);
        subtitulo.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitulo.setForeground(Estilos.COLOR_GRIS);
        subtitulo.setBounds(50, 165, 300, 20);
        panel.add(subtitulo);

        // Etiqueta "Matrícula:"
        JLabel lblMatricula = new JLabel("Matrícula:", SwingConstants.LEFT);
        lblMatricula.setForeground(Estilos.COLOR_NEGRO);
        lblMatricula.setFont(new Font("Arial", Font.BOLD, 13));
        lblMatricula.setBounds(60, 195, 100, 25);
        panel.add(lblMatricula);

        // Campo de texto para ingresar matrícula
        JTextField txtMatricula = new JTextField();
        txtMatricula.setBounds(60, 220, 280, 35);
        txtMatricula.setFont(new Font("Arial", Font.PLAIN, 16));
        txtMatricula.setHorizontalAlignment(JTextField.CENTER);
        txtMatricula.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilos.COLOR_BORDE, 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        txtMatricula.setToolTipText("Ingresa tu número de matrícula (10 dígitos)");
        panel.add(txtMatricula);

        // Etiqueta para mensajes de error/éxito
        JLabel lblMensaje = new JLabel(" ", SwingConstants.CENTER);
        lblMensaje.setFont(new Font("Arial", Font.PLAIN, 12));
        lblMensaje.setBounds(40, 265, 320, 25);
        panel.add(lblMensaje);

        // Botón Registrarse
        JButton btnRegistrar = crearBoton("Registrarse", Estilos.COLOR_AZUL, true, 20);
        btnRegistrar.setBounds(60, 300, 130, 40);
        panel.add(btnRegistrar);

        // Botón Entrar
        JButton btnEntrar = crearBoton("Entrar", Estilos.COLOR_VERDE, true, 20);
        btnEntrar.setBounds(210, 300, 130, 40);
        panel.add(btnEntrar);

        // Botón Visitar (como invitado)
        JButton btnVisitar = crearBoton("Visitar como Invitado", Estilos.COLOR_GRIS, false, 20);
        btnVisitar.setBounds(100, 360, 200, 35);
        panel.add(btnVisitar);

        // Configurar eventos
        configurarEventos(btnEntrar, btnRegistrar, btnVisitar, txtMatricula, lblMensaje);

        return panel;
    }

    /**
     * Crea un icono circular con silueta de usuario.
     */
    private JPanel crearIconoUsuario() {
        JPanel panelIcono = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Círculo exterior azul
                g2.setColor(Estilos.COLOR_AZUL);
                g2.fillOval(0, 0, getWidth(), getHeight());

                // Círculo interior blanco
                g2.setColor(Color.WHITE);
                g2.fillOval(3, 3, getWidth() - 6, getHeight() - 6);

                // Cabeza de la persona
                g2.setColor(Estilos.COLOR_AZUL);
                g2.fillOval(35, 20, 30, 30);

                // Cuerpo de la persona
                g2.fillOval(20, 55, 60, 40);
            }
        };
        panelIcono.setOpaque(false);
        return panelIcono;
    }

    /**
     * Crea un botón redondeado con efecto hover.
     */
    private JButton crearBoton(String texto, Color color, boolean lleno, int redondeo) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Arial", Font.BOLD, 14));
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (lleno) {
            Estilos.estilizarBotonRedondo(boton, color, Color.WHITE, redondeo);
        } else {
            Estilos.estilizarBotonRedondo(boton, Color.WHITE, color, redondeo);
            boton.setBorder(BorderFactory.createLineBorder(color, 1, true));
        }

        return boton;
    }

    // ==================== EVENTOS ====================
    private void configurarEventos(JButton btnEntrar, JButton btnRegistrar, JButton btnVisitar,
            JTextField txtMatricula, JLabel lblMensaje) {

        // Botón Entrar
        btnEntrar.addActionListener(e -> procesarAcceso(txtMatricula, lblMensaje));

        // Enter en el campo de matrícula
        txtMatricula.addActionListener(e -> procesarAcceso(txtMatricula, lblMensaje));

        // Botón Registrarse
        // En el método configurarEventos, cambia el btnRegistrar:

        btnRegistrar.addActionListener(e -> {
            String matriculaTexto = txtMatricula.getText().trim();
            int matricula = 0;
            boolean matriculaValida = false;

            // Intentar obtener la matrícula si ya se ingresó algo
            if (!matriculaTexto.isEmpty() && matriculaTexto.length() == LONGITUD_MATRICULA
                    && matriculaTexto.matches("\\d+")) {
                try {
                    matricula = Integer.parseInt(matriculaTexto);
                    matriculaValida = true;
                } catch (NumberFormatException ex) {
                    // Si no es válida, se abrirá sin matrícula
                }
            }

            dispose();
            if (matriculaValida) {
                new RegistroAlumnoFrame(matricula).setVisible(true);
            }

        });

        // Botón Visitar como Invitado
        btnVisitar.addActionListener(e -> {
            Alumno observador = crearAlumnoObservador();
            new DashboardFrame(observador).setVisible(true);
            dispose();
        });
    }

    // ==================== LÓGICA DE ACCESO ====================
    private void procesarAcceso(JTextField txtMatricula, JLabel lblMensaje) {
        String matriculaTexto = txtMatricula.getText().trim();

        // Validar que no esté vacío
        if (matriculaTexto.isEmpty()) {
            mostrarMensaje(lblMensaje, "Ingresa tu matrícula", Estilos.COLOR_ERROR);
            txtMatricula.requestFocus();
            return;
        }

        // Validar longitud de 10 dígitos
        if (matriculaTexto.length() != LONGITUD_MATRICULA) {
            mostrarMensaje(lblMensaje, "La matrícula debe tener " + LONGITUD_MATRICULA + " dígitos",
                    Estilos.COLOR_ERROR);
            txtMatricula.requestFocus();
            return;
        }

        // Validar que solo tenga números
        if (!matriculaTexto.matches("\\d+")) {
            mostrarMensaje(lblMensaje, "Solo se permiten números", Estilos.COLOR_ERROR);
            txtMatricula.requestFocus();
            return;
        }

        try {
            int matricula = Integer.parseInt(matriculaTexto);
            mostrarMensaje(lblMensaje, "Verificando...", Estilos.COLOR_INFO);

            var resultado = funcion.buscarPorMatricula(matricula);

            if (resultado.isPresent()) {
                Usuario usuario = resultado.get();

                // Verificar que sea alumno
                if (usuario.getRol() == Rol.ALUMNO || usuario.getRol() == Rol.OBSERVADOR) {
                    mostrarMensaje(lblMensaje, "¡Bienvenido!", Estilos.COLOR_EXITO);
                    Timer timer = new Timer(500, evt -> {
                        new DashboardFrame(usuario).setVisible(true);
                        dispose();
                    });
                    timer.setRepeats(false);
                    timer.start();
                } else {
                    mostrarMensaje(lblMensaje, "Esta matrícula no pertenece a un alumno", Estilos.COLOR_ERROR);
                }
            } else {
                // Alumno no encontrado - debe registrarse
                mostrarMensaje(lblMensaje, "Matrícula no encontrada. Regístrate primero.", Estilos.COLOR_ADVERTENCIA);
            }
        } catch (NumberFormatException ex) {
            mostrarMensaje(lblMensaje, "Matrícula no válida", Estilos.COLOR_ERROR);
        }
    }

    // ==================== UTILIDADES ====================
    private void mostrarMensaje(JLabel etiqueta, String mensaje, Color color) {
        etiqueta.setText(mensaje);
        etiqueta.setForeground(color);
    }
}