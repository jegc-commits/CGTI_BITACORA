package cgti.vistas;

import cgti.clase.*;
import cgti.funcion.RegistroFuncion;
import cgti.funcion.CatalogoFuncion;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;

// Importaciones para iText (generacion de PDF)
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import java.io.File;
import java.io.IOException;

/**
 * Ventana de Bitacora de Registros - CGTI
 * 
 * Permite visualizar y filtrar el historial completo de entradas
 * y salidas de los alumnos en los laboratorios.
 * 
 * Filtros disponibles desde la base de datos:
 * - Fecha especifica o todas las fechas
 * - Carrera
 * - Cuatrimestre
 * - Busqueda por nombre de alumno
 * 
 * El SUPERADMINISTRADOR puede exportar a PDF.
 */
public class BitacoraFrame extends JFrame {

    // ==================== CONSTANTES ====================
    private static final int VENTANA_ANCHO = 1400;
    private static final int VENTANA_ALTO = 800;
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter FORMATO_FECHA_CORTA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");

    // ==================== SERVICIOS ====================
    private RegistroFuncion registroFuncion = new RegistroFuncion();
    private CatalogoFuncion catalogoFuncion = new CatalogoFuncion();

    // ==================== COMPONENTES DE LA INTERFAZ ====================
    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTable tabla;
    private JLabel lblContador;

    // Filtros
    private JComboBox<String> cmbFiltroDia;
    private JComboBox<String> cmbFiltroCarrera;
    private JComboBox<String> cmbFiltroCuatrimestre;
    private JTextField txtBusqueda;
    private JButton btnFiltrar;
    private JButton btnLimpiarFiltros;
    private JButton btnExportarPDF;

    // Datos
    private List<Registro> todosLosRegistros;
    private Usuario admin;

    // Fuentes para PDF
    private PdfFont fontRegular;
    private PdfFont fontBold;

    // ==================== CONSTRUCTOR ====================

    public BitacoraFrame(Usuario admin) {
        this.admin = admin;

        // Inicializar fuentes para el PDF
        inicializarFuentesPDF();

        // Cargar todos los registros desde la base de datos
        todosLosRegistros = registroFuncion.listarTodos();

        // Construir la interfaz
        configurarVentana();
        add(crearBarraSuperior(), BorderLayout.NORTH);
        add(crearPanelPrincipal(), BorderLayout.CENTER);
        add(crearBarraEstado(), BorderLayout.SOUTH);

        // Cargar datos iniciales en la tabla
        cargarDatos(todosLosRegistros);

        setVisible(true);
    }

    // ==================== CONFIGURACION INICIAL ====================

