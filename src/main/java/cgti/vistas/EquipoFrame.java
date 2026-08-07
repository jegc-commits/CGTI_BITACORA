package cgti.vistas;

import cgti.guardado.*;
import cgti.clase.*;
import cgti.funcion.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;

public class EquipoFrame extends JFrame {

    private static final int VENTANA_ANCHO = 900;
    private static final int VENTANA_ALTO = 700;
    private static final int ALTURA_BARRA = 100;

    private EquipoFuncion equipoFuncion = new EquipoFuncion();
    private CatalogoFuncion catalogoFuncion = new CatalogoFuncion();

    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTable tabla;

    private JComboBox<String> cmbLabFiltro;
    private JTextField txtBusqueda;

    private JComboBox<String> cmbLabForm;
    private JTextField txtNombre;
    private JTextArea txtSoftware;
    private JPanel panelSoftwareCheckboxes;
    private JScrollPane scrollSoftwareCheckboxes;
    private JButton btnGuardar;
    private JButton btnModificar;
    private JButton btnEliminar;
    private JButton btnCancelar;
    private JButton btnLimpiar;

    private JLabel lblMensaje;
    private JLabel lblContador;
    private JPanel panelFormulario;

    private Usuario admin;
    private boolean soloLectura;
    private Long equipoEnEdicion = null;
    private String nombreOriginal = null;

    // Lista para mantener referencia a los checkboxes de software
    private List<JCheckBox> checkboxesSoftware = new ArrayList<>();

    public EquipoFrame(Usuario admin) {
        this.admin = admin;
        this.soloLectura = admin.getRol() == Rol.OBSERVADOR;

        configurarVentana();
        add(crearBarraSuperior(), BorderLayout.NORTH);
        add(crearPanelPrincipal(), BorderLayout.CENTER);
        add(crearBarraEstado(), BorderLayout.SOUTH);

        configurarAtajosTeclado();
        cargarDatos();
        setVisible(true);
    }

