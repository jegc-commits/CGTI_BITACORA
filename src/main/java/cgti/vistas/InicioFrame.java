package cgti.vistas;// pertenece al paquete 

import javax.swing.*;// importamos libreria javax.swing para crear botones ,pantallas etc
import java.awt.*;
import cgti.clase.*;

public class InicioFrame extends JFrame { // clase inicio que hereda funciones de JFrame

    public InicioFrame() { // contructor
        setTitle("BitaSys");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // al cerrar la ventana termina la aplicacion

        JPanel panel = new JPanel();
        panel.setBackground(Estilos.COLOR_BLANCO);
        panel.setLayout(null); // Diseño absoluto para usar setBounds

        JLabel titulo = new JLabel("Bienvenido a BitaSys", SwingConstants.CENTER);
        titulo.setFont(Estilos.GRANDE);
        titulo.setForeground(Estilos.COLOR_NEGRO);
        titulo.setBounds(50, 30, 300, 30);

        // Creación de botones
        JButton btnSesion = new JButton("Iniciar sesión");
        btnSesion.setBounds(100, 100, 200, 40);
        btnSesion.setFont(new Font("BakerSignet BT", Font.PLAIN, 18));
        Estilos.estilizarBotonRedondo(btnSesion, Estilos.COLOR_VERDE, Estilos.COLOR_BLANCO, 30);

        JButton btnRegistrar = new JButton("Registrarse");
        btnRegistrar.setBounds(100, 160, 200, 40);
        btnRegistrar.setFont(new Font("BakerSignet BT", Font.PLAIN, 18));
        Estilos.estilizarBotonRedondo(btnRegistrar, Estilos.COLOR_VERDE, Estilos.COLOR_BLANCO, 30);

        JButton btnObservador = new JButton("visitar");
        btnObservador.setBounds(150, 250, 100, 20);
        btnObservador.setFont(Estilos.PEQUEÑO);
        Estilos.estilizarBotonRedondo(btnObservador, Estilos.COLOR_VERDE, Estilos.COLOR_BLANCO, 10);

        // accion que hara boton admin
        btnSesion.addActionListener(e -> {
            new LoginFrame().setVisible(true); // crea al objeto LoginFrame() y lo muetra
            dispose(); // destruye esta ventana
        });

        btnRegistrar.addActionListener(e -> {
            new AlumnoAccesoFrame().setVisible(true);// crea el objeto AlumnoAccesoFrame() y lo muetra
            dispose();
        });

        btnObservador.addActionListener(e -> {

            Alumno observador = new Alumno();
            observador.setMatricula(0000000000);
            ;
            observador.setNombre("Modo");
            observador.setApellidoPaterno("Invitado");
            observador.setApellidoMaterno("");
            observador.setRol(Rol.OBSERVADOR);

            new DashboardFrame(observador).setVisible(true);
            dispose();
        });

        // Agregamos los componentes en sus posiciones fijas
        panel.add(titulo);
        panel.add(btnSesion);
        panel.add(btnRegistrar);
        panel.add(btnObservador);

        add(panel);

        // Forzamos las dimensiones de la ventana para evitar recortes
        this.setResizable(false);
        this.setSize(400, 340);
        this.setLocationRelativeTo(null);// crea la pantalla centrada (Siempre al final)
    }

}
