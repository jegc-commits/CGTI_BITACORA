package cgti.vistas;

import cgti.clase.*;
import cgti.funcion.HorarioFuncion;
import cgti.funcion.EquipoFuncion;
import cgti.funcion.RegistroFuncion;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Ventana de Registro de Entrada/Salida - CGTI
 * Horario: 8:00 - 21:00 | Atajos: ENTER = Entrada | ESC = Regresar
 */
public class RegistroFrame extends JFrame {

    private static final int VENTANA_ANCHO = 520, VENTANA_ALTO = 680, ALTURA_BARRA = 100;
    private static final Dimension COMBO_SIZE = new Dimension(400, 35), BOTON_SIZE = new Dimension(200, 42);
    private static final int ESPACIO = 10;
    private static Image imagenFondoCache;
    private static boolean imagenCargada;

    private final RegistroFuncion registroFuncion = new RegistroFuncion();
    private final HorarioFuncion apartadoFuncion = new HorarioFuncion();
    private final EquipoFuncion equipoFuncion;
    private final Usuario usuarioActual;
    private final Map<Long, Boolean> cacheDisp = new HashMap<>();

    private JComboBox<Equipo> cmbEquipo;
    private JComboBox<String> cmbLab;
    private JTextField txtProposito;
    private JButton btnEntrada, btnSalida, btnRegresar;
    private JLabel lblMensaje, lblEstado, lblSoftware, lblHorario;
    private JPanel panelInfo;

    public RegistroFrame(Usuario usuario) {
        this.usuarioActual = usuario;
        this.equipoFuncion = new EquipoFuncion(registroFuncion);

        configurarVentana();
        inicializarComponentes();
        ensamblarInterfaz();
        configurarEventos();
        configurarAtajos();
        actualizarComboEquipos((String) cmbLab.getSelectedItem());
        actualizarPantalla();
        if (usuario.getRol() == Rol.OBSERVADOR)
            modoSoloLectura();
        txtProposito.requestFocusInWindow();
    }

    private void configurarVentana() {
        setTitle("Registro de Laboratorio - CGTI");
        setSize(VENTANA_ANCHO, VENTANA_ALTO);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        Estilos.bloquearCierreConX(this, "Usa el botón Volver");
        setLayout(new BorderLayout());
        getContentPane().setBackground(Estilos.COLOR_BLANCO);
    }

    private void inicializarComponentes() {
        // Inicializar cmbLab con los laboratorios disponibles
        List<String> laboratorios = apartadoFuncion.listarLaboratorios();
        String[] labsArray = laboratorios.toArray(new String[0]);
        cmbLab = new JComboBox<>(labsArray);
        cmbLab.setFont(new Font("Arial", Font.PLAIN, 14));
        cmbLab.setMaximumSize(COMBO_SIZE);
        cmbLab.setAlignmentX(Component.CENTER_ALIGNMENT);
        cmbLab.setBackground(Color.WHITE);

        cmbEquipo = crearCombo();
        configurarRendererEquipos();
        txtProposito = crearCampoTexto();
        lblMensaje = crearLabel(" ", 12, Font.PLAIN);
        lblEstado = crearLabel(" ", 13, Font.BOLD);
        lblSoftware = crearLabel(" ", 12, Font.ITALIC, Estilos.COLOR_GRIS);
        lblHorario = crearLabel("Horario: 8:00 - 21:00", 12, Font.BOLD, Estilos.COLOR_AZUL);
        panelInfo = crearPanelInfo();
        btnEntrada = crearBoton("✓  Entrada", Estilos.COLOR_VERDE);
        btnSalida = crearBoton("↩  Salida", Estilos.COLOR_AZUL);
        btnRegresar = crearBoton("←  Volver", Estilos.COLOR_GRIS);
    }

