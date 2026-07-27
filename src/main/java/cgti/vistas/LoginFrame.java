package cgti.vistas; // pertenece al paquete 

import cgti.clase.*;
import cgti.funcion.UsuarioFuncion;
import java.awt.*; // importamos la libreria swing
import javax.swing.*;

public class LoginFrame extends JFrame {

    private UsuarioFuncion Funcion = new UsuarioFuncion();

    public LoginFrame() {
        setTitle("Inicio de Sesion");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 1. Creamos el panel
        JPanel panel = new JPanel();
        panel.setBackground(Estilos.COLOR_BLANCO);
        panel.setLayout(null); //

        // 2. TÍTULO
        JLabel titulo = new JLabel("Iniciar Sesión", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        titulo.setForeground(Estilos.COLOR_NEGRO);
        titulo.setBounds(100, 20, 200, 30); // Centrado arriba

        // Etiqueta "Matrícula:"
        JLabel lblMatriculaTag = new JLabel("Matrícula:");
        lblMatriculaTag.setForeground(Estilos.COLOR_NEGRO);
        lblMatriculaTag.setBounds(50, 80, 80, 25);

        // 3.
        JTextField txtMatricula = new JTextField();
        txtMatricula.setBounds(110, 80, 190, 25);
        txtMatricula.setForeground(Estilos.COLOR_NEGRO);

        // Etiqueta para mensajes de error o avisos
        JLabel lblMensaje = new JLabel("", SwingConstants.CENTER);
        lblMensaje.setForeground(Color.RED);
        lblMensaje.setBounds(40, 120, 320, 25); // Espacio libre en medio para avisos

        // 4. BOTONES (Ubicados uno al lado del otro)
        JButton btnEntrar = new JButton("Entrar");
        btnEntrar.setBounds(210, 170, 110, 35); // Botón izquierdo
        Estilos.estilizarBotonRedondo(btnEntrar, Estilos.COLOR_VERDE, Estilos.COLOR_BLANCO, 20);

        JButton btnRegresar = new JButton("Regresar");
        btnRegresar.setBounds(70, 170, 110, 35); // Botón derecho
        Estilos.estilizarBotonRedondo(btnRegresar, Estilos.COLOR_VERDE, Estilos.COLOR_BLANCO, 20);

        btnEntrar.addActionListener(e -> {
            String matriculaTexto = txtMatricula.getText().trim();
            if (matriculaTexto.isEmpty()) {
                lblMensaje.setText("Ingresa tu matrícula");
                return;
            }

            int matricula = Integer.parseInt(matriculaTexto);
            var resultado = Funcion.buscarPorMatricula(matricula);

            if (resultado.isPresent()) {
                dispose();
                new DashboardFrame(resultado.get()).setVisible(true);
            } else {
                lblMensaje.setText("NO EXISTE REGISTRO DE MATRICULA");
                lblMensaje.setForeground(Color.RED);
            }
        });

        btnRegresar.addActionListener(e -> {
            dispose();
            new InicioFrame().setVisible(true);
        });

        // 5. AGREGA TODOS LOS COMPONENTES AL PANEL
        panel.add(titulo);
        panel.add(lblMatriculaTag);
        panel.add(txtMatricula);
        panel.add(lblMensaje);
        panel.add(btnEntrar);
        panel.add(btnRegresar);

        // 6. Agregamos el panel principal al JFrame
        add(panel);
    } // Cierre correcto del constructor

}