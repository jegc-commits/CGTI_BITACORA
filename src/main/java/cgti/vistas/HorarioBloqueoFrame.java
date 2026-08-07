package cgti.vistas;

import cgti.clase.*;
import cgti.funcion.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import javax.swing.ListSelectionModel;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.JComponent;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class HorarioBloqueoFrame extends JFrame {

    private static final int VENTANA_ANCHO = 950;
    private static final int VENTANA_ALTO = 680;
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");

    private final HorarioFuncion apartadoFuncion;
    private final Usuario usuarioActual;
    private CatalogoFuncion catalogoFuncion = new CatalogoFuncion();

    private DefaultTableModel modeloTabla;
    private JTable tablaHorarios;
    private JLabel lblContador;
    private JLabel lblMensaje;

    private JComboBox<String> cmbLaboratorio;
    private JComboBox<String> cmbDiaSemana;

    private JButton btnImportarExcel;
    private JButton btnRegresar;

    private List<Apartado> bloqueosActuales;

    // ==================== CONSTRUCTOR ====================

    public HorarioBloqueoFrame(Usuario usuario) {
        this.usuarioActual = usuario;
        this.apartadoFuncion = new HorarioFuncion();

        configurarVentana();
        inicializarComponentes();
        ensamblarInterfaz();
        configurarEventos();
        cargarHorarios();

        setVisible(true);
    }

    private void configurarVentana() {
        setTitle("Horarios Semanales - CGTI");
        setSize(VENTANA_ANCHO, VENTANA_ALTO);
        setMinimumSize(new Dimension(800, 550));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        Estilos.bloquearCierreConX(this, "Usa el boton Regresar");
    }

    // ==================== INICIALIZAR COMPONENTES ====================

    private void inicializarComponentes() {
        List<String> laboratorios = catalogoFuncion.listarlaboratorio();
        cmbLaboratorio = new JComboBox<>(laboratorios.toArray(new String[0]));
        cmbLaboratorio.setFont(new Font("Arial", Font.PLAIN, 13));
        cmbLaboratorio.setPreferredSize(new Dimension(120, 30));

        cmbDiaSemana = new JComboBox<>(new String[] {
                "Todos", "Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado"
        });
        cmbDiaSemana.setFont(new Font("Arial", Font.PLAIN, 13));
        cmbDiaSemana.setPreferredSize(new Dimension(120, 30));

        lblMensaje = new JLabel(" ");
        lblMensaje.setFont(new Font("Arial", Font.PLAIN, 12));

        lblContador = new JLabel("Horarios: 0");
        lblContador.setFont(new Font("Arial", Font.BOLD, 12));
        lblContador.setForeground(Estilos.COLOR_AZUL);

        inicializarTabla();

        btnImportarExcel = crearBoton("Importar Excel", Estilos.COLOR_VERDE);
        btnRegresar = crearBoton("Regresar", Estilos.COLOR_AZUL);
    }

    private void inicializarTabla() {
        String[] columnas = { "Hora", "Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado" };

        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaHorarios = new JTable(modeloTabla);
        tablaHorarios.setFont(new Font("Arial", Font.PLAIN, 12));
        tablaHorarios.setRowHeight(35);
        tablaHorarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaHorarios.setShowGrid(true);
        tablaHorarios.setGridColor(new Color(200, 200, 200));

        JTableHeader header = tablaHorarios.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 13));
        header.setBackground(Estilos.COLOR_AZUL);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 40));

        tablaHorarios.getColumnModel().getColumn(0).setPreferredWidth(80);
        for (int i = 1; i <= 6; i++) {
            tablaHorarios.getColumnModel().getColumn(i).setPreferredWidth(120);
        }

        cargarEstructuraTabla();
    }

    // ==================== ENSAMBLAR INTERFAZ ====================

    private void ensamblarInterfaz() {
        add(crearBarraSuperior(), BorderLayout.NORTH);
        add(crearPanelPrincipal(), BorderLayout.CENTER);
        add(crearBarraEstado(), BorderLayout.SOUTH);
    }

    private JPanel crearBarraSuperior() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(Estilos.COLOR_AZUL);
        barra.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
        barra.setPreferredSize(new Dimension(VENTANA_ANCHO, 70));

        JLabel lblTitulo = new JLabel("Horarios Semanales de Laboratorios");
        lblTitulo.setFont(Estilos.TITULO);
        lblTitulo.setForeground(Estilos.COLOR_BLANCO);
        barra.add(lblTitulo, BorderLayout.WEST);

        return barra;
    }

    private JPanel crearPanelPrincipal() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Estilos.COLOR_GRIS_CLARO);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        panel.add(crearPanelFiltros(), BorderLayout.NORTH);
        panel.add(crearPanelTablaSemanal(), BorderLayout.CENTER);
        panel.add(crearPanelBotones(), BorderLayout.SOUTH);

        return panel;
    }

    private JPanel crearPanelFiltros() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilos.COLOR_BORDE, 1, true),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));

        JLabel lblLab = new JLabel("Laboratorio:");
        lblLab.setFont(new Font("Arial", Font.BOLD, 13));
        panel.add(lblLab);
        panel.add(cmbLaboratorio);

        JLabel lblDia = new JLabel("Dia:");
        lblDia.setFont(new Font("Arial", Font.BOLD, 13));
        panel.add(lblDia);
        panel.add(cmbDiaSemana);

        panel.add(lblMensaje);

        return panel;
    }

    private JScrollPane crearPanelTablaSemanal() {
        JScrollPane scroll = new JScrollPane(tablaHorarios);
        scroll.setBorder(BorderFactory.createLineBorder(Estilos.COLOR_BORDE));
        scroll.getViewport().setBackground(Color.WHITE);

        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelSur.setBackground(Color.WHITE);
        panelSur.add(lblContador);

        JPanel panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBackground(Color.WHITE);
        panelTabla.add(scroll, BorderLayout.CENTER);
        panelTabla.add(panelSur, BorderLayout.SOUTH);

        JScrollPane scrollPrincipal = new JScrollPane(panelTabla);
        scrollPrincipal.setBorder(BorderFactory.createEmptyBorder());
        return scrollPrincipal;
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));

        panel.add(btnImportarExcel);
        panel.add(btnRegresar);

        return panel;
    }

    private JPanel crearBarraEstado() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(Estilos.COLOR_AZUL);
        barra.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        barra.setPreferredSize(new Dimension(VENTANA_ANCHO, 28));

        JLabel lblAyuda = new JLabel("Importa tu archivo Excel (.xls/.xlsx) | F5: Refrescar");
        lblAyuda.setFont(new Font("Arial", Font.PLAIN, 11));
        lblAyuda.setForeground(new Color(255, 255, 255, 200));
        barra.add(lblAyuda, BorderLayout.WEST);

        return barra;
    }

    private JButton crearBoton(String texto, Color color) {
        JButton btn = new JButton(texto);
        Estilos.estilizarBotonRedondo(btn, color, Color.WHITE, 25);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ==================== EVENTOS ====================

    private void configurarEventos() {
        cmbLaboratorio.addActionListener(e -> cargarHorarios());
        cmbDiaSemana.addActionListener(e -> cargarHorarios());

        btnImportarExcel.addActionListener(e -> importarArchivoExcel());
        btnRegresar.addActionListener(e -> dispose());

        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("F5"), "refrescar");
        getRootPane().getActionMap().put("refrescar", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                cargarHorarios();
            }
        });
    }

    // ==================== LOGICA DE TABLA ====================

    private void cargarEstructuraTabla() {
        modeloTabla.setRowCount(0);
        for (int h = 8; h <= 21; h++) {
            modeloTabla.addRow(new Object[] { String.format("%02d:00", h), "", "", "", "", "", "" });
            if (h < 21) {
                modeloTabla.addRow(new Object[] { String.format("%02d:30", h), "", "", "", "", "", "" });
            }
        }
    }

    private void cargarHorarios() {
        for (int row = 0; row < modeloTabla.getRowCount(); row++) {
            for (int col = 1; col <= 6; col++) {
                modeloTabla.setValueAt("", row, col);
            }
        }

        String labSeleccionado = (String) cmbLaboratorio.getSelectedItem();
        String diaFiltro = (String) cmbDiaSemana.getSelectedItem();

        if (labSeleccionado == null)
            return;

        bloqueosActuales = apartadoFuncion.listarBloqueos();
        int contador = 0;

        for (Apartado bloqueo : bloqueosActuales) {
            if (bloqueo.getLaboratorio() == null ||
                    !bloqueo.getLaboratorio().equals(labSeleccionado))
                continue;

            String diaSemana = obtenerDiaSemana(bloqueo);
            if (diaSemana == null)
                continue;

            if (diaFiltro != null && !diaFiltro.equals("Todos") && !diaFiltro.equals(diaSemana))
                continue;

            int columna = obtenerColumnaDia(diaSemana);
            if (columna == -1)
                continue;

            if (bloqueo.getHoraInicio() == null || bloqueo.getHoraFin() == null)
                continue;

            LocalTime inicio = bloqueo.getHoraInicio().toLocalTime();
            LocalTime fin = bloqueo.getHoraFin().toLocalTime();

            for (int row = 0; row < modeloTabla.getRowCount(); row++) {
                String horaStr = (String) modeloTabla.getValueAt(row, 0);
                LocalTime horaFila = LocalTime.parse(horaStr);

                if (!horaFila.isBefore(inicio) && horaFila.isBefore(fin)) {
                    String motivo = bloqueo.getProposito() != null ? bloqueo.getProposito() : "Bloqueado";
                    modeloTabla.setValueAt(motivo, row, columna);
                    contador++;
                }
            }
        }

        lblContador.setText("Horarios: " + contador + " bloques");
    }

    // ==================== IMPORTAR EXCEL ====================

    private void importarArchivoExcel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Importar horarios desde Excel");

        FileNameExtensionFilter filtroExcel = new FileNameExtensionFilter(
                "Archivos Excel (*.xls, *.xlsx)", "xls", "xlsx");
        fileChooser.setFileFilter(filtroExcel);

        if (fileChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
            return;

        File archivo = fileChooser.getSelectedFile();

        try {
            importarExcel(archivo);
        } catch (Exception e) {
            mostrarError("Error al leer archivo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void importarExcel(File archivo) throws Exception {
        Workbook workbook;

        try (FileInputStream fis = new FileInputStream(archivo)) {
            if (archivo.getName().toLowerCase().endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(fis);
            } else {
                workbook = new HSSFWorkbook(fis);
            }
        }

        Sheet hoja = workbook.getSheetAt(0);
        String lab = (String) cmbLaboratorio.getSelectedItem();

        if (lab == null) {
            workbook.close();
            mostrarError("Selecciona un laboratorio primero");
            return;
        }

        int filaEncabezado = -1;
        int colHora = -1;
        for (int i = 0; i <= hoja.getLastRowNum() && filaEncabezado == -1; i++) {
            Row fila = hoja.getRow(i);
            if (fila == null)
                continue;

            for (Cell celda : fila) {
                String valor = obtenerValorCelda(fila, celda.getColumnIndex()).trim();
                if ("HORA".equalsIgnoreCase(valor)) {
                    filaEncabezado = i;
                    colHora = celda.getColumnIndex();
                    break;
                }
            }
        }

        if (filaEncabezado == -1) {
            workbook.close();
            mostrarError("No se encontro la fila de encabezado (HORA) en el archivo");
            return;
        }

        DiaSemana[] dias = { DiaSemana.LUNES, DiaSemana.MARTES, DiaSemana.MIERCOLES,
                DiaSemana.JUEVES, DiaSemana.VIERNES, DiaSemana.SABADO };

        int insertados = 0;
        int conflictos = 0;
        List<String> detalles = new ArrayList<>();

        for (int i = filaEncabezado + 1; i <= hoja.getLastRowNum(); i++) {
            Row fila = hoja.getRow(i);
            if (fila == null)
                continue;

            String rangoHora = obtenerValorCelda(fila, colHora).trim();
            if (rangoHora.isEmpty() || !rangoHora.contains("-"))
                continue;

            String[] partesHora = rangoHora.split("-");
            LocalTime horaInicio;
            LocalTime horaFin;
            try {
                horaInicio = parsearHora(partesHora[0]);
                horaFin = parsearHora(partesHora[1]);
            } catch (Exception e) {
                continue;
            }

            String hIni = horaInicio.format(FORMATO_HORA);
            String hFin = horaFin.format(FORMATO_HORA);

            for (int d = 0; d < dias.length; d++) {
                String motivo = obtenerValorCelda(fila, colHora + 1 + d).trim();
                if (motivo.isEmpty())
                    continue;

                String resultado = apartadoFuncion.importarHorarioClase(lab, dias[d], hIni, hFin, motivo);

                if (resultado.contains("correctamente")) {
                    insertados++;
                } else {
                    conflictos++;
                    detalles.add(dias[d] + " " + hIni + "-" + hFin + ": " + resultado);
                }
            }
        }

        workbook.close();

        String mensaje = String.format("Importacion para %s:%n  Insertados: %d%n  Conflictos: %d",
                lab, insertados, conflictos);

        if (!detalles.isEmpty()) {
            mensaje += "\n\nDetalle:\n" + String.join("\n", detalles);
        }

        JOptionPane.showMessageDialog(this, mensaje, "Resultado de Importacion",
                conflictos == 0 ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);

        cargarHorarios();
    }

    private LocalTime parsearHora(String texto) {
        String limpio = texto.trim();
        String[] partes = limpio.split(":");
        int h = Integer.parseInt(partes[0].trim());
        int m = partes.length > 1 ? Integer.parseInt(partes[1].replaceAll("[^0-9]", "")) : 0;
        return LocalTime.of(h, m);
    }

    private String obtenerValorCelda(Row fila, int columna) {
        Cell celda = fila.getCell(columna);
        if (celda == null)
            return "";

        return switch (celda.getCellType()) {
            case STRING -> celda.getStringCellValue().trim();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(celda)) {
                    yield celda.getLocalDateTimeCellValue().format(FORMATO_HORA);
                }
                yield String.valueOf((long) celda.getNumericCellValue());
            }
            default -> "";
        };
    }

    // ==================== UTILIDADES ====================

    private String obtenerDiaSemana(Apartado bloqueo) {
        if (bloqueo.getDiaSemana() != null) {
            String nombre = bloqueo.getDiaSemana().name();
            return nombre.substring(0, 1).toUpperCase() + nombre.substring(1).toLowerCase();
        }
        if (bloqueo.getHoraInicio() != null) {
            return traducirDiaSemana(bloqueo.getHoraInicio().getDayOfWeek().getValue());
        }
        return null;
    }

    private String traducirDiaSemana(int dia) {
        return switch (dia) {
            case 1 -> "Lunes";
            case 2 -> "Martes";
            case 3 -> "Miercoles";
            case 4 -> "Jueves";
            case 5 -> "Viernes";
            case 6 -> "Sabado";
            default -> null;
        };
    }

    private int obtenerColumnaDia(String dia) {
        return switch (dia) {
            case "Lunes" -> 1;
            case "Martes" -> 2;
            case "Miercoles" -> 3;
            case "Jueves" -> 4;
            case "Viernes" -> 5;
            case "Sabado" -> 6;
            default -> -1;
        };
    }

    private void mostrarError(String texto) {
        lblMensaje.setForeground(Estilos.COLOR_ERROR);
        lblMensaje.setText("X " + texto);
    }

    private void mostrarExito(String texto) {
        lblMensaje.setForeground(Estilos.COLOR_EXITO);
        lblMensaje.setText("> " + texto);
    }
}