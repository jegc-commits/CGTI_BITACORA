package cgti.vistas;

import cgti.clase.*;
import cgti.funcion.RegistroFuncion;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class BitacoraFrame extends JFrame {

    private RegistroFuncion registroFuncion = new RegistroFuncion();
    private DefaultTableModel model;
    private JTable tabla;
    private DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private List<Registro> todosLosRegistros;
    private JComboBox<String> cmbFiltroDia;

    private JButton btnExportar;
    private JButton btnExcel;
    private JButton btnPdf;
    private JButton btnRegresar;

    public BitacoraFrame(Usuario admin) {
        setTitle("Bitácora de Registros - CGTI");
        setSize(800, 600);
        Estilos.bloquearCierreConX(this, "usar boton regresar");
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        todosLosRegistros = registroFuncion.listarTodos();

        JPanel barra = new JPanel(null) {
            private Image imagenFondo;
            {
                try {
                    imagenFondo = new ImageIcon(getClass().getResource("/imagenes/d3.jpg")).getImage();
                    System.out.println("Imagen cargada correctamente");
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

        barra.setPreferredSize(new Dimension(500, 100));
        add(barra, BorderLayout.NORTH);

        JPanel panelFormulario = new JPanel();
        panelFormulario.setLayout(new BoxLayout(panelFormulario, BoxLayout.Y_AXIS));
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        panelFormulario.setBackground(Estilos.COLOR_BLANCO);

        JLabel lblTitulo = new JLabel("Bitácora de Entradas y Salidas");
        lblTitulo.setFont(Estilos.GRANDE);
        lblTitulo.setForeground(Estilos.COLOR_NEGRO);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblUsuario = new JLabel("Administrador: " + admin.getNombre() + " " + admin.getApellidoPaterno());
        lblUsuario.setFont(Estilos.TITULO.deriveFont(Font.PLAIN, 14));
        lblUsuario.setForeground(Estilos.COLOR_NEGRO);
        lblUsuario.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel panelFiltro = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        panelFiltro.setBackground(Estilos.COLOR_BLANCO);
        panelFiltro.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelFiltro.setMaximumSize(new Dimension(740, 40));

        JLabel lblFiltro = new JLabel("Filtrar por día:");
        lblFiltro.setFont(Estilos.TITULO.deriveFont(Font.PLAIN, 14));
        lblFiltro.setForeground(Estilos.COLOR_NEGRO);

        List<String> dias = todosLosRegistros.stream()
                .filter(r -> r.getHoraEntrada() != null)
                .map(r -> r.getHoraEntrada().toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE))
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        dias.add(0, "Todos los días");

        cmbFiltroDia = new JComboBox<>(dias.toArray(new String[0]));
        cmbFiltroDia.setFont(Estilos.TITULO.deriveFont(Font.PLAIN, 13));
        cmbFiltroDia.setPreferredSize(new Dimension(200, 30));
        cmbFiltroDia.setBackground(Color.WHITE);

        JButton btnFiltrar = new JButton("Filtrar");
        btnFiltrar.setFont(Estilos.PEQUEÑO);
        Estilos.estilizarBotonRedondo(btnFiltrar, Estilos.COLOR_AZUL, Estilos.COLOR_BLANCO, 20);
        btnFiltrar.setPreferredSize(new Dimension(100, 30));

        panelFiltro.add(lblFiltro);
        panelFiltro.add(cmbFiltroDia);
        panelFiltro.add(btnFiltrar);

        model = new DefaultTableModel(new Object[] {
                "Fecha",
                "<html><center>Apellidos,<br>Nombre(s)</html></center>",
                "Grupo",
                "<html><center>Laboratorio<br>a Usar</center></html>",
                "<html><center>Práctica a<br>Efectuar</center></html>",
                "<html><center>Hora<br>Entrada</center></html>",
                "<html><center>Hora<br>Salida</center></html>",
                "<html><center>No. De<br>Computadora</center></html>",
        }, 0);

        tabla = new JTable(model);
        tabla.getTableHeader().setPreferredSize(new Dimension(0, 40));
        tabla.setFont(Estilos.TITULO.deriveFont(Font.PLAIN, 12));
        tabla.getTableHeader().setFont(Estilos.TITULO.deriveFont(Font.BOLD, 12));
        tabla.getTableHeader().setBackground(Estilos.COLOR_AZUL);
        tabla.getTableHeader().setForeground(Estilos.COLOR_BLANCO);
        tabla.setRowHeight(25);

        JScrollPane scrollTabla = new JScrollPane(tabla);
        scrollTabla.setAlignmentX(Component.CENTER_ALIGNMENT);
        scrollTabla.setPreferredSize(new Dimension(740, 280));
        scrollTabla.setMaximumSize(new Dimension(740, 280));

        cargarDatos(todosLosRegistros);

        btnExportar = new JButton("Exportar a TXT");
        btnExcel = new JButton("Exportar a Excel");
        btnPdf = new JButton("Exportar a PDF");
        btnRegresar = new JButton("Regresar");

        Estilos.estilizarBotonRedondo(btnExportar, Estilos.COLOR_VERDE, Estilos.COLOR_BLANCO, 30);
        Estilos.estilizarBotonRedondo(btnExcel, Estilos.COLOR_VERDE, Estilos.COLOR_BLANCO, 30);
        Estilos.estilizarBotonRedondo(btnPdf, Estilos.COLOR_VERDE, Estilos.COLOR_BLANCO, 30);
        Estilos.estilizarBotonRedondo(btnRegresar, Estilos.COLOR_VERDE, Estilos.COLOR_BLANCO, 30);

        btnExportar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnExcel.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnPdf.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRegresar.setAlignmentX(Component.CENTER_ALIGNMENT);

        boolean esSuperAdmin = (admin.getRol() == Rol.SUPERADMINISTRADOR);
        btnExportar.setVisible(esSuperAdmin);
        btnExcel.setVisible(esSuperAdmin);
        btnPdf.setVisible(esSuperAdmin);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panelBotones.setBackground(Estilos.COLOR_BLANCO);
        panelBotones.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelBotones.add(btnExportar);
        panelBotones.add(btnExcel);
        panelBotones.add(btnPdf);
        panelBotones.add(btnRegresar);

        btnFiltrar.addActionListener(e -> {
            String filtro = (String) cmbFiltroDia.getSelectedItem();
            if (filtro.equals("Todos los días")) {
                cargarDatos(todosLosRegistros);
            } else {
                LocalDate fechaFiltro = LocalDate.parse(filtro);
                List<Registro> filtrados = todosLosRegistros.stream()
                        .filter(r -> r.getHoraEntrada() != null &&
                                r.getHoraEntrada().toLocalDate().equals(fechaFiltro))
                        .collect(Collectors.toList());
                cargarDatos(filtrados);
            }
        });

        btnExportar.addActionListener(e -> exportarTXT());
        btnExcel.addActionListener(e -> exportarExcel());
        btnPdf.addActionListener(e -> exportarPDF());
        btnRegresar.addActionListener(e -> {
            dispose();
            new DashboardFrame(admin).setVisible(true);
        });

        panelFormulario.add(lblTitulo);
        panelFormulario.add(Box.createVerticalStrut(8));
        panelFormulario.add(lblUsuario);
        panelFormulario.add(Box.createVerticalStrut(15));
        panelFormulario.add(panelFiltro);
        panelFormulario.add(Box.createVerticalStrut(15));
        panelFormulario.add(scrollTabla);
        panelFormulario.add(Box.createVerticalStrut(20));
        panelFormulario.add(panelBotones);

        add(panelFormulario, BorderLayout.CENTER);
        setVisible(true);
    }

    private void cargarDatos(List<Registro> registros) {
        model.setRowCount(0);
        for (Registro r : registros) {
            String grupo = "";
            if (r.getUsuario() instanceof Alumno) {
                grupo = ((Alumno) r.getUsuario()).getGrupo();
            }

            model.addRow(new Object[] {
                    r.getHoraEntrada() != null ? r.getHoraEntrada().toLocalDate().toString() : "",
                    r.getUsuario().getApellidoPaterno() + ", " + r.getUsuario().getNombre(),
                    grupo,
                    r.getLaboratorio() != null ? r.getLaboratorio().toString() : "",
                    r.getProposito(),
                    r.getHoraEntrada() != null ? r.getHoraEntrada().format(fmt) : "",
                    r.getHoraSalida() != null ? r.getHoraSalida().format(fmt) : "Aún dentro",
                    r.getEquipoId() != null ? r.getEquipoId().toString() : "No asignado"
            });
        }
    }

    private void exportarTXT() {
        JFileChooser chooser = new JFileChooser();
        String filtro = (String) cmbFiltroDia.getSelectedItem();
        String nombreArchivo = "bitacora";
        if (!filtro.equals("Todos los días")) {
            nombreArchivo += "_" + filtro;
        }
        nombreArchivo += ".txt";
        chooser.setSelectedFile(new java.io.File(nombreArchivo));

        int seleccion = chooser.showSaveDialog(this);
        if (seleccion != JFileChooser.APPROVE_OPTION)
            return;

        try (FileWriter writer = new FileWriter(chooser.getSelectedFile())) {
            writer.write("═══════════════════════════════════════════════════════════\n");
            writer.write("               BITÁCORA DE REGISTROS - CGTI                 \n");
            writer.write("═══════════════════════════════════════════════════════════\n");
            writer.write("Fecha de exportación: " + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + "\n");
            if (!filtro.equals("Todos los días")) {
                writer.write("Filtro aplicado: " + filtro + "\n");
            }
            writer.write("═══════════════════════════════════════════════════════════\n\n");

            writer.write(String.format("%-20s %-20s %-10s %-15s %-20s %-20s %-15s %-15s\n",
                    "Nombre", "Apellido", "Grupo", "Laboratorio",
                    "Entrada", "Salida", "Propósito", "Equipo ID"));

            List<Registro> registrosAExportar = obtenerRegistrosActuales();
            for (Registro r : registrosAExportar) {
                String grupo = "";
                if (r.getUsuario() instanceof Alumno) {
                    grupo = ((Alumno) r.getUsuario()).getGrupo();
                }

                writer.write(String.format("%-20s %-20s %-10s %-15s %-20s %-20s %-15s %-15s\n",
                        r.getUsuario().getNombre(),
                        r.getUsuario().getApellidoPaterno(),
                        grupo,
                        r.getLaboratorio() != null ? r.getLaboratorio().toString() : "",
                        r.getHoraEntrada() != null ? r.getHoraEntrada().format(fmt) : "",
                        r.getHoraSalida() != null ? r.getHoraSalida().format(fmt) : "Aún dentro",
                        r.getProposito(),
                        r.getEquipoId() != null ? r.getEquipoId().toString() : "No asignado"));
            }

            JOptionPane.showMessageDialog(this, "Archivo TXT exportado correctamente.");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error al exportar TXT: " + ex.getMessage());
        }
    }

    private void exportarExcel() {
        JFileChooser chooser = new JFileChooser();
        String filtro = (String) cmbFiltroDia.getSelectedItem();
        String nombreArchivo = "bitacora_computo.csv";
        chooser.setSelectedFile(new java.io.File(nombreArchivo));

        int seleccion = chooser.showSaveDialog(this);
        if (seleccion != JFileChooser.APPROVE_OPTION)
            return;

        try (FileWriter writer = new FileWriter(chooser.getSelectedFile(), java.nio.charset.StandardCharsets.UTF_8)) {
            writer.write('\ufeff');
            writer.write(
                    "Fecha;Nombre del Alumno;Grupo;Laboratorio a Usar;Práctica a Efectuar;Hora Entrada;Hora Salida;No. De Computadora\n");

            List<Registro> registrosAExportar = obtenerRegistrosActuales();
            for (Registro r : registrosAExportar) {
                String fecha = r.getHoraEntrada() != null ? r.getHoraEntrada().toLocalDate().toString() : "";
                String nombreCompleto = r.getUsuario().getApellidoPaterno() + ", " + r.getUsuario().getNombre();

                String grupo = "";
                if (r.getUsuario() instanceof Alumno) {
                    grupo = ((Alumno) r.getUsuario()).getGrupo();
                }

                String horaEntrada = r.getHoraEntrada() != null ? r.getHoraEntrada().format(fmt) : "";
                String horaSalida = r.getHoraSalida() != null ? r.getHoraSalida().format(fmt) : "Aún dentro";
                String lab = r.getLaboratorio() != null ? r.getLaboratorio().toString().replace(";", ",") : "";
                String proposito = r.getProposito() != null ? r.getProposito().replace(";", ",") : "";
                String equipoId = r.getEquipoId() != null ? r.getEquipoId().toString() : "No asignado";

                writer.write(String.format("%s;%s;%s;%s;%s;%s;%s;%s\n",
                        fecha, nombreCompleto, grupo, lab, proposito, horaEntrada, horaSalida, equipoId));
            }

            JOptionPane.showMessageDialog(this, "¡Archivo de Excel (.csv) exportado correctamente!");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error al exportar a Excel: " + ex.getMessage());
        }
    }

    private void exportarPDF() {
        JFileChooser chooser = new JFileChooser();
        String filtro = (String) cmbFiltroDia.getSelectedItem();
        String nombreArchivo = "bitacora_reporte.html";
        chooser.setSelectedFile(new java.io.File(nombreArchivo));

        int seleccion = chooser.showSaveDialog(this);
        if (seleccion != JFileChooser.APPROVE_OPTION)
            return;

        try (FileWriter writer = new FileWriter(chooser.getSelectedFile(), java.nio.charset.StandardCharsets.UTF_8)) {
            writer.write("<!DOCTYPE html>\n<html>\n<head>\n<meta charset='UTF-8'>\n");
            writer.write("<style>\nbody { font-family: Arial, sans-serif; margin: 30px; }\n");
            writer.write("table { width: 100%; border-collapse: collapse; margin-top: 15px; }\n");
            writer.write("th { background-color: #002f6c; color: white; padding: 10px; border: 1px solid #ddd; }\n");
            writer.write("td { padding: 8px; border: 1px solid #ddd; text-align: center; }\n");
            writer.write("</style>\n</head>\n<body>\n");
            writer.write("<h2>BITSYS - CGTI (Filtro: " + filtro + ")</h2>\n");
            writer.write(
                    "<table>\n<tr><th>Fecha</th><th>Nombre del Alumno</th><th>Grupo</th><th>Laboratorio</th><th>Práctica</th><th>Entrada</th><th>Salida</th><th>Computadora</th></tr>\n");

            List<Registro> registrosAExportar = obtenerRegistrosActuales();
            for (Registro r : registrosAExportar) {
                String fecha = r.getHoraEntrada() != null ? r.getHoraEntrada().toLocalDate().toString() : "";
                String nombreCompleto = r.getUsuario().getApellidoPaterno() + ", " + r.getUsuario().getNombre();

                String grupo = "";
                if (r.getUsuario() instanceof Alumno) {
                    grupo = ((Alumno) r.getUsuario()).getGrupo();
                }

                String horaEntrada = r.getHoraEntrada() != null ? r.getHoraEntrada().format(fmt) : "";
                String horaSalida = r.getHoraSalida() != null ? r.getHoraSalida().format(fmt) : "Aún dentro";
                String lab = r.getLaboratorio() != null ? r.getLaboratorio().toString() : "";
                String equipoId = r.getEquipoId() != null ? r.getEquipoId().toString() : "No asignado";

                writer.write(
                        String.format(
                                "<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>\n",
                                fecha, nombreCompleto, grupo, lab, r.getProposito(), horaEntrada, horaSalida,
                                equipoId));
            }

            writer.write("</table>\n</body>\n</html>");
            JOptionPane.showMessageDialog(this, "¡Reporte digital generado con éxito!");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error al crear reporte: " + ex.getMessage());
        }
    }

    private List<Registro> obtenerRegistrosActuales() {
        String filtro = (String) cmbFiltroDia.getSelectedItem();
        if (filtro.equals("Todos los días")) {
            return todosLosRegistros;
        } else {
            LocalDate fechaFiltro = LocalDate.parse(filtro);
            return todosLosRegistros.stream()
                    .filter(r -> r.getHoraEntrada() != null &&
                            r.getHoraEntrada().toLocalDate().equals(fechaFiltro))
                    .collect(Collectors.toList());
        }
    }
}