    private void ensamblarInterfaz() {
        add(crearBarraSuperior(), BorderLayout.NORTH);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 50, 25, 50));
        panel.setBackground(Estilos.COLOR_BLANCO);

        panel.add(crearEncabezado());
        panel.add(Box.createVerticalStrut(ESPACIO));
        panel.add(lblHorario);
        panel.add(Box.createVerticalStrut(ESPACIO));
        panel.add(crearSeccion(" Laboratorio:", cmbLab));
        panel.add(Box.createVerticalStrut(ESPACIO));
        panel.add(crearSeccion("Equipo:", cmbEquipo));
        panel.add(Box.createVerticalStrut(5));
        panel.add(panelInfo);
        panel.add(Box.createVerticalStrut(ESPACIO));
        panel.add(crearSeccion("Propósito:", txtProposito));
        panel.add(Box.createVerticalStrut(15));
        panel.add(crearPanelBotones());
        panel.add(Box.createVerticalStrut(ESPACIO));
        panel.add(lblMensaje);
        add(panel, BorderLayout.CENTER);
    }

    private JPanel crearEncabezado() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Estilos.COLOR_BLANCO);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titulo = new JLabel("Registro de Laboratorio");
        titulo.setFont(Estilos.GRANDE);
        titulo.setForeground(Estilos.COLOR_AZUL);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titulo);
        panel.add(Box.createVerticalStrut(5));

        JLabel usuario = new JLabel(usuarioActual.getNombre() + " " + usuarioActual.getApellidoPaterno());
        usuario.setFont(new Font("Arial", Font.BOLD, 14));
        usuario.setForeground(Estilos.COLOR_NEGRO);
        usuario.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(usuario);
        return panel;
    }

    private JPanel crearSeccion(String etiqueta, JComponent comp) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Estilos.COLOR_BLANCO);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(new Font("Arial", Font.BOLD, 13));
        lbl.setForeground(Estilos.COLOR_NEGRO);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lbl);
        panel.add(Box.createVerticalStrut(5));
        panel.add(comp);
        return panel;
    }

    private JPanel crearPanelInfo() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(248, 249, 250));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilos.COLOR_BORDE, 1, true),
                new EmptyBorder(12, 15, 12, 15)));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.setMaximumSize(new Dimension(400, 70));
        panel.add(lblEstado);
        panel.add(Box.createVerticalStrut(3));
        panel.add(lblSoftware);
        return panel;
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Estilos.COLOR_BLANCO);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(btnEntrada);
        panel.add(Box.createVerticalStrut(8));
        panel.add(btnSalida);
        panel.add(Box.createVerticalStrut(8));
        panel.add(btnRegresar);
        return panel;
    }

    private JPanel crearBarraSuperior() {
        JPanel barra = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                Image img = cargarImagen();
                if (img != null)
                    g2.drawImage(img, 0, 0, getWidth(), getHeight(), this);
                else {
                    g2.setColor(Estilos.COLOR_AZUL);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }

                // Degradado sutil en la parte inferior
                GradientPaint grad = new GradientPaint(0, getHeight() - 20, new Color(0, 0, 0, 0),
                        0, getHeight(), new Color(0, 0, 0, 30));
                g2.setPaint(grad);
                g2.fillRect(0, getHeight() - 20, getWidth(), 20);
            }
        };
        barra.setPreferredSize(new Dimension(VENTANA_ANCHO, ALTURA_BARRA));
        return barra;
    }

    private static Image cargarImagen() {
        if (!imagenCargada) {
            imagenCargada = true;
            try {
                imagenFondoCache = new ImageIcon(RegistroFrame.class.getResource("/imagenes/d2.jpg")).getImage();
            } catch (Exception e) {
                System.err.println("Imagen no encontrada");
            }
        }
        return imagenFondoCache;
    }

    // ==================== MÉTODOS DE CREACIÓN REUTILIZABLES ====================
    private JComboBox<Equipo> crearCombo() {
        JComboBox<Equipo> combo = new JComboBox<>();
        combo.setFont(new Font("Arial", Font.PLAIN, 14));
        combo.setMaximumSize(COMBO_SIZE);
        combo.setAlignmentX(Component.CENTER_ALIGNMENT);
        combo.setBackground(Color.WHITE);
        return combo;
    }

    private JTextField crearCampoTexto() {
        JTextField campo = new JTextField();
        Estilos.estilizarCampoTexto(campo);
        campo.setMaximumSize(COMBO_SIZE);
        campo.setAlignmentX(Component.CENTER_ALIGNMENT);
        campo.setToolTipText("Describe brevemente tu actividad");
        return campo;
    }

    private JLabel crearLabel(String texto, int size, int estilo) {
        return crearLabel(texto, size, estilo, Estilos.COLOR_NEGRO);
    }

    private JLabel crearLabel(String texto, int size, int estilo, Color color) {
        JLabel label = new JLabel(texto, SwingConstants.CENTER);
        label.setFont(new Font("Arial", estilo, size));
        label.setForeground(color);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private JButton crearBoton(String texto, Color color) {
        JButton btn = new JButton(texto);
        Estilos.estilizarBotonRedondo(btn, color, Color.WHITE, 30);
        btn.setMaximumSize(BOTON_SIZE);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setToolTipText(texto.replaceAll("[←✓↩]", "").trim());
        return btn;
    }

    // ==================== EVENTOS ====================
    private void configurarEventos() {
        cmbLab.addActionListener(e -> {
            String labSeleccionado = (String) cmbLab.getSelectedItem();
            if (labSeleccionado != null) {
                actualizarComboEquipos(labSeleccionado);
                actualizarPantalla();
            }
        });
        cmbEquipo.addActionListener(e -> actualizarPantalla());
        btnEntrada.addActionListener(e -> registrarEntrada());
        btnSalida.addActionListener(e -> registrarSalida());
        btnRegresar.addActionListener(e -> {
            if (registroFuncion.tieneSesionActiva(usuarioActual.getMatricula())) {
                int opcion = JOptionPane.showConfirmDialog(this,
                        "Tienes una sesión activa. ¿Estás seguro de salir sin registrar tu salida?",
                        "Confirmar salida", JOptionPane.YES_NO_OPTION);
                if (opcion != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            dispose();
        });
    }

    private void configurarAtajos() {
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "entrada");
        getRootPane().getActionMap().put("entrada", new AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (btnEntrada.isEnabled())
                    btnEntrada.doClick();
            }
        });
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "regresar");
        getRootPane().getActionMap().put("regresar", new AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                btnRegresar.doClick();
            }
        });
    }

    private void modoSoloLectura() {
        cmbLab.setEnabled(false);
        cmbEquipo.setEnabled(false);
        txtProposito.setEnabled(false);
        btnEntrada.setEnabled(false);
        btnSalida.setEnabled(false);
        mostrarMensaje("Modo solo lectura", Estilos.COLOR_NEGRO);
    }

    // ==================== LÓGICA ====================
    private void registrarEntrada() {
        Equipo equipo = (Equipo) cmbEquipo.getSelectedItem();
        String lab = (String) cmbLab.getSelectedItem();
        String proposito = txtProposito.getText().trim();

        if (equipo == null) {
            mostrarError("Selecciona un equipo");
            return;
        }
        if (proposito.isEmpty()) {
            mostrarError("Escribe el propósito");
            txtProposito.requestFocus();
            return;
        }
        if (apartadoFuncion.laboratorioBloqueadoAhora(lab)) {
            mostrarError("Laboratorio bloqueado en este horario");
            return;
        }
        if (!estaDisponible(equipo)) {
            mostrarError("Equipo ocupado: " + equipo.getNombreEqp());
            return;
        }
        if (registroFuncion.tieneSesionActiva(usuarioActual.getMatricula())) {
            mostrarAlerta("Ya tienes sesión activa. Registra tu salida primero");
            return;
        }

        String resultado = registroFuncion.registrarEntrada(usuarioActual, equipo, lab, proposito);
        procesarResultado(resultado, "Entrada registrada: " + equipo.getNombreEqp(), lab);
    }

    private void registrarSalida() {
        if (!registroFuncion.tieneSesionActiva(usuarioActual.getMatricula())) {
            mostrarError("No tienes sesión activa");
            return;
        }

        String resultado = registroFuncion.registrarSalida(usuarioActual.getMatricula());
        procesarResultado(resultado, "Salida registrada correctamente", (String) cmbLab.getSelectedItem());
    }

    private void procesarResultado(String resultado, String msgExito, String lab) {
        if (esExito(resultado)) {
            mostrarExito(msgExito);
            txtProposito.setText("");
            actualizarComboEquipos(lab);
            actualizarPantalla();
        } else {
            mostrarError(resultado);
        }
    }

    // ==================== CACHE ====================
    private boolean estaDisponible(Equipo e) {
        if (e == null)
            return false;
        return cacheDisp.computeIfAbsent(e.getId(), equipoFuncion::estaDisponible);
    }

    private void refrescarCache() {
        cacheDisp.clear();
        for (int i = 0; i < cmbEquipo.getItemCount(); i++) {
            Equipo eq = cmbEquipo.getItemAt(i);
            if (eq != null) {
                cacheDisp.put(eq.getId(), equipoFuncion.estaDisponible(eq.getId()));
            }
        }
    }

    // ==================== ACTUALIZAR UI ====================
    private void actualizarPantalla() {
        actualizarInfoEquipo();
        validarEstado();
    }

    private void configurarRendererEquipos() {
        cmbEquipo.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> list, Object value, int idx, boolean sel,
                    boolean foc) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, idx, sel, foc);
                if (value instanceof Equipo eq) {
                    label.setText((estaDisponible(eq) ? "✓ " : "✗ ") + eq.getNombreEqp());
                    label.setForeground(estaDisponible(eq) ? Estilos.COLOR_EXITO : Estilos.COLOR_ERROR);
                    label.setFont(new Font("Arial", estaDisponible(eq) ? Font.BOLD : Font.PLAIN, 13));
                }
                return label;
            }
        });
    }

    private void actualizarInfoEquipo() {
        Equipo eq = (Equipo) cmbEquipo.getSelectedItem();
        if (eq == null) {
            lblEstado.setText("Selecciona un equipo");
            lblSoftware.setText("");
            return;
        }
        lblEstado.setText(estaDisponible(eq) ? "✓ DISPONIBLE" : "✗ OCUPADO");
        lblEstado.setForeground(estaDisponible(eq) ? Estilos.COLOR_EXITO : Estilos.COLOR_ERROR);
        String sw = eq.getSoftware();
        lblSoftware.setText((sw != null && !sw.isBlank()) ? "💿 Software: " + sw : " Sin software especificado");
    }

    private void actualizarComboEquipos(String lab) {
        if (lab == null)
            return;

        cmbEquipo.removeAllItems();
        List<Equipo> equipos = equipoFuncion.listarPorLaboratorio(lab);
        for (Equipo e : equipos) {
            cmbEquipo.addItem(e);
        }
        refrescarCache();

        // Seleccionar el primer equipo disponible
        for (int i = 0; i < cmbEquipo.getItemCount(); i++) {
            Equipo eq = cmbEquipo.getItemAt(i);
            if (eq != null && Boolean.TRUE.equals(cacheDisp.get(eq.getId()))) {
                cmbEquipo.setSelectedIndex(i);
                return;
            }
        }
    }

    private void validarEstado() {
        if (cmbEquipo.getItemCount() == 0) {
            deshabilitar("No hay equipos en este laboratorio");
            return;
        }
        Equipo sel = (Equipo) cmbEquipo.getSelectedItem();
        if (sel == null) {
            deshabilitar("Selecciona un equipo");
            return;
        }
        if (registroFuncion.tieneSesionActiva(usuarioActual.getMatricula())) {
            btnEntrada.setEnabled(false);
            btnSalida.setEnabled(true);
            mostrarAlerta("⚠ Tienes sesión activa. Registra tu salida");
            return;
        }
        if (apartadoFuncion.laboratorioBloqueadoAhora((String) cmbLab.getSelectedItem())) {
            deshabilitar("🔒 Laboratorio bloqueado en este horario");
            return;
        }
        if (estaDisponible(sel)) {
            btnEntrada.setEnabled(true);
            btnSalida.setEnabled(false);
            mostrarExito("✓ " + sel.getNombreEqp() + " - Disponible");
        } else {
            deshabilitar("✗ Equipo ocupado: " + sel.getNombreEqp());
        }
    }

    private void deshabilitar(String msg) {
        btnEntrada.setEnabled(false);
        btnSalida.setEnabled(false);
        mostrarError(msg);
    }

    // ==================== MENSAJES ====================
    private void mostrarError(String t) {
        lblMensaje.setForeground(Estilos.COLOR_ERROR);
        lblMensaje.setText("✗ " + t);
    }

    private void mostrarExito(String t) {
        lblMensaje.setForeground(Estilos.COLOR_EXITO);
        lblMensaje.setText("✓ " + t);
    }

    private void mostrarAlerta(String t) {
        lblMensaje.setForeground(Estilos.COLOR_ADVERTENCIA);
        lblMensaje.setText("⚠ " + t);
    }

    private void mostrarMensaje(String t, Color c) {
        lblMensaje.setForeground(c);
        lblMensaje.setText(t);
    }

    private boolean esExito(String r) {
        return r != null && (r.contains("correctamente") || r.contains("éxito"));
    }
}