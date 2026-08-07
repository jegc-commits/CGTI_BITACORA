package cgti.vistas;

import cgti.clase.*;
import cgti.funcion.UsuarioFuncion;
import cgti.funcion.CatalogoFuncion;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;

/**
 * Ventana de Gestion de Usuarios - CGTI
 * 
 * Permite a los administradores gestionar todos los usuarios
 * del sistema con filtros avanzados por area y carrera.
 */
public class UsuarioFrame extends JFrame {

    // ==================== CONSTANTES ====================
    private static final int VENTANA_ANCHO = 1100;
    private static final int VENTANA_ALTO = 700;
    private static final int ALTURA_BARRA = 100;

    // ==================== SERVICIOS ====================
    private UsuarioFuncion funcion = new UsuarioFuncion();
    private CatalogoFuncion catalogoFuncion = new CatalogoFuncion();

    // ==================== COMPONENTES DE LA INTERFAZ ====================
    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTable tabla;
    private JLabel lblContador;

    // Filtros
    private JComboBox<String> cmbFiltroRol;
    private JComboBox<String> cmbFiltroArea;
    private JComboBox<String> cmbFiltroCarrera;
    private JTextField txtBusqueda;

    // Botones
    private JButton btnNuevo;
    private JButton btnEliminar;

    // ==================== ESTADO INTERNO ====================
    private Usuario admin;
    private boolean soloLectura;

    // ==================== CONSTRUCTOR ====================

    public UsuarioFrame(Usuario admin) {
        this.admin = admin;
        this.soloLectura = admin.getRol() != Rol.SUPERADMINISTRADOR;

        configurarVentana();
        add(crearBarraSuperior(), BorderLayout.NORTH);
        add(crearPanelPrincipal(), BorderLayout.CENTER);
        add(crearBarraEstado(), BorderLayout.SOUTH);

        configurarAtajosTeclado();
        cargarDatos();
        setVisible(true);
    }

    // ==================== CONFIGURACION INICIAL ====================

