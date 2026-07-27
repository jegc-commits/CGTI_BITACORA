package cgti.vistas;

import java.awt.Color;
import java.awt.Component;
import java.time.LocalDate;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import cgti.clase.Administrador;
import cgti.clase.Alumno;
import cgti.clase.Rol;
import cgti.clase.Usuario;
import cgti.funcion.UsuarioFuncion;

public class RegistroUsuarioFrame extends JFrame {

    private UsuarioFuncion Funcion = new UsuarioFuncion();

    public RegistroUsuarioFrame(Usuario admin) {
        setTitle("Nuevo Usuario - CGTI");
        setSize(450, 600);
        setLocationRelativeTo(null);
        Estilos.bloquearCierreConX(this, "Termina tu registro");

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel titulo = new JLabel("Nuevo Usuario");
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField txtNombre = new JTextField();
        JTextField txtPaterno = new JTextField();
        JTextField txtMaterno = new JTextField();
        JTextField txtFecha = new JTextField();
        JTextField txtTelefono = new JTextField();

        // Solo se muestran los roles que el usuario que crea (admin) tiene permiso de
        // asignar.
        // Un ADMINISTRADOR normal no puede crear otros administradores; eso es
        // exclusivo del SUPERADMINISTRADOR.
        java.util.List<Rol> rolesPermitidos = new java.util.ArrayList<>();
        for (Rol r : Rol.values()) {
            if (Funcion.puedeAsignarRol(admin.getRol(), r)) {
                rolesPermitidos.add(r);
            }
        }
        JComboBox<Rol> cmbRol = new JComboBox<>(rolesPermitidos.toArray(new Rol[0]));

        JTextField txtMatricula = new JTextField();
        JTextField txtCarrera = new JTextField();
        JTextField txtCuatrimestre = new JTextField();
        JTextField txtGrupoAlumno = new JTextField();

        JPanel panelAlumno = new JPanel();
        panelAlumno.setLayout(new BoxLayout(panelAlumno, BoxLayout.Y_AXIS));
        panelAlumno.add(new JLabel("Matrícula:"));
        panelAlumno.add(txtMatricula);
        panelAlumno.add(new JLabel("Carrera:"));
        panelAlumno.add(txtCarrera);
        panelAlumno.add(new JLabel("Cuatrimestre:"));
        panelAlumno.add(txtCuatrimestre);
        panelAlumno.add(new JLabel("Grupo:"));
        panelAlumno.add(txtGrupoAlumno);

        JPanel panelExtra = new JPanel();
        panelExtra.setLayout(new BoxLayout(panelExtra, BoxLayout.Y_AXIS));

        cmbRol.addActionListener(e -> {
            panelExtra.removeAll();
            if (cmbRol.getSelectedItem() == Rol.ALUMNO) {
                panelExtra.add(panelAlumno);
            }
            panelExtra.revalidate();
            panelExtra.repaint();
        });

        JLabel lblMensaje = new JLabel(" ");
        lblMensaje.setForeground(Color.RED);
        lblMensaje.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");

        btnGuardar.addActionListener(e -> {
            if (txtNombre.getText().isEmpty() || txtPaterno.getText().isEmpty() ||
                    txtFecha.getText().isEmpty()) {
                lblMensaje.setText("Completa todos los campos obligatorios");
                return;
            }
            try {
                Usuario nuevo;
                Rol rolSel = (Rol) cmbRol.getSelectedItem();
                switch (rolSel) {
                    case ALUMNO -> {
                        Alumno a = new Alumno();
                        if (txtMatricula.getText().trim().isEmpty()) {
                            lblMensaje.setText("La matrícula es obligatoria para alumnos");
                            return;
                        }
                        try {
                            a.setMatricula(Integer.parseInt(txtMatricula.getText().trim()));
                        } catch (NumberFormatException ex) {
                            lblMensaje.setText("La matrícula debe ser un número");
                            return;
                        }
                        a.setCarrera(txtCarrera.getText());
                        a.setCuatrimestre(txtCuatrimestre.getText());
                        a.setGrupo(txtGrupoAlumno.getText());
                        nuevo = a;
                    }
                    default -> nuevo = new Administrador();
                }

                nuevo.setNombre(txtNombre.getText());
                nuevo.setApellidoPaterno(txtPaterno.getText());
                nuevo.setApellidoMaterno(txtMaterno.getText());
                nuevo.setFechaNacimiento(LocalDate.parse(txtFecha.getText()));
                nuevo.setTelefono(txtTelefono.getText());
                nuevo.setRol(rolSel);

                String resultado = Funcion.registrar(nuevo);
                lblMensaje.setForeground(Color.BLACK);
                lblMensaje.setText(resultado);

                if (resultado.equals("Usuario registrado correctamente")) {
                    dispose();
                    new UsuarioFrame(admin).setVisible(true);
                }
            } catch (Exception ex) {
                lblMensaje.setForeground(Color.RED);
                lblMensaje.setText("Error: " + ex.getMessage());
            }
        });

        btnCancelar.addActionListener(e -> {
            dispose();
            new UsuarioFrame(admin).setVisible(true);
        });

        panel.add(titulo);
        panel.add(Box.createVerticalStrut(10));
        panel.add(new JLabel("Nombre:"));
        panel.add(txtNombre);
        panel.add(new JLabel("Apellido Paterno:"));
        panel.add(txtPaterno);
        panel.add(new JLabel("Apellido Materno:"));
        panel.add(txtMaterno);
        panel.add(new JLabel("Fecha Nacimiento (AAAA-MM-DD):"));
        panel.add(txtFecha);
        panel.add(new JLabel("Teléfono (opcional):"));
        panel.add(txtTelefono);
        panel.add(new JLabel("Rol:"));
        panel.add(cmbRol);
        panel.add(panelExtra);
        panel.add(Box.createVerticalStrut(15));
        panel.add(btnGuardar);
        panel.add(Box.createVerticalStrut(5));
        panel.add(btnCancelar);
        panel.add(Box.createVerticalStrut(10));
        panel.add(lblMensaje);

        add(panel);
    }
}