    private void configurarVentana() {
        setTitle("Gestion de Equipos - CGTI");
        setSize(VENTANA_ANCHO, VENTANA_ALTO);
        setMinimumSize(new Dimension(800, 600));
        Estilos.bloquearCierreConX(this, "Usa el boton Regresar para volver al panel principal");
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Estilos.COLOR_BLANCO);
    }

    private JPanel crearBarraSuperior() {
        JPanel barra = new JPanel(new BorderLayout()) {
            private Image imagenFondo;

            {
                try {
                    imagenFondo = new ImageIcon(
                            getClass().getResource("/imagenes/d3.jpg")).getImage();
                } catch (Exception e) {
                    System.err.println("Error al cargar imagen: " + e.getMessage());
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

    private JPanel crearPanelPrincipal() {
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBackground(Estilos.COLOR_GRIS_CLARO);
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel panelIzquierdo = new JPanel(new BorderLayout(0, 10));
        panelIzquierdo.setBackground(Estilos.COLOR_GRIS_CLARO);
        panelIzquierdo.add(crearPanelFiltros(), BorderLayout.NORTH);
        panelIzquierdo.add(crearPanelTabla(), BorderLayout.CENTER);

        JPanel panelFormularioContenedor = crearPanelFormulario();

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                panelIzquierdo,
                panelFormularioContenedor);
        splitPane.setDividerLocation(550);
        splitPane.setDividerSize(5);
        splitPane.setBorder(null);
        splitPane.setBackground(Estilos.COLOR_GRIS_CLARO);

        panelPrincipal.add(splitPane, BorderLayout.CENTER);
        return panelPrincipal;
    }

    private JPanel crearPanelFiltros() {
        JPanel panelFiltros = new JPanel(new BorderLayout(10, 0));
        panelFiltros.setBackground(Color.WHITE);
        panelFiltros.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilos.COLOR_BORDE, 1, true),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)));

        JPanel panelIzq = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelIzq.setBackground(Color.WHITE);

        JLabel lblFiltro = new JLabel("Filtrar por laboratorio:");
        lblFiltro.setFont(new Font("Arial", Font.BOLD, 13));
        lblFiltro.setForeground(Estilos.COLOR_AZUL);
        panelIzq.add(lblFiltro);

        cmbLabFiltro = new JComboBox<>();
        cmbLabFiltro.addItem("Todos");
        List<String> laboratorios = catalogoFuncion.listarlaboratorio();
        for (String lab : laboratorios) {
            cmbLabFiltro.addItem(lab);
        }
        cmbLabFiltro.setFont(new Font("Arial", Font.PLAIN, 13));
        cmbLabFiltro.setPreferredSize(new Dimension(150, 30));
        panelIzq.add(cmbLabFiltro);

        JPanel panelDer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelDer.setBackground(Color.WHITE);

        JLabel lblBuscar = new JLabel("Buscar:");
        lblBuscar.setFont(new Font("Arial", Font.BOLD, 13));
        lblBuscar.setForeground(Estilos.COLOR_AZUL);
        panelDer.add(lblBuscar);

        txtBusqueda = new JTextField(15);
        txtBusqueda.setFont(new Font("Arial", Font.PLAIN, 13));
        txtBusqueda.setPreferredSize(new Dimension(180, 30));
        txtBusqueda.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilos.COLOR_BORDE),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        txtBusqueda.setToolTipText("Escribe el nombre del equipo para filtrar");
        panelDer.add(txtBusqueda);

        btnLimpiar = new JButton("X");
        btnLimpiar.setFont(new Font("Arial", Font.BOLD, 12));
        btnLimpiar.setForeground(Estilos.COLOR_ERROR);
        btnLimpiar.setBackground(Color.WHITE);
        btnLimpiar.setBorder(BorderFactory.createLineBorder(Estilos.COLOR_BORDE));
        btnLimpiar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLimpiar.setToolTipText("Limpiar todos los filtros");
        btnLimpiar.setPreferredSize(new Dimension(35, 30));
        panelDer.add(btnLimpiar);

        panelFiltros.add(panelIzq, BorderLayout.WEST);
        panelFiltros.add(panelDer, BorderLayout.EAST);

        return panelFiltros;
    }

    private JPanel crearPanelTabla() {
        JPanel panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBackground(Color.WHITE);
        panelTabla.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilos.COLOR_BORDE, 1, true),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        model = new DefaultTableModel(
                new Object[] { "ID", "Laboratorio", "Equipo", "Software" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabla = new JTable(model);
        tabla.setFont(new Font("Arial", Font.PLAIN, 13));
        tabla.setRowHeight(32);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setShowGrid(true);
        tabla.setGridColor(new Color(230, 230, 230));
        tabla.setSelectionBackground(new Color(200, 220, 255));
        tabla.setSelectionForeground(Estilos.COLOR_NEGRO);
        tabla.setIntercellSpacing(new Dimension(5, 5));

        JTableHeader header = tabla.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 13));
        header.setBackground(Estilos.COLOR_AZUL);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 38));
        header.setBorder(BorderFactory.createLineBorder(Estilos.COLOR_AZUL_HOVER));

        sorter = new TableRowSorter<>(model);
        tabla.setRowSorter(sorter);

        JScrollPane scrollTabla = new JScrollPane(tabla);
        scrollTabla.setBorder(BorderFactory.createEmptyBorder());
        scrollTabla.getViewport().setBackground(Color.WHITE);

        JPanel panelContador = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelContador.setBackground(Estilos.COLOR_AZUL);
        panelContador.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));

        lblContador = new JLabel("Mostrando 0 equipos");
        lblContador.setFont(new Font("Arial", Font.BOLD, 12));
        lblContador.setForeground(Color.WHITE);
        panelContador.add(lblContador);

        panelTabla.add(scrollTabla, BorderLayout.CENTER);
        panelTabla.add(panelContador, BorderLayout.SOUTH);

        return panelTabla;
    }

    private JPanel crearPanelFormulario() {
        panelFormulario = new JPanel();
        panelFormulario.setLayout(new BoxLayout(panelFormulario, BoxLayout.Y_AXIS));
        panelFormulario.setBackground(Color.WHITE);
        panelFormulario.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilos.COLOR_BORDE, 1, true),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        JLabel lblTituloForm = new JLabel("Nuevo Equipo");
        lblTituloForm.setFont(new Font("Arial", Font.BOLD, 18));
        lblTituloForm.setForeground(Estilos.COLOR_AZUL);
        lblTituloForm.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelFormulario.add(lblTituloForm);
        panelFormulario.add(Box.createVerticalStrut(20));

        // Campo: Laboratorio
        panelFormulario.add(crearEtiquetaFormulario("Laboratorio:"));
        panelFormulario.add(Box.createVerticalStrut(5));
        List<String> laboratorios = catalogoFuncion.listarlaboratorio();
        cmbLabForm = new JComboBox<>(laboratorios.toArray(new String[0]));
        cmbLabForm.setFont(new Font("Arial", Font.PLAIN, 14));
        cmbLabForm.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        cmbLabForm.setAlignmentX(Component.CENTER_ALIGNMENT);
        cmbLabForm.setBackground(Color.WHITE);
        panelFormulario.add(cmbLabForm);
        panelFormulario.add(Box.createVerticalStrut(15));

        // Campo: Nombre del equipo
        panelFormulario.add(crearEtiquetaFormulario("Nombre del equipo:"));
        panelFormulario.add(Box.createVerticalStrut(5));
        txtNombre = new JTextField();
        Estilos.estilizarCampoTexto(txtNombre);
        txtNombre.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        txtNombre.setAlignmentX(Component.CENTER_ALIGNMENT);
        txtNombre.setToolTipText("Ejemplo: COMP01, LAPTOP05, SERVIDOR02");
        panelFormulario.add(txtNombre);
        panelFormulario.add(Box.createVerticalStrut(15));

        // Campo: Software instalado (Checkboxes desde BD)
        panelFormulario.add(crearEtiquetaFormulario("Software instalado:"));
        panelFormulario.add(Box.createVerticalStrut(5));

        // Panel con checkboxes de software
        panelSoftwareCheckboxes = new JPanel();
        panelSoftwareCheckboxes.setLayout(new BoxLayout(panelSoftwareCheckboxes, BoxLayout.Y_AXIS));
        panelSoftwareCheckboxes.setBackground(Color.WHITE);
        panelSoftwareCheckboxes.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilos.COLOR_BORDE),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        // Cargar software desde la base de datos
        cargarCheckboxesSoftware();

        scrollSoftwareCheckboxes = new JScrollPane(panelSoftwareCheckboxes);
        scrollSoftwareCheckboxes.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        scrollSoftwareCheckboxes.setAlignmentX(Component.CENTER_ALIGNMENT);
        scrollSoftwareCheckboxes.setBorder(BorderFactory.createEmptyBorder());
        panelFormulario.add(scrollSoftwareCheckboxes);
        panelFormulario.add(Box.createVerticalStrut(20));

        // Mensaje de estado
        lblMensaje = new JLabel(" ");
        lblMensaje.setFont(new Font("Arial", Font.PLAIN, 12));
        lblMensaje.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelFormulario.add(lblMensaje);
        panelFormulario.add(Box.createVerticalStrut(15));

        // Panel de botones
        panelFormulario.add(crearPanelBotonesFormulario());
        panelFormulario.add(Box.createVerticalStrut(15));

        // Boton Regresar
        JButton btnRegresar = crearBotonFormulario("Regresar", Estilos.COLOR_AZUL);
        btnRegresar.setToolTipText("Volver al panel principal");
        btnRegresar.addActionListener(e -> dispose());
        panelFormulario.add(btnRegresar);

        configurarEventos();

        if (soloLectura) {
            configurarModoLectura();
        }

        return panelFormulario;
    }

    /**
     * Carga los checkboxes de software desde la base de datos
     */
    private void cargarCheckboxesSoftware() {
        checkboxesSoftware.clear();
        panelSoftwareCheckboxes.removeAll();

        List<String> softwareList = catalogoFuncion.listarSoftware();

        for (String software : softwareList) {
            JCheckBox checkBox = new JCheckBox(software);
            checkBox.setFont(new Font("Arial", Font.PLAIN, 13));
            checkBox.setBackground(Color.WHITE);
            checkBox.setFocusPainted(false);
            checkboxesSoftware.add(checkBox);
            panelSoftwareCheckboxes.add(checkBox);
        }

        panelSoftwareCheckboxes.revalidate();
        panelSoftwareCheckboxes.repaint();
    }

    /**
     * Obtiene el software seleccionado como una cadena separada por comas
     */
    private String getSoftwareSeleccionado() {
        StringBuilder software = new StringBuilder();
        for (JCheckBox checkBox : checkboxesSoftware) {
            if (checkBox.isSelected()) {
                if (software.length() > 0) {
                    software.append(", ");
                }
                software.append(checkBox.getText());
            }
        }
        return software.toString();
    }

    /**
     * Marca los checkboxes según el software guardado en el equipo
     */
    private void setSoftwareSeleccionado(String softwareStr) {
        // Desmarcar todos primero
        for (JCheckBox checkBox : checkboxesSoftware) {
            checkBox.setSelected(false);
        }

        if (softwareStr != null && !softwareStr.isEmpty()) {
            String[] softwareArray = softwareStr.split(",\\s*");
            for (String sw : softwareArray) {
                for (JCheckBox checkBox : checkboxesSoftware) {
                    if (checkBox.getText().equals(sw.trim())) {
                        checkBox.setSelected(true);
                        break;
                    }
                }
            }
        }
    }

    private JPanel crearPanelBotonesFormulario() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnGuardar = crearBotonFormulario("Guardar Equipo", Estilos.COLOR_VERDE);
        btnGuardar.setToolTipText("Guardar nuevo equipo (Ctrl+G)");
        panel.add(btnGuardar);
        panel.add(Box.createVerticalStrut(8));

        btnCancelar = crearBotonFormulario("Cancelar Edicion", Estilos.COLOR_ADVERTENCIA);
        btnCancelar.setToolTipText("Cancelar edicion actual (ESC)");
        btnCancelar.setVisible(false);
        panel.add(btnCancelar);
        panel.add(Box.createVerticalStrut(8));

        btnModificar = crearBotonFormulario("Modificar Seleccionado", Estilos.COLOR_INFO);
        btnModificar.setToolTipText("Cargar equipo seleccionado para editar");
        panel.add(btnModificar);
        panel.add(Box.createVerticalStrut(8));

        btnEliminar = crearBotonFormulario("Eliminar Seleccionado", Estilos.COLOR_ERROR);
        btnEliminar.setToolTipText("Eliminar equipo seleccionado");
        panel.add(btnEliminar);

        return panel;
    }

    private JPanel crearBarraEstado() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(Estilos.COLOR_AZUL);
        barra.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        barra.setPreferredSize(new Dimension(VENTANA_ANCHO, 28));

        JLabel lblAyuda = new JLabel(
                "Doble clic en la tabla para editar | Ctrl+G: Guardar | ESC: Cancelar | F5: Refrescar");
        lblAyuda.setFont(new Font("Arial", Font.PLAIN, 11));
        lblAyuda.setForeground(new Color(255, 255, 255, 200));
        barra.add(lblAyuda, BorderLayout.WEST);

        return barra;
    }

    private JLabel crearEtiquetaFormulario(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Arial", Font.BOLD, 13));
        lbl.setForeground(Estilos.COLOR_NEGRO);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lbl;
    }

    private JButton crearBotonFormulario(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(color);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        Color colorOscuro = oscurecer(color, 0.85f);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(colorOscuro);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(color);
            }
        });

        return btn;
    }

    private Color oscurecer(Color color, float factor) {
        return new Color(
                Math.max((int) (color.getRed() * factor), 0),
                Math.max((int) (color.getGreen() * factor), 0),
                Math.max((int) (color.getBlue() * factor), 0));
    }

    private void configurarAtajosTeclado() {
        JRootPane rootPane = getRootPane();

        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("control G"), "guardar");
        rootPane.getActionMap().put("guardar", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (btnGuardar.isEnabled() && btnGuardar.isVisible()) {
                    btnGuardar.doClick();
                }
            }
        });

        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("ESCAPE"), "cancelar");
        rootPane.getActionMap().put("cancelar", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (btnCancelar.isVisible()) {
                    btnCancelar.doClick();
                }
            }
        });

        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("F5"), "refrescar");
        rootPane.getActionMap().put("refrescar", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                cargarDatos();
            }
        });
    }

    private void configurarEventos() {
        cmbLabFiltro.addActionListener(e -> aplicarFiltros());

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

        btnLimpiar.addActionListener(e -> {
            cmbLabFiltro.setSelectedIndex(0);
            txtBusqueda.setText("");
            aplicarFiltros();
        });

        btnGuardar.addActionListener(e -> guardarOActualizarEquipo());
        btnCancelar.addActionListener(e -> limpiarFormulario());
        btnModificar.addActionListener(e -> cargarEquipoParaEditar());
        btnEliminar.addActionListener(e -> eliminarEquipo());

        tabla.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    cargarEquipoParaEditar();
                }
            }
        });
    }

    private void aplicarFiltros() {
        String labSeleccionado = (String) cmbLabFiltro.getSelectedItem();
        String textoBusqueda = txtBusqueda.getText().trim().toLowerCase();

        RowFilter<DefaultTableModel, Object> filtro = new RowFilter<>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Object> entry) {
                if (labSeleccionado != null && !labSeleccionado.equals("Todos")) {
                    String labFila = entry.getStringValue(1);
                    if (!labFila.equals(labSeleccionado)) {
                        return false;
                    }
                }

                if (!textoBusqueda.isEmpty()) {
                    String nombreFila = entry.getStringValue(2).toLowerCase();
                    if (!nombreFila.contains(textoBusqueda)) {
                        return false;
                    }
                }

                return true;
            }
        };

        sorter.setRowFilter(filtro);
        actualizarContador();
    }

    private void configurarModoLectura() {
        cmbLabForm.setEnabled(false);
        txtNombre.setEnabled(false);
        for (JCheckBox checkBox : checkboxesSoftware) {
            checkBox.setEnabled(false);
        }
        btnGuardar.setVisible(false);
        btnCancelar.setVisible(false);
        btnModificar.setVisible(false);
        btnEliminar.setVisible(false);

        mostrarMensaje("Modo solo lectura: el observador no puede modificar equipos", Estilos.COLOR_INFO);
    }

    private void guardarOActualizarEquipo() {
        String nombre = txtNombre.getText().trim();

        if (nombre.isEmpty()) {
            mostrarError("El nombre del equipo es obligatorio");
            txtNombre.requestFocus();
            return;
        }

        if (!nombre.matches("^[a-zA-Z0-9\\-\\_]+$")) {
            mostrarError("Solo se permiten letras, numeros, guiones y guiones bajos");
            txtNombre.requestFocus();
            return;
        }

        String softwareSeleccionado = getSoftwareSeleccionado();
        if (softwareSeleccionado.isEmpty()) {
            mostrarError("Selecciona al menos un software");
            return;
        }

        Equipo equipo = new Equipo();
        String labSeleccionado = (String) cmbLabForm.getSelectedItem();
        equipo.setLaboratorio(labSeleccionado);
        equipo.setNombreEqp(nombre.toUpperCase());
        equipo.setSoftware(softwareSeleccionado);

        String resultado;

        if (equipoEnEdicion != null) {
            equipo.setId(equipoEnEdicion);
            resultado = equipoFuncion.actualizar(equipo);

            if (resultado == null) {
                mostrarExito("Equipo actualizado correctamente: " + equipo.getNombreEqp());
                limpiarFormulario();
                cargarDatos();
            } else {
                mostrarError(resultado);
            }
        } else {
            resultado = equipoFuncion.guardar(equipo);

            if (resultado == null) {
                mostrarExito("Equipo guardado correctamente: " + equipo.getNombreEqp());
                limpiarFormulario();
                cargarDatos();
                txtNombre.requestFocus();
            } else {
                mostrarError(resultado);
            }
        }
    }

    private void cargarEquipoParaEditar() {
        int fila = tabla.getSelectedRow();

        if (fila == -1) {
            mostrarError("Selecciona un equipo de la tabla primero");
            return;
        }

        int modelRow = tabla.convertRowIndexToModel(fila);

        Long id = (Long) model.getValueAt(modelRow, 0);
        String laboratorioStr = (String) model.getValueAt(modelRow, 1);
        String nombre = (String) model.getValueAt(modelRow, 2);
        String software = (String) model.getValueAt(modelRow, 3);

        equipoEnEdicion = id;
        nombreOriginal = nombre;

        // Seleccionar el laboratorio correcto
        for (int i = 0; i < cmbLabForm.getItemCount(); i++) {
            if (cmbLabForm.getItemAt(i).equals(laboratorioStr)) {
                cmbLabForm.setSelectedIndex(i);
                break;
            }
        }

        txtNombre.setText(nombre);
        setSoftwareSeleccionado(software);

        btnGuardar.setText("Actualizar Equipo");
        btnGuardar.setBackground(Estilos.COLOR_DORADO);
        btnCancelar.setVisible(true);
        btnModificar.setEnabled(false);
        panelFormulario.setBackground(Estilos.COLOR_FONDO_EDICION);

        mostrarMensaje("Editando: " + nombre + " (ID: " + id + ")", Estilos.COLOR_INFO);
        txtNombre.requestFocus();
        txtNombre.selectAll();
    }

    private void eliminarEquipo() {
        int fila = tabla.getSelectedRow();

        if (fila == -1) {
            mostrarError("Selecciona un equipo de la tabla primero");
            return;
        }

        int modelRow = tabla.convertRowIndexToModel(fila);
        Long id = (Long) model.getValueAt(modelRow, 0);
        String nombre = (String) model.getValueAt(modelRow, 2);
        String lab = (String) model.getValueAt(modelRow, 1);

        Object[] opciones = { "Si, eliminar", "Cancelar" };
        int confirmacion = JOptionPane.showOptionDialog(
                this,
                "<html><div style='padding:10px;'>" +
                        "<h3 style='color:#E74C3C;'>Confirmar eliminacion</h3>" +
                        "<p>Estas seguro de eliminar este equipo?</p>" +
                        "<table>" +
                        "<tr><td><b>ID:</b></td><td>" + id + "</td></tr>" +
                        "<tr><td><b>Nombre:</b></td><td>" + nombre + "</td></tr>" +
                        "<tr><td><b>Laboratorio:</b></td><td>" + lab + "</td></tr>" +
                        "</table>" +
                        "<p style='color:#E74C3C;'><b>Esta accion no se puede deshacer</b></p>" +
                        "</div></html>",
                "Confirmar eliminacion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                opciones,
                opciones[1]);

        if (confirmacion != JOptionPane.YES_OPTION)
            return;

        String resultado = equipoFuncion.eliminar(id);

        if (resultado == null) {
            mostrarExito("Equipo eliminado: " + nombre);

            if (equipoEnEdicion != null && equipoEnEdicion.equals(id)) {
                limpiarFormulario();
            }

            cargarDatos();
        } else {
            mostrarError(resultado);
        }
    }

    private void cargarDatos() {
        model.setRowCount(0);

        List<Equipo> equipos = equipoFuncion.listarTodos();

        for (Equipo eq : equipos) {
            model.addRow(new Object[] {
                    eq.getId(),
                    eq.getLaboratorio().toString(),
                    eq.getNombreEqp(),
                    eq.getSoftware() != null ? eq.getSoftware() : "-"
            });
        }

        actualizarContador();
    }

    private void actualizarContador() {
        int totalFiltrado = tabla.getRowCount();
        lblContador.setText("Mostrando " + totalFiltrado + " equipos");
    }

    private void limpiarFormulario() {
        equipoEnEdicion = null;
        nombreOriginal = null;

        txtNombre.setText("");
        cmbLabForm.setSelectedIndex(0);

        // Desmarcar todos los checkboxes
        for (JCheckBox checkBox : checkboxesSoftware) {
            checkBox.setSelected(false);
        }

        btnGuardar.setText("Guardar Equipo");
        btnGuardar.setBackground(Estilos.COLOR_VERDE);
        btnCancelar.setVisible(false);
        btnModificar.setEnabled(true);

        panelFormulario.setBackground(Color.WHITE);

        tabla.clearSelection();
        lblMensaje.setText(" ");
        txtNombre.requestFocus();
    }

    private void mostrarError(String texto) {
        lblMensaje.setForeground(Estilos.COLOR_ERROR);
        lblMensaje.setText("✗ " + texto);
    }

    private void mostrarExito(String texto) {
        lblMensaje.setForeground(Estilos.COLOR_EXITO);
        lblMensaje.setText("✓ " + texto);
    }

    private void mostrarMensaje(String texto, Color color) {
        lblMensaje.setForeground(color);
        lblMensaje.setText(texto);
    }
}