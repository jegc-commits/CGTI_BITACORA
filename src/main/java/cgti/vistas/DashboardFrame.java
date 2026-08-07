package cgti.vistas;

import cgti.clase.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Ventana principal tras iniciar sesión. Muestra opciones según el rol:
 * ADMINISTRADOR, SUPERADMINISTRADOR, ALUMNO/OBSERVADOR.
 */
public class DashboardFrame extends JFrame {

    // ==================== CONSTANTES ====================
    private static final int ALTURA_BARRA_SUPERIOR = 150;
    private static final int ANCHO_MENU = 250;
    private static final int ANCHO_BOTON_MENU = 210;
    private static final int ALTO_BOTON_MENU = 45;
    private static final int Y_INICIO = 40, Y_SALIR = 480, Y_OPCION_1 = 100, Y_OPCION_2 = 160,
            Y_OPCION_3 = 220, Y_OPCION_4 = 280;

    // Imagen de fondo cargada una sola vez para toda la app (evita I/O repetido
    // y fuga de memoria al abrir/cerrar sesión varias veces)
    private static Image imagenFondoCache;
    private static boolean imagenFondoIntentada = false;

    private JFrame ventanaSecundariaActual = null;

    public DashboardFrame(Usuario usuario) {
        configurarVentana();

        Dimension pantalla = Toolkit.getDefaultToolkit().getScreenSize();
        int altoContenido = pantalla.height - ALTURA_BARRA_SUPERIOR;

        add(crearBarraSuperior(pantalla.width));
        add(crearMenuLateral(usuario, altoContenido));
        add(crearPanelContenido(usuario, pantalla.width - ANCHO_MENU, altoContenido));

        setSize(pantalla);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // ==================== CONFIGURACIÓN ====================

    private void configurarVentana() {
        setTitle("BITASYS - CGTI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(Estilos.COLOR_BLANCO);
    }

    // ==================== BARRA SUPERIOR ====================

    /**
     * Carga la imagen de fondo una sola vez (compartida por todas las instancias).
     */
    private static Image obtenerImagenFondo() {
        if (!imagenFondoIntentada) {
            imagenFondoIntentada = true;
            try {
                imagenFondoCache = new ImageIcon(
                        DashboardFrame.class.getResource("/imagenes/d1.jpg")).getImage();
            } catch (Exception e) {
                System.err.println("Error al cargar imagen: " + e.getMessage());
            }
        }
        return imagenFondoCache;
    }

    private JPanel crearBarraSuperior(int ancho) {
        JPanel barra = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Image img = obtenerImagenFondo();
                if (img != null) {
                    g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
                } else {
                    g.setColor(Estilos.COLOR_AZUL);
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        barra.setBounds(0, 0, ancho, ALTURA_BARRA_SUPERIOR);
        return barra;
    }

    // ==================== MENÚ LATERAL ====================

    private JPanel crearMenuLateral(Usuario usuario, int alto) {
        JPanel menu = new JPanel(null);
        menu.setBounds(0, ALTURA_BARRA_SUPERIOR, ANCHO_MENU, alto);
        menu.setBackground(Estilos.COLOR_GRIS_CLARO);
        menu.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Estilos.COLOR_BORDE));

        menu.add(crearBotonInicio());
        agregarBotonesPorRol(menu, usuario);
        menu.add(crearBotonSalir());
        return menu;
    }

    private JButton crearBotonInicio() {
        JButton btn = crearBotonMenu(" Inicio", 20, Y_INICIO);
        Estilos.estilizarBotonRedondo(btn, Estilos.COLOR_VERDE, Estilos.COLOR_BLANCO, 30);
        btn.setToolTipText("Volver a la pantalla principal");
        btn.addActionListener(e -> cerrarVentanaSecundaria());
        return btn;
    }

    private JButton crearBotonSalir() {
        JButton btn = crearBotonMenu("Cerrar sesión", 20, Y_SALIR);
        Estilos.estilizarBotonRedondo(btn, Estilos.COLOR_AZUL, Estilos.COLOR_BLANCO, 30); // Cambiado a AZUL
        btn.setToolTipText("Cerrar sesión");
        btn.addActionListener(e -> {
            int op = JOptionPane.showConfirmDialog(this,
                    "¿Está seguro de que desea cerrar sesión?", "Confirmar",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (op == JOptionPane.YES_OPTION) {
                cerrarVentanaSecundaria();
                dispose();
                new AlumnoAccesoFrame().setVisible(true);
            }
        });
        return btn;
    }

    private void cerrarVentanaSecundaria() {
        if (ventanaSecundariaActual != null && ventanaSecundariaActual.isVisible()) {
            ventanaSecundariaActual.dispose();
        }
        ventanaSecundariaActual = null;
    }

    private void agregarBotonesPorRol(JPanel menu, Usuario usuario) {
        switch (usuario.getRol()) {
            case ADMINISTRADOR -> agregarBotonesAdministrador(menu, usuario);
            case SUPERADMINISTRADOR -> agregarBotonesSuperAdministrador(menu, usuario);
            case ALUMNO, OBSERVADOR -> agregarBotonesAlumno(menu, usuario);
            default -> {
            }
        }
    }

    private void agregarBotonesAdministrador(JPanel menu, Usuario usuario) {
        menu.add(crearBotonMenuConAccion(" Ver bitácora", 20, Y_OPCION_1,
                e -> abrirNuevaVentana(new BitacoraFrame(usuario))));
        menu.add(crearBotonMenuConAccion(" Ver usuarios", 20, Y_OPCION_2,
                e -> abrirNuevaVentana(new UsuarioFrame(usuario))));
        menu.add(crearBotonMenuConAccion(" Gestionar equipos", 20, Y_OPCION_3,
                e -> abrirNuevaVentana(new EquipoFrame(usuario))));
    }

    private void agregarBotonesSuperAdministrador(JPanel menu, Usuario usuario) {
        menu.add(crearBotonMenuConAccion(" Exportar bitácora", 20, Y_OPCION_1,
                e -> abrirNuevaVentana(new BitacoraFrame(usuario))));
        menu.add(crearBotonMenuConAccion(" Agregar usuario", 20, Y_OPCION_2,
                e -> abrirNuevaVentana(new UsuarioFrame(usuario))));
        menu.add(crearBotonMenuConAccion(" Gestionar equipos", 20, Y_OPCION_3,
                e -> abrirNuevaVentana(new EquipoFrame(usuario))));
        menu.add(crearBotonMenuConAccion(" Insertar horario", 20, Y_OPCION_4,
                e -> abrirNuevaVentana(new HorarioBloqueoFrame(usuario))));
    }

    private void agregarBotonesAlumno(JPanel menu, Usuario usuario) {
        menu.add(crearBotonMenuConAccion(" Entrada / Salida", 20, Y_OPCION_1,
                e -> abrirNuevaVentana(new RegistroFrame(usuario))));
        menu.add(crearBotonMenuConAccion(" Observaciones", 20, Y_OPCION_2,
                e -> abrirNavegador("https://siga.utng.edu.mx/")));
    }

    // ==================== PANEL DE CONTENIDO ====================

    private JPanel crearPanelContenido(Usuario usuario, int ancho, int alto) {
        JPanel contenido = new JPanel(null);
        contenido.setBounds(ANCHO_MENU, ALTURA_BARRA_SUPERIOR, ancho, alto);
        contenido.setBackground(Estilos.COLOR_BLANCO);

        contenido.add(crearMensajeBienvenida(usuario));
        contenido.add(crearEtiquetaRol(usuario));
        agregarCardsPorRol(contenido, usuario);
        return contenido;
    }

    private JLabel crearMensajeBienvenida(Usuario usuario) {
        String nombre = usuario.getNombre() + " " + usuario.getApellidoPaterno();
        JLabel lbl = new JLabel("Bienvenido, " + nombre);
        lbl.setBounds(40, 30, 700, 40);
        lbl.setFont(Estilos.GRANDE);
        lbl.setForeground(Estilos.COLOR_NEGRO);
        return lbl;
    }

    /** Etiqueta de rol como "pill" con acento dorado institucional. */
    private JLabel crearEtiquetaRol(Usuario usuario) {
        JLabel lbl = new JLabel("  " + usuario.getRol() + "  ") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Estilos.aclarar(Estilos.COLOR_DORADO, 1.35f));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lbl.setBounds(40, 78, 220, 32);
        lbl.setFont(Estilos.NOMBRE.deriveFont(14f));
        lbl.setForeground(Estilos.COLOR_AZUL.darker());
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        return lbl;
    }

    private void agregarCardsPorRol(JPanel contenido, Usuario usuario) {
        switch (usuario.getRol()) {
            case ADMINISTRADOR -> agregarCardsAdministrador(contenido, usuario);
            case SUPERADMINISTRADOR -> agregarCardsSuperAdministrador(contenido, usuario);
            case ALUMNO, OBSERVADOR -> agregarCardsAlumno(contenido, usuario);
            default -> {
            }
        }
    }

    private void agregarCardsAdministrador(JPanel contenido, Usuario usuario) {
        contenido.add(crearCard("Acceder a Usuarios", "Consulta los usuarios del sistema",
                40, 140, e -> abrirNuevaVentana(new UsuarioFrame(usuario))));
        contenido.add(crearCard("Gestionar Equipos", "Administra los equipos disponibles",
                390, 140, e -> abrirNuevaVentana(new EquipoFrame(usuario))));
        contenido.add(crearCard("Acceder a Bitácora", "Revisa el historial de actividades",
                740, 140, e -> abrirNuevaVentana(new BitacoraFrame(usuario))));
    }

    private void agregarCardsSuperAdministrador(JPanel contenido, Usuario usuario) {
        contenido.add(crearCardvdos("Gestionar Usuarios", "Administra los usuarios del sistema",
                40, 140, e -> abrirNuevaVentana(new UsuarioFrame(usuario))));
        contenido.add(crearCardvdos("Gestionar Equipos", "Administra los equipos disponibles",
                300, 140, e -> abrirNuevaVentana(new EquipoFrame(usuario))));
        contenido.add(crearCardvdos("Exportar Bitácora", "Revisa el historial de actividades",
                560, 140, e -> abrirNuevaVentana(new BitacoraFrame(usuario))));
        contenido.add(crearCardvdos("Insertar Horario", "Importar horarios de clase",
                820, 140, e -> abrirNuevaVentana(new HorarioBloqueoFrame(usuario))));
    }

    private void agregarCardsAlumno(JPanel contenido, Usuario usuario) {
        contenido.add(crearCard("Registrar Entrada/Salida", "Registra tu ingreso o salida",
                40, 140, e -> abrirNuevaVentana(new RegistroFrame(usuario))));
        contenido.add(crearCard("Observaciones", "Reporta daños en equipos",
                390, 140, e -> abrirNavegador("https://siga.utng.edu.mx/")));
    }

    // ==================== COMPONENTES REUTILIZABLES ====================

    private JPanel crearCard(String titulo, String descripcion, int x, int y,
            ActionListener action) {
        return construirCard(titulo, descripcion, x, y, 320, 250, action,
                Estilos.TITULO, 20, 80, 20, 120, 20, 180, 280);
    }

    private JPanel crearCardvdos(String titulo, String descripcion, int x, int y,
            ActionListener action) {
        return construirCard(titulo, descripcion, x, y, 250, 200, action,
                Estilos.NOMBRE, 20, 20, 20, 50, 20, 120, 180);
    }

    /**
     * Construye una card usando Estilos.crearTarjeta() (sombra + esquinas
     * redondeadas) con barra de acento superior que resalta en hover.
     */
    private JPanel construirCard(String titulo, String descripcion, int x, int y,
            int ancho, int alto, ActionListener action, Font fuenteTitulo,
            int xT, int yT, int xD, int yD, int xB, int yB, int anchoB) {
        JPanel card = Estilos.crearTarjeta();
        card.setLayout(null);
        card.setBounds(x, y, ancho, alto);
        card.setToolTipText(descripcion);

        JPanel acento = new JPanel();
        acento.setBounds(0, 0, ancho - 4, 5);
        acento.setBackground(Estilos.COLOR_DORADO);
        card.add(acento);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setBounds(xT, yT, ancho - 40, 30);
        lblTitulo.setFont(fuenteTitulo);
        lblTitulo.setForeground(Estilos.COLOR_AZUL);
        card.add(lblTitulo);

        JLabel lblDescripcion = new JLabel("<html>" + descripcion + "</html>");
        lblDescripcion.setBounds(xD, yD, ancho - 40, 50);
        lblDescripcion.setFont(Estilos.PEQUEÑO);
        lblDescripcion.setForeground(Estilos.COLOR_GRIS);
        card.add(lblDescripcion);

        JButton btnAccion = new JButton("Acceder");
        Estilos.estilizarBotonRedondo(btnAccion, Estilos.COLOR_VERDE, Estilos.COLOR_BLANCO, 20);
        btnAccion.setBounds(xB, yB, anchoB, 40);
        if (action != null)
            btnAccion.addActionListener(action);
        card.add(btnAccion);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                acento.setBackground(Estilos.COLOR_VERDE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                acento.setBackground(Estilos.COLOR_DORADO);
            }
        });
        return card;
    }

    private JButton crearBotonMenu(String texto, int x, int y) {
        JButton boton = new JButton(texto);
        boton.setBounds(x, y, ANCHO_BOTON_MENU, ALTO_BOTON_MENU);
        boton.setBackground(Estilos.COLOR_VERDE);
        boton.setForeground(Estilos.COLOR_BLANCO);
        boton.setFont(Estilos.PEQUEÑO);
        boton.setHorizontalAlignment(SwingConstants.LEFT);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setBorder(new EmptyBorder(5, 15, 5, 5));

        Color colorOriginal = boton.getBackground();
        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                boton.setBackground(Estilos.COLOR_VERDE_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                boton.setBackground(colorOriginal);
            }
        });
        return boton;
    }

    private JButton crearBotonMenuConAccion(String texto, int x, int y, ActionListener action) {
        JButton boton = crearBotonMenu(texto, x, y);
        Estilos.estilizarBotonRedondo(boton, Estilos.COLOR_VERDE, Estilos.COLOR_BLANCO, 30);
        boton.addActionListener(action);
        return boton;
    }

    // ==================== NAVEGACIÓN ====================

    private void abrirNuevaVentana(JFrame nuevaVentana) {
        cerrarVentanaSecundaria();
        ventanaSecundariaActual = nuevaVentana;
        nuevaVentana.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (ventanaSecundariaActual == nuevaVentana)
                    ventanaSecundariaActual = null;
            }
        });
        nuevaVentana.setVisible(true);
    }

    private void abrirNavegador(String url) {
        try {
            Desktop.getDesktop().browse(new java.net.URI(url));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "No se pudo abrir el navegador: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}