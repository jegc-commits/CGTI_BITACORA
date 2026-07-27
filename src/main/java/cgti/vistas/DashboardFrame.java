package cgti.vistas;

import cgti.clase.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class DashboardFrame extends JFrame {

    private JFrame ventanaSecundariaActual = null;

    public DashboardFrame(Usuario usuario) {

        setTitle("BITASYS - CGTI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        JPanel barra = new JPanel() {
            private Image imagenFondo;

            {
                try {
                    ImageIcon icono = new ImageIcon(getClass().getResource("/imagenes/d1.jpg"));
                    imagenFondo = icono.getImage();
                    System.out.println("Imagen cargada correctamente: " + imagenFondo.getWidth(null) + "x"
                            + imagenFondo.getHeight(null));
                } catch (Exception e) {
                    System.err.println("Error al cargar imagen: " + e.getMessage());
                    imagenFondo = null;
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (imagenFondo != null) {
                    g.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), this);
                } else {
                    g.setColor(Estilos.COLOR_AZUL);
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };

        barra.setBounds(0, 0, 1920, 150);
        barra.setPreferredSize(new Dimension(1920, 150)); // AÑADIR ESTO
        add(barra);

        // ========== MENÚ LATERAL ==========
        JPanel menu = new JPanel(null);
        menu.setBounds(0, 150, 250, 820);
        menu.setBackground(Estilos.COLOR_GRIS);
        menu.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Estilos.COLOR_BORDE));
        add(menu);

        // Botón Inicio
        // Botón Inicio - Corregido
        JButton btnInicio = crearBotonMenu(" Inicio", 20, 40);
        Estilos.estilizarBotonRedondo(btnInicio, Estilos.COLOR_VERDE, Estilos.COLOR_BLANCO, 30);
        btnInicio.addActionListener(e -> {
            // Solo cerrar ventanas secundarias si existen
            if (ventanaSecundariaActual != null && ventanaSecundariaActual.isVisible()) {
                ventanaSecundariaActual.dispose();
            }
            // No es necesario crear un nuevo Dashboard, solo mantener el actual
            // Si quieres "refrescar", puedes recargar el contenido
        });
        menu.add(btnInicio);
        // Botón Salir - Corregido para todos los roles
        JButton btnSalir = crearBotonMenu(" Salir", 20, 480);
        Estilos.estilizarBotonRedondo(btnSalir, Estilos.COLOR_VERDE, Estilos.COLOR_BLANCO, 30);
        btnSalir.addActionListener(e -> {
            int option = JOptionPane.showConfirmDialog(
                    this,
                    "¿Está seguro de que desea cerrar sesión?",
                    "Confirmar",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (option == JOptionPane.YES_OPTION) {
                // Primero cerrar ventana secundaria si existe
                if (ventanaSecundariaActual != null && ventanaSecundariaActual.isVisible()) {
                    ventanaSecundariaActual.dispose();
                    ventanaSecundariaActual = null;
                }

                // Ocultar el Dashboard antes de mostrar InicioFrame
                setVisible(false);

                // Usar SwingUtilities para asegurar que se ejecute en el EDT
                SwingUtilities.invokeLater(() -> {
                    InicioFrame inicio = new InicioFrame();
                    inicio.setVisible(true);
                });

                // Finalmente destruir el Dashboard
                dispose();
            }
        });
        menu.add(btnSalir);
        // ========== PANEL DE CONTENIDO ==========
        JPanel contenido = new JPanel(null);
        contenido.setBounds(250, 150, 1670, 820);
        contenido.setBackground(Estilos.COLOR_BLANCO);
        add(contenido);

        // Título de bienvenida
        JLabel bienvenida = new JLabel("Bienvenido, " + usuario.getNombre() + " " + usuario.getApellidoPaterno());
        bienvenida.setBounds(40, 30, 700, 40);
        bienvenida.setFont(Estilos.GRANDE);
        bienvenida.setForeground(Estilos.COLOR_NEGRO);
        contenido.add(bienvenida);

        JLabel lblRol = new JLabel("Rol: " + usuario.getRol());
        lblRol.setBounds(40, 80, 400, 30);
        lblRol.setFont(Estilos.TITULO);
        lblRol.setForeground(Estilos.COLOR_GRIS);
        contenido.add(lblRol);

        // ========== CONTENIDO SEGÚN ROL ==========
        if (usuario.getRol() == Rol.ADMINISTRADOR) {
            contenido.add(crearCard(
                    "Acceder a Usuarios",
                    "Consulta los usuarios del sistema",
                    40, 140,
                    e -> abrirNuevaVentana(new UsuarioFrame(usuario))));

            contenido.add(crearCard(
                    "Gestionar Equipos",
                    "Administra los equipos disponibles",
                    390, 140,
                    e -> abrirNuevaVentana(new EquipoFrame(usuario))));

            contenido.add(crearCard(
                    "Acceder a Bitácora",
                    "Revisa el historial de actividades",
                    740, 140,
                    e -> abrirNuevaVentana(new BitacoraFrame(usuario))));

            JButton btnBitacora = crearBotonMenu(" Ver bitacora", 20, 100);
            Estilos.estilizarBotonRedondo(btnBitacora, Estilos.COLOR_VERDE, Estilos.COLOR_BLANCO, 30);
            menu.add(btnBitacora);
            btnBitacora.addActionListener(e -> abrirNuevaVentana(new BitacoraFrame(usuario)));

            JButton btnUsuario = crearBotonMenu("Ver usuarios", 20, 160);
            Estilos.estilizarBotonRedondo(btnUsuario, Estilos.COLOR_VERDE, Estilos.COLOR_BLANCO, 30);
            menu.add(btnUsuario);
            btnUsuario.addActionListener(e -> abrirNuevaVentana(new UsuarioFrame(usuario)));

            JButton btnEquipos = crearBotonMenu("Gestionar equipos", 20, 220);
            Estilos.estilizarBotonRedondo(btnEquipos, Estilos.COLOR_VERDE, Estilos.COLOR_BLANCO, 30);
            menu.add(btnEquipos);
            btnEquipos.addActionListener(e -> abrirNuevaVentana(new EquipoFrame(usuario)));

        } else if (usuario.getRol() == Rol.SUPERADMINISTRADOR) {
            contenido.add(crearCardvdos(
                    "Gestionar Usuarios",
                    "Administra los usuarios del sistema",
                    40, 140,
                    e -> abrirNuevaVentana(new UsuarioFrame(usuario))));

            contenido.add(crearCardvdos(
                    "Gestionar Equipos",
                    "Administra los equipos disponibles",
                    300, 140,
                    e -> abrirNuevaVentana(new EquipoFrame(usuario))));

            contenido.add(crearCardvdos(
                    "Exportar Bitácora",
                    "Revisa el historial de actividades",
                    560, 140,
                    e -> abrirNuevaVentana(new BitacoraFrame(usuario))));

            contenido.add(crearCardvdos(
                    "Insertar Horario",
                    "Importar horarios de clase",
                    820, 140,
                    e -> abrirNuevaVentana(new HorarioBloqueoFrame(usuario))));

            JButton btnBitacora = crearBotonMenu(" Exportar bitacora", 20, 100);
            Estilos.estilizarBotonRedondo(btnBitacora, Estilos.COLOR_VERDE, Estilos.COLOR_BLANCO, 30);
            menu.add(btnBitacora);
            btnBitacora.addActionListener(e -> abrirNuevaVentana(new BitacoraFrame(usuario)));

            JButton btnUsuario = crearBotonMenu("Agregar usuario", 20, 160);
            Estilos.estilizarBotonRedondo(btnUsuario, Estilos.COLOR_VERDE, Estilos.COLOR_BLANCO, 30);
            menu.add(btnUsuario);
            btnUsuario.addActionListener(e -> abrirNuevaVentana(new UsuarioFrame(usuario)));

            JButton btnEquipos = crearBotonMenu("gestionar equipos", 20, 220);
            Estilos.estilizarBotonRedondo(btnEquipos, Estilos.COLOR_VERDE, Estilos.COLOR_BLANCO, 30);
            menu.add(btnEquipos);
            btnEquipos.addActionListener(e -> abrirNuevaVentana(new EquipoFrame(usuario)));

            JButton btnBloqueo = crearBotonMenu("insertar horario", 20, 280);
            Estilos.estilizarBotonRedondo(btnBloqueo, Estilos.COLOR_VERDE, Estilos.COLOR_BLANCO, 30);
            menu.add(btnBloqueo);
            btnBloqueo.addActionListener(e -> abrirNuevaVentana(new HorarioBloqueoFrame(usuario)));

        } else if (usuario.getRol() == Rol.ALUMNO || usuario.getRol() == Rol.OBSERVADOR) {

            contenido.add(crearCard(
                    "Registrar Entrada/Salida",
                    "Registra tu ingreso o salida",
                    40, 140,
                    e -> abrirNuevaVentana(new RegistroFrame(usuario))));

            contenido.add(crearCard(
                    "Observaciones",
                    "Reporta daños en equipos",
                    390, 140,
                    e -> abrirNavegador("https://siga.utng.edu.mx/")));

            JButton btnEnt_sal = crearBotonMenu("Entrada / salida", 20, 100);
            Estilos.estilizarBotonRedondo(btnEnt_sal, Estilos.COLOR_VERDE, Estilos.COLOR_BLANCO, 30);
            menu.add(btnEnt_sal);
            btnEnt_sal.addActionListener(e -> abrirNuevaVentana(new RegistroFrame(usuario)));

            JButton btnObservaciones = crearBotonMenu("observaciones", 20, 160);
            Estilos.estilizarBotonRedondo(btnObservaciones, Estilos.COLOR_VERDE, Estilos.COLOR_BLANCO, 30);
            menu.add(btnObservaciones);
            btnObservaciones.addActionListener(e -> abrirNavegador("https://siga.utng.edu.mx/"));
        }

        // Configuración final
        getContentPane().setBackground(Estilos.COLOR_BLANCO);
        Dimension pantalla = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(pantalla.width, pantalla.height);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Método para crear cards del dashboard
    private JPanel crearCard(String titulo, String descripcion, int x, int y,
            java.awt.event.ActionListener action) {
        JPanel card = new JPanel(null);
        card.setBounds(x, y, 320, 250);
        card.setBackground(Color.WHITE);

        Border bordeOriginal = BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(2, 2, 2, 2),
                BorderFactory.createCompoundBorder(
                        new LineBorder(new Color(200, 200, 200), 1, true),
                        new EmptyBorder(18, 18, 18, 18)));
        card.setBorder(bordeOriginal);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setBounds(20, 80, 280, 30);
        lblTitulo.setFont(Estilos.TITULO);
        lblTitulo.setForeground(Estilos.COLOR_NEGRO);
        card.add(lblTitulo);

        JLabel lblDescripcion = new JLabel("<html>" + descripcion + "</html>");
        lblDescripcion.setBounds(20, 120, 280, 50);
        lblDescripcion.setFont(Estilos.PEQUEÑO);
        lblDescripcion.setForeground(Estilos.COLOR_GRIS);
        card.add(lblDescripcion);

        JButton btnAccion = new JButton("Acceder");
        btnAccion.setBounds(20, 180, 280, 40);
        btnAccion.setBackground(Estilos.COLOR_VERDE);
        btnAccion.setForeground(Color.WHITE);
        btnAccion.setFont(Estilos.PEQUEÑO);
        btnAccion.setBorderPainted(false);
        btnAccion.setFocusPainted(false);
        btnAccion.setCursor(new Cursor(Cursor.HAND_CURSOR));

        Color colorOriginalBtn = btnAccion.getBackground();

        btnAccion.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnAccion.setBackground(Estilos.COLOR_VERDE_HOVER.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnAccion.setBackground(colorOriginalBtn);
            }
        });

        if (action != null) {
            btnAccion.addActionListener(action);
        }

        card.add(btnAccion);

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder(2, 2, 2, 2),
                        BorderFactory.createCompoundBorder(
                                new LineBorder(Estilos.COLOR_VERDE_HOVER, 1, true),
                                new EmptyBorder(18, 18, 18, 18))));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBorder(bordeOriginal);
            }
        });

        return card;
    }

    private JPanel crearCardvdos(String titulo, String descripcion, int x, int y,
            java.awt.event.ActionListener action) {
        JPanel card = new JPanel(null);
        card.setBounds(x, y, 250, 200);
        card.setBackground(Color.WHITE);

        Border bordeOriginal = BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(2, 2, 2, 2),
                BorderFactory.createCompoundBorder(
                        new LineBorder(new Color(200, 200, 200), 1, true),
                        new EmptyBorder(18, 18, 18, 18)));
        card.setBorder(bordeOriginal);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setBounds(20, 20, 280, 30);
        lblTitulo.setFont(Estilos.Nombre);
        lblTitulo.setForeground(Estilos.COLOR_NEGRO);
        card.add(lblTitulo);

        JLabel lblDescripcion = new JLabel("<html>" + descripcion + "</html>");
        lblDescripcion.setBounds(20, 50, 280, 50);
        lblDescripcion.setFont(Estilos.PEQUEÑO);
        lblDescripcion.setForeground(Estilos.COLOR_GRIS);
        card.add(lblDescripcion);

        JButton btnAccion = new JButton("Acceder");
        btnAccion.setBounds(20, 120, 180, 40);
        Estilos.estilizarBotonRedondo(btnAccion, Estilos.COLOR_VERDE, Estilos.COLOR_BLANCO, 20);
        btnAccion.setFont(Estilos.PEQUEÑO);
        btnAccion.setBorderPainted(false);
        btnAccion.setFocusPainted(false);
        btnAccion.setCursor(new Cursor(Cursor.HAND_CURSOR));

        Color colorOriginalBtn = btnAccion.getBackground();

        btnAccion.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnAccion.setBackground(Estilos.COLOR_VERDE_HOVER.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnAccion.setBackground(colorOriginalBtn);
            }
        });

        if (action != null) {
            btnAccion.addActionListener(action);
        }

        card.add(btnAccion);

        card.addMouseListener(new java.awt.event.MouseAdapter() {

            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder(2, 2, 2, 2),
                        BorderFactory.createCompoundBorder(
                                new LineBorder(Estilos.COLOR_VERDE_HOVER, 1, true),
                                new EmptyBorder(18, 18, 18, 18))));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBorder(bordeOriginal);
            }

        });

        return card;
    }

    // Método para crear botones del menú
    private JButton crearBotonMenu(String texto, int x, int y) {
        JButton boton = new JButton(texto);
        boton.setBounds(x, y, 210, 45);
        boton.setBackground(Estilos.COLOR_VERDE);
        boton.setForeground(Estilos.COLOR_BLANCO);
        boton.setFont(Estilos.PEQUEÑO);
        boton.setHorizontalAlignment(SwingConstants.LEFT);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setBorder(new EmptyBorder(5, 15, 5, 5));

        Color colorOriginal = boton.getBackground();

        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boton.setBackground(Estilos.COLOR_VERDE_HOVER);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                boton.setBackground(colorOriginal);
            }
        });

        return boton;
    }

    private void abrirNuevaVentana(JFrame nuevaVentana) {
        if (ventanaSecundariaActual != null && ventanaSecundariaActual.isVisible()) {
            ventanaSecundariaActual.dispose();
        }

        ventanaSecundariaActual = nuevaVentana;

        nuevaVentana.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                if (ventanaSecundariaActual == nuevaVentana) {
                    ventanaSecundariaActual = null;
                }
            }
        });

        nuevaVentana.setVisible(true);

    }

    private void abrirNavegador(String url) {
        try {
            Desktop.getDesktop().browse(new java.net.URI(url));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo abrir el navegador: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

}