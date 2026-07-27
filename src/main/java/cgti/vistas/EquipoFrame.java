package cgti.vistas;

import cgti.clase.*;
import cgti.funcion.EquipoFuncion;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class EquipoFrame extends JFrame {

    private EquipoFuncion equipoFuncion = new EquipoFuncion();
    private DefaultTableModel model;
    private JLabel lblMensaje;

    public EquipoFrame(Usuario admin) {
        setTitle("Gestión de Equipos - CGTI");
        setSize(800, 650);
        Estilos.bloquearCierreConX(this, "usar boton regresar");
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
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

        barra.setPreferredSize(new Dimension(800, 100));
        add(barra, BorderLayout.NORTH);

        // ----- Panel del formulario -----
        JPanel panelFormulario = new JPanel();
        panelFormulario.setLayout(new BoxLayout(panelFormulario, BoxLayout.Y_AXIS));
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        panelFormulario.setBackground(Estilos.COLOR_BLANCO);

        JLabel lblTitulo = new JLabel("Gestión de Equipos");
        lblTitulo.setFont(Estilos.GRANDE);
        lblTitulo.setForeground(Estilos.COLOR_NEGRO);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblUsuario = new JLabel("Administrador: " + admin.getNombre() + " " + admin.getApellidoPaterno());
        lblUsuario.setFont(Estilos.TITULO.deriveFont(Font.PLAIN, 14));
        lblUsuario.setForeground(Estilos.COLOR_NEGRO);
        lblUsuario.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Formulario para nuevo equipo
        JComboBox<Laboratorio> cmbLab = new JComboBox<>(Laboratorio.values());
        cmbLab.setMaximumSize(new Dimension(400, 30));
        cmbLab.setFont(Estilos.TITULO.deriveFont(Font.PLAIN, 14));
        cmbLab.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField txtNombre = new JTextField();
        txtNombre.setMaximumSize(new Dimension(400, 30));
        txtNombre.setFont(Estilos.TITULO.deriveFont(Font.PLAIN, 14));
        txtNombre.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextArea txtSoftware = new JTextArea(3, 20);
        txtSoftware.setFont(Estilos.TITULO.deriveFont(Font.PLAIN, 14));
        txtSoftware.setLineWrap(true);
        txtSoftware.setWrapStyleWord(true);
        JScrollPane scrollSoftware = new JScrollPane(txtSoftware);
        scrollSoftware.setMaximumSize(new Dimension(400, 60));
        scrollSoftware.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnGuardar = new JButton("Guardar Equipo");
        Estilos.estilizarBotonRedondo(btnGuardar, Estilos.COLOR_VERDE, Estilos.COLOR_BLANCO, 30);
        btnGuardar.setAlignmentX(Component.CENTER_ALIGNMENT);

        // El observador solo puede consultar el listado de equipos, no puede registrar
        // nuevos
        boolean soloLectura = admin.getRol() == Rol.OBSERVADOR;
        cmbLab.setEnabled(!soloLectura);
        txtNombre.setEnabled(!soloLectura);
        txtSoftware.setEnabled(!soloLectura);
        btnGuardar.setVisible(!soloLectura);

        lblMensaje = new JLabel(" ");
        lblMensaje.setForeground(Color.RED);
        lblMensaje.setFont(Estilos.TITULO.deriveFont(Font.PLAIN, 13));
        lblMensaje.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Tabla
        model = new DefaultTableModel(
                new Object[] { "ID", "Laboratorio", "Equipo", "Software" }, 0);
        JTable tabla = new JTable(model);
        tabla.setFont(Estilos.TITULO.deriveFont(Font.PLAIN, 12));
        tabla.getTableHeader().setFont(Estilos.TITULO.deriveFont(Font.BOLD, 12));
        tabla.getTableHeader().setBackground(Estilos.COLOR_AZUL);
        tabla.getTableHeader().setForeground(Estilos.COLOR_BLANCO);
        tabla.setRowHeight(25);

        JScrollPane scrollTabla = new JScrollPane(tabla);
        scrollTabla.setAlignmentX(Component.CENTER_ALIGNMENT);
        scrollTabla.setPreferredSize(new Dimension(740, 200));
        scrollTabla.setMaximumSize(new Dimension(740, 200));

        cargarDatos();

        // Botón regresar
        JButton btnRegresar = new JButton("Regresar");
        Estilos.estilizarBotonRedondo(btnRegresar, Estilos.COLOR_VERDE, Estilos.COLOR_BLANCO, 30);
        btnRegresar.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- Acciones ---
        btnGuardar.addActionListener(e -> {
            if (txtNombre.getText().isEmpty()) {
                lblMensaje.setForeground(Color.RED);
                lblMensaje.setText("Escribe el nombre del equipo");
                return;
            }

            Equipo equipo = new Equipo();
            equipo.setLaboratorio((Laboratorio) cmbLab.getSelectedItem());
            equipo.setNombreEqp(txtNombre.getText());
            equipo.setSoftware(txtSoftware.getText());

            equipoFuncion.guardar(equipo);

            lblMensaje.setForeground(new Color(0, 128, 0));
            lblMensaje.setText("✓ Equipo guardado correctamente");

            txtNombre.setText("");
            txtSoftware.setText("");
            cargarDatos();
        });

        btnRegresar.addActionListener(e -> {
            dispose();
        });

        // Ensamblar
        panelFormulario.add(lblTitulo);
        panelFormulario.add(Box.createVerticalStrut(8));
        panelFormulario.add(lblUsuario);
        panelFormulario.add(Box.createVerticalStrut(20));
        panelFormulario.add(new JLabel("Laboratorio:") {
            {
                setAlignmentX(Component.CENTER_ALIGNMENT);
            }
        });
        panelFormulario.add(Box.createVerticalStrut(5));
        panelFormulario.add(cmbLab);
        panelFormulario.add(Box.createVerticalStrut(10));
        panelFormulario.add(new JLabel("Nombre del equipo (ej. COMP01):") {
            {
                setAlignmentX(Component.CENTER_ALIGNMENT);
            }
        });
        panelFormulario.add(Box.createVerticalStrut(5));
        panelFormulario.add(txtNombre);
        panelFormulario.add(Box.createVerticalStrut(10));
        panelFormulario.add(new JLabel("Software (descripción):") {
            {
                setAlignmentX(Component.CENTER_ALIGNMENT);
            }
        });
        panelFormulario.add(Box.createVerticalStrut(5));
        panelFormulario.add(scrollSoftware);
        panelFormulario.add(Box.createVerticalStrut(15));
        panelFormulario.add(btnGuardar);
        panelFormulario.add(Box.createVerticalStrut(8));
        panelFormulario.add(lblMensaje);
        panelFormulario.add(Box.createVerticalStrut(20));
        panelFormulario.add(scrollTabla);
        panelFormulario.add(Box.createVerticalStrut(20));
        panelFormulario.add(btnRegresar);

        add(panelFormulario, BorderLayout.CENTER);
        setVisible(true);
    }

    private void cargarDatos() {
        model.setRowCount(0);
        for (Equipo eq : equipoFuncion.listarTodos()) {
            model.addRow(new Object[] {
                    eq.getId(),
                    eq.getLaboratorio(),
                    eq.getNombreEqp(),
                    eq.getSoftware()
            });
        }
    }
}