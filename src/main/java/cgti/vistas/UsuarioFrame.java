package cgti.vistas;

import cgti.clase.*;
import cgti.funcion.UsuarioFuncion;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class UsuarioFrame extends JFrame {

    private UsuarioFuncion Funcion = new UsuarioFuncion();
    private DefaultTableModel model;
    private JTable tabla;

    public UsuarioFrame(Usuario admin) {
        setTitle("Gestión de Usuarios - CGTI");
        setSize(800, 600);
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

        barra.setPreferredSize(new Dimension(500, 100));
        add(barra, BorderLayout.NORTH);

        // ----- Panel del formulario -----
        JPanel panelFormulario = new JPanel();
        panelFormulario.setLayout(new BoxLayout(panelFormulario, BoxLayout.Y_AXIS));
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        panelFormulario.setBackground(Estilos.COLOR_BLANCO);

        JLabel lblTitulo = new JLabel("Gestión de Usuarios");
        lblTitulo.setFont(Estilos.GRANDE);
        lblTitulo.setForeground(Estilos.COLOR_NEGRO);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblUsuario = new JLabel("Administrador: " + admin.getNombre() + " " + admin.getApellidoPaterno());
        lblUsuario.setFont(Estilos.TITULO.deriveFont(Font.PLAIN, 14));
        lblUsuario.setForeground(Estilos.COLOR_NEGRO);
        lblUsuario.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Tabla con matrícula en lugar de ID
        model = new DefaultTableModel(new Object[] { "Matrícula", "Nombre", "Apellido", "Rol" }, 0);
        tabla = new JTable(model);
        tabla.setFont(Estilos.TITULO.deriveFont(Font.PLAIN, 12));
        tabla.getTableHeader().setFont(Estilos.TITULO.deriveFont(Font.BOLD, 12));
        tabla.getTableHeader().setBackground(Estilos.COLOR_AZUL);
        tabla.getTableHeader().setForeground(Estilos.COLOR_BLANCO);
        tabla.setRowHeight(25);

        JScrollPane scrollTabla = new JScrollPane(tabla);
        scrollTabla.setAlignmentX(Component.CENTER_ALIGNMENT);
        scrollTabla.setPreferredSize(new Dimension(740, 300));
        scrollTabla.setMaximumSize(new Dimension(740, 300));

        cargarDatos();

        // Botones
        JButton btnNuevo = new JButton("Nuevo Usuario");
        JButton btnEliminar = new JButton("Eliminar Usuario");
        JButton btnRegresar = new JButton("Regresar");

        Estilos.estilizarBotonRedondo(btnNuevo, Estilos.COLOR_VERDE, Estilos.COLOR_BLANCO, 30);
        Estilos.estilizarBotonRedondo(btnEliminar, Estilos.COLOR_VERDE, Estilos.COLOR_BLANCO, 30);
        Estilos.estilizarBotonRedondo(btnRegresar, Estilos.COLOR_VERDE, Estilos.COLOR_BLANCO, 30);

        btnNuevo.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnEliminar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRegresar.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Solo el superadministrador puede crear o eliminar usuarios
        boolean soloLectura = admin.getRol() != Rol.SUPERADMINISTRADOR;
        btnNuevo.setVisible(!soloLectura);
        btnEliminar.setVisible(!soloLectura);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        panelBotones.setBackground(Estilos.COLOR_BLANCO);
        panelBotones.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelBotones.add(btnNuevo);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnRegresar);

        // --- Acciones ---
        btnNuevo.addActionListener(e -> {
            dispose();
            new RegistroUsuarioFrame(admin).setVisible(true);
        });

        btnEliminar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila == -1) {
                JOptionPane.showMessageDialog(this,
                        "Seleccione un usuario para eliminar",
                        "Aviso",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            int matricula = (int) model.getValueAt(fila, 0);
            String nombre = (String) model.getValueAt(fila, 1);
            String apellido = (String) model.getValueAt(fila, 2);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Eliminar al usuario " + nombre + " " + apellido + "?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                String resultado = Funcion.eliminar(matricula); // CORREGIDO: String en lugar de int
                cargarDatos();
                JOptionPane.showMessageDialog(this,
                        resultado,
                        resultado.contains("correctamente") ? "Éxito" : "Aviso",
                        resultado.contains("correctamente") ? JOptionPane.INFORMATION_MESSAGE
                                : JOptionPane.WARNING_MESSAGE);
            }
        });

        btnRegresar.addActionListener(e -> {
            dispose();
            new DashboardFrame(admin).setVisible(true); // AGREGADO: abrir DashboardFrame
        });

        // Ensamblar
        panelFormulario.add(lblTitulo);
        panelFormulario.add(Box.createVerticalStrut(8));
        panelFormulario.add(lblUsuario);
        panelFormulario.add(Box.createVerticalStrut(20));
        panelFormulario.add(scrollTabla);
        panelFormulario.add(Box.createVerticalStrut(20));
        panelFormulario.add(panelBotones);

        add(panelFormulario, BorderLayout.CENTER);
        setVisible(true);
    }

    private void cargarDatos() {
        model.setRowCount(0);
        for (Usuario u : Funcion.listarTodos()) {
            model.addRow(new Object[] {
                    u.getMatricula(), // AGREGADO
                    u.getNombre(),
                    u.getApellidoPaterno(),
                    u.getRol()
            });
        }
    }
}