package cgti.vistas;

import cgti.clase.Apartado;
import cgti.clase.Laboratorio;
import cgti.clase.Rol;
import cgti.clase.Usuario;
import cgti.funcion.ApartadoFuncion;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class HorarioBloqueoFrame extends JFrame {

    private ApartadoFuncion apartadoFuncion = new ApartadoFuncion();
    private DefaultTableModel modeloBloqueos;
    private DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public HorarioBloqueoFrame(Usuario usuario) {
        setTitle("Insertar horario de laboratorio - CGTI");
        setSize(560, 700);
        Estilos.bloquearCierreConX(this, "usar boton regresar");
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        JPanel barra = new JPanel(null) {
            private Image imagenFondo;
            {
                try {

                    imagenFondo = new ImageIcon(getClass().getResource("/imagenes/d2.jpg")).getImage();
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

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 50, 20, 50));
        panel.setBackground(Estilos.COLOR_BLANCO);

        JLabel titulo = new JLabel("Insertar horario");
        titulo.setFont(Estilos.GRANDE);
        titulo.setForeground(Estilos.COLOR_NEGRO);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JComboBox<Laboratorio> cmbLab = new JComboBox<>(Laboratorio.values());
        JTextField txtFecha = new JTextField(LocalDate.now().toString());
        JComboBox<String> cmbHoraInicio = new JComboBox<>(crearHoras());
        JComboBox<String> cmbHoraFin = new JComboBox<>(crearHoras());
        JTextField txtMotivo = new JTextField("Clase / mantenimiento");

        Dimension campoSize = new Dimension(420, 30);
        cmbLab.setMaximumSize(campoSize);
        txtFecha.setMaximumSize(campoSize);
        cmbHoraInicio.setMaximumSize(campoSize);
        cmbHoraFin.setMaximumSize(campoSize);
        txtMotivo.setMaximumSize(campoSize);

        cmbLab.setAlignmentX(Component.CENTER_ALIGNMENT);
        txtFecha.setAlignmentX(Component.CENTER_ALIGNMENT);
        cmbHoraInicio.setAlignmentX(Component.CENTER_ALIGNMENT);
        cmbHoraFin.setAlignmentX(Component.CENTER_ALIGNMENT);
        txtMotivo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblMensaje = new JLabel(" ");
        lblMensaje.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblMensaje.setForeground(Color.RED);

        JButton btnGuardar = new JButton("Insertar horario");
        JButton btnImportar = new JButton("Importar horario");
        JButton btnRegresar = new JButton("Regresar");
        Estilos.estilizarBotonRedondo(btnGuardar, Estilos.COLOR_VERDE, Estilos.COLOR_BLANCO, 30);
        Estilos.estilizarBotonRedondo(btnImportar, Estilos.COLOR_VERDE, Estilos.COLOR_BLANCO, 30);
        Estilos.estilizarBotonRedondo(btnRegresar, Estilos.COLOR_VERDE, Estilos.COLOR_BLANCO, 30);
        btnGuardar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnImportar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRegresar.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextArea txtImportar = new JTextArea(5, 30);
        txtImportar.setText("LAB01,2026-07-08,16:00,17:00,Clase de redes");
        txtImportar.setLineWrap(true);
        txtImportar.setWrapStyleWord(true);
        JScrollPane scrollImportar = new JScrollPane(txtImportar);
        scrollImportar.setPreferredSize(new Dimension(420, 90));
        scrollImportar.setMaximumSize(new Dimension(420, 90));
        scrollImportar.setAlignmentX(Component.CENTER_ALIGNMENT);

        modeloBloqueos = new DefaultTableModel(
                new Object[] { "Laboratorio", "Inicio", "Fin", "Motivo" }, 0);
        JTable tabla = new JTable(modeloBloqueos);
        tabla.setRowHeight(24);
        tabla.setFont(Estilos.TITULO.deriveFont(java.awt.Font.PLAIN, 12));
        tabla.getTableHeader().setFont(Estilos.TITULO.deriveFont(java.awt.Font.BOLD, 12));
        tabla.getTableHeader().setBackground(Estilos.COLOR_AZUL);
        tabla.getTableHeader().setForeground(Estilos.COLOR_BLANCO);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setPreferredSize(new Dimension(420, 180));
        scroll.setMaximumSize(new Dimension(420, 180));
        scroll.setAlignmentX(Component.CENTER_ALIGNMENT);

        cargarBloqueos();

        btnGuardar.addActionListener(e -> {
            try {
                LocalDate fecha = LocalDate.parse(txtFecha.getText().trim());
                LocalTime horaInicio = LocalTime.parse((String) cmbHoraInicio.getSelectedItem());
                LocalTime horaFin = LocalTime.parse((String) cmbHoraFin.getSelectedItem());
                LocalDateTime inicio = LocalDateTime.of(fecha, horaInicio);
                LocalDateTime fin = LocalDateTime.of(fecha, horaFin);

                if (!fin.isAfter(inicio)) {
                    lblMensaje.setForeground(Color.RED);
                    lblMensaje.setText("La hora fin debe ser mayor a la hora inicio");
                    return;
                }

                String resultado = apartadoFuncion.bloquearLaboratorio(
                        (Laboratorio) cmbLab.getSelectedItem(),
                        inicio,
                        fin,
                        txtMotivo.getText().trim());

                boolean correcto = resultado.contains("correctamente");
                lblMensaje.setForeground(correcto ? new Color(0, 128, 0) : Color.RED);
                lblMensaje.setText(resultado);
                cargarBloqueos();
            } catch (Exception ex) {
                lblMensaje.setForeground(Color.RED);
                lblMensaje.setText("Error: " + ex.getMessage());
            }
        });

        btnRegresar.addActionListener(e -> dispose());

        btnImportar.addActionListener(e -> {
            String resultado = apartadoFuncion.importarHorarios(txtImportar.getText());
            boolean correcto = resultado.contains("0 con error");
            lblMensaje.setForeground(correcto ? new Color(0, 128, 0) : Color.RED);
            lblMensaje.setText(resultado);
            cargarBloqueos();
        });

        if (usuario.getRol() != Rol.SUPERADMINISTRADOR) {
            btnGuardar.setEnabled(false);
            btnImportar.setEnabled(false);
            lblMensaje.setForeground(Color.RED);
            lblMensaje.setText("Solo el superadministrador puede insertar horarios");
        }

        panel.add(titulo);
        panel.add(Box.createVerticalStrut(15));
        panel.add(Estilos.crearLabelCentrado("Laboratorio:"));
        panel.add(Box.createVerticalStrut(5));
        panel.add(cmbLab);
        panel.add(Box.createVerticalStrut(10));
        panel.add(Estilos.crearLabelCentrado("Fecha (AAAA-MM-DD):"));
        panel.add(Box.createVerticalStrut(5));
        panel.add(txtFecha);
        panel.add(Box.createVerticalStrut(10));
        panel.add(Estilos.crearLabelCentrado("Hora inicio:"));
        panel.add(Box.createVerticalStrut(5));
        panel.add(cmbHoraInicio);
        panel.add(Box.createVerticalStrut(10));
        panel.add(Estilos.crearLabelCentrado("Hora fin:"));
        panel.add(Box.createVerticalStrut(5));
        panel.add(cmbHoraFin);
        panel.add(Box.createVerticalStrut(10));
        panel.add(Estilos.crearLabelCentrado("Motivo:"));
        panel.add(Box.createVerticalStrut(5));
        panel.add(txtMotivo);
        panel.add(Box.createVerticalStrut(15));
        panel.add(btnGuardar);
        panel.add(Box.createVerticalStrut(8));
        panel.add(Estilos.crearLabelCentrado("Importar horario: LAB01,2026-07-08,16:00,17:00,Motivo"));
        panel.add(Box.createVerticalStrut(5));
        panel.add(scrollImportar);
        panel.add(Box.createVerticalStrut(8));
        panel.add(btnImportar);
        panel.add(Box.createVerticalStrut(8));
        panel.add(btnRegresar);
        panel.add(Box.createVerticalStrut(10));
        panel.add(lblMensaje);
        panel.add(Box.createVerticalStrut(15));
        panel.add(Estilos.crearLabelCentrado("Horarios insertados:"));
        panel.add(Box.createVerticalStrut(5));
        panel.add(scroll);

        add(new JScrollPane(panel), BorderLayout.CENTER);
        setVisible(true);
    }

    private String[] crearHoras() {
        String[] horas = new String[12];
        int idx = 0;
        for (int h = 16; h <= 21; h++) {
            horas[idx++] = String.format("%02d:00", h);
            horas[idx++] = String.format("%02d:30", h);
        }
        return horas;
    }

    private void cargarBloqueos() {
        modeloBloqueos.setRowCount(0);
        for (Apartado bloqueo : apartadoFuncion.listarBloqueos()) {
            modeloBloqueos.addRow(new Object[] {
                    bloqueo.getLaboratorio(),
                    bloqueo.getHoraInicio() != null ? bloqueo.getHoraInicio().format(fmt) : "",
                    bloqueo.getHoraFin() != null ? bloqueo.getHoraFin().format(fmt) : "",
                    bloqueo.getProposito()
            });
        }
    }
}