    private void configurarVentana() {
        setTitle("Bitacora de Registros - CGTI");
        setSize(VENTANA_ANCHO, VENTANA_ALTO);
        setMinimumSize(new Dimension(1000, 600));
        Estilos.bloquearCierreConX(this, "Usa el boton Regresar para volver al panel principal");
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Estilos.COLOR_BLANCO);
    }

    private void inicializarFuentesPDF() {
        try {
            fontRegular = PdfFontFactory.createFont("Helvetica");
            fontBold = PdfFontFactory.createFont("Helvetica-Bold");
        } catch (IOException e) {
            System.err.println("Error al cargar fuentes PDF: " + e.getMessage());
        }
    }

    // ==================== BARRA SUPERIOR ====================

    private JPanel crearBarraSuperior() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(Estilos.COLOR_AZUL);
        barra.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
        barra.setPreferredSize(new Dimension(VENTANA_ANCHO, 70));

        JLabel tituloApp = new JLabel("BITASYS - Bitacora de Registros");
        tituloApp.setFont(Estilos.TITULO);
        tituloApp.setForeground(Estilos.COLOR_BLANCO);
        barra.add(tituloApp, BorderLayout.WEST);

        JLabel usuarioLbl = new JLabel("Administrador: " + admin.getNombre() + " " + admin.getApellidoPaterno());
        usuarioLbl.setForeground(new Color(255, 255, 255, 200));
        usuarioLbl.setFont(new Font("Arial", Font.PLAIN, 14));
        barra.add(usuarioLbl, BorderLayout.EAST);

        return barra;
    }

    // ==================== PANEL PRINCIPAL ====================

    private JPanel crearPanelPrincipal() {
        JPanel panelPrincipal = new JPanel(new BorderLayout(15, 0));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panelPrincipal.setBackground(Estilos.COLOR_GRIS_CLARO);

        // Panel de filtros a la izquierda
        JPanel panelFiltros = crearPanelFiltros();
        panelPrincipal.add(panelFiltros, BorderLayout.WEST);

        // Panel central con tabla y botones
        JPanel panelCentral = crearPanelCentral();
        panelPrincipal.add(panelCentral, BorderLayout.CENTER);

        return panelPrincipal;
    }

    private JPanel crearPanelCentral() {
        JPanel panelCentral = new JPanel(new BorderLayout(0, 10));
        panelCentral.setBackground(Estilos.COLOR_GRIS_CLARO);

        // Panel de titulo con contador
        JPanel panelTitulo = new JPanel(new BorderLayout());
        panelTitulo.setBackground(Color.WHITE);
        panelTitulo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilos.COLOR_BORDE, 1, true),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));

        JLabel lblTitulo = new JLabel("Historial de Entradas y Salidas");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setForeground(Estilos.COLOR_AZUL);
        panelTitulo.add(lblTitulo, BorderLayout.WEST);

        lblContador = new JLabel("Total: 0 registros");
        lblContador.setFont(new Font("Arial", Font.PLAIN, 13));
        lblContador.setForeground(Estilos.COLOR_GRIS);
        panelTitulo.add(lblContador, BorderLayout.EAST);

        panelCentral.add(panelTitulo, BorderLayout.NORTH);
        panelCentral.add(crearPanelTabla(), BorderLayout.CENTER);
        panelCentral.add(crearPanelBotones(), BorderLayout.SOUTH);

        return panelCentral;
    }

    // ==================== PANEL DE FILTROS ====================

    private JPanel crearPanelFiltros() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilos.COLOR_BORDE, 1, true),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        panel.setPreferredSize(new Dimension(300, 0));

        // Titulo de la seccion
        JLabel lblTituloFiltros = new JLabel("Filtros de Busqueda");
        lblTituloFiltros.setFont(new Font("Arial", Font.BOLD, 16));
        lblTituloFiltros.setForeground(Estilos.COLOR_AZUL);
        lblTituloFiltros.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblTituloFiltros);
        panel.add(Box.createVerticalStrut(20));

        // ==== FILTRO POR FECHA ====
        panel.add(crearEtiquetaFiltro("Fecha:"));
        panel.add(Box.createVerticalStrut(5));

        List<String> fechas = todosLosRegistros.stream()
                .filter(r -> r.getHoraEntrada() != null)
                .map(r -> r.getHoraEntrada().toLocalDate())
                .distinct()
                .sorted()
                .map(fecha -> fecha.format(FORMATO_FECHA_CORTA))
                .collect(Collectors.toList());
        fechas.add(0, "Todas las fechas");

        cmbFiltroDia = new JComboBox<>(fechas.toArray(new String[0]));
        cmbFiltroDia.setFont(new Font("Arial", Font.PLAIN, 13));
        cmbFiltroDia.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        cmbFiltroDia.setBackground(Color.WHITE);
        cmbFiltroDia.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(cmbFiltroDia);
        panel.add(Box.createVerticalStrut(15));

        // ==== FILTRO POR CARRERA ====
        panel.add(crearEtiquetaFiltro("Carrera:"));
        panel.add(Box.createVerticalStrut(5));

        List<String> carreras = catalogoFuncion.listarCarreras();
        carreras.add(0, "Todas las carreras");

        cmbFiltroCarrera = new JComboBox<>(carreras.toArray(new String[0]));
        cmbFiltroCarrera.setFont(new Font("Arial", Font.PLAIN, 13));
        cmbFiltroCarrera.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        cmbFiltroCarrera.setBackground(Color.WHITE);
        cmbFiltroCarrera.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(cmbFiltroCarrera);
        panel.add(Box.createVerticalStrut(15));

        // ==== FILTRO POR CUATRIMESTRE ====
        panel.add(crearEtiquetaFiltro("Cuatrimestre:"));
        panel.add(Box.createVerticalStrut(5));

        List<String> cuatrimestres = catalogoFuncion.listarCuatrimestres();
        cuatrimestres.add(0, "Todos los cuatrimestres");

        cmbFiltroCuatrimestre = new JComboBox<>(cuatrimestres.toArray(new String[0]));
        cmbFiltroCuatrimestre.setFont(new Font("Arial", Font.PLAIN, 13));
        cmbFiltroCuatrimestre.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        cmbFiltroCuatrimestre.setBackground(Color.WHITE);
        cmbFiltroCuatrimestre.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(cmbFiltroCuatrimestre);
        panel.add(Box.createVerticalStrut(20));

        // ==== SEPARADOR ====
        JSeparator separador = new JSeparator();
        separador.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        separador.setForeground(Estilos.COLOR_BORDE);
        separador.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(separador);
        panel.add(Box.createVerticalStrut(15));

        // ==== BUSQUEDA POR NOMBRE ====
        panel.add(crearEtiquetaFiltro("Buscar alumno:"));
        panel.add(Box.createVerticalStrut(5));

        txtBusqueda = new JTextField();
        txtBusqueda.setFont(new Font("Arial", Font.PLAIN, 13));
        txtBusqueda.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        txtBusqueda.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtBusqueda.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilos.COLOR_BORDE),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        txtBusqueda.setToolTipText("Escribe el nombre del alumno para buscar");
        panel.add(txtBusqueda);
        panel.add(Box.createVerticalStrut(20));

        // ==== BOTONES ====
        btnFiltrar = new JButton("Aplicar Filtros");
        btnFiltrar.setFont(new Font("Arial", Font.BOLD, 14));
        Estilos.estilizarBotonRedondo(btnFiltrar, Estilos.COLOR_AZUL, Estilos.COLOR_BLANCO, 20);
        btnFiltrar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnFiltrar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnFiltrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnFiltrar.setToolTipText("Aplicar todos los filtros seleccionados");
        panel.add(btnFiltrar);
        panel.add(Box.createVerticalStrut(10));

        btnLimpiarFiltros = new JButton("Limpiar Filtros");
        btnLimpiarFiltros.setFont(new Font("Arial", Font.BOLD, 14));
        Estilos.estilizarBotonRedondo(btnLimpiarFiltros, Estilos.COLOR_GRIS, Estilos.COLOR_BLANCO, 20);
        btnLimpiarFiltros.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnLimpiarFiltros.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLimpiarFiltros.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLimpiarFiltros.setToolTipText("Restablecer todos los filtros");
        panel.add(btnLimpiarFiltros);

        panel.add(Box.createVerticalGlue());

        // Configurar eventos
        configurarEventosFiltros();

        return panel;
    }

    // ==================== PANEL DE TABLA ====================

    private JScrollPane crearPanelTabla() {
        model = new DefaultTableModel(
                new Object[] {
                        "FECHA", "NOMBRE DEL ALUMNO", "LABORATORIO", "PRACTICA/PROPOSITO",
                        "HORA ENTRADA", "HORA SALIDA", "NO. COMPUTADORA"
                }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabla = new JTable(model);
        tabla.setFont(new Font("Arial", Font.PLAIN, 11));
        tabla.setRowHeight(28);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setShowGrid(true);
        tabla.setGridColor(new Color(230, 230, 230));
        tabla.setSelectionBackground(new Color(200, 220, 255));
        tabla.setSelectionForeground(Estilos.COLOR_NEGRO);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        JTableHeader header = tabla.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 11));
        header.setBackground(Estilos.COLOR_AZUL);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 45));
        header.setReorderingAllowed(false);

        sorter = new TableRowSorter<>(model);
        tabla.setRowSorter(sorter);

        // Ajustar anchos de columnas (7 columnas)
        tabla.getColumnModel().getColumn(0).setPreferredWidth(85); // Fecha
        tabla.getColumnModel().getColumn(1).setPreferredWidth(220); // Nombr
        tabla.getColumnModel().getColumn(2).setPreferredWidth(110); // Laboratorio
        tabla.getColumnModel().getColumn(3).setPreferredWidth(200); // Practica
        tabla.getColumnModel().getColumn(4).setPreferredWidth(95); // Entrada
        tabla.getColumnModel().getColumn(5).setPreferredWidth(95); // Salida
        tabla.getColumnModel().getColumn(6).setPreferredWidth(110); // Computadora

        JScrollPane scrollTabla = new JScrollPane(tabla);
        scrollTabla.setBorder(BorderFactory.createLineBorder(Estilos.COLOR_BORDE));
        scrollTabla.getViewport().setBackground(Color.WHITE);

        return scrollTabla;
    }

    // ==================== PANEL DE BOTONES ====================

    private JPanel crearPanelBotones() {
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panelBotones.setBackground(Color.WHITE);
        panelBotones.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));

        btnExportarPDF = new JButton("Exportar a PDF");
        Estilos.estilizarBotonRedondo(btnExportarPDF, Estilos.COLOR_ERROR, Estilos.COLOR_BLANCO, 30);
        btnExportarPDF.setFont(new Font("Arial", Font.BOLD, 13));
        btnExportarPDF.setToolTipText("Generar archivo PDF con los registros filtrados");
        btnExportarPDF.setVisible(admin.getRol() == Rol.SUPERADMINISTRADOR);
        panelBotones.add(btnExportarPDF);

        JButton btnRefrescar = new JButton("Refrescar Datos");
        Estilos.estilizarBotonRedondo(btnRefrescar, Estilos.COLOR_INFO, Estilos.COLOR_BLANCO, 30);
        btnRefrescar.setFont(new Font("Arial", Font.BOLD, 13));
        btnRefrescar.setToolTipText("Recargar datos desde la base de datos");
        panelBotones.add(btnRefrescar);

        JButton btnRegresar = new JButton("Regresar");
        Estilos.estilizarBotonRedondo(btnRegresar, Estilos.COLOR_VERDE, Estilos.COLOR_BLANCO, 30);
        btnRegresar.setFont(new Font("Arial", Font.BOLD, 13));
        btnRegresar.setToolTipText("Volver al panel principal");
        panelBotones.add(btnRegresar);

        configurarEventosBotones(btnRefrescar, btnRegresar);

        return panelBotones;
    }

    // ==================== BARRA DE ESTADO ====================

    private JPanel crearBarraEstado() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(Estilos.COLOR_AZUL);
        barra.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        barra.setPreferredSize(new Dimension(VENTANA_ANCHO, 28));

        JLabel lblAyuda = new JLabel("Selecciona los filtros y haz clic en Aplicar Filtros | F5: Refrescar datos");
        lblAyuda.setFont(new Font("Arial", Font.PLAIN, 11));
        lblAyuda.setForeground(new Color(255, 255, 255, 200));
        barra.add(lblAyuda, BorderLayout.WEST);

        return barra;
    }

    // ==================== METODOS AUXILIARES ====================

    private JLabel crearEtiquetaFiltro(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(new Font("Arial", Font.BOLD, 13));
        etiqueta.setForeground(Estilos.COLOR_NEGRO);
        etiqueta.setAlignmentX(Component.LEFT_ALIGNMENT);
        return etiqueta;
    }

    // ==================== CONFIGURACION DE EVENTOS ====================

    private void configurarEventosFiltros() {
        btnFiltrar.addActionListener(e -> aplicarFiltros());
        btnLimpiarFiltros.addActionListener(e -> limpiarFiltros());
        txtBusqueda.addActionListener(e -> aplicarFiltros());
    }

    private void configurarEventosBotones(JButton btnRefrescar, JButton btnRegresar) {
        btnExportarPDF.addActionListener(e -> exportarPDF());

        btnRefrescar.addActionListener(e -> {
            todosLosRegistros = registroFuncion.listarTodos();
            cargarDatos(todosLosRegistros);
            JOptionPane.showMessageDialog(this,
                    "Datos actualizados correctamente",
                    "Refrescar", JOptionPane.INFORMATION_MESSAGE);
        });

        btnRegresar.addActionListener(e -> dispose());

        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("F5"), "refrescar");
        getRootPane().getActionMap().put("refrescar", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                btnRefrescar.doClick();
            }
        });
    }

    // ==================== LOGICA DE FILTROS ====================

    private void aplicarFiltros() {
        List<Registro> filtrados = todosLosRegistros;

        // Filtro 1: Por fecha
        String filtroFecha = (String) cmbFiltroDia.getSelectedItem();
        if (filtroFecha != null && !filtroFecha.equals("Todas las fechas")) {
            LocalDate fechaFiltro = LocalDate.parse(filtroFecha, FORMATO_FECHA_CORTA);
            filtrados = filtrados.stream()
                    .filter(r -> r.getHoraEntrada() != null &&
                            r.getHoraEntrada().toLocalDate().equals(fechaFiltro))
                    .collect(Collectors.toList());
        }

        // Filtro 2: Por carrera
        String filtroCarrera = (String) cmbFiltroCarrera.getSelectedItem();
        if (filtroCarrera != null && !filtroCarrera.equals("Todas las carreras")) {
            filtrados = filtrados.stream()
                    .filter(r -> r.getUsuario() instanceof Alumno &&
                            filtroCarrera.equals(((Alumno) r.getUsuario()).getCarrera()))
                    .collect(Collectors.toList());
        }

        // Filtro 3: Por cuatrimestre
        String filtroCuatrimestre = (String) cmbFiltroCuatrimestre.getSelectedItem();
        if (filtroCuatrimestre != null && !filtroCuatrimestre.equals("Todos los cuatrimestres")) {
            filtrados = filtrados.stream()
                    .filter(r -> r.getUsuario() instanceof Alumno &&
                            filtroCuatrimestre.equals(((Alumno) r.getUsuario()).getCuatrimestre()))
                    .collect(Collectors.toList());
        }

        // Filtro 4: Por nombre de alumno
        String textoBusqueda = txtBusqueda.getText().trim().toLowerCase();
        if (!textoBusqueda.isEmpty()) {
            filtrados = filtrados.stream()
                    .filter(r -> {
                        String nombreCompleto = (r.getUsuario().getNombre() + " " +
                                r.getUsuario().getApellidoPaterno()).toLowerCase();
                        return nombreCompleto.contains(textoBusqueda);
                    })
                    .collect(Collectors.toList());
        }

        cargarDatos(filtrados);
    }

    private void limpiarFiltros() {
        cmbFiltroDia.setSelectedIndex(0);
        cmbFiltroCarrera.setSelectedIndex(0);
        cmbFiltroCuatrimestre.setSelectedIndex(0);
        txtBusqueda.setText("");
        cargarDatos(todosLosRegistros);
    }

    // ==================== CARGA DE DATOS ====================

    private void cargarDatos(List<Registro> registros) {
        model.setRowCount(0);

        for (Registro r : registros) {
            String noComputadora = "";

            // Obtener datos del alumno si corresponde
            if (r.getUsuario() instanceof Alumno) {
                Alumno alumno = (Alumno) r.getUsuario();
            }

            // Obtener nombre del equipo
            if (r.getEquipoNombre() != null && !r.getEquipoNombre().isEmpty()) {
                noComputadora = r.getEquipoNombre();
            } else if (r.getEquipoId() != null) {
                noComputadora = "ID: " + r.getEquipoId();
            }

            // Obtener laboratorio
            String laboratorio = r.getLaboratorio() != null ? r.getLaboratorio().toString() : "";

            model.addRow(new Object[] {
                    r.getHoraEntrada() != null
                            ? r.getHoraEntrada().toLocalDate().format(FORMATO_FECHA_CORTA)
                            : "",
                    r.getUsuario().getNombre() + " " + r.getUsuario().getApellidoPaterno(),
                    laboratorio,
                    r.getProposito() != null ? r.getProposito() : "",
                    r.getHoraEntrada() != null ? r.getHoraEntrada().format(FORMATO_HORA) : "",
                    r.getHoraSalida() != null ? r.getHoraSalida().format(FORMATO_HORA) : "Aun dentro",
                    noComputadora
            });
        }

        lblContador.setText("Total: " + registros.size() + " registros");
    }

    private List<Registro> obtenerRegistrosActuales() {
        String filtroFecha = (String) cmbFiltroDia.getSelectedItem();
        if (filtroFecha != null && !filtroFecha.equals("Todas las fechas")) {
            LocalDate fechaFiltro = LocalDate.parse(filtroFecha, FORMATO_FECHA_CORTA);
            return todosLosRegistros.stream()
                    .filter(r -> r.getHoraEntrada() != null &&
                            r.getHoraEntrada().toLocalDate().equals(fechaFiltro))
                    .collect(Collectors.toList());
        }
        return todosLosRegistros;
    }

    // ==================== EXPORTACION A PDF ====================
    private void exportarPDF() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Guardar Bitacora como PDF");
            fileChooser.setSelectedFile(new File("Bitacora_CGTI_" +
                    LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".pdf"));

            if (fileChooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
                return;
            }

            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".pdf")) {
                filePath += ".pdf";
            }

            PdfWriter writer = new PdfWriter(filePath);
            PdfDocument pdf = new PdfDocument(writer);
            pdf.setDefaultPageSize(PageSize.LETTER.rotate());

            Document document = new Document(pdf);
            document.setMargins(25, 25, 25, 25);

            // ==================== ENCABEZADO ====================
            try {
                Image logo = new Image(ImageDataFactory.create("src/main/resources/imagenes/l1.jpg"));
                logo.setWidth(150);
                logo.setHeight(60);
                document.add(logo);
            } catch (Exception e) {
                document.add(new Paragraph("UNIVERSIDAD TECNOLOGICA DEL NORTE DE GUANAJUATO")
                        .setFont(fontBold).setFontSize(12).setTextAlignment(TextAlignment.CENTER));
            }

            document.add(new Paragraph()
                    .add(new Text("AREA: ").setFont(fontBold))
                    .add(new Text("CENTRO GLOBAL DE TECNOLOGIAS DE LA INFORMACION").setFont(fontBold).setUnderline())
                    .setFontSize(10).setTextAlignment(TextAlignment.CENTER));

            document.add(new Paragraph()
                    .add(new Text("CUATRIMESTRE: ").setFont(fontBold))
                    .add(new Text("Mayo a Agosto de 2026").setFont(fontBold).setUnderline())
                    .setFontSize(10).setTextAlignment(TextAlignment.CENTER));

            document.add(new Paragraph()
                    .add(new Text("BITACORA DE USO DE EQUIPO DE COMPUTO "))
                    .add(new Text("(EXTRA CLASE)").setFont(fontBold))
                    .setFontSize(11).setTextAlignment(TextAlignment.CENTER));

            document.add(new Paragraph(" "));

            // ==================== TABLA PDF (7 COLUMNAS) ====================
            // Los anchos deben coincidir con la cantidad de columnas
            float[] columnWidths = { 0.9f, 2.2f, 1.3f, 2.0f, 1.0f, 1.0f, 1.1f };
            Table tablaPDF = new Table(UnitValue.createPercentArray(columnWidths));
            tablaPDF.useAllAvailableWidth();
            tablaPDF.setFontSize(7);

            // 7 encabezados
            String[] encabezados = {
                    "FECHA", "NOMBRE DEL ALUMNO",
                    "LABORATORIO", "PRACTICA A EJECUTAR",
                    "HORA ENTRADA", "HORA SALIDA", "No. COMP."
            };

            for (String encabezado : encabezados) {
                tablaPDF.addCell(new Cell()
                        .add(new Paragraph(encabezado).setFont(fontBold).setFontSize(6))
                        .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setPadding(3));
            }

            // Datos de la tabla
            List<Registro> registros = obtenerRegistrosActuales();
            int maxFilas = 15;

            for (int i = 0; i < maxFilas; i++) {
                if (i < registros.size()) {
                    agregarFilaPDF(tablaPDF, registros.get(i));
                } else {
                    agregarFilaVaciaPDF(tablaPDF);
                }
            }

            document.add(tablaPDF);

            // ==================== PIE DE PAGINA ====================
            document.add(new Paragraph(" "));
            document.add(new Paragraph("FJCG-05").setFontSize(8).setTextAlignment(TextAlignment.RIGHT));

            document.close();

            JOptionPane.showMessageDialog(this,
                    "PDF generado correctamente en:\n" + filePath,
                    "Exportacion Exitosa", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error al generar PDF:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Agrega una fila con datos de un registro a la tabla PDF.
     * 7 celdas: FECHA, NOMBRE, LABORATORIO, PRACTICA, HORA ENTRADA, HORA SALIDA,
     * COMPUTADORA
     */
    private void agregarFilaPDF(Table tablaPDF, Registro r) {
        // 1. FECHA
        tablaPDF.addCell(crearCeldaPDF(r.getHoraEntrada() != null
                ? r.getHoraEntrada().toLocalDate().format(FORMATO_FECHA_CORTA)
                : ""));

        // 2. NOMBRE DEL ALUMNO
        tablaPDF.addCell(crearCeldaPDF(r.getUsuario().getNombre() + " " + r.getUsuario().getApellidoPaterno()));

        // 3. LABORATORIO
        tablaPDF.addCell(crearCeldaPDF(r.getLaboratorio() != null ? r.getLaboratorio().toString() : ""));

        // 4. PRACTICA A EJECUTAR
        tablaPDF.addCell(crearCeldaPDF(r.getProposito() != null ? r.getProposito() : ""));

        // 5. HORA ENTRADA
        tablaPDF.addCell(crearCeldaPDF(r.getHoraEntrada() != null
                ? r.getHoraEntrada().format(FORMATO_HORA)
                : ""));

        // 6. HORA SALIDA
        tablaPDF.addCell(crearCeldaPDF(r.getHoraSalida() != null
                ? r.getHoraSalida().format(FORMATO_HORA)
                : ""));

        // 7. No. COMPUTADORA
        String computadora = "";
        if (r.getEquipoNombre() != null && !r.getEquipoNombre().isEmpty()) {
            computadora = r.getEquipoNombre();
        } else if (r.getEquipoId() != null) {
            computadora = "ID: " + r.getEquipoId();
        }
        tablaPDF.addCell(crearCeldaPDF(computadora));
    }

    /**
     * Agrega una fila vacia a la tabla PDF.
     * DEBEN SER 7 CELDAS para que coincida con los encabezados.
     */
    private void agregarFilaVaciaPDF(Table tablaPDF) {
        for (int i = 0; i < 7; i++) { // 7 columnas
            tablaPDF.addCell(crearCeldaPDF(""));
        }
    }

    /**
     * Crea una celda estandar para el PDF.
     */
    private Cell crearCeldaPDF(String texto) {
        return new Cell()
                .add(new Paragraph(texto).setFontSize(7))
                .setPadding(3)
                .setTextAlignment(TextAlignment.CENTER);
    }
}