    private void configurarVentana() {
        setTitle("Gestion de Usuarios - CGTI");
        setSize(VENTANA_ANCHO, VENTANA_ALTO);
        setMinimumSize(new Dimension(900, 550));
        Estilos.bloquearCierreConX(this, "Usa el boton Regresar para volver al panel principal");
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Estilos.COLOR_BLANCO);
    }

    // ==================== BARRA SUPERIOR ====================

    private JPanel crearBarraSuperior() {
        JPanel barra = new JPanel(null) {
            private Image imagenFondo;
            {
                try {
                    imagenFondo = new ImageIcon(
                            getClass().getResource("/imagenes/d3.jpg")).getImage();
                } catch (Exception e) {
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
        barra.setPreferredSize(new Dimension(VENTANA_ANCHO, ALTURA_BARRA));
        return barra;
    }

    // ==================== PANEL PRINCIPAL ====================

    private JPanel crearPanelPrincipal() {
        JPanel panelFormulario = new JPanel();
        panelFormulario.setLayout(new BoxLayout(panelFormulario, BoxLayout.Y_AXIS));
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        panelFormulario.setBackground(Estilos.COLOR_BLANCO);

        // Titulo
        JLabel lblTitulo = new JLabel("Gestion de Usuarios");
        lblTitulo.setFont(Estilos.GRANDE);
        lblTitulo.setForeground(Estilos.COLOR_AZUL);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelFormulario.add(lblTitulo);
        panelFormulario.add(Box.createVerticalStrut(8));

        // Info del admin
        panelFormulario.add(crearPanelInfoAdmin());
        panelFormulario.add(Box.createVerticalStrut(15));

        // Filtros
        panelFormulario.add(crearPanelFiltros());
        panelFormulario.add(Box.createVerticalStrut(15));

        // Tabla
        panelFormulario.add(crearPanelTabla());
        panelFormulario.add(Box.createVerticalStrut(20));

        // Botones
        panelFormulario.add(crearPanelBotones());

        return panelFormulario;
    }

    // ==================== PANEL INFO ADMIN ====================

    private JPanel crearPanelInfoAdmin() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilos.COLOR_BORDE, 1, true),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblAdmin = new JLabel("Administrador: " + admin.getNombre() + " " + admin.getApellidoPaterno());
        lblAdmin.setFont(new Font("Arial", Font.PLAIN, 14));
        lblAdmin.setForeground(Estilos.COLOR_NEGRO);
        panel.add(lblAdmin, BorderLayout.WEST);

        JLabel lblRolAdmin = new JLabel("Rol: " + admin.getRol());
        lblRolAdmin.setFont(new Font("Arial", Font.BOLD, 13));
        lblRolAdmin.setForeground(Estilos.COLOR_AZUL);
        panel.add(lblRolAdmin, BorderLayout.EAST);

        if (soloLectura) {
            JLabel lblModo = new JLabel(" [Modo Lectura]");
            lblModo.setFont(new Font("Arial", Font.ITALIC, 12));
            lblModo.setForeground(Estilos.COLOR_ADVERTENCIA);
            panel.add(lblModo, BorderLayout.CENTER);
        }

        return panel;
    }

    // ==================== PANEL DE FILTROS ====================
    private JPanel crearPanelFiltros() {
        JPanel panelFiltros = new JPanel();
        panelFiltros.setLayout(new BoxLayout(panelFiltros, BoxLayout.Y_AXIS));
        panelFiltros.setBackground(Color.WHITE);
        panelFiltros.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilos.COLOR_BORDE, 1, true),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)));
        panelFiltros.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Fila 1: Rol + Busqueda
        JPanel fila1 = new JPanel(new BorderLayout(10, 0));
        fila1.setBackground(Color.WHITE);

        // Filtro por Rol
        JPanel panelRol = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panelRol.setBackground(Color.WHITE);
        JLabel lblRol = new JLabel("Rol:");
        lblRol.setFont(new Font("Arial", Font.BOLD, 13));
        lblRol.setForeground(Estilos.COLOR_AZUL);
        panelRol.add(lblRol);

        cmbFiltroRol = new JComboBox<>(
                new String[] { "Todos", "ALUMNO", "ADMINISTRADOR", "SUPERADMINISTRADOR", "OBSERVADOR" });
        cmbFiltroRol.setFont(new Font("Arial", Font.PLAIN, 13));
        cmbFiltroRol.setPreferredSize(new Dimension(180, 30));
        cmbFiltroRol.setBackground(Color.WHITE);
        panelRol.add(cmbFiltroRol);

        fila1.add(panelRol, BorderLayout.WEST);

        // Busqueda
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        panelBusqueda.setBackground(Color.WHITE);
        JLabel lblBuscar = new JLabel("Buscar:");
        lblBuscar.setFont(new Font("Arial", Font.BOLD, 13));
        lblBuscar.setForeground(Estilos.COLOR_AZUL);
        panelBusqueda.add(lblBuscar);

        txtBusqueda = new JTextField(15);
        txtBusqueda.setFont(new Font("Arial", Font.PLAIN, 13));
        txtBusqueda.setPreferredSize(new Dimension(200, 30));
        txtBusqueda.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilos.COLOR_BORDE),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        txtBusqueda.setToolTipText("Buscar por matricula, nombre o apellido");
        panelBusqueda.add(txtBusqueda);

        JButton btnLimpiar = new JButton("X");
        btnLimpiar.setFont(new Font("Arial", Font.BOLD, 12));
        btnLimpiar.setForeground(Estilos.COLOR_ERROR);
        btnLimpiar.setBackground(Color.WHITE);
        btnLimpiar.setBorder(BorderFactory.createLineBorder(Estilos.COLOR_BORDE));
        btnLimpiar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLimpiar.setToolTipText("Limpiar todos los filtros");
        btnLimpiar.setPreferredSize(new Dimension(35, 30));
        panelBusqueda.add(btnLimpiar);

        fila1.add(panelBusqueda, BorderLayout.EAST);
        panelFiltros.add(fila1);
        panelFiltros.add(Box.createVerticalStrut(10));

        // Fila 2: Area + Carrera
        JPanel fila2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        fila2.setBackground(Color.WHITE);

        // Filtro por Area
        JLabel lblArea = new JLabel("Area:");
        lblArea.setFont(new Font("Arial", Font.BOLD, 13));
        lblArea.setForeground(Estilos.COLOR_AZUL);
        fila2.add(lblArea);

        // ===== CORRECCION: Inicializar cmbFiltroArea PRIMERO =====
        cmbFiltroArea = new JComboBox<>();
        cmbFiltroArea.setFont(new Font("Arial", Font.PLAIN, 13));
        cmbFiltroArea.setPreferredSize(new Dimension(200, 30));
        cmbFiltroArea.setBackground(Color.WHITE);
        fila2.add(cmbFiltroArea);

        // Filtro por Carrera
        JLabel lblCarrera = new JLabel("Carrera:");
        lblCarrera.setFont(new Font("Arial", Font.BOLD, 13));
        lblCarrera.setForeground(Estilos.COLOR_AZUL);
        fila2.add(lblCarrera);

        // ===== CORRECCION: Inicializar cmbFiltroCarrera DESPUES =====
        cmbFiltroCarrera = new JComboBox<>();
        cmbFiltroCarrera.setFont(new Font("Arial", Font.PLAIN, 13));
        cmbFiltroCarrera.setPreferredSize(new Dimension(250, 30));
        cmbFiltroCarrera.setBackground(Color.WHITE);
        fila2.add(cmbFiltroCarrera);

        panelFiltros.add(fila2);

        cargarAreasEnFiltro();

        // Evento: al cambiar area, actualizar carreras
        cmbFiltroArea.addActionListener(e -> {
            String areaSeleccionada = (String) cmbFiltroArea.getSelectedItem();
            actualizarCarrerasFiltro(areaSeleccionada);
            aplicarFiltros();
        });

        // Configurar eventos
        configurarEventosFiltros(btnLimpiar);

        return panelFiltros;
    }

    // ==================== PANEL DE TABLA ====================

    private JPanel crearPanelTabla() {
        JPanel panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBackground(Color.WHITE);
        panelTabla.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilos.COLOR_BORDE, 1, true),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        panelTabla.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 8 columnas: Matricula, Nombre, Apellido, Rol, Area, Carrera, Cuatrimestre,
        // Grupo
        model = new DefaultTableModel(
                new Object[] { "Matricula", "Nombre", "Apellido", "Rol", "Area", "Carrera", "Cuatrimestre",
                        "Grupo" },
                0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Integer.class : String.class;
            }
        };

        tabla = new JTable(model);
        tabla.setFont(new Font("Arial", Font.PLAIN, 12));
        tabla.setRowHeight(30);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setShowGrid(true);
        tabla.setGridColor(new Color(230, 230, 230));
        tabla.setSelectionBackground(new Color(200, 220, 255));
        tabla.setSelectionForeground(Estilos.COLOR_NEGRO);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        JTableHeader header = tabla.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 12));
        header.setBackground(Estilos.COLOR_AZUL);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 38));

        sorter = new TableRowSorter<>(model);
        tabla.setRowSorter(sorter);

        // Ajustar anchos de columnas
        tabla.getColumnModel().getColumn(0).setPreferredWidth(90); // Matricula
        tabla.getColumnModel().getColumn(1).setPreferredWidth(150); // Nombre
        tabla.getColumnModel().getColumn(2).setPreferredWidth(130); // Apellido
        tabla.getColumnModel().getColumn(3).setPreferredWidth(130); // Rol
        tabla.getColumnModel().getColumn(4).setPreferredWidth(180); // Area
        tabla.getColumnModel().getColumn(5).setPreferredWidth(250); // Carrera
        tabla.getColumnModel().getColumn(6).setPreferredWidth(110); // Cuatrimestre
        tabla.getColumnModel().getColumn(7).setPreferredWidth(80); // Grupo

        JScrollPane scrollTabla = new JScrollPane(tabla);
        scrollTabla.setBorder(BorderFactory.createEmptyBorder());
        scrollTabla.getViewport().setBackground(Color.WHITE);
        scrollTabla.setPreferredSize(new Dimension(1050, 350));

        // Contador
        JPanel panelContador = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelContador.setBackground(Estilos.COLOR_AZUL);
        panelContador.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));

        lblContador = new JLabel("Mostrando 0 usuarios");
        lblContador.setFont(new Font("Arial", Font.BOLD, 12));
        lblContador.setForeground(Color.WHITE);
        panelContador.add(lblContador);

        panelTabla.add(scrollTabla, BorderLayout.CENTER);
        panelTabla.add(panelContador, BorderLayout.SOUTH);

        return panelTabla;
    }

    // ==================== PANEL DE BOTONES ====================

    private JPanel crearPanelBotones() {
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        panelBotones.setBackground(Estilos.COLOR_BLANCO);
        panelBotones.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnNuevo = new JButton("Nuevo Usuario");
        Estilos.estilizarBotonRedondo(btnNuevo, Estilos.COLOR_VERDE, Estilos.COLOR_BLANCO, 30);
        btnNuevo.setFont(new Font("Arial", Font.BOLD, 13));
        btnNuevo.setToolTipText("Abrir formulario para registrar un nuevo usuario");

        btnEliminar = new JButton("Eliminar Usuario");
        Estilos.estilizarBotonRedondo(btnEliminar, Estilos.COLOR_ERROR, Estilos.COLOR_BLANCO, 30);
        btnEliminar.setFont(new Font("Arial", Font.BOLD, 13));
        btnEliminar.setToolTipText("Eliminar el usuario seleccionado");

        JButton btnRegresar = new JButton("Regresar");
        Estilos.estilizarBotonRedondo(btnRegresar, Estilos.COLOR_AZUL, Estilos.COLOR_BLANCO, 30);
        btnRegresar.setFont(new Font("Arial", Font.BOLD, 13));
        btnRegresar.setToolTipText("Volver al panel principal");

        if (soloLectura) {
            btnNuevo.setVisible(false);
            btnEliminar.setVisible(false);
        }

        panelBotones.add(btnNuevo);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnRegresar);

        configurarEventosBotones(btnRegresar);

        return panelBotones;
    }

    // ==================== BARRA DE ESTADO ====================

    private JPanel crearBarraEstado() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(Estilos.COLOR_AZUL);
        barra.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        barra.setPreferredSize(new Dimension(VENTANA_ANCHO, 28));

        String ayuda = soloLectura
                ? "Modo lectura | F5: Refrescar"
                : "Selecciona un usuario para eliminar | F5: Refrescar";

        JLabel lblAyuda = new JLabel(ayuda);
        lblAyuda.setFont(new Font("Arial", Font.PLAIN, 11));
        lblAyuda.setForeground(new Color(255, 255, 255, 200));
        barra.add(lblAyuda, BorderLayout.WEST);

        return barra;
    }

    // ==================== ATAJOS DE TECLADO ====================

    private void configurarAtajosTeclado() {
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("F5"), "refrescar");
        getRootPane().getActionMap().put("refrescar", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                cargarDatos();
            }
        });
    }

    // ==================== EVENTOS ====================

    private void configurarEventosFiltros(JButton btnLimpiar) {
        cmbFiltroRol.addActionListener(e -> aplicarFiltros());

        txtBusqueda.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                aplicarFiltros();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                aplicarFiltros();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                aplicarFiltros();
            }
        });

        cmbFiltroCarrera.addActionListener(e -> aplicarFiltros());

        btnLimpiar.addActionListener(e -> {
            cmbFiltroRol.setSelectedIndex(0);
            cmbFiltroArea.setSelectedIndex(0);
            txtBusqueda.setText("");
            aplicarFiltros();
        });
    }

    private void configurarEventosBotones(JButton btnRegresar) {
        btnNuevo.addActionListener(e -> {
            dispose();
            new RegistroUsuarioFrame(admin).setVisible(true);
        });

        btnEliminar.addActionListener(e -> eliminarUsuarioSeleccionado());

        btnRegresar.addActionListener(e -> {
            dispose();
            new DashboardFrame(admin).setVisible(true);
        });
    }

    // ==================== CARGA DE DATOS EN FILTROS ====================

    private void cargarAreasEnFiltro() {
        cmbFiltroArea.removeAllItems();
        cmbFiltroArea.addItem("Todas las areas");
        for (String area : catalogoFuncion.listarAreas()) {
            cmbFiltroArea.addItem(area);
        }
        // Cargar todas las carreras inicialmente
        actualizarCarrerasFiltro("Todas las areas");
    }

    private void actualizarCarrerasFiltro(String area) {
        cmbFiltroCarrera.removeAllItems();
        cmbFiltroCarrera.addItem("Todas las carreras");

        List<String> carreras;
        if (area == null || area.equals("Todas las areas")) {
            carreras = catalogoFuncion.listarCarreras();
        } else {
            carreras = catalogoFuncion.listarCarrerasPorArea(area);
        }

        for (String carrera : carreras) {
            cmbFiltroCarrera.addItem(carrera);
        }
    }

    // ==================== FILTROS ====================

    private void aplicarFiltros() {
        String rolSeleccionado = (String) cmbFiltroRol.getSelectedItem();
        String areaSeleccionada = (String) cmbFiltroArea.getSelectedItem();
        String carreraSeleccionada = (String) cmbFiltroCarrera.getSelectedItem();
        String textoBusqueda = txtBusqueda.getText().trim().toLowerCase();

        RowFilter<DefaultTableModel, Object> filtro = new RowFilter<>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Object> entry) {
                // Filtro por Rol (columna 3)
                if (rolSeleccionado != null && !rolSeleccionado.equals("Todos")) {
                    String rolFila = entry.getStringValue(3);
                    if (!rolFila.equalsIgnoreCase(rolSeleccionado)) {
                        return false;
                    }
                }

                // Filtro por Area (columna 4)
                if (areaSeleccionada != null && !areaSeleccionada.equals("Todas las areas")) {
                    String areaFila = entry.getStringValue(4);
                    if (areaFila == null || !areaFila.equals(areaSeleccionada)) {
                        return false;
                    }
                }

                // Filtro por Carrera (columna 5)
                if (carreraSeleccionada != null && !carreraSeleccionada.equals("Todas las carreras")) {
                    String carreraFila = entry.getStringValue(5);
                    if (carreraFila == null || !carreraFila.equals(carreraSeleccionada)) {
                        return false;
                    }
                }

                // Filtro por texto
                if (!textoBusqueda.isEmpty()) {
                    String matricula = String.valueOf(entry.getValue(0));
                    String nombre = entry.getStringValue(1).toLowerCase();
                    String apellido = entry.getStringValue(2).toLowerCase();

                    if (!matricula.contains(textoBusqueda) &&
                            !nombre.contains(textoBusqueda) &&
                            !apellido.contains(textoBusqueda)) {
                        return false;
                    }
                }

                return true;
            }
        };

        sorter.setRowFilter(filtro);
        actualizarContador();
    }

    // ==================== ELIMINAR USUARIO ====================

    private void eliminarUsuarioSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona un usuario de la tabla para eliminar",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = tabla.convertRowIndexToModel(fila);
        int matricula = (int) model.getValueAt(modelRow, 0);
        String nombre = (String) model.getValueAt(modelRow, 1);
        String apellido = (String) model.getValueAt(modelRow, 2);
        String rol = (String) model.getValueAt(modelRow, 3);

        if (matricula == admin.getMatricula()) {
            JOptionPane.showMessageDialog(this,
                    "No puedes eliminar tu propio usuario",
                    "Operacion no permitida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Object[] opciones = { "Si, eliminar", "Cancelar" };
        int confirmacion = JOptionPane.showOptionDialog(this,
                "<html><div style='padding:10px;'>" +
                        "<h3 style='color:#E74C3C;'>Confirmar eliminacion</h3>" +
                        "<p>Estas seguro de eliminar este usuario?</p>" +
                        "<table>" +
                        "<tr><td><b>Matricula:</b></td><td>" + matricula + "</td></tr>" +
                        "<tr><td><b>Nombre:</b></td><td>" + nombre + " " + apellido + "</td></tr>" +
                        "<tr><td><b>Rol:</b></td><td>" + rol + "</td></tr>" +
                        "</table>" +
                        "<p style='color:#E74C3C;'><b>Esta accion no se puede deshacer</b></p>" +
                        "</div></html>",
                "Confirmar eliminacion",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
                null, opciones, opciones[1]);

        if (confirmacion != JOptionPane.YES_OPTION)
            return;

        String resultado = funcion.eliminar(matricula);
        cargarDatos();

        JOptionPane.showMessageDialog(this, resultado,
                resultado.contains("correctamente") ? "Exito" : "Aviso",
                resultado.contains("correctamente") ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
    }

    // ==================== CARGA DE DATOS ====================

    private void cargarDatos() {
        model.setRowCount(0);
        List<Usuario> usuarios = funcion.listarTodos();

        for (Usuario u : usuarios) {
            String area = "";
            String carrera = "";
            String cuatrimestre = "";
            String grupo = "";

            if (u instanceof Alumno) {
                Alumno alumno = (Alumno) u;
                carrera = alumno.getCarrera() != null ? alumno.getCarrera() : "";
                cuatrimestre = alumno.getCuatrimestre() != null ? alumno.getCuatrimestre() : "";
                grupo = alumno.getGrupo() != null ? alumno.getGrupo() : "";
                // Obtener area desde la carrera
                area = obtenerAreaDeCarrera(carrera);
            }

            model.addRow(new Object[] {
                    u.getMatricula(),
                    u.getNombre(),
                    u.getApellidoPaterno(),
                    u.getRol().toString(),
                    area,
                    carrera,
                    cuatrimestre,
                    grupo
            });
        }

        actualizarContador();
    }

    /**
     * Obtiene el area correspondiente a una carrera.
     * Busca en el catalogo de areas y carreras.
     */
    private String obtenerAreaDeCarrera(String carrera) {
        if (carrera == null || carrera.isEmpty())
            return "";

        // Buscar en cada area
        for (String area : catalogoFuncion.listarAreas()) {
            for (String c : catalogoFuncion.listarCarrerasPorArea(area)) {
                if (c.equals(carrera)) {
                    return area;
                }
            }
        }
        return "";
    }

    private void actualizarContador() {
        lblContador.setText("Mostrando " + tabla.getRowCount() + " usuarios");
    }
}