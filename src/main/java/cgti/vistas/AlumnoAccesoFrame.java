package cgti.vistas;

import cgti.funcion.UsuarioFuncion;
import java.awt.*;
import javax.swing.*;

public class AlumnoAccesoFrame extends JFrame {

    private UsuarioFuncion funcion = new UsuarioFuncion();

    public AlumnoAccesoFrame() {
        setTitle("Registro");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setBackground(Estilos.COLOR_BLANCO);
        panel.setLayout(null);

        JLabel titulo = new JLabel("Acceso Alumno", SwingConstants.CENTER);
        titulo.setFont(Estilos.TITULO);
        titulo.setForeground(Estilos.COLOR_NEGRO);
        titulo.setBounds(50, 25, 200, 30);

        // Etiqueta de Matrícula
        JLabel lblMatricula = new JLabel("Matrícula:", SwingConstants.CENTER);
        lblMatricula.setForeground(Estilos.COLOR_NEGRO);
        lblMatricula.setFont(new Font("Arial", Font.BOLD, 12));
        lblMatricula.setBounds(50, 75, 200, 25);

        // Campo de texto para la Matrícula
        JTextField txtMatricula = new JTextField();
        txtMatricula.setBounds(50, 105, 200, 30);
        txtMatricula.setHorizontalAlignment(JTextField.CENTER);

        // Etiqueta para mensajes de error o avisos
        JLabel lblMensaje = new JLabel(" ", SwingConstants.CENTER);
        lblMensaje.setForeground(Color.RED);
        lblMensaje.setBounds(20, 145, 260, 25);

        // Configuración y personalización de botones
        JButton btnEntrar = new JButton("Entrar");
        btnEntrar.setBounds(160, 185, 100, 35);
        Estilos.estilizarBotonRedondo(btnEntrar, Estilos.COLOR_VERDE, Estilos.COLOR_BLANCO, 20);

        JButton btnRegresar = new JButton("Regresar");
        btnRegresar.setBounds(40, 185, 100, 35);
        Estilos.estilizarBotonRedondo(btnRegresar, Estilos.COLOR_VERDE, Estilos.COLOR_BLANCO, 20);

        // --- EVENTOS DE LOS BOTONES ---
        btnEntrar.addActionListener(e -> {
            String matriculaTexto = txtMatricula.getText().trim();
            if (matriculaTexto.isEmpty()) {
                lblMensaje.setText("Ingresa tu matrícula");
                return;
            } else if (matriculaTexto.length() != 10) {
                lblMensaje.setText("Su matrícula debe tener 10 caracteres");
                return;
            }

            int matricula = Integer.parseInt(matriculaTexto);
            var resultado = funcion.buscarPorMatricula(matricula);

            if (resultado.isPresent()) {
                new DashboardFrame(resultado.get()).setVisible(true);
                dispose();
            } else {
                new RegistroAlumnoFrame(matricula).setVisible(true);
                dispose();
            }
        });

        btnRegresar.addActionListener(e -> {
            dispose();
            new InicioFrame().setVisible(true);
        });

        // Agregando todos los componentes al panel mediante sus posiciones fijas
        panel.add(titulo);
        panel.add(lblMatricula);
        panel.add(txtMatricula);
        panel.add(lblMensaje);
        panel.add(btnEntrar);
        panel.add(btnRegresar);

        // Agregamos el panel principal al JFrame
        add(panel);

        // Forzamos las dimensiones exactas sin riesgo de que la barra superior recorte
        // los botones
        this.setResizable(false);
        this.setSize(315, 280);
        this.setLocationRelativeTo(null);
    }

}